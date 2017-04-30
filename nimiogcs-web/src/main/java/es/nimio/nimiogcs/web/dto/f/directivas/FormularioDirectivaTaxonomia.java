package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaTaxonomia extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = -8911653494697434513L;

	private static final String TEXTO_DIRECTIVA = "Taxonomía donde se clasificará el artefacto";

	public FormularioDirectivaTaxonomia() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Taxonomía")
	@BloqueDescripcion("Taxonomía en la que se clasifica el artefacto. Caracteres en minúsculas separados por la barra de separación '/'.")
	private String taxonomia;

	// -------------------
	
	@Size(min=3, max=150, message="La longitud de una taxonomía no debe ser inferior a tres caracteres ni superior a ciento cincuenta.")
	@Pattern(regexp="^[a-z]+(\\/[a-z]+)*$", message="La taxonomía únicamente puede estar compuesta por caracteres alfabéticos separados por la barra '/'")
	public String getTaxonomia() {
		return taxonomia;
	}

	public void setTaxonomia(String taxonomia) {
		this.taxonomia = taxonomia;
	}

	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// NADA QUE VALIDAR
	}

	@Override
	public DirectivaTaxonomia nueva(IContextoEjecucion ce) {
		DirectivaTaxonomia nueva = new DirectivaTaxonomia();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaTaxonomia o = (DirectivaTaxonomia)original;
		o.setTaxonomia(taxonomia);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaTaxonomia d = (DirectivaTaxonomia)directiva;
		setTaxonomia(d.getTaxonomia());
	}
}
