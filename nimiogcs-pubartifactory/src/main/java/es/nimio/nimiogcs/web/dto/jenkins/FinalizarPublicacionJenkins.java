package es.nimio.nimiogcs.web.dto.jenkins;

import javax.validation.constraints.NotNull;

import es.nimio.nimiogcs.subtareas.apiweb.PublicacionFinalizadaJenkins;

public class FinalizarPublicacionJenkins 
	implements PublicacionFinalizadaJenkins.IFinalizarPublicacion {

	private String tiqueOperacion;

	// ---------

	@NotNull(message="Debe indicarse el ticket de la operación")
	public String getTiqueOperacion() {
		return tiqueOperacion;
	}

	public void setTiqueOperacion(String tiqueOperacion) {
		this.tiqueOperacion = tiqueOperacion;
	}
	
}
