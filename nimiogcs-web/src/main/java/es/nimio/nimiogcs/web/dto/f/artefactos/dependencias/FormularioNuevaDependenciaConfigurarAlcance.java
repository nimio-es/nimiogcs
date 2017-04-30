package es.nimio.nimiogcs.web.dto.f.artefactos.dependencias;

import java.io.Serializable;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="ALCANCE", 
				nombre = "Alcance del artefacto",
				orden=1, 
				textoDescripcion="Elección del alcance que se usará en la relación")
})
public class FormularioNuevaDependenciaConfigurarAlcance implements Serializable {

	private static final long serialVersionUID = 5290941957102856402L;

	public FormularioNuevaDependenciaConfigurarAlcance() {}
	
	public FormularioNuevaDependenciaConfigurarAlcance(Artefacto artefacto, Artefacto idElegido) {
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
			grupoContiene="ALCANCE",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Alcance")
	@Seleccion(idColeccion="alcances")	
	@BloqueDescripcion("Elija el alcance que quiere emplear")
	private String alcance;

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

	public String getAlcance() {
		return alcance;
	}

	public void setAlcance(String alcance) {
		this.alcance = alcance;
	}
	
	
}
