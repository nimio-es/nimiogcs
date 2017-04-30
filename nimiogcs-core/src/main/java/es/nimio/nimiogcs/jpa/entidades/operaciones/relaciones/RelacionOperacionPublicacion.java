package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;

/**
 * Relaciona una operación con un entorno
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "PRPUA")
public class RelacionOperacionPublicacion extends RelacionOperacion {

	public RelacionOperacionPublicacion() { super(); }

	public RelacionOperacionPublicacion(Operacion operacion, Publicacion publicacion) { 
		super(operacion);
		this.publicacion = publicacion;
	}
	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Publicacion publicacion;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public Publicacion getPublicacion() {
		return publicacion;
	}

	public void setPublicacion(Publicacion entorno) {
		this.publicacion = entorno;
	}
	
}
