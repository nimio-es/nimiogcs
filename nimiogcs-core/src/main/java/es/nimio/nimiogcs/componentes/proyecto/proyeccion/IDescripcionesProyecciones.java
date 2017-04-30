package es.nimio.nimiogcs.componentes.proyecto.proyeccion;

import java.util.List;

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;

/**
 * Identifica qué proyecciones pueden ser utilizadas por un proyecto
 */
public interface IDescripcionesProyecciones {


	/**
	 * Añade nombres y descripciones a la lista de descripciones que se le pasa
	 */
	void descripciones(List<NombreDescripcion> listaDescripciones);
}
