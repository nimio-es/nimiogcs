package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.K;

/**
 * Directiva que permite la caracterización de una familia de artefactos
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_CARACTERIZACION")
@DiscriminatorValue(value = "CARACTERIZACION")
public class DirectivaCaracterizacion extends DirectivaBase {

	public DirectivaCaracterizacion() {}
	
	// --------------------------------------
	// Estado
	// --------------------------------------
	
	@Column(name="DIRECTIVAS_REQUERIDAS", nullable=true, length=200)
	private String directivasRequeridas;
	
	@Column(name="DIRECTIVAS_OPCIONALES", nullable=true, length=200)
	private String directivasOpcionales;
	
	@Column(name="GENERAR_COOR_MAVEN", nullable=false, length=2)
	private String generarCoordenadasMaven = K.L.NO;
	
	@Column(name="EMPAQUETADO_COORDENADA", nullable=true, length=20)
	private String empaquetadoCoordenada;
	
	@Column(name="LIBRERIA_EXTERNA", nullable=false, length=2)
	private String libreriaExterna = K.L.NO;
	
	@Column(name="PATRON_VALIDACION_NOMBRE", nullable=true, length=200)
	private String plantillaValidacionNombre;

	// --------------------------------------
	// Lectura y esctitura del estado
	// --------------------------------------
	
	public String[] getDirectivasRequeridas() {
		if(Strings.isNullOrEmpty(directivasRequeridas)) return new String[] {};
		return directivasRequeridas.split(",");
	}

	public void setDirectivasRequeridas(String... directivasRequeridas) {
		this.directivasRequeridas = join(directivasRequeridas);
	}

	public String[] getDirectivasOpcionales() {
		if(Strings.isNullOrEmpty(directivasOpcionales)) return new String[] {};
		return directivasOpcionales.split(",");
	}

	public void setDirectivasOpcionales(String... directivasOpcionales) {
		this.directivasOpcionales = join(directivasOpcionales);
	}

	public boolean getGenerarCoordenadasMaven() {
		return K.L.desde(generarCoordenadasMaven);
	}

	public void setGenerarCoordenadasMaven(boolean generarCoordenadasMaven) {
		this.generarCoordenadasMaven = K.L.para(generarCoordenadasMaven);
	}
	
	public String getEmpaquetadoCoordenada() {
		return empaquetadoCoordenada;
	}

	public void setEmpaquetadoCoordenada(String empaquetadoCoordenada) {
		this.empaquetadoCoordenada = empaquetadoCoordenada;
	}

	public boolean getLibreriaExterna() {
		return K.L.desde(libreriaExterna);
	}
	
	public void setLibreriaExterna(boolean esLibreriaExterna) {
		libreriaExterna = K.L.para(esLibreriaExterna);
	}
	
	public String getPlantillaValidacionNombre() {
		return plantillaValidacionNombre;
	}

	public void setPlantillaValidacionNombre(String plantillaValidacionNombre) {
		this.plantillaValidacionNombre = plantillaValidacionNombre;
	}
	
	
	// ---------------

	private String join(String... valores) {
		if(valores == null) return "";
		String r = "";
		for(String v: valores) r += r.length() > 0 ? "," + v : v;
		return r;
	}
}
