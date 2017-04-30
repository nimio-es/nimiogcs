package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;
import java.util.List;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

/**
 * Párrafo de texto sencillo
 */
@Fragmento(id="area-pre")
public class AreaPre implements IComponente {

	private String texto = "";
	private List<String> clases = new ArrayList<String>();
	
	public AreaPre() {}
	
	public AreaPre(String texto) {
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
	
	public AreaPre conTexto(String texto) { this.texto = texto; return this; }
	public AreaPre conLetraPeq() { clases.add("small"); return this; }
	public AreaPre deTipoDefecto() { clases.add("text-default"); return this; }
	public AreaPre deTipoInfo() { clases.add("text-info"); return this; }
	public AreaPre deTipoAviso() { clases.add("text-warning"); return this; }
	public AreaPre deTipoExito() { clases.add("text-success"); return this; }
	public AreaPre deTipoPeligro() { clases.add("text-danger"); return this; }
	public AreaPre deTipoPrincipal() { clases.add("text-primary"); return this; } 
	public AreaPre enNegrita() { clases.add("negrita"); return this; }

}
