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
@Table(name = "GCS_DIRECTIVAS_TAXONOMIA")
@DiscriminatorValue(value = "TAXONOMIA")
public class DirectivaTaxonomia extends DirectivaBase {

	public DirectivaTaxonomia() {}
	
	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="TAXONOMIA", nullable=false, length=150)
	private String taxonomia;

	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getTaxonomia() {
		return taxonomia;
	}

	public void setTaxonomia(String taxonomia) {
		this.taxonomia = taxonomia;
	} 
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("TAXONOMIA", taxonomia);
		return mapa;
	}
	
}
