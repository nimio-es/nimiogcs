package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionJenkins;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaPublicacionJenkins extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = -8911653494697434513L;

	private static final String TEXTO_DIRECTIVA = "Parámetros para la configuración usando Jenkins";

	public FormularioDirectivaPublicacionJenkins() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Tarea")
	@BloqueDescripcion("Nombre de la tarea Jenkins que se invocará para desplegar el artefacto.")
	private String tarea;

	// -------------------
	
	@Size(min=3, max=100, message="La longitud del nombre de la tarea debe estar entre 3 y 100 caracteres.")
	@Pattern(
			regexp="^[a-zA-Z0-9-_]+$", 
			message="Debe tener un nombre adecuado."
	)
	public String getTarea() {
		return tarea;
	}

	public void setTarea(String tarea) {
		this.tarea = tarea;
	}

	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// NADA QUE VALIDAR
	}

	@Override
	public DirectivaPublicacionJenkins nueva(IContextoEjecucion ce) {
		DirectivaPublicacionJenkins nueva = new DirectivaPublicacionJenkins();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaPublicacionJenkins o = (DirectivaPublicacionJenkins)original;
		o.setTarea(tarea);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaPublicacionJenkins d = (DirectivaPublicacionJenkins)directiva;
		setTarea(d.getTarea());
	}
}
