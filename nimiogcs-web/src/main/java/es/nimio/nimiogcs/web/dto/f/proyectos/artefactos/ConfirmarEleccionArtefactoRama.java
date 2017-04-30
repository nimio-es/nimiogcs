package es.nimio.nimiogcs.web.dto.f.proyectos.artefactos;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(
		grupos={
				@GrupoDeDatos(
						id="DATOS",
						nombre="Confirmar operación",
						orden=1
				)
		}
)
public class ConfirmarEleccionArtefactoRama {

	public ConfirmarEleccionArtefactoRama() {}

	public ConfirmarEleccionArtefactoRama(String id, String nombre) {
		idArtefacto = id;
		artefacto = nombre;
	}

	
	@Privado
	private String idArtefacto;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Artefacto")
	@Estatico
	private String artefacto;

	public String getIdArtefacto() {
		return idArtefacto;
	}

	public void setIdArtefacto(String idArtefacto) {
		this.idArtefacto = idArtefacto;
	}

	public String getArtefacto() {
		return artefacto;
	}

	public void setArtefacto(String artefacto) {
		this.artefacto = artefacto;
	}
	
	
	
}
