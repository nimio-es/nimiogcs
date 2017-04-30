package es.nimio.nimiogcs.operaciones.proyecto;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;

public class RelacionarProyectoYArtefacto 
	extends ProcesoAsincronoModulo<RelacionarProyectoYArtefacto.Peticion> {
	
	/**
	 * Estructura con la petición
	 */
	public final static class Peticion extends Tuples.T2<Proyecto, Artefacto> {

		private static final long serialVersionUID = 7659237739217084925L;

		public Peticion(Proyecto t, Artefacto u) {
			super(t, u);
		}

		public Proyecto proyecto() { return _1; }
		public Artefacto artefacto() { return _2; }
	}


	// ------------
	
	public RelacionarProyectoYArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	@Override
	protected String nombreUnicoOperacion(Peticion datos, ProcesoAsincrono op) {
		return "ASOCIAR EL PROYECTO '" 
					+ datos.proyecto().getNombre() 
					+ "' CON EL ARTEFACTO '" 
					+ datos.artefacto().getNombre() 
					+ "'";
	}

	@Override
	protected void relacionarOperacionConEntidades(Peticion datos, ProcesoAsincrono op) {
		registraRelacionConOperacion(op, datos.proyecto());
		registraRelacionConOperacion(op, datos.artefacto());
	}


	@Override
	protected void hazlo(Peticion datos, ProcesoAsincrono op) {

		// comprobamos que no haya ya una relación entre el proyecto y el artefacto 
		// o entre el proyecto y una evolución del artefacto
		noDebeExistirRelacionPrevia(datos);
		
		// registramos la evolución del artefacto, si fuera necesario
		final Artefacto artefacto = registrarArtefactoEnProyecto(datos.proyecto(), datos.artefacto(), op);
		
		// creamos la rama de trabajo, si necesario
		crearRamaTrabajo(datos.proyecto(), artefacto);
	}
	
	/**
	 * Confirma que no exista una relación previa entre el artefacto y el proyecto
	 * @param datos
	 */
	private void noDebeExistirRelacionPrevia(Peticion datos) {
		
		Proyecto proyecto = datos.proyecto();
		Artefacto artefacto = datos.artefacto();
		
		for(RelacionElementoProyectoArtefacto rpa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto))) {
			if(rpa.getArtefacto().getId().equalsIgnoreCase(artefacto.getId())) 
				throw new ErrorExisteRelacionArtefactoProyecto();
			
			if(rpa.getArtefacto() instanceof EvolucionArtefacto) 
				if(((EvolucionArtefacto)rpa.getArtefacto()).getArtefactoAfectado().getId().equalsIgnoreCase(artefacto.getId()))
					throw new ErrorExisteEvolutivoArtefactoRelacionadoProyecto();
		}
	}
	
	/**
	 * Registramos el artefacto en el repositorio de datos 
	 * y creamos la estructura de carpetas en el repositorio de código 
	 */
	private Artefacto registrarArtefactoEnProyecto(
			Proyecto p,
			Artefacto a, 
			ProcesoAsincrono op) {
		
		// lo primero es asociar el artefacto con la operación
		ce.relacionesOperaciones().saveAndFlush(
				new RelacionOperacionArtefacto(op, a)
		);
		
		if(a instanceof EvolucionArtefacto) {
			escribeMensaje("El artefacto con el que se quiere relacionar es una evolución");
			escribeMensaje("Sobreentendemos que viene compartido desde otro proyecto");
			return a;
		}
		
		// confirmamos si se trata de un artefacto con evolución por proyectos 
		boolean porProyectos = P.of(a).evolucion() != null ? P.of(a).evolucion().esPorProyecto() : false;

		if(porProyectos) escribeMensaje("Se trata de un artefacto con estrategia basada en proyectos");
		else  escribeMensaje("Se trata de un artefacto con estrategia basada en rama única");
				
		// empecemos decidiendo si tenemos que trabajar directamente con la entidad
		// o usar una de evolución
		Artefacto asociable = a;
		if(porProyectos) {
			EvolucionArtefacto e = new EvolucionArtefacto();
			e.setTipoArtefacto(ce.tipos().findOne("EVOLUTIVO"));
			e.setArtefactoAfectado(a);
			e.setNombre("EVOLUCIÓN DE '" + a.getNombre() + "'");
			ce.artefactos().saveAndFlush(e);
			
			// lista de las directivas que se deben copiar
			ParametroGlobal pg = ce.global().findOne("ARTEFACTOS.DIRECTIVAS.EVOLUCIONABLES");
			if(pg!=null) {
				String[] de = pg.getContenido().trim().replace("\n", "").replace("\r", "").split(",");
				for(String nd: de) {
					DirectivaBase dn = P.of(a).buscarDirectiva(nd);
					if(dn!=null) {
						DirectivaBase da = dn.replicar();
						DirectivaBase ps = ce.directivas().save(da);
						e.getDirectivasArtefacto().add(ps);
					}
				}
				ce.artefactos().saveAndFlush(e);
			}
			
			// también lo relacionamos con la operación
			ce.relacionesOperaciones().saveAndFlush(
					new RelacionOperacionArtefacto(op, e)
			);
			
			// además, hay que replicar todas las relaciones que tiene el artefacto de origen
			for(Dependencia rd: ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(a.getId())) {
				
				// duplicamos
				Dependencia rdDup = rd.reproducir();
				rdDup.setDependiente(e);
				ce.dependenciasArtefactos().saveAndFlush(rdDup);
			}
			
			// y lo volvemos a cargar para que esté completo (cargue el artefacto asociado)
			asociable = ce.artefactos().findOne(e.getId());
		}
		
		// en este punto, sea o no un artefacto evolutivo, ya tenemos la relación proyecto/artefacto
		ce.relacionesProyectos().saveAndFlush(
				new RelacionElementoProyectoArtefacto(p, asociable)
		);
		
		return asociable;
	}
	
	private void crearRamaTrabajo(Proyecto p, Artefacto a) {
		
		// casos en que excluimos esta parte
		// 1. si no se ha creado un artefacto evolutivo es que la estrategia es de rama única
		if(!(a instanceof EvolucionArtefacto)) return;
		
		EvolucionArtefacto ea = (EvolucionArtefacto)a;

		// 2. si el artefacto afectado no tiene directiva de código es que no hay código que ramificar
		if(P.of(ea.getArtefactoAfectado()).repositorioCodigo()==null) return;

		// vamos a necesitar la taxonomía
		final String taxonomia = P.of(ea.getArtefactoAfectado()).taxonomia().getTaxonomia();
		
		// a partir de aquí ya podemos proceder a crear la correspondiente rama.
		// partiendo de la lente que nos permitirá acceder a los datos de la directiva
		final DirectivaRepositorioCodigo codigo = P.of(ea.getArtefactoAfectado()).repositorioCodigo();
			
		// cargamos el repositorio
		final RepositorioCodigo repo = codigo.getRepositorio();
			
		// sacamos la ruta donde hay que meter las etiquetas y de dónde vamos a copiar
		final String rutaTroncal = repo.getUriRaizRepositorio() + "/" + codigo.getParcialEstables();
		final String rutaRamas = 
				repo.getUriRaizRepositorio()
				+ "/"
				+ repo.getSubrutaDesarrollo()
				+ "/" 
				+ repo.getCarpetaRamas()
				+ "/"
				+ taxonomia
				+ "/"
				+ ea.getArtefactoAfectado().getNombre().toLowerCase();
			
		// rama final de trabajo
		final String ramaFinalTrabajo = 
				rutaRamas + "/" + p.getNombre().toLowerCase(); 
			
		// se crea la rama en el repositorio 
		new Subversion(repo)
			.copiarCarpetas(
					rutaTroncal, 
					ramaFinalTrabajo,
					"Creando la rama de trabajo de '" + a.getNombre() + "' para el proyecto '" + p.getNombre() + "'");
		
		// y añadimos la nueva directiva al artefacto de evolución
		DirectivaRamaCodigo da = new DirectivaRamaCodigo();
		da.setRamaCodigo(ramaFinalTrabajo);
		DirectivaBase ps = ce.directivas().saveAndFlush(da);
		ea.getDirectivasArtefacto().add(ps);
		ce.artefactos().saveAndFlush(ea);
	}
	
	// ----
	
	static final class ErrorExisteRelacionArtefactoProyecto extends RuntimeException {

		private static final long serialVersionUID = -1646549417143209489L;

		public ErrorExisteRelacionArtefactoProyecto() {
			super("Ya existe una relación entre el proyecto y el artefacto");
		}
	}


	static final class ErrorExisteEvolutivoArtefactoRelacionadoProyecto extends RuntimeException {

		private static final long serialVersionUID = -7505061719725552392L;

		public ErrorExisteEvolutivoArtefactoRelacionadoProyecto() {
			super("Existe un evolutivo para el artefacto relacionado con el proyecto");
		}
	}
}
