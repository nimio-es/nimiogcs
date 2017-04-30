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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.modelo.EntidadNombrable;
import es.nimio.nimiogcs.modelo.enumerados.EnumEstadoValidezYActividad;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_ARTEFACTOS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_REGISTRO", discriminatorType = DiscriminatorType.STRING, length = 10)
@DiscriminatorValue(value = "ARTEFACTO")
public class Artefacto 
	extends MetaRegistro
	implements EntidadNombrable {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name = "TIPO_REGISTRO", insertable = false, updatable = false)
	private String tipoRegistro;
	
	@Id
	@Column(name = "ID", nullable=false, length=32)
	private String id;
	
	@JoinColumn(name = "TIPO_ARTEFACTO", nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private TipoArtefacto tipo;

	@Column(name = "NOMBRE", nullable=false, length=40)
	private String nombre;

	@Column(name = "ESTADO_VALIDEZACTVIDAD", nullable = false)
	@Enumerated(EnumType.STRING)
	private EnumEstadoValidezYActividad estadoValidezActivacion = EnumEstadoValidezYActividad.ValidoActivo;

	@OneToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="GCS_ARTEFACTOS_DIRECTIVAS_REL",
			joinColumns=@JoinColumn(name="ID_ARTEFACTO"),
			inverseJoinColumns=@JoinColumn(name="ID_DIRECTIVA")
	)
	private List<DirectivaBase> directivas = new ArrayList<DirectivaBase>();
	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public TipoArtefacto getTipoArtefacto() {
		return this.tipo;
	}
	
	public void setTipoArtefacto(TipoArtefacto tipoArtefacto) {
		this.tipo = tipoArtefacto;
	}

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// FIXME: necesario por el formuulario antiguo. Hasta que se pase a un DTO especial
	public EnumEstadoValidezYActividad getEstadoValidezActivacion() { return estadoValidezActivacion; }

	// FIXME: necesario por el formuulario antiguo. Hasta que se pase a un DTO especial
	public void setEstadoValidezActivacion(EnumEstadoValidezYActividad valor) { this.estadoValidezActivacion = valor; }
	
	public boolean getEstadoValidez() { 
		return estadoValidezActivacion != EnumEstadoValidezYActividad.Invalido; 
	}

	public void setEstadoValidez(boolean valor) {

	    this.estadoValidezActivacion = valor ? 
	    		EnumEstadoValidezYActividad.ValidoInactivo 
	    		: EnumEstadoValidezYActividad.Invalido;
	}

	public boolean getEstadoActivacion() {
	  return estadoValidezActivacion == EnumEstadoValidezYActividad.ValidoActivo;
	}

	public void setEstadoActivacion(boolean estado) {
	    // solamente aplicable cuando el estado previo es valido
	   if(getEstadoValidez()) 
		   this.estadoValidezActivacion = estado ? 
				   EnumEstadoValidezYActividad.ValidoActivo 
				   : EnumEstadoValidezYActividad.ValidoInactivo ;
	}
	
	public List<DirectivaBase> getDirectivasArtefacto() {
		return this.directivas;
	}

	
	// --------------------------------------
	// ID
	// --------------------------------------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 32);
	}
}
