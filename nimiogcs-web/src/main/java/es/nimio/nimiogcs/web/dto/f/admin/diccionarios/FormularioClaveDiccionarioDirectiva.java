package es.nimio.nimiogcs.web.dto.f.admin.diccionarios;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;

@GruposDeDatos(
		grupos = @GrupoDeDatos(id="DATOS", nombre="Datos principales", orden=1, textoDescripcion="Datos que identifican al parámetro del diccionario.")
)
public class FormularioClaveDiccionarioDirectiva implements Serializable {

	private static final long serialVersionUID = 4931479550181601863L;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Clave")
	@BloqueDescripcion("Clave con la que se almacenará el valor")
	private String clave;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Etiqueta de formulario")
	@BloqueDescripcion("Etiqueta que se mostrará al usuario cuando se solicite un valor para este parámetro")
	private String etiqueta;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Posición")
	@BloqueDescripcion("Posición u orden en el que se mostrará este parámetro cuando se solicite al usuario")
	private String posicion;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=4)
	@EtiquetaFormulario("Patrón de control")
	@Placeholder("Expresión regular, p.e. '^[a-z]*$")
	@BloqueDescripcion("Expresión regular con la que se podrá controlar los valores que se introducen y el formarto de los mismos")
	private String patronControl;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=5)
	@EtiquetaFormulario("Bloque descripción")
	@AreaTexto
	@BloqueDescripcion("Descripción que se quiere mostrar al usuario cuando vaya a introducir la información")
	private String bloqueDescripcion;

	// -----
	
	@NotNull(message="El valor para la clave debe estar definido.")
	@Size(min=5, max=30, message="El tamaño de la clave debe estar comprendido entre los 5 y los 30 caracteres")
	@Pattern(regexp="^[A-Z]{1}[A-Z0-9_]*$", message="La clave debe construirse empleando caracteres alfanuméricos y el guión bajo")
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	@NotNull(message="El valor para la etiqueta de formulario debe estar definido.")
	@Size(min=2, max=40, message="El tamaño de la etiqueta de formulario debe estar comprendido entre los 2 y los 40 caracteres")
	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@NotNull(message="El valor para la posición no puede quedar vacío.")
	@Pattern(regexp="^[1-9]{1}[0-9]{0,2}$", message="La posición debe ser un valor comprendido entre 1 y 99")
	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String getPatronControl() {
		return patronControl;
	}

	public void setPatronControl(String patronControl) {
		this.patronControl = patronControl;
	}
	
	public String getBloqueDescripcion() {
		return bloqueDescripcion;
	}
	
	public void setBloqueDescripcion(String descripcion) {
		bloqueDescripcion = descripcion;
	}
	
}
