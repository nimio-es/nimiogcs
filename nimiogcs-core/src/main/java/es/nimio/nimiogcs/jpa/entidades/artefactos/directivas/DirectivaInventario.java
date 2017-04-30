package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.sistema.AplicacionEmpresa;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_INVENTARIO")
@DiscriminatorValue(value = "INVENTARIO")
public class DirectivaInventario extends DirectivaBase {

	public DirectivaInventario() {}	

	// --------------------------------------
	// Estado
	// --------------------------------------
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ID_APLICACION")
	private AplicacionEmpresa aplicacion; 
	
	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------
	

	public AplicacionEmpresa getAplicacion() {
		return aplicacion;
	}
	
	public void setAplicacion(AplicacionEmpresa aplicacion) {
		this.aplicacion = aplicacion;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------
	
	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("ID-APP", aplicacion.getId());
		mapa.put("NOMBRE-APP", aplicacion.getNombre());
		return mapa;
	}
	
}
