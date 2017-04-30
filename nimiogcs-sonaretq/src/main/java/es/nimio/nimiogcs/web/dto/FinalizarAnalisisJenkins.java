package es.nimio.nimiogcs.web.dto;

import javax.validation.constraints.NotNull;

import es.nimio.nimiogcs.subtareas.apiweb.AnalisisFinalizadoJenkins;

public class FinalizarAnalisisJenkins 
	implements AnalisisFinalizadoJenkins.IFinalizarAnalisis {

	private String tiqueOperacion;
	private Integer violaciones;

	// ---------

	@NotNull(message="Debe indicarse el ticket de la operación")
	public String getTiqueOperacion() {
		return tiqueOperacion;
	}

	public void setTiqueOperacion(String tiqueOperacion) {
		this.tiqueOperacion = tiqueOperacion;
	}

	@NotNull(message="Debe indicarse el número de violaciones bloqueantes detectadas")
	public Integer getNumeroViolacionesBloqueantes() {
		return violaciones;
	}
	
	public void setNumeroViolacionesBloqueantes(Integer violaciones) {
		this.violaciones = violaciones;
	}
	
}
