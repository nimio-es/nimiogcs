package es.nimio.nimiogcs.web.dto.f.proyectos;

import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="Anotacion", 
				nombre = "Observaciones y comentarios",
				orden=1, 
				textoDescripcion="Página en blanco en la que escribir lo que se considere oportuno y que sea reseñable de cara a la gestión del proyecto."),
})
public class FormularioEditarAnotaciones {

	public FormularioEditarAnotaciones() {}

	public FormularioEditarAnotaciones(Proyecto proyecto) {
		idProyecto = proyecto.getId();
		markdown = proyecto.getAnotaciones();
	}

	// ---
	
	@Privado
	private String idProyecto;
	
	@GrupoAsociado(grupoContiene="Anotacion", ordenEnGrupo=1)
	@EtiquetaFormulario("Texto")
	@AreaTexto(filas=20)
	@BloqueDescripcion("Introduzca el contenido siguiendo el formato Markdown para generar un contenido HTML.")
	private String markdown;

	// ---
	
	
	public String getIdProyecto() {
		return idProyecto;
	}

	public void setIdProyecto(String idProyecto) {
		this.idProyecto = idProyecto;
	}

	public String getMarkdown() {
		return markdown;
	}

	public void setMarkdown(String markdown) {
		this.markdown = markdown;
	}
	
	
	
}
