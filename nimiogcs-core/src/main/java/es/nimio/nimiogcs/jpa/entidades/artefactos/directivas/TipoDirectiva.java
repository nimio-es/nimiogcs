package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_TIPOS_DIRECTIVAS")
public class TipoDirectiva implements EntidadNombrable {

	// -----------------------------------------
	// Estado
	// -----------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=20)
	private String id;
	
	@Column(name="NOMBRE", nullable=false, length=20)
	private String nombre;
	
	@Column(name="PARA_TIPO_ARTEFACTO", nullable=false, length=2)
	private String paraTipoArtefacto = K.L.SI;
	
	@Column(name="PARA_ARTEFACTO", nullable=false, length=2)
	private String paraArtefacto = K.L.SI;
	
	@Column(name="CON_DICCIONARIO", nullable=false, length=2)
	private String conDiccionario = K.L.NO;
	
	// -----------------------------------------
	// Lectura y escritura del estado
	// -----------------------------------------
	
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
	
	public boolean getParaTipoArtefacto() {
		return K.L.desde(paraTipoArtefacto);
	}
	
	public void setParaTipoArtefacto(boolean v) {
		paraTipoArtefacto = K.L.para(v);
	}
	
	public boolean getParaArtefacto() {
		return K.L.desde(paraArtefacto);
	}
	
	public void setParaArtefacto(boolean v) {
		paraArtefacto = K.L.para(v);
	}
	
	public boolean getConDiccionario() {
		return K.L.desde(conDiccionario);
	}
	
	public void setConDiccionario(boolean conDiccionario) {
		this.conDiccionario = K.L.para(conDiccionario);
	}
	
}

