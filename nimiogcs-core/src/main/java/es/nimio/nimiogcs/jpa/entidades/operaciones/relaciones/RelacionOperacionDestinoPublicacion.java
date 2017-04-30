package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;

/**
 * Relaciona una operación con un entorno
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "ENTRN")
public class RelacionOperacionDestinoPublicacion extends RelacionOperacion {

	public RelacionOperacionDestinoPublicacion() { super(); }

	public RelacionOperacionDestinoPublicacion(Operacion operacion, DestinoPublicacion entorno) { 
		super(operacion);
		this.entorno = entorno;
	}

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private DestinoPublicacion entorno;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public DestinoPublicacion getEntorno() {
		return entorno;
	}

	public void setEntorno(DestinoPublicacion entorno) {
		this.entorno = entorno;
	}
	
}
