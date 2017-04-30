package es.nimio.nimiogcs.maven.componentes.proyecto.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.proyecto.web.DefinicionOperacionPosible;
import es.nimio.nimiogcs.componentes.proyecto.web.IOperacionesPosiblesSobreProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;

@Component
public class OperacionesMavenPosiblesSobreProyecto implements IOperacionesPosiblesSobreProyecto{

	@Override
	public Collection<DefinicionOperacionPosible> defineAcciones(Proyecto proyecto) {
		
		final ArrayList<DefinicionOperacionPosible> operaciones = new ArrayList<DefinicionOperacionPosible>();
		
		// si dispone de una proyección de tipo maven, podemos regenerar
		if(tieneProyeccionMaven(proyecto)) {
			operaciones.add(
					new DefinicionOperacionPosible(C.OPERACION_RECONSTRUIR_MAVEN, "Reconstruir estructura Maven", "proyectos/op/maven/recrear")
			);
			operaciones.add(
					new DefinicionOperacionPosible(C.OPERACION_ELIMINAR_PROYECCION_MAVEN, "Eliminar proyección Maven", "proyectos/op/maven/eliminar")
			);
		} else {
			operaciones.add(
					new DefinicionOperacionPosible(C.OPERACION_CREAR_PROYECCION_MAVEN, "Asociar proyección Maven", "proyectos/op/maven/activar")
			);
		}
		
		return operaciones;
	}
	
	@Override
	public Collection<DefinicionOperacionPosible> defineAcciones(EtiquetaProyecto etiqueta) {

		final ArrayList<DefinicionOperacionPosible> operaciones = new ArrayList<DefinicionOperacionPosible>();
		
		// si dispone de una proyección de tipo maven, podemos regenerar
		if(tieneProyeccionMaven(etiqueta)) {
			operaciones.add(
					new DefinicionOperacionPosible(C.OPERACION_RECONSTRUIR_MAVEN, "Reconstruir estructura Maven", "proyectos/op/maven/recrear")
			);
		}
		
		return operaciones;
	}
	
	private boolean tieneProyeccionMaven(ElementoBaseProyecto proyecto) {
		for(UsoYProyeccionProyecto uso: proyecto.getUsosYProyecciones())
			if(uso instanceof ProyeccionMavenDeProyecto) return true;
		
		return false;
	}
}
