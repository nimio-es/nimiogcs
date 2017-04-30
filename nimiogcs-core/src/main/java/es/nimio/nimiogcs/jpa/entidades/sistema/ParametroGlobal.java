package es.nimio.nimiogcs.jpa.entidades.sistema;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_PARAMETROS_GLOBALES")
public class ParametroGlobal extends MetaRegistro {

	// --------------------------------------------
	// Estado
	// --------------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=80)
	private String id;
	
	@Lob
	@Column(name="CONTENIDO", nullable=true)
	private String contenido;
	
	@Lob
	@Column(name="DESCRIPCION", nullable=true)
	private String descripcion;
	
	
	// --------------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------------
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public String getContenido() {
		return contenido;
	}
	
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	// ---
	
	/**
	 * Cuando el contenido es una valor de una sola línea.
	 */
	public String getComoValorSimple() {
		
		return Strings.isNullOrEmpty(contenido) ? "" 
				: contenido
				.replace("\n", "")
				.replace("\r","")
				.replace("\t","")
				.trim();
	}
	
	/**
	 * Trata el contenido como un valor, ignorando los saltos 
	 * de línea y recortando espacios a ambos lados.
	 */
	public boolean comoValorIgualA(String valor) {
		if(Strings.isNullOrEmpty(contenido)) return false;
		return 
				getComoValorSimple()
				.equalsIgnoreCase(valor);
	}
	
	
	// --------------------------------------------
	// ID
	// --------------------------------------------
	
	@Override
	protected String generarIdElemento() {
		// Debe ser indicado manualmente y no autogenerado
		return null;
	}
}
