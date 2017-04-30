package es.nimio.nimiogcs.jpa.entidades.artefactos;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.K;

/**
 * Artefacto que congela cualquier evolución del artefacto. 
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "FREEZE")
public class CongelarEvolucionArtefacto 
	extends Artefacto 
	implements ITestaferroArtefacto {

	// -------------------------------------------------------
	// Estado
	// -------------------------------------------------------

	@JoinColumn(name="ID_ARTEFACTO_AFECTADO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Artefacto artefacto;

	@Column(name="SINCRONIZADO_ESTABLE", length=2, nullable=false)
	private String sincronizadoEstable = "SI";
	
	// -------------------------------------------------------
	// Lectura y escritura del estado
	// -------------------------------------------------------

	@Override
	public Artefacto getArtefactoAfectado() {
		return artefacto;
	}
	
	public void setArtefactoAfectado(final Artefacto artefacto) {
		this.artefacto = artefacto;
	}
	
	/**
	 * Indica cuándo el artefacto se encuentra sincronizado 
	 * con la rama estable.
	 */
	@Override
	public boolean getSincronizadoEstable() {
		return K.L.desde(sincronizadoEstable);
	}
	
	public void setSincronizadoEstable(boolean sincronizado) {
		this.sincronizadoEstable = K.L.para(sincronizado);
	}
}
