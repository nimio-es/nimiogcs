package es.nimio.nimiogcs.jpa.entidades.artefactos;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_TIPOS_ARTEFACTOS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_TIPO", discriminatorType = DiscriminatorType.STRING, length = 8)
@DiscriminatorValue(value = "TIPO")
public class TipoArtefacto 
	extends MetaRegistro 
	implements EntidadNombrable {
	
	// ------------------------------------
	// Estado
	// ------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=15)
	private String id;
	
	@Column(name="NOMBRE", nullable=false, length=40)
	private String nombre;
	
	@Column(name="DE_USUARIO", nullable=false, length=2)
	private String deUsuario = K.L.SI;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="GCS_TIPOS_ART_DIRECTIVAS_REL",
			joinColumns=@JoinColumn(name="ID_TIPO_ARTEFACTO"),
			inverseJoinColumns=@JoinColumn(name="ID_DIRECTIVA")
	)
	private List<DirectivaBase> directivasTipo = new ArrayList<DirectivaBase>();	
	
	// ------------------------------------
	// Escritura y lectura del estado
	// ------------------------------------
	
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

	public boolean getDeUsuario() {
		return K.L.desde(deUsuario);
	}
	
	public void setDeUsuario(boolean deUsuario) {
		this.deUsuario = K.L.para(deUsuario);
	}
	
	public List<DirectivaBase> getDirectivasTipo() {
		return this.directivasTipo;
	}
	
	// ------------------------------------
	// ID
	// ------------------------------------
	
	@Override
	protected String generarIdElemento() {
		// En estas entidades, el id debe ser el que se indique como tipo
		return null;
	}
	
}
