package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Mecanismo de publicación empleando una llamada a un servicio REST
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_PUBJENKINS")
@DiscriminatorValue(value = "PUBLICACION_JENKINS")
public class DirectivaPublicacionJenkins extends DirectivaBase {

	@Column(name="TAREA", nullable=false, length=100)
	private String tarea;

	public String getTarea() {
		return tarea;
	}

	public void setTarea(String tarea) {
		this.tarea = tarea;
	}

	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("TAREA", tarea);
		return mapa;
	}
}
