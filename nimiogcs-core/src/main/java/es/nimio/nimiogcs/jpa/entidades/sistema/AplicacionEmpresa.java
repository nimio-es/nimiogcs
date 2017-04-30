package es.nimio.nimiogcs.jpa.entidades.sistema;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

/**
 * Representa la entidad Aplicación empresarial
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_APLICACIONES")
public class AplicacionEmpresa extends MetaRegistro implements EntidadNombrable {

	public AplicacionEmpresa() {}
	
	public AplicacionEmpresa(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	
	// ------------------------------
	// Estado
	// ------------------------------

	@Id
	@Column(name="ID", nullable=false, length=3)
	private String id;
	
	@Column(name="NOMBRE", nullable=false, length=40)
	private String nombre;
	
	
	// ------------------------------
	// Lectura y escritura del estado
	// ------------------------------

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
