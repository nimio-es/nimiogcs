package es.nimio.nimiogcs.web.dto.f.proyectos;

import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

public class ConfirmarEliminacion {

	public ConfirmarEliminacion() {}

	public ConfirmarEliminacion(Proyecto proyecto) {
		idProyecto = proyecto.getId();
	}
	// ----------------------------------
		// Estado
		// ----------------------------------
		
		@Privado
		private String idProyecto;

		// ----------------------------------
		// Leer y escribir estado
		// ----------------------------------
		
		public String getIdProyecto() { return idProyecto; }
		public void setIdProyecto(String id) { idProyecto = id; }

}
