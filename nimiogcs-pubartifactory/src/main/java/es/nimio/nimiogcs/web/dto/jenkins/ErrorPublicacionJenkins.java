package es.nimio.nimiogcs.web.dto.jenkins;

public class ErrorPublicacionJenkins {

	private String tiqueOperacion;
	private String mensaje;

	// ---------

	
	public String getTiqueOperacion() {
		return tiqueOperacion;
	}
	public void setTiqueOperacion(String tiqueOperacion) {
		this.tiqueOperacion = tiqueOperacion;
	}
	
	public String getMensaje() { return this.mensaje; }
	public void setMensaje(String mensaje) { this.mensaje = mensaje; }

}
