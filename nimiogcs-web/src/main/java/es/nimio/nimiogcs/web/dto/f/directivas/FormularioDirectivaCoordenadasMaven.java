package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@EsExtension
public class FormularioDirectivaCoordenadasMaven extends FormularioBaseDirectiva {

	private static final long serialVersionUID = -390441324583168342L;

	private static final String TEXTO_DIRECTIVA = "Coordenadas Maven que aplican al artefacto en su clasificación como tal.";

	public FormularioDirectivaCoordenadasMaven() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// ----------------------

	@GrupoAsociado(grupoContiene = "DATOS", ordenEnGrupo = 2)
	@EtiquetaFormulario("Grupo")
	@BloqueDescripcion("Grupo del Artefacto ")
	private String idGrupo;

	@GrupoAsociado(grupoContiene = "DATOS", ordenEnGrupo = 3)
	@EtiquetaFormulario("Artefacto")
	@BloqueDescripcion("Identificador del Artefacto ")
	private String idArtefacto;

	@GrupoAsociado(grupoContiene = "DATOS", ordenEnGrupo = 4)
	@EtiquetaFormulario("Version")
	@BloqueDescripcion("Versión del Artefacto")
	private String version;

	@GrupoAsociado(grupoContiene = "DATOS", ordenEnGrupo = 5)
	@EtiquetaFormulario("Clasificador")
	@BloqueDescripcion("Clasificador del Artefacto si fuese necesario")
	private String clasificador;

	@GrupoAsociado(grupoContiene = "DATOS", ordenEnGrupo = 6)
	@EtiquetaFormulario("Empaquetado")
	@BloqueDescripcion("Seleccione el tipo que quiere que siga el artefacto.")
	@Seleccion(idColeccion = "empaquetados")
	private String empaquetado;
	
	// ----------------------

	@Size(min=1, message="Debe indicarse un id de grupo.")
	@Pattern(regexp = "[A-Za-z0-9 -._]*", message="El id de grupo no cumple con las restricciones de formato.")
	public String getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}

	@Size(min=1, message="Debe indicarse un id de artefacto.")
	@Pattern(regexp = "[A-Za-z0-9 -._]*", message="El id de artefacto no cumple con las restricciones de formato.")
	public String getIdArtefacto() {
		return idArtefacto;
	}

	public void setIdArtefacto(String idArtefacto) {
		this.idArtefacto = idArtefacto;
	}

	@NotNull
	@Size(min=1, message="Debe indicarse la versión del artefacto.")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClasificador() {
		return clasificador;
	}

	public void setClasificador(String clasificador) {
		this.clasificador = clasificador;
	}

	@NotNull(message = "Debe indicar el tipo elegido.")
	@Size(min = 1, message = "Debe indicar el tipo elegido.")
	public String getEmpaquetado() {
		return empaquetado;
	}

	public void setEmpaquetado(String tipo) {
		this.empaquetado = tipo;
	}

	// ----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// TODO: Nada que validar
	}

	@Override
	public DirectivaCoordenadasMaven nueva(IContextoEjecucion ce) {
		DirectivaCoordenadasMaven nueva = new DirectivaCoordenadasMaven();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaCoordenadasMaven o = (DirectivaCoordenadasMaven)original;
		o.setIdGrupo(idGrupo);
		o.setIdArtefacto(idArtefacto);
		o.setVersion(version);
		o.setEmpaquetado(empaquetado);
		o.setClasificador(clasificador);
	}

	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaCoordenadasMaven d = (DirectivaCoordenadasMaven)directiva;
		idGrupo = d.getIdGrupo();
		idArtefacto = d.getIdArtefacto();
		version = d.getVersion();
		clasificador = d.getClasificador();
		empaquetado = d.getEmpaquetado();
	}

}
