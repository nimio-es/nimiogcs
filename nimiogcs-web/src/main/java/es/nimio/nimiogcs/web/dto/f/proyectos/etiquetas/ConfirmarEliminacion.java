package es.nimio.nimiogcs.web.dto.f.proyectos.etiquetas;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

public class ConfirmarEliminacion {
	public ConfirmarEliminacion() {}

	public ConfirmarEliminacion(Proyecto proyecto, EtiquetaProyecto etiqueta)  {
		idProyecto = proyecto.getId();
		idEtiqueta = etiqueta.getId();
	}
	// ----------------------------------
		// Estado
		// ----------------------------------
		
		@Privado
		private String idProyecto;
		
		@Privado
		private String idEtiqueta;

		// ----------------------------------
		// Leer y escribir estado
		// ----------------------------------
		
		public String getIdProyecto() { return idProyecto; }
		public void setIdProyecto(String id) { idProyecto = id; }
		
		public String getIdEtiqueta() { return idEtiqueta; }
		public void setIdEtiqueta(String id) { idEtiqueta = id; }

}
