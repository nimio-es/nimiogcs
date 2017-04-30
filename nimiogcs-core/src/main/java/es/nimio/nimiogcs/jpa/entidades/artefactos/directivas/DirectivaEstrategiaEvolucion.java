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
@Table(name = "GCS_DIRECTIVAS_ESTRATEGIA")
@DiscriminatorValue(value = "EVOLUCION")
public class DirectivaEstrategiaEvolucion extends DirectivaBase {

	public DirectivaEstrategiaEvolucion() {}
	
	public static final String VALOR_PROYECTO = "PROYECTO";
	public static final String VALOR_UNICA = "UNICA";
	public static final String EXPLICACION_PROYECTO = "Cada proyecto evolucionará una versión distinta";
	public static final String EXPLICACION_UNICA = "Todos los proyectos trabajan con la misma versión";
	public static final String EXPLICACION_NO_SE_SABE = "No se tiene descripción del tipo de evolución";
	
	
	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="ESTRATEGIA", nullable=false, length=100)
	private String estrategia;

	
	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getEstrategia() {
		return estrategia;
	}

	public void setEstrategia(String estrategia) {
		this.estrategia = estrategia;
	}

	
	public boolean esUnica() {
		return estrategia.equalsIgnoreCase(VALOR_UNICA);
	}

	public boolean esPorProyecto() {
		return estrategia.equalsIgnoreCase(VALOR_PROYECTO);
	}
		
	public String getDescripcionEstrategia() {
		if(esPorProyecto()) return EXPLICACION_PROYECTO;
		if(esUnica()) return EXPLICACION_UNICA;
		
		return EXPLICACION_NO_SE_SABE;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------
	
	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("ESTRATEGIA", estrategia);
		return mapa;
	}
}
