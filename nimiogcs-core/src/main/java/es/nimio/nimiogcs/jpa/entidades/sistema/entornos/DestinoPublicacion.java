package es.nimio.nimiogcs.jpa.entidades.sistema.entornos;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_ENTORNOS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO", discriminatorType = DiscriminatorType.STRING, length = 4)
@DiscriminatorValue(value = "BASE")
public class DestinoPublicacion 
	extends MetaRegistro
	implements EntidadNombrable {

	
	// -----------------------------------------------
	// Estado
	// -----------------------------------------------

	@Id
	@Column(name = "ID", nullable=false, length=32)
	private String id;
	
	@Column(name = "NOMBRE", nullable=false, length=30)
	private String nombre;

	// ------------------------------
	// Lectura / Escritura estado
	// ------------------------------

	@NotNull
	@Size(min=3, max=15, message="El identificador debe tener al menos tres caracteres y no superar los quince caracteres.")
	@Pattern(regexp = "^[A-Z]+$", message="El identificador del entorno no cumple el formato. Solamente se admiten letras.")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	@NotNull
	@Size(min=6, max=60, message="El nombre de un entorno debe tener al menos seis caracteres y no superar los sesenta caracteres.")
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
