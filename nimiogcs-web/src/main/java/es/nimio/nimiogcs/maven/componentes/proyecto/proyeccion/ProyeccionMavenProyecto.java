package es.nimio.nimiogcs.maven.componentes.proyecto.proyeccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionProyecto;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.maven.KMaven;
import es.nimio.nimiogcs.maven.subtareas.proyecto.CrearEstructuraBaseProyeccionMavenProyecto;
import es.nimio.nimiogcs.maven.subtareas.proyecto.EliminarEstructuraBaseProyeccion;
import es.nimio.nimiogcs.operaciones.OperacionBase;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Component
public class ProyeccionMavenProyecto implements ITareaProyeccionProyecto {

	private IContextoEjecucion ce;
	
	@Autowired
	public ProyeccionMavenProyecto(IContextoEjecucion ce) {
		this.ce = ce;
	}

	@Override
	public void ejecutarTareaProyeccion(
			String nombre, 
			Proyecto proyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion {
		
		// tras comprobar que se trate de una proyección de maven...
		if(KMaven.PROYECCION_NOMBRE.equalsIgnoreCase(nombre))
			ejecutarTarea(proyecto, invocante, cuandoExito, cuandoFallo);
	}
	
	@Override
	public boolean confirmaProcede(ElementoBaseProyecto elementoProyecto) {
		return tieneProyeccionMaven(elementoProyecto);
	}
	
	/**
	 * Solicita la reconstrucción de la estructura de proyecto, si es necesario
	 * @throws Throwable 
	 */
	public void ejecutarTareaReconstruccionProyeccion(
			ElementoBaseProyecto elementoProyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion {
		
		
		// se comprueba si el proyecto cuenta con una proyeccion de tipo Maven
		if(tieneProyeccionMaven(elementoProyecto)) 
			ejecutarTarea(elementoProyecto, invocante, cuandoExito, cuandoFallo);
	}
	
	@Override
	public void ejecutarTareaEliminarProyeccion(ElementoBaseProyecto elementoProyecto, OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito, Function<ElementoBaseProyecto, ?> cuandoFallo)
					throws ErrorInesperadoOperacion {

		// se comprueba si el proyecto cuenta con una proyeccion de tipo Maven
		if(tieneProyeccionMaven(elementoProyecto)) 
			ejecutarTareaBorrado(elementoProyecto, invocante, cuandoExito, cuandoFallo);
	}
	
	// -----
	
	private void ejecutarTarea(
			ElementoBaseProyecto elementoProyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion {
		
		// ... creamos la tarea...
		ProcesoAsincronoModulo<ElementoBaseProyecto> t = new CrearEstructuraBaseProyeccionMavenProyecto(this.ce);
		
		// ... asignamos el usuario de la tarea invocante, si hubiera...
		if(invocante!=null) {
			if(invocante instanceof ProcesoAsincronoModulo<?>)
				t.setUsuario(((ProcesoAsincronoModulo<?>)invocante).getUsuario());
		
			t.escribeMensajeExterior("Siendo invocada como parte de la ejecución de otra operación.");
		}
	
		//... y terminamos ejecutando
		t.ejecutarCon(elementoProyecto, cuandoExito, cuandoFallo);
	}
	
	private void ejecutarTareaBorrado(
			ElementoBaseProyecto elementoProyecto, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito,
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion {
		
		// ... creamos la tarea...
		ProcesoAsincronoModulo<ElementoBaseProyecto> t = new EliminarEstructuraBaseProyeccion(this.ce);
		
		// ... asignamos el usuario de la tarea invocante, si hubiera...
		if(invocante!=null) {
			if(invocante instanceof ProcesoAsincronoModulo<?>)
				t.setUsuario(((ProcesoAsincronoModulo<?>)invocante).getUsuario());
		
			t.escribeMensajeExterior("Siendo invocada como parte de la ejecución de otra operación.");
		}
	
		//... y terminamos ejecutando
		t.ejecutarCon(elementoProyecto, cuandoExito, cuandoFallo);
	}
	
	private boolean tieneProyeccionMaven(ElementoBaseProyecto elementoProyecto) {
		for(UsoYProyeccionProyecto uso: elementoProyecto.getUsosYProyecciones()) {
			if(uso instanceof ProyeccionMavenDeProyecto) return true;
		}
		return false;
	}
}
