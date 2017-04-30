package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(id="glyphicon")
public class GlyphIcon implements IComponente {

	private ArrayList<String> clases;
	
	public GlyphIcon() {
		clases = new ArrayList<String>();
		
		// la primera posición la ocupará siempre la constante glyphicon
		clases.add("glyphicon");
		
		// la segunda posición la ocupará siempre el glifo que se decida, de entrada glyphicon-question-sign
		clases.add("glyphicon-question-sign");
		
		// la tercera posición la ocupará el recarcado de texto, que de momento se deja en blanco
		clases.add("");
	}
	
	// ---------------------------------------------------
	// API fluido
	// ---------------------------------------------------
	
	public GlyphIcon peque() { clases.add("small"); return this; }
	public GlyphIcon ramarcado() { clases.add("negrita"); return this; }
	
	public GlyphIcon exito() { clases.set(2, "text-success"); return this; }
	public GlyphIcon exitoSi(boolean condicion) { if(condicion) return exito(); return this; }
	public GlyphIcon peligro() { clases.set(2, "text-danger"); return this; }
	public GlyphIcon peligroSi(boolean condicion) { if(condicion) return peligro(); return this; }
	public GlyphIcon aviso() { clases.set(2, "text-warning"); return this; }
	public GlyphIcon avisoSi(boolean condicion) { if(condicion) return aviso(); return this; }
	public GlyphIcon info() { clases.set(2, "text-info"); return this; }
	
	public GlyphIcon deClase(String clase) { clases.add(clase); return this; }
	public GlyphIcon deClaseSi(boolean condicion, String clase) { if(condicion) return deClase(clase); return this; }
	
	// -- los iconos
	public GlyphIcon mas() { clases.set(1, "glyphicon-plus"); return this; }
	public GlyphIcon ok() { clases.set(1, "glyphicon-ok"); return this; }
	public GlyphIcon signoExclamacion() { clases.set(1, "glyphicon-exclamation-sign"); return this; }
	public GlyphIcon signoExclamacionSi(boolean condicion) { if(condicion) return signoExclamacion(); return this; }
	public GlyphIcon pulgarArriba() { clases.set(1, "glyphicon-thumbs-up"); return this; }
	public GlyphIcon pulgarArribaSi(boolean condicion) { if(condicion) return pulgarArriba(); return this; }
	public GlyphIcon pulgarAbajo() { clases.set(1, "glyphicon-thumbs-down"); return this; }
	public GlyphIcon pulgarAbajoSi(boolean condicion) { if(condicion) return pulgarAbajo(); return this; }
	public GlyphIcon dedoIndiceDerecha() { clases.set(1, "glyphicon-hand-right"); return this; }
	public GlyphIcon dedoIndiceDerechaSi(boolean condicion) { if(condicion) return dedoIndiceDerecha(); return this; }
	public GlyphIcon lupa() { clases.set(1, "glyphicon-search"); return this; }
	public GlyphIcon flechaDerecha() { clases.set(1, "glyphicon-arrow-right"); return this; }
	public GlyphIcon bandera() { clases.set(1, "glyphicon-flag"); return this; }
	public GlyphIcon banderaSi(boolean condicion) { if(condicion) return bandera(); return this; }
	
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
