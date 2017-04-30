package es.nimio.nimiogcs.web.dto.f.artefactos.dependencias;

import javax.validation.constraints.Pattern;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="EDITAR", 
				nombre = "Editar relación con módulo web",
				orden=1, 
				textoDescripcion="Datos de la relación que se desea editar")
})
public class EditarDependenciaWeb {
	
	public EditarDependenciaWeb() {}

	public EditarDependenciaWeb(String idRelacion, String artefacto, String requerido) {
		this.idRelacion = idRelacion;
		this.artefacto = artefacto;
		this.requerido = requerido;
	}
	
	// ------------------
	// Estado
	// ------------------
	
	@Privado
	private String idRelacion;

	@GrupoAsociado(grupoContiene="EDITAR", ordenEnGrupo=1)
	@EtiquetaFormulario("Artefacto dependiente:")
	@Estatico
	private String artefacto;
	
	@GrupoAsociado(grupoContiene="EDITAR", ordenEnGrupo=2)
	@EtiquetaFormulario("Artefacto requerido:")
	@Estatico
	private String requerido;
	
	@GrupoAsociado(grupoContiene="EDITAR", ordenEnGrupo=3)
	@EtiquetaFormulario("Ruta de contexto:")
	@Placeholder("Contexto, p.e. /gcs")
	@BloqueDescripcion("Ruta de contexto con el que responderá el módulo web")
	private String rutaContexto;

	// ------------------
	// Lectura y escritura del estado
	// ------------------
	
	public String getIdRelacion() {
		return idRelacion;
	}

	public void setIdRelacion(String idRelacion) {
		this.idRelacion = idRelacion;
	}

	public String getArtefacto() {
		return artefacto;
	}

	public void setArtefacto(String artefacto) {
		this.artefacto = artefacto;
	}

	public String getRequerido() {
		return requerido;
	}

	public void setRequerido(String requerido) {
		this.requerido = requerido;
	}

	@Pattern(regexp="^\\/[a-z0-9]*$", message="La ruta debe comenzar con un separador y solo puede contener caracteres en minúscula y números.")
	public String getRutaContexto() {
		return rutaContexto;
	}

	public void setRutaContexto(String rutaContexto) {
		this.rutaContexto = rutaContexto;
	}
	
	
	
}
