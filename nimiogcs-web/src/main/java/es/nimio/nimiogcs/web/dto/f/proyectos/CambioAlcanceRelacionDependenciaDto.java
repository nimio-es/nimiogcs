package es.nimio.nimiogcs.web.dto.f.proyectos;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@GruposDeDatos(grupos={
		@GrupoDeDatos(id="PROYECTO", nombre="Proyecto y artefacto", orden=1, textoDescripcion="Proyecto y artefacto por el proyecto para los que se quiere modificar el alcancel de la relación en la dependencia."),
		@GrupoDeDatos(id="DEPENDENCIA", nombre="Alcance de la dependencia", orden=2, textoDescripcion="Datos de la dependencia y su alcance.")
})
public final class CambioAlcanceRelacionDependenciaDto {

	// -----------------------------------------
	// Estado: Datos del formulario
	// -----------------------------------------
	
	@Privado
	private String idProyecto;

	@Privado
	private String idRelacion;
	
	@Privado
	private String idDependencia;

	@GrupoAsociado(
			grupoContiene="PROYECTO",
			ordenEnGrupo=1)
	@Estatico
	@EtiquetaFormulario("Nombre del proyecto")
	private String nombreProyecto;
	
	@GrupoAsociado(			
			grupoContiene="PROYECTO",
			ordenEnGrupo=1)
	@Estatico
	@EtiquetaFormulario("Artefacto afectado")
	private String nombreArtefactoAfectado;

	@GrupoAsociado(
			grupoContiene="DEPENDENCIA",
			ordenEnGrupo=1
			)
	@Estatico
	@EtiquetaFormulario("Artefacto depende")
	private String nombreArtefactoDependencia;
	
	@GrupoAsociado(
			grupoContiene="DEPENDENCIA",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Alcance del artefacto")
	@Seleccion(idColeccion="alcances")
	private String idEnumAlcance;

	// -----------------------------------------
	// Lectura / Escritura del estado
	// -----------------------------------------

	public String getIdProyecto() {
		return idProyecto;
	}

	public void setIdProyecto(String idProyecto) {
		this.idProyecto = idProyecto;
	}

	public String getIdRelacion() {
		return idRelacion;
	}

	public void setIdRelacion(String idRelacion) {
		this.idRelacion = idRelacion;
	}

	public String getIdDependencia() {
		return idDependencia;
	}

	public void setIdDependencia(String idDependencia) {
		this.idDependencia = idDependencia;
	}

	public String getNombreProyecto() {
		return nombreProyecto;
	}

	public void setNombreProyecto(String nombreProyecto) {
		this.nombreProyecto = nombreProyecto;
	}

	public String getNombreArtefactoAfectado() {
		return nombreArtefactoAfectado;
	}

	public void setNombreArtefactoAfectado(String nombreArtefactoAfectado) {
		this.nombreArtefactoAfectado = nombreArtefactoAfectado;
	}

	public String getNombreArtefactoDependencia() {
		return nombreArtefactoDependencia;
	}

	public void setNombreArtefactoDependencia(String nombreArtefactoDependencia) {
		this.nombreArtefactoDependencia = nombreArtefactoDependencia;
	}

	public String getIdEnumAlcance() {
		return idEnumAlcance;
	}

	public void setIdEnumAlcance(String idEnumAlcance) {
		this.idEnumAlcance = idEnumAlcance;
	}

}
