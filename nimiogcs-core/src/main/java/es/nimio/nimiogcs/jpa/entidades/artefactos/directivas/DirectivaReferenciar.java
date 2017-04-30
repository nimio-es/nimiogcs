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
@Table(name = "GCS_DIRECTIVAS_REFERENCIAR")
@DiscriminatorValue(value = "REFERENCIAR")
public class DirectivaReferenciar extends DirectivaBase {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="POSIBLES_POSICIONAL", nullable=true, length=60)
	private String posiblesPosicional;

	@Column(name="POSIBLES_ALCANCE", nullable=true, length=60)
	private String posiblesAlcance;

	@Column(name="POSIBLES_WEB", nullable=true, length=60)
	private String posiblesWeb;

	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String[] getPosiblesPosicional() {
		if(Strings.isNullOrEmpty(posiblesPosicional)) return new String[] {}; 
		return posiblesPosicional.split(",");
	}

	public void setPosiblesPosicional(String... posibles) {
		String p = "";
		for(String posible: posibles) {
			p += p.length() > 0 ? "," + posible : posible;
		}
		this.posiblesPosicional = p;
	}

	public String[] getPosiblesAlcance() {
		if(Strings.isNullOrEmpty(posiblesAlcance)) return new String[] {}; 
		return posiblesAlcance.split(",");
	}

	public void setPosiblesAlcance(String... posibles) {
		String p = "";
		for(String posible: posibles) {
			p += p.length() > 0 ? "," + posible : posible;
		}
		this.posiblesAlcance = p;
	}
	
	public String[] getPosiblesWeb() {
		if(Strings.isNullOrEmpty(posiblesWeb)) return new String[] {}; 
		return posiblesWeb.split(",");
	}

	public void setPosiblesWeb(String... posibles) {
		String p = "";
		for(String posible: posibles) {
			p += p.length() > 0 ? "," + posible : posible;
		}
		this.posiblesWeb = p;
	}
	
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("POSIBLES-POSICIONAL", posiblesPosicional);
		mapa.put("POSIBLES_ALCANCE", posiblesAlcance);
		mapa.put("POSIBLES-WEB", posiblesWeb);
		return mapa;
	}	
	
}
