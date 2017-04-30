package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;

/**
 * Relaciona una operación con un entorno
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "SITIO")
public class RelacionOperacionSitio extends RelacionOperacion {

	public RelacionOperacionSitio() { super(); }

	public RelacionOperacionSitio(Operacion operacion, Servidor sitio) { 
		super(operacion);
		this.entorno = sitio;
	}

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Servidor entorno;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public Servidor getSitio() {
		return entorno;
	}

	public void setSitio(Servidor sitio) {
		this.entorno = sitio;
	}
}
