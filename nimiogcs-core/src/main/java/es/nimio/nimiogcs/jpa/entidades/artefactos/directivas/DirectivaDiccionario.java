package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

/**
 * Directiva abierta que se basa en un subtipo o diccionario con claves/valor
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_DICCIONARIO")
@DiscriminatorValue(value = "DICCIONARIO")
public class DirectivaDiccionario extends DirectivaBase {

	// ------------------------------
	// Estado
	// ------------------------------

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DICCIONARIO", nullable=false)
	private TipoDirectivaDiccionario diccionario;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@MapKeyColumn(name="CLAVE")
	@CollectionTable(name="GCS_DIRECTIVAS_DICCIONARIO_VAL", joinColumns=@JoinColumn(name="ID"))
	@Column(name="VALOR")
	private Map<String, String> valores = new HashMap<String, String>();

	// ------------------------------
	// Lectura y escritura del estado
	// ------------------------------

	public Map<String, String> getValores() {
		return valores;
	}

	public void setValores(Map<String, String> valores) {
		this.valores = valores;
	}

	public TipoDirectivaDiccionario getDiccionario() {
		return diccionario;
	}
	
	public void setDiccionario(TipoDirectivaDiccionario diccionario) {
		this.diccionario = diccionario;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.putAll(this.valores);
		return mapa;
	}
	
}
