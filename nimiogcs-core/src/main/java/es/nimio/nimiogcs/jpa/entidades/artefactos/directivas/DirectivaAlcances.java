package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.nimio.nimiogcs.functional.BiFunction;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_ALCANCES")
@DiscriminatorValue(value = "ALCANCES")
public class DirectivaAlcances extends DirectivaBase {

	public DirectivaAlcances() {}
	
	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="ALCANCES", nullable=false, length=100)
	private String alcances; 
	
	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------
	
	public EnumAlcanceDependencia[] getAlcances() {
		if ((alcances == null) || (alcances.trim().equals("")))
			return new EnumAlcanceDependencia[] {};

		return Streams.toArray(Streams.of(alcances.split(";")).map(
				new Function<String, EnumAlcanceDependencia>() {
					@Override
					public EnumAlcanceDependencia apply(String v) {
						return EnumAlcanceDependencia.valueOf(v);
					}
				}));
	}
	
	public void setAlcances(EnumAlcanceDependencia... alcances) {
		
		final String s = Streams.of(alcances).foldLeft("",
				new BiFunction<String, EnumAlcanceDependencia, String>() {
					@Override
					public String apply(String acc, EnumAlcanceDependencia alc) {
						String sAlc = alc.toString();
						return acc.equals("") ? sAlc : acc + ";" + sAlc;
					}
				});
		
		this.alcances = s;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("ALCANCES", alcances);
		return mapa;
	}
	
}
