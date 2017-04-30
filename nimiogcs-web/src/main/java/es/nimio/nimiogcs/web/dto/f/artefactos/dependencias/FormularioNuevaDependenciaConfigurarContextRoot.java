package es.nimio.nimiogcs.web.dto.f.artefactos.dependencias;

import java.io.Serializable;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="CTX", 
				nombre = "Ruta de contexto",
				orden=1, 
				textoDescripcion="Ruta de contexto dentro del EAR al que se asocie el módulo web")
})
public class FormularioNuevaDependenciaConfigurarContextRoot implements Serializable {

	private static final long serialVersionUID = 7592717211067659538L;

	public FormularioNuevaDependenciaConfigurarContextRoot() {}
	
	public FormularioNuevaDependenciaConfigurarContextRoot(Artefacto artefacto, Artefacto idElegido) {
		this.idArtefacto = artefacto.getId();
		this.idElegido = idElegido.getId(); 
	}
	
	// ---------------------
	// Estado
	// ---------------------
	
	@Privado
	private String idArtefacto;
	
	@Privado
	private String idElegido;

	@GrupoAsociado(
			grupoContiene="CTX",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Ruta de contexto")
	@BloqueDescripcion("Debe indicar una ruta de contexto para el módulo web dentro del EAR")
	private String rutaDeContexto;

	// ---------------------
	// Lectura y escritura del estado
	// ---------------------
	
	public String getIdArtefacto() {
		return idArtefacto;
	}

	public void setIdArtefacto(String idArtefacto) {
		this.idArtefacto = idArtefacto;
	}
	
	public String getIdElegido() {
		return idElegido;
	}

	public void setIdElegido(String idElegido) {
		this.idElegido = idElegido;
	}

	public String getRutaDeContexto() {
		return rutaDeContexto;
	}

	public void setRutaDeContexto(String alcance) {
		this.rutaDeContexto = alcance;
	}
	
}
