package es.nimio.nimiogcs.operaciones.artefactos.externa;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.artifact.JavaScopes;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConAlcance;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public class BuscarDependenciasAdicionales extends ProcesoAsincronoModulo<Artefacto> {

	public BuscarDependenciasAdicionales(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ----
	
	@Override
	protected String nombreUnicoOperacion(Artefacto artefacto, ProcesoAsincrono op) {
		return "BUSCAR DEPENDENCIAS EXTERNAS DEL ARTEFACTO '" +  artefacto.getNombre() + "'";
	}

	@Override
	protected void relacionarOperacionConEntidades(Artefacto artefacto, ProcesoAsincrono op) {
		registraRelacionConOperacion(op, artefacto);
	}

	@Override
	protected void hazlo(Artefacto artefacto, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		final RepositorySystem system = ce.contextoAplicacion().getBean(RepositorySystem.class);
		final RepositorySystemSession session = ce.contextoAplicacion().getBean(RepositorySystemSession.class);
		final List<RemoteRepository> repositories = repositorios();
		
		DirectivaCoordenadasMaven dcm = P.of(artefacto).coordenadasMaven();

		escribeMensaje("Recolectar dependencias para el artefacto.");
		
		// pasamos a la recolección de dependencias
		Artifact artifact = new DefaultArtifact(
			dcm.getIdGrupo()
			+ ":" + dcm.getIdArtefacto()
			+ ":" + dcm.getEmpaquetado() 
			+ (dcm.getClasificador() != null && !dcm.getClasificador().isEmpty() ? ":" + dcm.getClasificador() : "" )
			+ ":" + dcm.getVersion());
		
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot( new Dependency(artifact, ""));
		collectRequest.setRepositories(repositories);
	
		CollectResult collectResult;
		try {
			collectResult = system.collectDependencies( session, collectRequest );
		} catch (DependencyCollectionException e) {
			throw new ErrorInesperadoOperacion(e);
		}
		
		escribeMensaje("Recolección de dependencias completada.");
		escribeMensaje("Procesar el grafo de dependencias.");
		
		// pasamos a recorrer las dependencias para construir el árbol asociado
		final BuscarDependenciasAdicionales _this = this;
		final Function<String, Boolean> f_escribe = new Function<String, Boolean>() {
			@Override
			public Boolean apply(String m) {
				_this.escribeMensaje(m);
				return true;
			}
		};
		collectResult.getRoot().accept(new DependencyGraphDumper(ce, artefacto.getTipoArtefacto(), op, f_escribe));
		
		// llegados a este punto, se supone que ya hemos encontrado todas las dependencias
		// por lo que nos queda decidir qué hacer con el estado
		if(!artefacto.getEstadoValidez()) {
			artefacto.setEstadoValidez(true);
			ce.artefactos().saveAndFlush(artefacto);
		}
	}
	
	/**
	 * Construye la lista de repositorios a los que recurrir
	 */
	private List<RemoteRepository> repositorios() {
		
		// creamos la lista
		final ArrayList<RemoteRepository> repositorios = new ArrayList<RemoteRepository>();
		
		// vamos a buscar los valores globales
		ParametroGlobal pg = ce.global().findOne("MAVEN.BUSCARDEPENDENCIAS.REPOSITORIOS");
		for(String lineaCruda: pg.getContenido().split("\n")) {
			String linea = lineaCruda.replace("\r", "");
			if(Strings.isNullOrEmpty(linea)) continue;
			String[] valores = linea.split(",");
			repositorios.add(
					new RemoteRepository.Builder(valores[0], valores[1], valores[2]).build()
			);
		}
		
		return repositorios; 
	}
	
	// -------------------------------------------------------------
	// Clase utilidad para recorrer todas las dependencias y volcarlas
	// correctamente en el repositorio.
	// -------------------------------------------------------------
	
	private static class DependencyGraphDumper implements DependencyVisitor
	{
		final private Function<String, Boolean> f_escribe;
		final private TipoArtefacto tipoArtefacto;
		final private IContextoEjecucion ce;
		final private ProcesoAsincrono operacion;

	    public DependencyGraphDumper(IContextoEjecucion ce, TipoArtefacto tipo, ProcesoAsincrono operacion, Function<String, Boolean> f_escribe)
	    {
	    	this.ce = ce;
	    	this.tipoArtefacto = tipo;
	    	this.operacion = operacion;
	    	this.f_escribe = f_escribe;
	    }

	    @Override
	    public boolean visitEnter( DependencyNode node )
	    {
	        // buscamos en el repositorio la entidad que representa el artacto actual
	        Artifact artifact = node.getArtifact(); 
	        f_escribe.apply(
	        		"Visitando el nodo: " 
			        + artifact.getGroupId() 
			        + ":" + artifact.getArtifactId() 
			        + ":" + artifact.getVersion() + ":" 
			        + artifact.getClassifier() 
			        + ":" + artifact.getExtension()
	        );
	        
	        // buscamos en la base de datos 
	        Artefacto maven = buscaYCreaSiNecesarioArtefactoMaven(artifact);
	        
	        // creamos la relación con todos los hijos
	        creaRelacionConHijos(maven, node.getChildren());
	        
	        return true;
	    }

	    @Override
	    public boolean visitLeave( DependencyNode node )
	    {
	        return true;
	    }


	    // ---------------------------------------------------
	    // Privados
	    // ---------------------------------------------------

	    private void creaRelacionConHijos(Artefacto maven, List<DependencyNode> hijos) {
	    	
	    	f_escribe.apply("Hay que relacionar el artefacto '" + maven.getNombre() + "' con " + hijos.size() + " dependencies requeridas.");
	    	
	    	int posicion = 1;
	    	for(DependencyNode hijo: hijos) {

		    	// por cada uno de los hijos vamos a buscar el artefacto de repositorio que lo representa
		        Artifact artifact = hijo.getArtifact(); 
		        Artefacto mavenHijo = buscaYCreaSiNecesarioArtefactoMaven(artifact);
		        

		        // si no se ha creado el hijo, entonces hay que revisar que esté ya en 
		        // la relación de usos.
		        final Artefacto fMvnHijo = mavenHijo;
		        boolean existeRelacionPreviamente = 
	        		Streams.of(
	        				ce.dependenciasArtefactos()
	        				.relacionesDependenciaDeUnArtefacto(maven.getId())
	        		)
	        		.exists(new Predicate<Dependencia>() {
						@Override public boolean test(Dependencia relacion) {
							return relacion.getRequerida().getId().equalsIgnoreCase(fMvnHijo.getId());
					} });
	        
		        // ¿creamos la relación?
		        if(!existeRelacionPreviamente) {
		        	
		        	// miramos qué dependencia hay registrada
		        	Dependency dependency = hijo.getDependency();
		        	String scope = dependency.getScope();
		        	EnumAlcanceDependencia alcance = EnumAlcanceDependencia.Provisto;  // por defecto es provisto
		        	if (scope.length() == 0 || scope.equalsIgnoreCase(JavaScopes.COMPILE)) {
		        		alcance = EnumAlcanceDependencia.CompilarYEmpaquetar;
		        		if (dependency.isOptional()) alcance = EnumAlcanceDependencia.CompilarSinEmpaquetar;
		        	} else if (scope.equalsIgnoreCase(JavaScopes.TEST)) {
		        		alcance = EnumAlcanceDependencia.Testing;
		        	}
		        	
		        	DependenciaConAlcance dependencia = new DependenciaConAlcance();
		        	dependencia.setPosicion(posicion);
		        	dependencia.setDependiente(maven);
		        	dependencia.setRequerida(mavenHijo);
		        	dependencia.setAlcanceElegido(alcance); 
		        	ce.dependenciasArtefactos().saveAndFlush(dependencia);
		        }

		        posicion++;
	    	}
	    	
	    } // end: creaRelacionConHijos
	    
	    
	    // ...................................................
	    // Repositorios
	    // ...................................................
	    
	    private Artefacto buscaYCreaSiNecesarioArtefactoMaven(Artifact artifact) {
	        
	    	String groupId = artifact.getGroupId();
	        String artifactId = artifact.getArtifactId();
	        String version = artifact.getVersion();
	        String tipo = artifact.getExtension();
	        
	        f_escribe.apply(
	        		"Buscar en base de datos: tipo=" + tipoArtefacto.getId()
	        		+ ", idGrupo=" + groupId
	        		+ ", idArtefacto=" + artifactId
	        		+ ", version=" + version
	        		+ ", empaquetado=" + tipo
	        );
	        
	        List<String> proto_mavens = ce.artefactos().artefactoDeTipoYConCoordenadaMaven(tipoArtefacto.getId(), groupId, artifactId, version, tipo);
	        Artefacto maven = proto_mavens.size() > 0 ? ce.artefactos().findOne(proto_mavens.get(0)) : null;
	        
	        // cuando se visita un nodo, siempre, siempre se da por hecho que debe existir 
	        // (de hecho, el primero que se visita es la raíz, a partir de la que se van creando el resto).
	        if(maven==null) {
	        	
	        	f_escribe.apply("No encontrado. Se registrará nuevo artefacto.");
	        	
	        	// el artefacto
	        	maven = new Artefacto();
	        	maven.setTipoArtefacto(tipoArtefacto);
	        	maven.setNombre(
	        			(
	        					artifactId
	        					+ "-"
	        					+ version
	        			)
	        			.toUpperCase()
	        	);
	        	maven.setEstadoValidez(true);
	        	maven.setEstadoActivacion(false);
	        	
	        	// las directivas de coordenadas maven 
	        	// y alcance
	        	DirectivaCoordenadasMaven dm = new DirectivaCoordenadasMaven();
	        	dm.setIdGrupo(groupId);
	        	dm.setIdArtefacto(artifactId);
	        	dm.setVersion(version);
	        	dm.setEmpaquetado(tipo.toLowerCase());

	        	DirectivaAlcances da = new DirectivaAlcances();
	        	da.setAlcances(EnumAlcanceDependencia.Provisto);
        	
	        	// el proceso de guardado es el tradicional,
	        	// primero el artefacto
	        	maven = ce.artefactos().save(maven);
	        	
	        	// luego las directivas
	        	dm = ce.directivas().save(dm);
	        	da = ce.directivas().save(da);
	        	
	        	// y finalmente las metemos en las del artefacto para poder guardarlo nuevamente
	        	maven.getDirectivasArtefacto().add(dm);
	        	maven.getDirectivasArtefacto().add(da);
	        	maven =  ce.artefactos().saveAndFlush(maven);
	        	
	        	// además, como éste artefacto se ha creado dentro del ámbito de una operación 
	        	// vinculamos el artefacto a dicha operación
	        	ce.relacionesOperaciones().saveAndFlush(new RelacionOperacionArtefacto(this.operacion, maven));
	        	
	        	f_escribe.apply("Registrado nuevo artefacto '" + tipoArtefacto.getNombre() + "' con ID:" + maven.getId() + " y nombre " + maven.getNombre());
	        }

	        return maven;
	    }

	}
	
}
