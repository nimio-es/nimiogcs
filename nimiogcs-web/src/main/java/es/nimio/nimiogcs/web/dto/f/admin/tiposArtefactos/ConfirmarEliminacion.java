package es.nimio.nimiogcs.web.dto.f.admin.tiposArtefactos;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

public class ConfirmarEliminacion {

	public ConfirmarEliminacion() {}

	public ConfirmarEliminacion(TipoArtefacto entidad) {
		idTipo = entidad.getId();
	}
	// ----------------------------------
		// Estado
		// ----------------------------------
		
		@Privado
		private String idTipo;
		// ----------------------------------
		// Leer y escribir estado
		// ----------------------------------
		
		public String getIdTipo() { return idTipo; }
		public void setIdTipo(String id) { idTipo = id; }	
}
