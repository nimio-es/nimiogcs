package es.nimio.nimiogcs.jpa.entidades.sistema.servidores;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_SITIOS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_SITIO", discriminatorType = DiscriminatorType.STRING, length = 3)
@DiscriminatorValue(value = "STO")
public class Servidor 
	extends MetaRegistro 
	implements EntidadNombrable {

	// --------------------------------
	// Estado
	// --------------------------------

	@Id
	@Column(name = "ID", nullable=false, length=32)
	private String id;
	
	@Column(name = "NOMBRE", nullable=false, length=30)
	private String nombre;

	// --------------------------------
	// Leer/Escribir estado
	// --------------------------------

	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	// ------------------------------
	// ID
	// ------------------------------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 32);
	}


}
