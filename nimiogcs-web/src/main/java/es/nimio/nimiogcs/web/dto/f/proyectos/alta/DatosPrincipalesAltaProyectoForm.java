package es.nimio.nimiogcs.web.dto.f.proyectos.alta;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

/**
 * Datos principales para el alta del proyecto
 */
@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="GENERAL", 
				nombre = "Configuración inicial",
				orden=1, 
				textoDescripcion="Configuración inicial que se quiere dar al proyecto en el proceso de alta"),
		
		@GrupoDeDatos(
				id="REPOSITORIO", 
				nombre = "Repositorio de proyecto",
				orden=2, 
				textoDescripcion="Repositorio donde se gestionará toda la configuración y uso del proyecto"),

		@GrupoDeDatos(
				id="PROYECTABLE", 
				nombre = "Tipo proyectable",
				orden=3, 
				textoDescripcion="En el alta se debe asociar el proyecto a un primer artefacto.")

})
public class DatosPrincipalesAltaProyectoForm {

	// ------------------------------
	// Estado
	// ------------------------------
	
	@GrupoAsociado(
			grupoContiene="GENERAL",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Nombre")
	private String nombre;
	
	@GrupoAsociado(
			grupoContiene="GENERAL",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Tipo uso inicial")
	@Seleccion(idColeccion="usos")	
	@BloqueDescripcion("En el alta del proyecto se debe seleccionar un primer uso o proyección, cuya estructura será creada como parte del alta. Desde el panel se podrán gestionar otras proyecciones.")
	private String proyeccion;

	@GrupoAsociado(
			grupoContiene="REPOSITORIO",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Repositorio")
	@Seleccion(idColeccion="repositorios")
	private String repositorio;
	
	@GrupoAsociado(
			grupoContiene="PROYECTABLE",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Tipo del artefacto")
	@Seleccion(idColeccion="proyectables")
	@BloqueDescripcion("En el alta del proyecto se debe seleccionar un primer artefacto con el que asociarlo. Una vez creado, se podrán asociar otros artefactos desde el panel de control.")
	private String proyectable;
	
	// ------------------------------
	// Lectura y escritura del estado
	// ------------------------------
	
	@NotNull(message="Debe indicar el nombre del proyecto")
	@Size(min=4, message="El tamaño mínimo para el nombre de un proyecto es de cuatro caracteres.")
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@NotNull(message="Debe indicar el tipo de uso que quiere emplear")
	@Size(min=1, message="Debe indicar el tipo de uso que quiere emplear")
	public String getProyeccion() {
		return this.proyeccion;
	}
	
	public void setProyeccion(String proyeccion) {
		this.proyeccion = proyeccion;
	}

	@NotNull(message="Debe indicarse el repositorio")
	@Size(min=1, message="Debe indicarse el repositorio")
	public String getRepositorio() {
		return repositorio;
	}
	
	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}
	
	@NotNull(message="Debe indicar el tipo de del artefacto proyectable")
	@Size(min=1, message="Debe indicar el tipo de del artefacto proyectable")
	public String getProyectable() {
		return proyectable;
	}

	public void setProyectable(String proyectable) {
		this.proyectable = proyectable;
	}
	
}
