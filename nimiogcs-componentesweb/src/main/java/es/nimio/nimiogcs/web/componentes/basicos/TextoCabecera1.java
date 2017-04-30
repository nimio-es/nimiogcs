package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(id="texto-cabecera-1")
public class TextoCabecera1 implements IComponente {

	private String texto = "";
	private ArrayList<String> clases = new ArrayList<String>();
	
	public TextoCabecera1() {}
	
	
	// ---------------------------------------------------
	// Leer estado
	// ---------------------------------------------------
	
	public String texto() { return texto; }

	// ---------------------------------------------------
	// API fluido
	// ---------------------------------------------------
	
	public TextoCabecera1 conTexto(String texto) { this.texto = texto; return this; }

	public TextoCabecera1 defecto() { clases.add("text-default"); return this; }
	public TextoCabecera1 primario() { clases.add("text-primary"); return this; }
	public TextoCabecera1 exito() { clases.add("text-success"); return this; }
	public TextoCabecera1 exitoSi(boolean condicion) { if(condicion) return exito(); return this; }
	public TextoCabecera1 peligro() { clases.add("text-danger"); return this; }
	public TextoCabecera1 peligroSi(boolean condicion) { if(condicion) return peligro(); return this; }
	public TextoCabecera1 aviso() { clases.add("text-warning"); return this; }
	public TextoCabecera1 avisoSi(boolean condicion) { if(condicion) return aviso(); return this; }

	
	public TextoCabecera1 deClase(String clase) { clases.add(clase); return this; }
	public TextoCabecera1 deClaseSi(boolean condicion, String clase) { if(condicion) return deClase(clase); return this; }
	public TextoCabecera1 enColumna(int ancho) { clases.add("col-xs-" + ancho); return this; }
	
	// ---------------------------------------------------
	// IComponente
	// ---------------------------------------------------
	
	@Override
	public String clasesParaHtml() {
		StringBuilder sb = new StringBuilder();
		for(String s: clases) sb.append(s).append(" ");
		return sb.toString();
	}
}
