package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;
import java.util.List;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

/**
 * Párrafo de texto sencillo
 */
@Fragmento(id="parrafo")
public class Parrafo implements IComponente {

	private String texto = "";
	private List<String> clases = new ArrayList<String>();
	
	public Parrafo() {}
	
	public Parrafo(String texto) {
		this.texto = texto;
	}
	
	public String texto() { return texto; }
	
	@Override
	public String clasesParaHtml() {
		String clase = "";
		for(String c: clases) clase += c + " ";
		return clase.trim();
	}

	// ---------------------------------------------------
	// Api fluido
	// ---------------------------------------------------
	
	public Parrafo conTexto(String texto) { this.texto = texto; return this; }
	public Parrafo conLetraPeq() { clases.add("small"); return this; }
	public Parrafo deTipoDefecto() { clases.add("text-default"); return this; }
	public Parrafo deTipoInfo() { clases.add("text-info"); return this; }
	public Parrafo deTipoAviso() { clases.add("text-warning"); return this; }
	public Parrafo deTipoExito() { clases.add("text-success"); return this; }
	public Parrafo deTipoPeligro() { clases.add("text-danger"); return this; }
	public Parrafo deTipoPrincipal() { clases.add("text-primary"); return this; } 
	public Parrafo enNegrita() { clases.add("negrita"); return this; }

}
