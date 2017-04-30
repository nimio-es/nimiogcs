package es.nimio.nimiogcs.jpa.entidades.proyectos.usos;

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

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_PROYECTOS_USOS_Y_PROYECC")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_REGISTRO", discriminatorType = DiscriminatorType.STRING, length = 10)
@DiscriminatorValue(value = "BASE")
public class UsoYProyeccionProyecto extends MetaRegistro {

	
	// ----------------------------------------
	// Estado
	// ----------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=32)
	private String id;
	
	@Column(name="ID_PROYECTO", nullable=false, length=32)
	private String idElemento;
	
	// ----------------------------------------
	// Lectura y escritura del estado
	// ----------------------------------------
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public String getIdElemento() {
		return idElemento;
	}
	
	public void setIdElemento(String idElemento) {
		this.idElemento = idElemento;
	}

	// ----------------------------------------
	// ID
	// ----------------------------------------
	
	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 32);
	}
	
}
