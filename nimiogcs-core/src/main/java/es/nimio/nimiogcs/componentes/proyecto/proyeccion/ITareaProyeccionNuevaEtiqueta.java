package es.nimio.nimiogcs.componentes.proyecto.proyeccion;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.operaciones.OperacionBase;

public interface ITareaProyeccionNuevaEtiqueta {

	/**
	 * Lanza la ejeecución de la tarea de proyección concreta para un proyecto
	 * determinado si le corresponde.
	 */
	void ejecutarTareaProyeccion(
			EtiquetaProyecto etiqueta, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion; 

}
