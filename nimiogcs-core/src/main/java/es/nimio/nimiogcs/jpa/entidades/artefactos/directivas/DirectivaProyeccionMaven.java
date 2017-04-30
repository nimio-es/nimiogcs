package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS_PROYMAVEN")
@DiscriminatorValue(value = "PROYECCION_MAVEN")
public class DirectivaProyeccionMaven extends DirectivaBase {

	// --------------------------------------
	// Estado
	// --------------------------------------

	@Lob
	@Column(name="PROPIEDADES_GLOBALES", nullable=true)
	private String propiedadesGlobales;
	
	@Lob
	@Column(name="GESTION_DEPENDENCIAS_GLOBALES", nullable=true)
	private String gestionDependenciasGlobales;

	@Lob
	@Column(name="GESTION_PLUGINS_GLOBALES", nullable=true)
	private String gestionPluginsGlobales;

	@Lob
	@Column(name="PLUGINS_GLOBALES", nullable=true)
	private String pluginsGlobales;

	@Lob
	@Column(name="PROPIEDADES_ADICIONALES", nullable=true)
	private String propiedadesAdicionales;
	
	@Lob
	@Column(name="DEPENDENCIAS_AD_PREVIAS", nullable=true)
	private String dependenciasAdicionalesPrevias;
	
	@Lob
	@Column(name="DEPENDENCIAS_AD_POSTEIORES", nullable=true)
	private String dependenciasAdicionalesPosteiores;
	
	@Lob
	@Column(name="PLUGINS", nullable=true)
	private String plugins;
	
	@Lob
	@Column(name="PERFILES", nullable=true)
	private String perfiles;
	
	@Lob
	@Column(name="CARPETAS", nullable=true)
	private String carpetas;


	// --------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------
	
	public String getPropiedadesGlobales() {
		return propiedadesGlobales;
	}

	public void setPropiedadesGlobales(String propiedadesGlobales) {
		this.propiedadesGlobales = propiedadesGlobales;
	}

	public String getGestionDependenciasGlobales() {
		return gestionDependenciasGlobales;
	}

	public void setGestionDependenciasGlobales(String gestionDependenciasGlobales) {
		this.gestionDependenciasGlobales = gestionDependenciasGlobales;
	}

	public String getGestionPluginsGlobales() {
		return gestionPluginsGlobales;
	}

	public void setGestionPluginsGlobales(String gestionPluginsGlobales) {
		this.gestionPluginsGlobales = gestionPluginsGlobales;
	}

	public String getPluginsGlobales() {
		return pluginsGlobales;
	}

	public void setPluginsGlobales(String pluginsGlobales) {
		this.pluginsGlobales = pluginsGlobales;
	}

	public String getPropiedadesAdicionales() {
		return propiedadesAdicionales;
	}

	public void setPropiedadesAdicionales(String propiedadesAdicionales) {
		this.propiedadesAdicionales = propiedadesAdicionales;
	}

	public String getDependenciasAdicionalesPrevias() {
		return dependenciasAdicionalesPrevias;
	}

	public void setDependenciasAdicionalesPrevias(String dependenciasAdicionalesPrevias) {
		this.dependenciasAdicionalesPrevias = dependenciasAdicionalesPrevias;
	}

	public String getDependenciasAdicionalesPosteiores() {
		return dependenciasAdicionalesPosteiores;
	}

	public void setDependenciasAdicionalesPosteiores(String dependenciasAdicionalesPosteiores) {
		this.dependenciasAdicionalesPosteiores = dependenciasAdicionalesPosteiores;
	}

	public String getPlugins() {
		return plugins;
	}

	public void setPlugins(String plugins) {
		this.plugins = plugins;
	}

	public String getPerfiles() {
		return perfiles;
	}

	public void setPerfiles(String perfiles) {
		this.perfiles = perfiles;
	}

	public String getCarpetas() {
		return carpetas;
	}

	public void setCarpetas(String carpetas) {
		this.carpetas = carpetas;
	}
	
}
