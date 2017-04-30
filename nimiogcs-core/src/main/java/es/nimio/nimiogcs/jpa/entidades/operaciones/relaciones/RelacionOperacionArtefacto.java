package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "ARTF")
public class RelacionOperacionArtefacto extends RelacionOperacion {

	public RelacionOperacionArtefacto() { super(); }

	public RelacionOperacionArtefacto(Operacion operacion, Artefacto artefacto) { 
		super(operacion);
		this.entidad = artefacto;
	}
	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Artefacto entidad;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public Artefacto getEntidad() {
		return entidad;
	}

	public void setEntidad(Artefacto entidad) {
		this.entidad = entidad;
	}
}
