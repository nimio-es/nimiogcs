package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

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

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;

/**
 * Clase base de las relaciones
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_OPERACIONES_RELACIONES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_RELACION", discriminatorType = DiscriminatorType.STRING, length = 5)
@DiscriminatorValue(value = "-----")
public class RelacionOperacion extends MetaRegistro {

	public RelacionOperacion() {}
	
	public RelacionOperacion(Operacion operacion) {
		this.operacion = operacion;
	}
	
	// ---------------------------------------
	// Estado
	// ---------------------------------------
	
	@Column(name = "TIPO_RELACION", insertable = false, updatable = false)
	private String tipoRelacion;
	
	@Id
	@Column(name = "ID", nullable=false, length=36)
	private String id;
	
	@JoinColumn(name="ORIGEN", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Operacion operacion;

	// ---------------------------------------
	// Lectura y escritura del estado
	// ---------------------------------------

	public String getTipoRelacion() { return tipoRelacion; }
	
	@Override public String getId() { return id; }
	@Override public void setId(String id) { this.id = id; }
	
	public Operacion getOperacion() {
		return operacion;
	}
	
	public void setOpeacion(Operacion operacion) {
		this.operacion = operacion;
	}
	
	// ---------------------------------------
	// Lectura y escritura del estado
	// ---------------------------------------

	@Override
	protected String generarIdElemento() {
		String id = super.generarIdElemento();
		return id.substring(0, id.length() > 36 ? 36 : id.length()); 
	}
}
