package es.nimio.nimiogcs.jpa.entidades.sistema.usuarios;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;

@Entity
@Access(AccessType.FIELD)
@Table(name="GCS_USUARIOS")
public class Usuario extends MetaRegistro {

	@Id
	@Column(name="USERNAME", nullable=false, length=30)
	private String id;
	
	@Column(name="NOMBRE", nullable=true, length=150)
	private String nombre;
	
	@Column(name="CORREO", nullable=true, length=100)
	private String corre;

	
	// ---
	
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

	public String getCorre() {
		return corre;
	}

	public void setCorre(String corre) {
		this.corre = corre;
	}
	
}
