package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;

@Fragmento(id="continente-vacio")
public class ContinenteSinAspecto implements IContinente<IComponente> {

	private String id = "";
	private final ArrayList<String> clases = new ArrayList<String>();
	private final Collection<IComponente> componentes = new ArrayList<IComponente>();
	
	public ContinenteSinAspecto() {}
	
	public ContinenteSinAspecto(Collection<IComponente> componentes) {
		this.componentes.addAll(componentes);
	}
	
	// ----------------------------------------------
	// API fluido
	// ----------------------------------------------

	public ContinenteSinAspecto conId(String id) {
		this.id = id;
		return this;
	}
	
	public ContinenteSinAspecto conComponente(IComponente componente) {
		componentes.add(componente);
		return this;
	}

	public ContinenteSinAspecto conComponentes(IComponente... componentes) {
		return conComponentes(Arrays.asList(componentes));
	}

	public ContinenteSinAspecto conComponentes(Collection<IComponente> componentes) {
		this.componentes.addAll(componentes);
		return this;
	}

	
	public ContinenteSinAspecto conComponenteSi(boolean condicion, IComponente componente) {
		if(condicion) return conComponente(componente);
		return this;
	}

	public ContinenteSinAspecto enColumna(int tam) { clases.add(0, "col-xs-" + tam); return this; }
	
	public ContinenteSinAspecto alineacionDerecha() { clases.add("text-right"); return this; }
	
	// ------
	
	public String getId() {
		return id;
	}
	
	@Override
	public String clasesParaHtml() {
		String c = "";
		for(String cc: clases) c += c.length() > 0 ? " " + cc : cc;
		return c;
	}

	@Override
	public Collection<IComponente> componentes() {
		return this.componentes;
	}

}
