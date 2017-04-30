package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name="GCS_TIPOS_DIRECTIVAS_DICC_DEFS")
public class TipoDirectivaDiccionarioDefinicion {

	// ---------------------------------
	// Estado
	// ---------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=32)
	private String id = generarIdElemento();
	
	@Column(name="ID_DICCIONARIO", nullable=false, length=20)
	private String idDiccionario;
	
	@Column(name="CLAVE", nullable=false, length=30)
	private String clave;
	
	@Column(name="POSICION", nullable=false)
	private int posicion;
	
	@Column(name="ETIQUETA", nullable=true, length=40)
	private String etiqueta;
	
	@Column(name="PATRON_CONTROL", nullable=true, length=200)
	private String patronControl;

	@Column(name="DESCRIPCION", nullable=true, length=300)
	private String bloqueDescripcion;

	// ----------------------------------
	// Lectura y escritura del estado
	// ----------------------------------

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getIdDiccionario() {
		return idDiccionario;
	}
	
	public void setIdDiccionario(String id) {
		this.idDiccionario = id;
	}
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getPatronControl() {
		return patronControl;
	}
	public void setPatronControl(String patronControl) {
		this.patronControl = patronControl;
	}
	public String getBloqueDescripcion() {
		return bloqueDescripcion;
	}
	public void setBloqueDescripcion(String descripcion) {
		bloqueDescripcion = descripcion;
	}
	
	// ------------------------------
	// ID
	// ------------------------------

	protected String generarIdElemento() {
		UUID r = UUID.randomUUID();
		UUID r2 = UUID.randomUUID();
		String finalId = new StringBuilder(40) 
				.append(Long.toString(Math.abs(System.currentTimeMillis()), Character.MAX_RADIX))
				.append('-') 
				.append(
						Long.toString(
								Math.abs(r.getMostSignificantBits()), Character.MAX_RADIX
						)
				)
				.append(
						Long.toString(
								Math.abs(r.getLeastSignificantBits()), Character.MAX_RADIX
						)
				)
				.append(
						Long.toString(
								Math.abs(r2.getMostSignificantBits()), Character.MAX_RADIX
						)
				)
				.toString()
				.toUpperCase();

		// y lo devolvemos
		return finalId.substring(0, 32);
	}
	
}
