package es.nimio.nimiogcs.web.dto.f.proyectos.etiquetas;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="GENERAL", 
				nombre = "Nombre de etiqueta",
				orden=1, 
				textoDescripcion="Elija el nombre con el que se etiquetará"
		)
})
public class NuevaEtiquetaForm {

	public NuevaEtiquetaForm() {}

	public NuevaEtiquetaForm(String control, String idProyecto, String protoNombre) {
		this.control = control;
		this.idProyecto = idProyecto;
		this.nombre = protoNombre;
	}

	// ------------------------------
	// Estado
	// ------------------------------

	@Privado
	private String control;
	
	@Privado
	private String idProyecto;
	
	@GrupoAsociado(
			grupoContiene="GENERAL",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Nombre")
	@BloqueDescripcion("Debe tener en cuenta que al nombre que se elija para la etiqueta, se antepondrá el propio nombre del proyecto.")
	private String nombre;

	// ------------------------------
	// Lectura y escritura del estado
	// ------------------------------

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}
	
	public String getIdProyecto() {
		return idProyecto;
	}
	
	public void setIdProyecto(String idProyecto) {
		this.idProyecto = idProyecto;
	}

	@NotNull(message="Debe indicar el nombre")
	@Size(min=5, max=20, message="El nombre de la etiqueta debe tener un mínimo de 5 caracteres y un máximo de 20")
	@Pattern(regexp = "[A-Za-z0-9-]*", message="El nombre de la etiqueta solo puede contener caracteres alganuméricos y guiones")	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
