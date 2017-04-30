package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "TART")
public class RelacionOperacionTipoArtefacto extends RelacionOperacion {

	public RelacionOperacionTipoArtefacto() { super(); }

	public RelacionOperacionTipoArtefacto(Operacion operacion, TipoArtefacto artefacto) { 
		super(operacion);
		this.tipoArtefacto = artefacto;
	}
	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private TipoArtefacto tipoArtefacto;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public TipoArtefacto getTipoArtefacto() {
		return tipoArtefacto;
	}

	public void setTipoArtefacto(TipoArtefacto entidad) {
		this.tipoArtefacto = entidad;
	}
}
