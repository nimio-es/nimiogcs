package es.nimio.nimiogcs.web.componentes.paneles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

@Fragmento(id="panel")
public class PanelContinente implements IContinente<IComponente> {

	private List<String> clases = new ArrayList<String>();
	private final ArrayList<IComponente> cabecera = new ArrayList<IComponente>();
	private final ArrayList<IComponente> componentes = new ArrayList<IComponente>();
	private final ArrayList<IComponente> pie = new ArrayList<IComponente>();
	private boolean contraible = false;
	private boolean contraido = false;
	
	public PanelContinente() {}
	public PanelContinente(Collection<IComponente> componentes) { this(); this.componentes.addAll(componentes); }
	
	public Collection<IComponente> cabecera() { return this.cabecera; }
	public Collection<IComponente> pie() { return this.pie; }
	
	@Override
	public Collection<IComponente> componentes() {
		return this.componentes;
	}

	public boolean sinCabecera() { return this.cabecera.size() == 0; }
	public boolean sinPie() { return this.pie.size() == 0; }
	public boolean esContraible() { return this.contraible; }
	public boolean contraido() { return this.contraido; }
	
	// -------
	// Creación rápida
	// -------

	public static PanelContinente defecto(String titulo) {
		return 
				new PanelContinente()
				.conTitulo(titulo)
				.paraTipoDefecto();
	}
	
	public static PanelContinente defectoContraible(String titulo) {
		return 
				defecto(titulo)
				.siendoContraible();
	}
	
	// -------
	// api de definición fluido
	// -------
	
	public PanelContinente conTitulo(String titulo) { this.cabecera.add(new TextoSimple(titulo)); return this; }
	public PanelContinente paraTipoInfo() { clases.add("panel-info"); return this; }
	public PanelContinente paraTipoDefecto() { clases.add("panel-default"); return this; }
	public PanelContinente paraTipoDefectoSi(boolean condicion) { if(condicion) return paraTipoDefecto(); return this; }
	public PanelContinente paraTipoAviso() { clases.add("panel-warning"); return this; }
	public PanelContinente paraTipoAvisoSi(boolean condicion) { if(condicion) return paraTipoAviso(); return this; }
	public PanelContinente paraTipoPrimario() { clases.add("panel-primary"); return this; }
	public PanelContinente paraTipoPeligro() { clases.add("panel-danger"); return this; }
	public PanelContinente paraTipoPeligroSi(boolean condicion) { if(condicion) return paraTipoPeligro(); return this; }
	public PanelContinente paraTipoExito() { clases.add("panel-success"); return this; }
	public PanelContinente siendoContraible() { this.contraible = true; return this; }
	public PanelContinente empiezaContraido() { this.contraido = true; return this; }
	public PanelContinente empiezaContraidoSi(boolean condicion) { if(condicion) return empiezaContraido(); return this; }
	public PanelContinente conLetraPeq() { clases.add("small"); return this; }
	public PanelContinente conComponentesCabecera(Collection<IComponente> componentes) { this.cabecera.addAll(componentes); return this; }
	public PanelContinente conComponentesCabecera(IComponente... componentes) { return this.conComponentesCabecera(Arrays.asList(componentes)); }
	public PanelContinente conComponente(IComponente componente) { this.componentes.add(componente); return this; }
	public PanelContinente conComponenteSi(boolean condicion, IComponente componente) { 
		if (condicion) this.componentes.add(componente);
		return this;
	}
	public PanelContinente conComponentes(Collection<IComponente> componentes) { this.componentes.addAll(componentes); return this; }
	public PanelContinente conComponentes(IComponente...componentes) { return conComponentes(Arrays.asList(componentes)); }
	public PanelContinente conComponentesSi(boolean condicion, IComponente...componentes) { if(condicion) return conComponentes(componentes); return this; }
	public PanelContinente conComponentesSi(boolean condicion, Collection<IComponente> componentes) { if(condicion) return conComponentes(componentes); return this; }
	public PanelContinente conComponentesPie(Collection<IComponente> componentes) { this.pie.addAll(componentes); return this; }
	public PanelContinente conComponentesPie(IComponente... componentes) { return this.conComponentesPie(Arrays.asList(componentes)); }
	
	// facilitar la inclusión de ciertos componentes
	
	public PanelContinente conParrafo(String texto) {
		return conComponentes(
				new Parrafo(texto)
		);
	}
	
	@Override
	public String clasesParaHtml() {
		String clase = "panel";
		for(String c: clases) clase += " " + c; 
		return clase.trim();
	}

}
