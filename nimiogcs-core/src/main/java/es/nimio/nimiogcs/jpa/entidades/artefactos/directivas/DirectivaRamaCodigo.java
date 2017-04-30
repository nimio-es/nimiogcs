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
@Table(name = "GCS_DIRECTIVAS_RAMACODIGO")
@DiscriminatorValue(value = "RAMA_CODIGO")
public class DirectivaRamaCodigo extends DirectivaBase {

	public DirectivaRamaCodigo() {}
	
	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="RAMA_CODIGO", nullable=false, length=300)
	private String ramaCodigo;

	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getRamaCodigo() {
		return ramaCodigo;
	}

	public void setRamaCodigo(String rama) {
		this.ramaCodigo = rama;
	} 
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("RAMA-CODIGO", ramaCodigo);
		return mapa;
	}	
}
