package es.nimio.nimiogcs.componentes.proyecto.web;

import java.util.Collection;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;

/**
 * Determina qué otras operaciones, además de las básicas, pueden realizarse sobre un proyecto determinado
 */
public interface IOperacionesPosiblesSobreProyecto {

	Collection<DefinicionOperacionPosible> defineAcciones(Proyecto proyecto);
	
	Collection<DefinicionOperacionPosible> defineAcciones(EtiquetaProyecto etiqueta);
}
