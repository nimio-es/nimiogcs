package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(id="texto-simple")
public class TextoSimple implements IComponente {

	private String texto;
	private ArrayList<String> clases = new ArrayList<String>();
	
	public TextoSimple() {}
	
	public TextoSimple(String texto) {
		this(texto, false);
	}

	public TextoSimple(String texto, boolean pequeño) {
		this.texto = texto;
		if(pequeño) clases.add("small");
	}
	
	
	// ---------------------------------------------------
	// Leer estado
	// ---------------------------------------------------
	
	public String texto() { return texto; }

	// ---------------------------------------------------
	// API fluido
	// ---------------------------------------------------
	
	public TextoSimple conTexto(String texto) { this.texto = texto; return this; }
	public TextoSimple conTextoSi(boolean condicion, String texto) { if(condicion) return conTexto(texto); return this; }
	public TextoSimple conLetraPeq() { clases.add("small"); return this; }
	public TextoSimple enNegrita() { clases.add("negrita"); return this; }
	public TextoSimple enNegritaSi(boolean condicion) { if(condicion) return enNegrita(); return this; }
	
	public TextoSimple exito() { clases.add("text-success"); return this; }
	public TextoSimple exitoSi(boolean condicion) { if(condicion) return exito(); return this; }
	public TextoSimple peligro() { clases.add("text-danger"); return this; }
	public TextoSimple peligroSi(boolean condicion) { if(condicion) return peligro(); return this; }
	public TextoSimple aviso() { clases.add("text-warning"); return this; }
	public TextoSimple avisoSi(boolean condicion) { if(condicion) return aviso(); return this; }
	public TextoSimple principal() { clases.add("text-primary"); return this; }
	public TextoSimple info() { clases.add("text-info"); return this; }

	
	public TextoSimple deClase(String clase) { clases.add(clase); return this; }
	public TextoSimple deClaseSi(boolean condicion, String clase) { if(condicion) return deClase(clase); return this; }
	public TextoSimple enColumna(int ancho) { clases.add("col-xs-" + ancho); return this; }
	
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
