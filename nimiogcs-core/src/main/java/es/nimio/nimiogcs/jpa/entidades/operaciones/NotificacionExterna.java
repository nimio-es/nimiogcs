package es.nimio.nimiogcs.jpa.entidades.operaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Registro del resultado de una notificación externa
 * @author se03325
 *
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("NOTIFICACION_EXTERNA")
public class NotificacionExterna extends Operacion {

	// ---------------------------------------
	// Construcción
	// ---------------------------------------

	public NotificacionExterna() {
		super();
	}
	
	public NotificacionExterna(String servicio) {
		this();
		servicioNotifica = servicio;
	}
	

	// ---------------------------------------
	// Estado
	// ---------------------------------------

	@Column(name="SISTEMA_EXTERNO", nullable=true, length=20)
	private String servicioNotifica;
	
	
	// ---------------------------------------
	// Lectura y escritura del estado
	// ---------------------------------------

	public String getServicioNotifica() {
		return servicioNotifica;
	}
	
	public void setServicioNotifica(String servicio) {
		this.servicioNotifica = servicio;
	}
	
}
