package es.nimio.nimiogcs.web.dto.f.directivas;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@EsExtension
public class FormularioDirectivaEstrategia extends FormularioBaseDirectiva {

	private static final long serialVersionUID = 8545190477719298953L;
	
	private static final String TEXTO_DIRECTIVA = "Estrategia de evolución para todos los artefactos del tipo.";

	public FormularioDirectivaEstrategia() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Estrategia evolución")
	@Seleccion(idColeccion="estrategias")
	@BloqueDescripcion("Estrategia de evolución que se empleará en todos los artefactos que se vinculen a este tipo.")
	private String estrategia;

	
	// -------------------
	
	public String getEstrategia() {
		return estrategia;
	}

	public void setEstrategia(String estrategia) {
		this.estrategia = estrategia;
	}

	
	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// En este caso no aplica
	}
	
	@Override
	public DirectivaEstrategiaEvolucion nueva(IContextoEjecucion ce) {
		DirectivaEstrategiaEvolucion nueva = new DirectivaEstrategiaEvolucion();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaEstrategiaEvolucion o = (DirectivaEstrategiaEvolucion)original;
		o.setEstrategia(this.estrategia);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaEstrategiaEvolucion d = (DirectivaEstrategiaEvolucion)directiva;
		setEstrategia(d.getEstrategia());
	}
}
