package es.nimio.nimiogcs.componentes.proyecto.etiqueta;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;

public interface IPosCreacionEtiqueta {

	/**
	 * Pregunta si hay algo que ejecutar
	 */
	boolean paraEjecutar(final EtiquetaProyecto etiqueta);
	
	/**
	 * Devuelve una tarea capaz de ejecutar el proceso
	 */
	ProcesoAsincronoBase<IContextoEjecucionBase, EtiquetaProyecto> subtarea();
}
