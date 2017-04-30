package es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;

/**
 * Relación genérica de una entida con otra entidad.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_ARTEFACTOS_DEPENDENCIAS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_DEPENDENCIA", discriminatorType = DiscriminatorType.STRING, length = 4)
@DiscriminatorValue(value = "---")
public class Dependencia extends MetaRegistro {

	public Dependencia() { super(); }

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------

	@Id
	@Column(name="ID", nullable=false, length=32)
	private String id;
	
	@JoinColumn(name="ORIGEN", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Artefacto dependiente;
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Artefacto requerido;

	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Artefacto getDependiente() {
		return dependiente;
	}

	public void setDependiente(Artefacto dependiente) {
		this.dependiente = dependiente;
	}

	public Artefacto getRequerida() {
		return requerido;
	}

	public void setRequerida(Artefacto requerida) {
		this.requerido = requerida;
	}

	// ---------------------------------------------
	// ID
	// ---------------------------------------------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 36);
	}

	// ---------------------------------------------
	// Equivalencia
	// ---------------------------------------------

	/**
	 * Revisa si es una del mismo tipo y si origen 
	 * y destino son los mismos
	 * @param df
	 * @return
	 */
	public boolean pseudoEquivalente(Dependencia df) {
		return 
				this.getClass().equals(df.getClass())
				&& this.getDependiente().getId().equalsIgnoreCase(df.getDependiente().getId())
				&& this.getRequerida().getId().equalsIgnoreCase(df.getRequerida().getId());
	}
	
	/**
	 * Comprueba si la dependencia actual es equivalente
	 * a la que se pasa por parámetro
	 */
	public boolean equivalente(Dependencia df) {
		return pseudoEquivalente(df);
	}

	// ---------------------------------------------
	// Actualiza
	// ---------------------------------------------

	public void actualizaDesde(Dependencia df) throws ErrorInconsistenciaDatos {

		if(!pseudoEquivalente(df)) 
			throw new ErrorInconsistenciaDatos("Se intenta realizar una actualización de una dependencia desde una estructura incompetible");
		
		// en la base no hay nada que hacer
	}
	
	// ---------------------------------------------
	// Auto-réplica
	// ---------------------------------------------
	
	public Dependencia reproducir() {
		Dependencia nueva = (Dependencia) nuevaInstancia();
		nueva.requerido = requerido;
		return nueva;
	}
	
}
