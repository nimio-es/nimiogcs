package es.nimio.nimiogcs.web.componentes.paneles;

import java.util.ArrayList;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(id="panelinfo")
public class PanelInformativo implements IComponente {

	private ArrayList<String> clases = new ArrayList<String>();
	private String texto = "";
	
	public PanelInformativo() {
		clases.add("alert");
	}
	
	public String texto() { return texto; }
	
	// --
	
	public PanelInformativo conTexto(String texto) { this.texto = texto; return this; }
	public PanelInformativo tipoExito() { this.clases.add("alert-success"); return this; }
	public PanelInformativo tipoAviso() { this.clases.add("alert-warning"); return this; }
	public PanelInformativo tipoInfo() { this.clases.add("alert-info"); return this; }
	public PanelInformativo tipoPeligro() { this.clases.add("alert-danger"); return this; }
	
	@Override
	public String clasesParaHtml() {
		String clase = "";
		for(String c: clases) clase += clase.length() > 0 ? " " + c : c; 
		return clase;
	}
}
