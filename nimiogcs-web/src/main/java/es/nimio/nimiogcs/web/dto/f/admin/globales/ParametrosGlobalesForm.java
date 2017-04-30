package es.nimio.nimiogcs.web.dto.f.admin.globales;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.NoEditable;


@GruposDeDatos(grupos={

		@GrupoDeDatos(
				id="DATOS", 
				nombre="Globales", 
				orden=1, 
				textoDescripcion="Datos Globales.")
       })

public class ParametrosGlobalesForm {

	public ParametrosGlobalesForm() {}
	
	public ParametrosGlobalesForm(String id) { 
		idGlobal = id;
	}
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Identificador")
	@BloqueDescripcion("Introduzca el identificador Global")
	@NoEditable
	private String idGlobal;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Contenido")
	@BloqueDescripcion("Introduzca el contenido Global")
	@AreaTexto(filas=20)
	private String contenido;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Descripcion")
	@BloqueDescripcion("Introduzca una pequeña descripción")
	@NoEditable
	private String descripcion;

	@NotNull
	@Size(min=1, message="Por favor,el identificador de los parametros globales no puede ir vacio")
	public String getIdGlobal(){
		return idGlobal;
	}

	public void setIdGlobal(String idGlobal) {
		this.idGlobal = idGlobal;
	}

	@NotNull
	@Size(min=1, message="Por favor, dedique unas palabras para indicar la causa por la que se cambia el estado de la operación")
	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	@NotNull
	@Size(min=1, message="Por favor, dedique unas palabras para indicar la causa por la que se cambia el estado de la operación")
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public ParametrosGlobalesForm desde(ParametroGlobal datos, Map<String, Collection<NombreDescripcion>> diccionarios) {
		
		this.idGlobal = datos.getId();
		this.contenido = datos.getContenido();
		this.descripcion= datos.getDescripcion();
		
	
		return this;
	}

}
