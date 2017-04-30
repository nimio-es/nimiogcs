package es.nimio.nimiogcs.componentes.proyecto.etiqueta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.KSonarEtiqueta;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.proyecto.etiqueta.IPosCreacionEtiqueta;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;
import es.nimio.nimiogcs.subtareas.proyecto.etiqueta.QASonarEtiquetaUsandoJenkins;

@Component
public class ControlCalidadSonarEtiqueta implements IPosCreacionEtiqueta {

	private static final Logger logger = LoggerFactory.getLogger(ControlCalidadSonarEtiqueta.class);
	
	private final IContextoEjecucionBase ce;
	
	@Autowired
	public ControlCalidadSonarEtiqueta(final IContextoEjecucionBase ce) {
		this.ce = ce;
	}

	// ---
	
	@Override
	public boolean paraEjecutar(EtiquetaProyecto protoEtiqueta) {

		logger.debug(
				"Comprobar ejecutabilidad de análisis de calidad para la etiqueta '" + protoEtiqueta.getNombre() + "'");
		
		// confirmamos que esté activa
		if(!ce.repos().global().buscar(KSonarEtiqueta.PG_ACTIVO).comoValorIgualA("1")) 
			return false;
		
		// dado que es un proceso que se puede ejecutar asíncronamente sobre una
		// versión anterior de la etiqueta, nos molestaremos en volver a cargarla
		logger.debug("Recargar datos de la etiqueta '" + protoEtiqueta.getNombre() + "'");
		EtiquetaProyecto etiqueta = ce.repos().elementosProyectos().etiquetas().buscar(protoEtiqueta.getId());
		
		// Si hay proyección MAVEN, podemos hacer uso del análisis de calidad
		// empleando el circuito SONAR (vía JENKINS)
		logger.debug("Comprobar si la etiqueta '" + etiqueta.getNombre() + "' tiene una proyección Maven asociada.");
		final boolean hayProyeccionMaven = 
				Streams.of(etiqueta.getUsosYProyecciones())
				.exists(
						new Predicate<UsoYProyeccionProyecto>() {

							@Override
							public boolean test(UsoYProyeccionProyecto usoProyeccion) {
								return usoProyeccion instanceof ProyeccionMavenDeProyecto;
							}
							
						}
				);
		
		logger.debug(
				(hayProyeccionMaven ? "Hay" : "No hay") + " proyección manen asociado a la etiqueta '" + etiqueta.getNombre() + "'");
		
		return hayProyeccionMaven;
	}

	@Override
	public ProcesoAsincronoBase<IContextoEjecucionBase, EtiquetaProyecto> subtarea() {
		
		logger.debug("Crear un proceso de ejecución de análisis de calidad.");
		
		return new QASonarEtiquetaUsandoJenkins(this.ce);
	}
	
}
