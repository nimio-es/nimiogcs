package es.nimio.nimiogcs.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.subtareas.apiweb.NotificarResultadoAnalisisCalidad;

public final class NotificarResultadoCalidad 
	implements NotificarResultadoAnalisisCalidad.INotificarResultadoCalidad {

	private String etiquetaPase;
	private boolean superaCalidad;
	
	@Override
	@NotNull(message="No hay identificador de la etiqueta de pase para la operación Deployer asociado")
	@Size(min=10, message="El identificador de la etiqueta de pase Deployer no tiene el tamaño mínimo requerido")
	public String getEtiquetaPase() {
		return etiquetaPase;
	}

	public void setEtiquetaPase(String etiqueta) {
		etiquetaPase = etiqueta;
	}
	
	@Override
	@NotNull(message="No se ha indicado el resultado del análisis de calidad")
	public boolean getSuperaCalidad() {
		return superaCalidad;
	}
	
	public void setSuperaCalidad(Boolean calidad) {
		superaCalidad = calidad;
	}

}
