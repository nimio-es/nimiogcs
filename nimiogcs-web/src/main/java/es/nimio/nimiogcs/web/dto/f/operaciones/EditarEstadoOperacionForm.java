package es.nimio.nimiogcs.web.dto.f.operaciones;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;


@GruposDeDatos(grupos={

		@GrupoDeDatos(
				id="DATOS", 
				nombre="Informar del error", 
				orden=1, 
				textoDescripcion="Introduzca el mensaje de error que se quiere registrar al cambiar el estado de la operación.")
       })

public class EditarEstadoOperacionForm {

	
	public EditarEstadoOperacionForm() {}
	
	public EditarEstadoOperacionForm(String id) { 
		idOperacion = id;
	}
	
	@Privado
	private String idOperacion;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Mensaje error:")
	@BloqueDescripcion("Puede indicar el mensaje de error o la causa por la que ha cambiado el estado de esta operación")
	private String texto;

	public String getIdOperacion() {
		return idOperacion;
	}

	public void setIdOperacion(String idOperacion) {
		this.idOperacion = idOperacion;
	}

	@NotNull
	@Size(min=10, message="Por favor, dedique unas palabras para indicar la causa por la que se cambia el estado de la operación")
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
}
