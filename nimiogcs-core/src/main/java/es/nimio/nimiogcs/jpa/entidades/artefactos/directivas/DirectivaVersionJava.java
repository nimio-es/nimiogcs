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
@Table(name = "GCS_DIRECTIVAS_VERSIONJAVA")
@DiscriminatorValue(value = "VERSION_JAVA")
public class DirectivaVersionJava extends DirectivaBase {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Column(name="VERSION_COMPILA", nullable=false, length=5)
	private String versionCompila;

	@Column(name="VERSION_DESTINO", nullable=false, length=5)
	private String versionDestino;

	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------

	public String getVersionCompila() {
		return versionCompila;
	}

	public void setVersionCompila(String versionCompila) {
		this.versionCompila = versionCompila;
	}

	public String getVersionDestino() {
		return versionDestino;
	}

	public void setVersionDestino(String versionDestino) {
		this.versionDestino = versionDestino;
	}
	
	// -------------------------------------
	// Seudoclonación
	// -------------------------------------

	@Override
	public DirectivaVersionJava replicar() {
		DirectivaVersionJava replica = (DirectivaVersionJava)super.replicar();
		replica.versionCompila = versionCompila;
		replica.versionDestino = versionDestino;
		return replica;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		Map<String, String> mapa = super.getMapaValores();
		mapa.put("VERSION-COMPILA", versionCompila);
		mapa.put("VERSION-DESTINO", versionDestino);
		return mapa;
	}	
	
}
