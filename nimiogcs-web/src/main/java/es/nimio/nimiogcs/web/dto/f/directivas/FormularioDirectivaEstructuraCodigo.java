package es.nimio.nimiogcs.web.dto.f.directivas;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstructuraCodigo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaEstructuraCodigo extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = 4457087553856716578L;
	
	private static final String TEXTO_DIRECTIVA = "Carpetas que se crearán como estructura inicial de código";

	public FormularioDirectivaEstructuraCodigo() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Carpetas")
	@AreaTexto
	@BloqueDescripcion("Carpetas que se crearán al dar de alta un artefacto. Usar el carácter '/' para separar las subcarpetas.")
	private String carpetas;

	// -------------------

	public String getCarpetas() {
		return carpetas;
	}

	public void setCarpetas(String carpetas) {
		this.carpetas = carpetas;
	}

	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// NADA QUE VALIDAR
	}

	@Override
	public DirectivaEstructuraCodigo nueva(IContextoEjecucion ce) {
		DirectivaEstructuraCodigo nueva = new DirectivaEstructuraCodigo();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaEstructuraCodigo o = (DirectivaEstructuraCodigo)original;
		o.setCarpetas(carpetas);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaEstructuraCodigo d = (DirectivaEstructuraCodigo)directiva;
		setCarpetas(d.getCarpetas());
	}
}
