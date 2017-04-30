package es.nimio.nimiogcs.web.dto.f.artefactos.dependencias;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="EDITAR", 
				nombre = "Editar relación de alcance",
				orden=1, 
				textoDescripcion="Datos de la relación que se desea editar")
})
public class EditarDependenciaAlcance {

	public EditarDependenciaAlcance() {}
	
	public EditarDependenciaAlcance(String idRelacion, String artefacto, String requerido) {
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
	@EtiquetaFormulario("Alcance:")
	@Seleccion(idColeccion="alcances")
	@BloqueDescripcion("Alcance de la relación entre artefactos")
	private String alcance;

	
	// ------------------
	// Lectura y escritura
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

	@NotNull
	@Size(min=1, message="Debe indicar el alcance")
	public String getAlcance() {
		return alcance;
	}

	public void setAlcance(String alcance) {
		this.alcance = alcance;
	}
	
	
}
