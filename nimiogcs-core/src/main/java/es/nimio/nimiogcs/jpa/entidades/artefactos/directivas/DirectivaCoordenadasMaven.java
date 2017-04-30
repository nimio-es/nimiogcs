package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.nimio.nimiogcs.Strings;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_COORMAVEN")
@DiscriminatorValue(value = "COOR_MAVEN")
public class DirectivaCoordenadasMaven extends DirectivaBase {

	public DirectivaCoordenadasMaven() {}
	
	// --------------------------------------
	// Estado
	// --------------------------------------
	
	@Column(name="ID_GRUPO", nullable=false, length=100)
	private String idGrupo;
	
	@Column(name="ID_ARTEFACTO", nullable=false, length=100)
	private String idArtefacto;
	
	@Column(name="VERSION", nullable=false, length=30)
	private String version;

	@Column(name="EMPAQUETADO", nullable=false, length=15)
	private String empaquetado;
	
	@Column(name="CLASIFICADOR", nullable=true, length=100)
	private String clasificador;


	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------
	
	public String getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getIdArtefacto() {
		return idArtefacto;
	}

	public void setIdArtefacto(String idArtefacto) {
		this.idArtefacto = idArtefacto;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEmpaquetado() {
		return empaquetado;
	}

	public void setEmpaquetado(String empaquetado) {
		this.empaquetado = empaquetado;
	}

	public String getClasificador() {
		return clasificador;
	}

	public void setClasificador(String clasificador) {
		this.clasificador = clasificador;
	}
	
	public String getCoordenadaFinal() {
		return 
				getIdGrupo() 
				+ ":" + getIdArtefacto() 
				+ ":" + getVersion() 
				+ ":" + getEmpaquetado()
				+ (Strings.isNullOrEmpty(getClasificador())? "" : ":" + getClasificador());
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("ID_GRUPO", idGrupo);
		mapa.put("ID_ARTEFACTO", idArtefacto);
		mapa.put("VERSION", version);
		mapa.put("EMPAQUETADO", empaquetado);
		mapa.put("CLASIFICADOR", clasificador);
		return mapa;
	}
	
	
}
