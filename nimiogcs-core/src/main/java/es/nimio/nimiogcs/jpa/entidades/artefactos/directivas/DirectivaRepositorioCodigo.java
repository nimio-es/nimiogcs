package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_REPOCODIGO")
@DiscriminatorValue(value = "REPO-CODIGO")
public class DirectivaRepositorioCodigo extends DirectivaBase {

	public DirectivaRepositorioCodigo() {}

	// --------------------------------------
	// Estado
	// --------------------------------------
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ID_REPOSITORIO")
	private RepositorioCodigo repositorio; 
	
	@Column(name="PARCIAL_ETIQUETAS", nullable=false, length=200)
	private String parcialEtiquetas;
	
	@Column(name="PARCIAL_ESTABLES", nullable=false, length=200)
	private String parcialEstables;

	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------
	
	public RepositorioCodigo getRepositorio() {
		return repositorio;
	}
	
	public void setRepositorio(RepositorioCodigo repositorio) {
		this.repositorio = repositorio;
	}
	
	public String getParcialEtiquetas() {
		return parcialEtiquetas;
	}

	public void setParcialEtiquetas(String parcialEtiquetas) {
		this.parcialEtiquetas = parcialEtiquetas;
	}

	public String getParcialEstables() {
		return parcialEstables;
	}

	public void setParcialEstables(String parcialEstables) {
		this.parcialEstables = parcialEstables;
	}
	
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("ID-REPOSITORIO", repositorio.getId());
		mapa.put("NOMBRE-REPOSITORIO", repositorio.getNombre());
		mapa.put("URI-BASE", repositorio.getUriRaizRepositorio());
		mapa.put("TIPO-REPOSITORIO", "SVN");
		return mapa;
	}	
}
