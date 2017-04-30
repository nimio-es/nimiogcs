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
@Table(name = "GCS_DIRECTIVAS_PARAMDEPLOYER")
@DiscriminatorValue(value = "PARAMETROS_DEPLOYER")
public class DirectivaParametrosDeployer extends DirectivaBase {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="RUTA", nullable=false, length=40)
	private String pathInRequest;

	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getPathInRequest() {
		return pathInRequest;
	}

	public void setPathInRequest(String pathInRequest) {
		this.pathInRequest = pathInRequest;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	@Override
	public Map<String, String> getMapaValores() {
		Map<String, String> map = super.getMapaValores();
		map.put("PATH-EN-PETICION", getPathInRequest());
		return map;
	}	
}
