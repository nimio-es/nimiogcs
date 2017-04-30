package es.nimio.nimiogcs.componentes.proyecto.proyeccion;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.operaciones.OperacionBase;

public interface ITareaPostProyeccionProyecto {

	/**
	 * Realiza tareas de post proyección tras crear la estructura de los proyectos.
	 * Pensado para incorporar los mecenismos de creación de un entorno de control
	 * de calidad, de publicación continua, etc.
	 */
	void ejecutarTareasPostProyeccion(String nombre, Proyecto proyecto, OperacionBase<?, ?, ?> invocante) throws ErrorInesperadoOperacion ;

}
