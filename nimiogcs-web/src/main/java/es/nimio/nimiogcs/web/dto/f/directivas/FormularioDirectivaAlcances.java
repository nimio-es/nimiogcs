package es.nimio.nimiogcs.web.dto.f.directivas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Checkbox;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaAlcances extends FormularioBaseDirectiva {

	private static final long serialVersionUID = -4182311464939314071L;
	
	private static final String TEXTO_DIRECTIVA = "Definición de alcances autorizados para el uso del artefacto.";

	public FormularioDirectivaAlcances() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}

	
	// ------------------------------
	
	@GrupoAsociado(
			grupoContiene="DATOS",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Alcances autorizados")
	@Checkbox(texto="Compilar y empaquetar")
	private boolean compilarYEmpaquetar;
	
	@GrupoAsociado(
			grupoContiene="DATOS",
			ordenEnGrupo=3)
	@Checkbox(texto="Compilar sin empaquetar")
	private boolean compilarSinEmpaquetar;

	@GrupoAsociado(
			grupoContiene="DATOS",
			ordenEnGrupo=4)
	@Checkbox(texto="Provisto (no aplicar de forma transitiva)")
	private boolean provisto;

	@GrupoAsociado(
			grupoContiene="DATOS",
			ordenEnGrupo=5)
	@Checkbox(texto="Ejecución de pruebas")
	@BloqueDescripcion("Seleccione los alcances que ofrecerá el artefacto.")
	private boolean testing;

	
	// ------------------------------


	public boolean isCompilarYEmpaquetar() {
		return compilarYEmpaquetar;
	}

	public void setCompilarYEmpaquetar(boolean compilarYEmpaquetar) {
		this.compilarYEmpaquetar = compilarYEmpaquetar;
	}

	public boolean isCompilarSinEmpaquetar() {
		return compilarSinEmpaquetar;
	}

	public void setCompilarSinEmpaquetar(boolean compilarSinEmpaquetar) {
		this.compilarSinEmpaquetar = compilarSinEmpaquetar;
	}

	public boolean isProvisto() {
		return provisto;
	}

	public void setProvisto(boolean provisto) {
		this.provisto = provisto;
	}

	public boolean isTesting() {
		return testing;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}
	
	// ------------------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// al menos un alcance
		boolean sinAlcanceElegido = !(compilarYEmpaquetar || compilarSinEmpaquetar || provisto || testing);
		if(sinAlcanceElegido) {
			errores.rejectValue("compilarYEmpaquetar", "FALTA-ALCANCE", "Debe elegir al menos un alcance.");
			errores.rejectValue("compilarSinEmpaquetar", "FALTA-ALCANCE", "Debe elegir al menos un alcance.");
			errores.rejectValue("provisto", "FALTA-ALCANCE", "Debe elegir al menos un alcance.");
			errores.rejectValue("testing", "FALTA-ALCANCE", "Debe elegir al menos un alcance.");
		}
	}

	@Override
	public DirectivaAlcances nueva(IContextoEjecucion ce) {
		DirectivaAlcances nueva = new DirectivaAlcances();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaAlcances o = (DirectivaAlcances)original;
		o.setAlcances(alcancesParaRellenar());
	}
	
	private EnumAlcanceDependencia[] alcancesParaRellenar() {
		// lista de alcances
		ArrayList<EnumAlcanceDependencia> alcances = new ArrayList<EnumAlcanceDependencia>();
		if(compilarYEmpaquetar) alcances.add(EnumAlcanceDependencia.CompilarYEmpaquetar);
		if(compilarSinEmpaquetar) alcances.add(EnumAlcanceDependencia.CompilarSinEmpaquetar);
		if(provisto) alcances.add(EnumAlcanceDependencia.Provisto);
		if(testing) alcances.add(EnumAlcanceDependencia.Testing);
		
		return alcances.toArray(new EnumAlcanceDependencia[alcances.size()]);
	}

	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaAlcances d = (DirectivaAlcances)directiva;
		List<EnumAlcanceDependencia> alcances = Arrays.asList(d.getAlcances());
		this.compilarYEmpaquetar = alcances.contains(EnumAlcanceDependencia.CompilarYEmpaquetar);
		this.compilarSinEmpaquetar = alcances.contains(EnumAlcanceDependencia.CompilarSinEmpaquetar);
		this.provisto = alcances.contains(EnumAlcanceDependencia.Provisto);
		this.testing = alcances.contains(EnumAlcanceDependencia.Testing);
	}

}
