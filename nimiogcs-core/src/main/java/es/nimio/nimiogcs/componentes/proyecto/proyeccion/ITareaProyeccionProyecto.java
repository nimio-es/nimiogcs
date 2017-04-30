package es.nimio.nimiogcs.componentes.proyecto.proyeccion;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.operaciones.OperacionBase;

/**
 * Interfaz que deben implementar los componentes capaces de proyectar una nueva etiqueta 
 */
public interface ITareaProyeccionProyecto {

	/**
	 * Lanza la ejeecución de la tarea de proyección concreta para un proyecto
	 * determinado si le corresponde.
	 */
	void ejecutarTareaProyeccion(
			String nombre, 
			Proyecto proyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion; 

	
	/**
	 * Indica si para un elemento de proyecto, procede ejecutar alguna de las 
	 * operaciones de mantenimiento definidas.
	 * @param elementoProyecto
	 * @return
	 */
	boolean confirmaProcede(ElementoBaseProyecto elementoProyecto);
	
	/**
	 * Solicita la reconstrucción de la estructura de proyecto, si es necesario
	 * @throws Throwable 
	 */
	void ejecutarTareaReconstruccionProyeccion(
			ElementoBaseProyecto elementoProyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion;
	
	
	/**
	 * Solicita la eliminación de una proyección
	 * @throws Throwable
	 */
	void ejecutarTareaEliminarProyeccion(
			ElementoBaseProyecto elementoProyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion;
}
