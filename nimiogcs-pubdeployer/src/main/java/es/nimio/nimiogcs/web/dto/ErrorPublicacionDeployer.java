package es.nimio.nimiogcs.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ErrorPublicacionDeployer {

	private String entorno;
	private String etiquetaPase;
	private String mensaje;

	// ---------
	
	@NotNull(message="Entorno no indicado")
	@Pattern(regexp="INTEGRACION|PREPRODUCCION|PRODUCCION", message="Entorno debe tener como valor 'INTEGRACION', 'PREPRODUCCION' o 'PRODUCCION'")
	public String getEntorno() {
		return entorno;
	}

	public void setEntorno(String entorno) {
		this.entorno = entorno;
	}
	
	@NotNull(message="No hay identificador de etiqueta de pase para la operación Deployer asociado")
	@Size(min=10, message="El identificador de la etiqueta de pase no tiene el tamaño mínimo requerido")
	public String getEtiquetaPase() {
		return etiquetaPase;
	}

	public void setEtiquetaPase(String idOperacionDeployer) {
		this.etiquetaPase = idOperacionDeployer;
	}

	public String getMensaje() { return this.mensaje; }
	public void setMensaje(String mensaje) { this.mensaje = mensaje; }

}
