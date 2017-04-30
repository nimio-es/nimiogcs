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
@DiscriminatorValue("PROGRAMADA")
public class Programada extends Operacion {

	// ---------------------------------------
	// Construcción
	// ---------------------------------------

	public Programada() {
		super();
	}
	
	public Programada(String subsistema) {
		this();
		this.subsistema = subsistema;
	}
	

	// ---------------------------------------
	// Estado
	// ---------------------------------------

	@Column(name="SISTEMA_EXTERNO", nullable=true, length=20)
	private String subsistema;
	
	
	// ---------------------------------------
	// Lectura y escritura del estado
	// ---------------------------------------

	public String getSubsistema() {
		return subsistema;
	}
	
	public void setSubsistema(String subsistema) {
		this.subsistema = subsistema;
	}
	
}
