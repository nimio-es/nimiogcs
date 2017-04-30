package es.nimio.nimiogcs.maven.componentes.proyecto.proyeccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionNuevaEtiqueta;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.maven.subtareas.proyecto.CrearEstructuraBaseProyeccionMavenProyecto;
import es.nimio.nimiogcs.operaciones.OperacionBase;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Component
public class ProyeccionMavenEtiqueta implements ITareaProyeccionNuevaEtiqueta {

	private IContextoEjecucion ce;
	
	@Autowired
	public ProyeccionMavenEtiqueta(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	@Override
	public void ejecutarTareaProyeccion(
			EtiquetaProyecto etiqueta, 
			OperacionBase<?, ?, ?> invocante,
			Function<ElementoBaseProyecto, ?> cuandoExito, 
			Function<ElementoBaseProyecto, ?> cuandoFallo) throws ErrorInesperadoOperacion {
		
		// hay que comprobar que la etiqueta pertenece a un proyecto 
		// con un uso Maven
		boolean conUsoMaven = false;
		for(UsoYProyeccionProyecto uso: etiqueta.getProyecto().getUsosYProyecciones())
			if(uso instanceof ProyeccionMavenDeProyecto) { 
				conUsoMaven = true;
				break;
			}
		
		if(conUsoMaven) ejecutarTarea(etiqueta, invocante, cuandoExito, cuandoFallo);
	}
	
	private void ejecutarTarea(
			EtiquetaProyecto etiqueta, 
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
		t.ejecutarCon(etiqueta, cuandoExito, cuandoFallo);
	}
}
