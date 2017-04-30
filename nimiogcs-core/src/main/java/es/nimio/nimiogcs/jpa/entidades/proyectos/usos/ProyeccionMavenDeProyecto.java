package es.nimio.nimiogcs.jpa.entidades.proyectos.usos;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "MAVEN")
public class ProyeccionMavenDeProyecto extends UsoYProyeccionProyecto {

	// -------------------------------------
	// Estado
	// -------------------------------------

	@Column(name="URL_USO", nullable=false, length=250)
	private String urlRepositorio;

	// -------------------------------------
	// Lectura y escritura del estado
	// -------------------------------------

	public String getUrlRepositorio() {
		return this.urlRepositorio;
	}
	
	public void setUrlRepositorio(String url) {
		urlRepositorio = url;
	}
}
