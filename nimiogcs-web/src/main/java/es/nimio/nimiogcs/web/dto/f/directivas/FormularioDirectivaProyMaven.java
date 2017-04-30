package es.nimio.nimiogcs.web.dto.f.directivas;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccionMaven;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaProyMaven extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = -3520159495144134629L;

	private static final String TEXTO_DIRECTIVA = "Características de la proyección Maven específicas del tipo y que se aplicará a todos los artefactos asociados.";

	public FormularioDirectivaProyMaven() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Gestión dependencias")
	@AreaTexto
	@BloqueDescripcion("Gestión especial para dependencias del tipo. Sección 'DependencyManagement' del archivo pom.xml padre")
	private String gestionDependenciasGlobales;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Gestión de plugins")
	@AreaTexto
	@BloqueDescripcion("Gestión de plugins para el tipo. Sección 'pluginManagement' del archivo pom.xml del padre")
	private String gestionPluginsGlobales;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=4)
	@EtiquetaFormulario("Propiedades")
	@AreaTexto
	@BloqueDescripcion("Conjunto de propiedades que se añadirán en la sección de propiedades del archivo pom.xml. Sección 'Properties' del archivo pom.xml del módulo")
	private String propiedades;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=5)
	@EtiquetaFormulario("Dependencias (lista previa)")
	@AreaTexto
	@BloqueDescripcion("Lista de dependencias a añadir siempre antes de las propias definidas en el artefacto. Sección 'dependencies' del archivo pom.xml del módulo")
	private String dependenciasAdicionalesPrevias;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=6)
	@EtiquetaFormulario("Dependencias (lista posterior)")
	@AreaTexto
	@BloqueDescripcion("Lista de dependencias a añadir siempre después de las propias definidas en el artefacto. Sección 'dependencies' del archivo pom.xml del módulo")
	private String dependenciasAdicionalesPosteiores;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=7)
	@EtiquetaFormulario("Plugins")
	@AreaTexto
	@BloqueDescripcion("Plugins para el tipo de artefacto que afectará a la forma en que se construyen todos los artefactos asociados. Sección 'plugins' del archivo pom.xml del módulo")
	private String plugins;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=8)
	@EtiquetaFormulario("Descripción rutas carpetas")
	@AreaTexto
	@BloqueDescripcion("Las carpetas de código fuente y de recursos y las políticas de inclusión/exclusión cuando no apliquen los valores por defecto.")
	private String carpetas;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=9)
	@EtiquetaFormulario("Perfiles")
	@AreaTexto
	@BloqueDescripcion("Definición de perfiles para otros usos que se quiera ofrecer en los artefactos de un tipo de artefacto. Sección 'profiles' del archivo pom.xml del módulo")
	private String perfiles;


	
	// -------------------
	
	public String getPropiedades() {
		return propiedades;
	}

	public void setPropiedades(String propiedades) {
		this.propiedades = propiedades;
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


	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// En este caso no aplica
	}

	@Override
	public DirectivaProyeccionMaven nueva(IContextoEjecucion ce) {
		DirectivaProyeccionMaven nueva = new DirectivaProyeccionMaven();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaProyeccionMaven o = (DirectivaProyeccionMaven)original;
		o.setGestionDependenciasGlobales(this.gestionDependenciasGlobales);
		o.setGestionPluginsGlobales(this.gestionPluginsGlobales);
		o.setPropiedadesAdicionales(this.propiedades);
		o.setDependenciasAdicionalesPrevias(this.dependenciasAdicionalesPrevias);
		o.setDependenciasAdicionalesPosteiores(this.dependenciasAdicionalesPosteiores);
		o.setPlugins(this.plugins);
		o.setCarpetas(this.carpetas);
		o.setPerfiles(this.perfiles);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {

		DirectivaProyeccionMaven d = (DirectivaProyeccionMaven)directiva;
		setGestionDependenciasGlobales(d.getGestionDependenciasGlobales());
		setGestionPluginsGlobales(d.getGestionPluginsGlobales());
		setPropiedades(d.getPropiedadesAdicionales());
		setDependenciasAdicionalesPrevias(d.getDependenciasAdicionalesPrevias());
		setDependenciasAdicionalesPosteiores(d.getDependenciasAdicionalesPosteiores());
		setPlugins(d.getPlugins());
		setCarpetas(d.getCarpetas());
		setPerfiles(d.getPerfiles());
	}
}
