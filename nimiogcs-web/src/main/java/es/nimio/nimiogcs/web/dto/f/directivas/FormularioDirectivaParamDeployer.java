package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaParametrosDeployer;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaParamDeployer extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = 331457645001681044L;

	private static final String TEXTO_DIRECTIVA = "Parámetros del artefacto para la publicación usando el canal de publicaciones Deployer.";

	public FormularioDirectivaParamDeployer() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Ruta")
	@BloqueDescripcion("Ruta que se facilitará en la petición a Deployer")
	private String ruta;
	
	// -------------------

	@NotNull(message="Debe indicar un valor para la ruta")
	@Size(min=1, max=40, message="Debe indicar un valor cuya longitud no supere los cuarenta caracteres")
	@Pattern(regexp="^[a-zA-Z0-9]+(\\/[a-zA-Z0-9-]+)*$", message="Debe indicar una ruta válida")
	public String getRuta() {
		return ruta;
	}
	
	public void setRuta(String comportamiento) {
		this.ruta = comportamiento;
	}

	// -----------------------

	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// La validación propia de los campos
	}
	
	@Override
	public DirectivaParametrosDeployer nueva(IContextoEjecucion ce) {
		DirectivaParametrosDeployer nueva = new DirectivaParametrosDeployer();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaParametrosDeployer o =(DirectivaParametrosDeployer)original;
		o.setPathInRequest(ruta);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaParametrosDeployer d = (DirectivaParametrosDeployer)directiva;
		setRuta(d.getPathInRequest());
	}
}
