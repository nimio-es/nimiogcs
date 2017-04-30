package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_PUBDEPLOYER")
@DiscriminatorValue(value = "PUBLICACION_DEPLOYER")
public class DirectivaPublicacionDeployer extends DirectivaBase {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="COMPORTAMIENTO", nullable=false, length=20)
	private String comportamiento;
	
	@Column(name="CODIGO_ELEMENTO", nullable=false, length=4)
	private String codigoElemento;

	@Column(name="CARPETA_ELEMENTO", nullable=false, length=30)
	private String carpetaElemento;

	@Column(name="ELEMENTO_TARGET", nullable=true, length=90)
	private String elementoTarget;
	
	@Column(name="DIRECTIVAS", nullable=true, length=200)
	private String directivas; 
	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getComportamiento() {
		return comportamiento;
	}
	
	public void setComportamiento(String comportamiento) {
		this.comportamiento = comportamiento;
	}
	
	public String getCodigoElemento() {
		return codigoElemento;
	}

	public void setCodigoElemento(String codigoElemento) {
		this.codigoElemento = codigoElemento;
	}

	public String getCarpetaElemento() {
		return carpetaElemento;
	}

	public void setCarpetaElemento(String carpetaElemento) {
		this.carpetaElemento = carpetaElemento;
	}
	
	public String getElementoTarget() {
		return elementoTarget;
	}

	public void setElementoTarget(String elementoTarget) {
		this.elementoTarget = elementoTarget;
	}

	public String[] getDirectivas() {
		if(directivas==null) return new String[] {};
		return directivas.split(",");
	}

	public void setDirectivas(String[] directivas) {
		this.directivas = join(directivas);
	}

	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	@Override
	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("DEPLOYER-COMPORTAMIENTO", comportamiento);
		mapa.put("DEPLOYER-CODIGO-ELEMENTO", codigoElemento);
		mapa.put("DEPLOYER-CARPETA-ELEMENTO", carpetaElemento);
		mapa.put("ELEMENTO-TARGET", elementoTarget);
		mapa.put("DIRECTIVAS", directivas);
		return mapa;
	}
	
	
	private String join(String[] datos) {
		String r = "";
		for(String d: datos)
			r += r.length() > 0 ? "," + d : d;
		return r;
	}

}
