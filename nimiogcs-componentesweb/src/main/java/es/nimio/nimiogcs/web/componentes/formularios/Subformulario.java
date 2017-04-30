package es.nimio.nimiogcs.web.componentes.formularios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;

@Fragmento(grupo="_formularios", id="subformulario")
public class Subformulario
	implements IContinente<IComponente> {

	private ArrayList<String> clases = new ArrayList<String>();
	private ArrayList<IComponente> componentes = new ArrayList<IComponente>();
	private String idModelo = "datos";
	
	// -----------------------
	// API fluida
	// -----------------------

	public Subformulario conClase(String clase) {
		this.clases.add(clase);
		return this;
	}
	
	public Subformulario conIdModelo(String idModelo) {
		this.idModelo = idModelo;
		return this;
	}
	
	public Subformulario conComponentes(IComponente... componentes) {
		this.componentes.addAll(Arrays.asList(componentes));
		return this;
	}

	public Subformulario conComponentes(Collection<IComponente> componentes) {
		this.componentes.addAll(componentes);
		return this;
	}

	// -----------------------
	// Lectura
	// -----------------------

	public String idModelo() { return idModelo; }

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
		return componentes;
	}
}
