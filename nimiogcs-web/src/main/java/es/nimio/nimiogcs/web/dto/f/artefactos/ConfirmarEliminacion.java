package es.nimio.nimiogcs.web.dto.f.artefactos;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

public class ConfirmarEliminacion {

	public ConfirmarEliminacion() {}

	public ConfirmarEliminacion(Artefacto entidad) {
		idArtefacto = entidad.getId();
	}

	
	// ----------------------------------
	// Estado
	// ----------------------------------
	
	@Privado
	private String idArtefacto;

	// ----------------------------------
	// Leer y escribir estado
	// ----------------------------------
	
	public String getIdArtefacto() { return idArtefacto; }
	public void setIdArtefacto(String id) { idArtefacto = id; }

}
