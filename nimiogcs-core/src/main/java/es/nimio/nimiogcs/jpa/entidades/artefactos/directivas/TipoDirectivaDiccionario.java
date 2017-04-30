package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_TIPOS_DIRECTIVAS_DICC")
public class TipoDirectivaDiccionario {

	// ---------------------------------
	// Estado
	// ---------------------------------

	@Id
	@Column(name="ID", nullable=false, length=20)
	private String id;
	
	@Column(name="DESCRIPCION", nullable=false, length=60)
	private String descripcion;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="ID_DICCIONARIO", referencedColumnName="ID")
	private List<TipoDirectivaDiccionarioDefinicion> definicionesDiccionario;


	// ---------------------------------
	// Lectura y escritura del estado
	// ---------------------------------

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<TipoDirectivaDiccionarioDefinicion> getDefinicionesDiccionario() {
		return definicionesDiccionario;
	}
}
