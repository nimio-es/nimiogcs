package es.nimio.nimiogcs.operaciones.artefactos.repo;

import java.util.ArrayList;
import java.util.List;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstructuraCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;

/**
 * Subproceso asíncrono que se encarga de crear la estructura 
 * de código del repositorio cuando un artefacto tiene 
 * definida una directiva de repositorio de código.
 */
public class CrearEstructuraRepositorioCodigo 
	extends ProcesoAsincronoModulo<Artefacto> {

	public CrearEstructuraRepositorioCodigo(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	@Override
	protected String nombreUnicoOperacion(Artefacto artefacto, ProcesoAsincrono op) {
		return "CREAR ESTRUCTURA REPOSITORIO ARTEFACTO '" + artefacto.getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(Artefacto artefacto, ProcesoAsincrono op) {
		registraRelacionConOperacion(op, artefacto);
	}

	@Override
	protected void hazlo(Artefacto artefacto, ProcesoAsincrono op) {
		
		// cogemos la directiva que representa la estructura de código
		DirectivaRepositorioCodigo lente = P.of(artefacto).repositorioCodigo(); 
		
		// cogemos, tambiém, la directiva de taxonomía
		DirectivaTaxonomia taxonomia = P.of(artefacto).taxonomia();
			
		// el repositorio asociado lo usaremos varias veces
		RepositorioCodigo repositorioCodigo = lente.getRepositorio(); 
			
		// el acceso al repositorio subversion
		Subversion subversion = new Subversion(repositorioCodigo);
			
		// las rutas internas para rama estable y etiquetas
		// las calculamos para dar de alta 
		String parcialArtefacto = 
				repositorioCodigo.getSubrutaEstables() 
				+ (taxonomia != null ? "/" + taxonomia.getTaxonomia() : "")
				+ "/" + artefacto.getNombre().toLowerCase();
		String estable = parcialArtefacto + "/" + repositorioCodigo.getCarpetaEstables();
		String marcas = parcialArtefacto + "/" + repositorioCodigo.getCarpetaEtiquetas();
			
		// creamos todas las carpetas necesarias
		List<String> carpetas = new ArrayList<String>();
		
		// si el artefacto cuenta con una directiva de estructura de código
		// usaremos dicha estructura para crear las carpetas
		// En caso contrario usaremos las comunes de todos los proyectos
		DirectivaEstructuraCodigo dec = PT.of(artefacto.getTipoArtefacto()).directiva(DirectivaEstructuraCodigo.class);
		if(dec==null) {
			carpetas.add(estable + "/src/main/java");
			carpetas.add(estable + "/src/main/resources");
			carpetas.add(estable + "/src/test/java");
			carpetas.add(estable + "/src/test/resources");
		} else {
			for(String carpeta: dec.getCarpetas().replace("\r", "").split("\n")) {
				carpetas.add(estable + "/" + carpeta);
			}
		}
		carpetas.add(marcas);
		
			
		subversion.crearCarpetas(carpetas.toArray(new String[carpetas.size()]));
			
		// una vez creadas las estructuras, vamos a rellenar lo que falta de la directiva
		lente.setParcialEstables(estable);
		lente.setParcialEtiquetas(marcas);
		ce.directivas().saveAndFlush(lente);
		
		// después de crear las estructuras de carpetas, si el artefacto es, además
		// de rama única, entonces vamos a crear la única rama de desarrollo que habrá:
		// la rama troncal
		if(P.of(artefacto).evolucion().esUnica()) {			
		
			String urlRepoOrigen = lente.getRepositorio().getUriRaizRepositorio() + "/" + lente.getParcialEstables();
			String urlRepoFinal = new StringBuilder()
					.append(lente.getRepositorio().getUriRaizRepositorio())
					.append('/')
					.append(repositorioCodigo.getSubrutaDesarrollo())
					.append('/')
					.append(repositorioCodigo.getCarpetaRamas())
					.append('/')
					.append(taxonomia != null ? taxonomia.getTaxonomia() : "")
					.append('/')
					.append(artefacto.getNombre().toLowerCase())
					.append('/')
					.append("troncal")
					.toString();
				
			// lanzamos la tarea de subversion
			subversion.copiarCarpetas(
					urlRepoOrigen,
					urlRepoFinal, 
					"Creando la rama única para el desarrollo de '" + artefacto.getNombre() + "'");
			
			// hay que añadir al artefacto la directiva de la rama de código
			DirectivaRamaCodigo drc = new DirectivaRamaCodigo();
			drc.setRamaCodigo(urlRepoFinal);
			ce.directivas().save(drc);
			artefacto.getDirectivasArtefacto().add(drc);
			ce.artefactos().saveAndFlush(artefacto);
		}
	}
}
