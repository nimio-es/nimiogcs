package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccionMaven;
import es.nimio.nimiogcs.web.componentes.basicos.AreaPre;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceExterno;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

public final class PanelDirectivaProyeccionMaven extends PanelDirectivaBase<DirectivaProyeccionMaven> {

	public PanelDirectivaProyeccionMaven(DirectivaProyeccionMaven directiva) {
		super(directiva);
	}
	
	public PanelDirectivaProyeccionMaven(DirectivaProyeccionMaven directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaProyeccionMaven(DirectivaProyeccionMaven directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {

		this.conComponente(new Parrafo("Valores que se emplearán a la hora de construir las secciones del archivo pom.xml asociado:"));
		
		ponSeccion(directiva.getPropiedadesGlobales(), "Sección de propiedades del archivo padre (global)");
		ponSeccion(directiva.getPropiedadesAdicionales(), "Sección de propiedades del archivo pom.xml:");
		ponSeccion(directiva.getGestionDependenciasGlobales(), "Sección para gestión de dependencias en archivo padre (global)");
		ponSeccion(directiva.getDependenciasAdicionalesPrevias(), "Sección de dependencias, relación previa, archivo pom.xml:");
		ponSeccion(directiva.getDependenciasAdicionalesPosteiores(), "Sección de dependencias, relación posterior, archivo pom.xml:");
		ponSeccion(directiva.getGestionPluginsGlobales(), "Seccion gestión de plugins en archivo padre (global) pom.xml:");
		ponSeccion(directiva.getPlugins(), "Sección plugin del archivo pom.xml:");
		ponSeccion(directiva.getCarpetas(), "Subsección dentro de la sección build del archivo pom.xml que define las carpetas de código y de recursos:");
		ponSeccion(directiva.getPerfiles(), "Perfiles adicionales a definir en el archivo.pom.xml:");
		
		this.conComponentes(
				new Parrafo(" "),
				new ContinenteSinAspecto()
				.conComponentes(
						new TextoSimple("Usaremos sintaxis de ")
						.conLetraPeq()
						.info(),
						new EnlaceExterno()
						.conTexto("plantillas Velocity")
						.paraUrl("http://velocity.apache.org/engine/devel/translations/user-guide_es.html")
						.conLetraPeq()
						.info()
				)
				.enColumna(12)
		);
	}
	
	private void ponSeccion(String texto, String parrafo) {
		if(Strings.isNotEmpty(texto))
			this.conComponentes(
					new Parrafo(parrafo).enNegrita(),
					
					new AreaPre(texto)
			);
	}
}
