package es.nimio.nimiogcs.web.dto.f;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class EtiquetarProyectoForm {

	private String idProyecto;
	private String nombreEtiqueta;
	private Boolean publicarEnIntegracion = false;
	
	public EtiquetarProyectoForm() {}
	
	public EtiquetarProyectoForm(String idProyecto, String nombreEtiqueta) {
		this();
		this.idProyecto = idProyecto;
		this.nombreEtiqueta = nombreEtiqueta;
	}
	
	public String getIdProyecto() {
		return idProyecto;
	}
	public void setIdProyecto(String idProyecto) {
		this.idProyecto = idProyecto;
	}

	@NotNull
	@Size(min=3, max=20)
	public String getNombreEtiqueta() {
		return nombreEtiqueta;
	}
	
	public void setNombreEtiqueta(String nombreEtiqueta) {
		this.nombreEtiqueta = nombreEtiqueta;
	}
	
	public Boolean getPublicarEnIntegracion() { return this.publicarEnIntegracion; }
	
	public void setPublicarEnIntegracion(boolean valor) { this.publicarEnIntegracion = valor; }
}
