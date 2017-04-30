package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_PROYECCION")
@DiscriminatorValue(value = "PROYECCION")
public class DirectivaProyeccion extends DirectivaBase {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="NUM_CARPETAS", nullable=false)
	private Integer numeroCarpetas;

	@Column(name="CARPETA_ORIGEN_1", nullable=true, length=20)
	private String carpetaOrigen1;

	@Column(name="CARPETA_DESTINO_1", nullable=true, length=20)
	private String carpetaDestino1;

	@Column(name="CARPETA_ORIGEN_2", nullable=true, length=20)
	private String carpetaOrigen2;

	@Column(name="CARPETA_DESTINO_2", nullable=true, length=20)
	private String carpetaDestino2;

	@Column(name="CARPETA_ORIGEN_3", nullable=true, length=20)
	private String carpetaOrigen3;

	@Column(name="CARPETA_DESTINO_3", nullable=true, length=20)
	private String carpetaDestino3;

	@Column(name="CARPETA_ORIGEN_4", nullable=true, length=20)
	private String carpetaOrigen4;

	@Column(name="CARPETA_DESTINO_4", nullable=true, length=20)
	private String carpetaDestino4;

	@Column(name="CARPETA_ORIGEN_5", nullable=true, length=20)
	private String carpetaOrigen5;

	@Column(name="CARPETA_DESTINO_5", nullable=true, length=20)
	private String carpetaDestino5;

	@Lob
	@Column(name="SVN_IGNORES", nullable=true, columnDefinition="clob default empty_clob()")
	private String svnIgnores;
	

	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public Integer getNumeroCarpetas() {
		return numeroCarpetas;
	}

	public void setNumeroCarpetas(Integer numeroCarpetas) {
		this.numeroCarpetas = numeroCarpetas;
	}

	public String getCarpetaOrigen1() {
		return carpetaOrigen1;
	}

	public void setCarpetaOrigen1(String carpetaOrigen1) {
		this.carpetaOrigen1 = carpetaOrigen1;
	}

	public String getCarpetaDestino1() {
		return carpetaDestino1;
	}

	public void setCarpetaDestino1(String carpetaDestino1) {
		this.carpetaDestino1 = carpetaDestino1;
	}

	public String getCarpetaOrigen2() {
		return carpetaOrigen2;
	}

	public void setCarpetaOrigen2(String carpetaOrigen2) {
		this.carpetaOrigen2 = carpetaOrigen2;
	}

	public String getCarpetaDestino2() {
		return carpetaDestino2;
	}

	public void setCarpetaDestino2(String carpetaDestino2) {
		this.carpetaDestino2 = carpetaDestino2;
	}

	public String getCarpetaOrigen3() {
		return carpetaOrigen3;
	}

	public void setCarpetaOrigen3(String carpetaOrigen3) {
		this.carpetaOrigen3 = carpetaOrigen3;
	}

	public String getCarpetaDestino3() {
		return carpetaDestino3;
	}

	public void setCarpetaDestino3(String carpetaDestino3) {
		this.carpetaDestino3 = carpetaDestino3;
	}

	public String getCarpetaOrigen4() {
		return carpetaOrigen4;
	}

	public void setCarpetaOrigen4(String carpetaOrigen4) {
		this.carpetaOrigen4 = carpetaOrigen4;
	}

	public String getCarpetaDestino4() {
		return carpetaDestino4;
	}

	public void setCarpetaDestino4(String carpetaDestino4) {
		this.carpetaDestino4 = carpetaDestino4;
	}

	public String getCarpetaOrigen5() {
		return carpetaOrigen5;
	}

	public void setCarpetaOrigen5(String carpetaOrigen5) {
		this.carpetaOrigen5 = carpetaOrigen5;
	}

	public String getCarpetaDestino5() {
		return carpetaDestino5;
	}

	public void setCarpetaDestino5(String carpetaDestino5) {
		this.carpetaDestino5 = carpetaDestino5;
	}

	public String getSvnIgnores() {
		return svnIgnores;
	}

	public void setSvnIgnores(String svnIgnores) {
		this.svnIgnores = svnIgnores;
	}

	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("NUMERO-CARPETAS-CODIGO", Integer.toString(numeroCarpetas));
		mapa.put("SVN-IGNORES",svnIgnores.replace("\n", "").replace("\r", "").replace("\t", ""));
		return mapa;
	}	
}
