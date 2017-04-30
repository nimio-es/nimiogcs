package es.nimio.nimiogcs.web.dto.f.directivas;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@EsExtension
public class FormularioDirectivaInventario extends FormularioBaseDirectiva {

	private static final long serialVersionUID = -1287362410337325363L;

	private static final String TEXTO_DIRECTIVA = "Aplicación empresarial a la que asociar el artefacto";

	public FormularioDirectivaInventario() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}

	// ---------------------------------

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Aplicación")
	@Seleccion(idColeccion="aplicaciones")
	@BloqueDescripcion("Aplicación a la que se asociará el artefacto")
	private String aplicacion;
	
	// ---------------------------------
	
	public String getAplicacion() {
		return aplicacion;
	}
	
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	
	// ---------------------------------

	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// NADA QUE HACER
	}

	@Override
	public DirectivaInventario nueva(IContextoEjecucion ce) {
		DirectivaInventario nueva = new DirectivaInventario();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaInventario o = (DirectivaInventario)original;
		o.setAplicacion(ce.aplicaciones().findOne(aplicacion));
	}

	@Override
	public void datosDesde(DirectivaBase directiva) {
		this.aplicacion = ((DirectivaInventario)directiva).getAplicacion().getId();
	}
	
}
