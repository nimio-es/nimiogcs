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
@Table(name = "GCS_DIRECTIVAS_ESTRUCTCODIGO")
@DiscriminatorValue(value = "ESTRUCT_CODIGO")
public class DirectivaEstructuraCodigo extends DirectivaBase {
	
	@Column(name="CARPETAS", nullable=false, length=300)
	private String carpetas;

	public String getCarpetas() {
		return carpetas;
	}

	public void setCarpetas(String carpetas) {
		this.carpetas = carpetas;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("CARPETAS", carpetas);
		return mapa;
	}
	
}
