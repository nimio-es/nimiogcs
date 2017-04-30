package es.nimio.nimiogcs.web.dto.f.admin.diccionarios;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.NoEditable;

@GruposDeDatos(
		grupos = @GrupoDeDatos(id="DATOS", nombre="Datos principales", orden=1, textoDescripcion="Datos que identifican al diccionario.")
)
public class FormularioDiccionarioDirectiva implements Serializable {

	private static final long serialVersionUID = 7345908030920127117L;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@NoEditable
	@EtiquetaFormulario("Identificador")
	@BloqueDescripcion("El identificador del diccionario, que será único para todos los diccionarios que se hayan registrado.")
	private String id;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Descripción")
	@BloqueDescripcion("Descripción o nombre del diccionario que facilitará comprender para qué casos se empleará.")
	private String descripcion;

	
	// ---
	
	@NotNull(message="El identificador no puede dejarse vacío.")
	@Size(min=6, max=20, message="El tamaño del identificador debe estar comprendido entre los 6 y los 20 caracteres.")
	@Pattern(regexp="^[a-zA-Z]{1}[a-zA-Z0-9]*$", message="El formato de un identificador debe ser empezando por un carácter alfabético y continuando con caracteres alfanuméricos.")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@NotNull(message="La descripción no puede quedar vacía.")
	@Size(min=6, max=60, message="El tamaño para la descripción debe estar comprendido entre los 6 y los 60 caracteres.")
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
