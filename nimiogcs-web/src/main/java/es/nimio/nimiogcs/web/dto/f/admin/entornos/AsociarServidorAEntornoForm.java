package es.nimio.nimiogcs.web.dto.f.admin.entornos;

import javax.validation.constraints.NotNull;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.EntornoConServidores;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

/**
 * Formulario para asociar un servidor a un entorno determinado.
 */
@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="SERVIDOR", 
				nombre = "Selección del servidor",
				orden=1, 
				textoDescripcion=""),
})
public class AsociarServidorAEntornoForm {

	// ---------------------------------------------------
	// Estado
	// ---------------------------------------------------
	
	@Privado
	private String idEntorno;
	
	@GrupoAsociado(
			grupoContiene="SERVIDOR",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Servidor")
	@Seleccion(idColeccion="servidores")
	@BloqueDescripcion("Servidor que se asociará.")
	private String servidor;

	// ---------------------------------------------------
	// Lectura y escritura
	// ---------------------------------------------------

	public String getIdEntorno() {
		return idEntorno;
	}
	
	public void setIdEntorno(String id) {
		idEntorno = id;
	}
	
	@NotNull
	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}
	
	
	// ---------------------------------------------------
	// Fluido
	// ---------------------------------------------------

	public AsociarServidorAEntornoForm paraEntorno(EntornoConServidores entorno) {
		setIdEntorno(entorno.getId());
		return this;
	}
}
