package es.nimio.nimiogcs.web.componentes.formularios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import es.nimio.nimiogcs.web.componentes.Boton;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;

/**
 * Un formulario que debe facilitar la construcción a base de fragmentos 
 * de múltiples modelos que se pasarán a la página
 */
@Fragmento(grupo="_formularios", id="formulario-multimodelo")
public class FormularioMultiModelo 
	implements IContinente<IComponente>{

	private ArrayList<String> clases = new ArrayList<String>();
	private String urlAceptacion;
	private String urlCancelacion;
	private String metodo = "POST";
	private ArrayList<IComponente> componentes = new ArrayList<IComponente>();
	private boolean autoincluirBotonera = false;
	

	// -----------------------
	// API fluida
	// -----------------------

	public FormularioMultiModelo conClase(String clase) {
		this.clases.add(clase);
		return this;
	}
	
	public FormularioMultiModelo conMetodoPost() {
		this.metodo = "POST";
		return this;
	}

	public FormularioMultiModelo conMetodoGet() {
		this.metodo = "GET";
		return this;
	}
	
	public FormularioMultiModelo conUrlAceptacion(String urlAceptacion) {
		this.urlAceptacion = urlAceptacion;
		return this;
	}
	
	public FormularioMultiModelo conUrlCancelacion(String urlCancelacion) {
		this.urlCancelacion = urlCancelacion;
		return this;
	}
	
	public FormularioMultiModelo conUrls(String urlAceptacion, String urlCancelacion) {
		return 
				this.conUrlAceptacion(urlAceptacion)
				.conUrlCancelacion(urlCancelacion);
	}
	
	public FormularioMultiModelo conComponentes(Subformulario... subformularios) {
		this.componentes.addAll(Arrays.asList(subformularios));
		return this;
	}

	public FormularioMultiModelo conComponentes(Collection<Subformulario> subformularios) {
		this.componentes.addAll(subformularios);
		return this;
	}
	
	public FormularioMultiModelo autoincluirBotonera() {
		this.autoincluirBotonera = true;
		return this;
	}

	// -----------------------
	// Lectura
	// -----------------------

	public String urlAceptacion() { return urlAceptacion; }
	public String metodo() { return metodo; }
	
	// -----------------------
	// IComponente
	// -----------------------

	@Override
	public String clasesParaHtml() {
		String css = "";
		for(String c: clases) css += css.length() > 0 ? " " + c : c;
		return css;
	}
	
	// -----------------------
	// IContinente
	// -----------------------

	@Override
	public Collection<IComponente> componentes() {
		
		ArrayList<IComponente> resultado = new ArrayList<IComponente>(this.componentes);
		
		if(autoincluirBotonera) {
			resultado.add(
					new Botonera(
							new BotonInput("Aceptar", urlAceptacion, Boton.TIPO_PRINCIPAL, Boton.TAM_NORMAL),
							new BotonEnlace("Cancelar", urlCancelacion)
					).conAlineacionALaDerecha()
			);
		}
		
		return Collections.unmodifiableCollection(resultado);
	}
}
