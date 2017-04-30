package es.nimio.nimiogcs.web.dto.f.artefactos.dependencias;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="CFN", 
				nombre = "Confirmar eliminación",
				orden=1, 
				textoDescripcion="Datos de la relación que se desea eliminar")
})
public class ConfirmarEliminarDependencia {

	public ConfirmarEliminarDependencia() {}

	public ConfirmarEliminarDependencia(
			String idRelacion, 
			String artefacto, 
			String requerido,
			String tipoRelacion,
			String valor) {
		this.idRelacion = idRelacion;
		this.artefacto = artefacto;
		this.requerido = requerido;
		this.tipo = tipoRelacion;
		this.valor = valor;
	}
	
	// ------------------
	// Estado
	// ------------------
	
	@Privado
	private String idRelacion;

	@GrupoAsociado(grupoContiene="CFN", ordenEnGrupo=1)
	@EtiquetaFormulario("Artefacto dependiente")
	@Estatico
	private String artefacto;
	
	@GrupoAsociado(grupoContiene="CFN", ordenEnGrupo=2)
	@EtiquetaFormulario("Artefacto requerido")
	@Estatico
	private String requerido;
	
	@GrupoAsociado(grupoContiene="CFN", ordenEnGrupo=3)
	@EtiquetaFormulario("Tipo relación")
	@Estatico
	private String tipo;
	
	@GrupoAsociado(grupoContiene="CFN", ordenEnGrupo=4)
	@EtiquetaFormulario("Valor relación")
	@Estatico
	private String valor;

	// ---------
	// Lectura y escritura
	// ---------
	
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
