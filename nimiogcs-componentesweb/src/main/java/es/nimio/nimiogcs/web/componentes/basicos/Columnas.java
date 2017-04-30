package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;

/**
 * Representa la página o sección como columnas de componentes
 */
@Fragmento(grupo="_columnas", id="columnas")
public class Columnas implements IContinente<Columnas.Columna>{

	/**
	 * Representación de la propia columna.
	 */
	@Fragmento(grupo="_columnas", id="columna")
	public static class Columna implements IContinente<IComponente> {

		private ArrayList<String> clases = new ArrayList<String>();
		private String style = "";
		private ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		private boolean conFilas = true;

		public Columna() {
			// la primera posición es siempre para el ancho
			clases.add("col-xs-12");
		}
		

		// -------------------------------------
		// Lectura del estado
		// -------------------------------------

		public boolean separarConFilas() { return this.conFilas; }
		
		public String estilo() { return style; }
		
		// -------------------------------------
		// Api fluido
		// -------------------------------------

		public Columna conComponentes(IComponente ... componentes) {
			return this.conComponentes(Arrays.asList(componentes));
		}
		
		public Columna conComponentes(Collection<IComponente> componentes) {
			this.componentes.addAll(componentes);
			return this;
		}
		
		public Columna conComponentesSi(boolean condicion, IComponente...componentes) {
			if(condicion) return conComponentes(componentes);
			return this;
		}

		public Columna conAncho(int ancho) {
			clases.set(0, "col-xs-" + (ancho > 0 && ancho <= 12 ? ancho : 12));
			return this;
		}
		
		public Columna textoDerecha() {
			clases.add("text-right");
			return this;
		}
		
		public Columna sinFilas() {
			this.conFilas = false;
			return this;
		}
		
		public Columna conClase(String clase) {
			this.clases.add(clase);
			return this;
		}
		
		public Columna fondoExito() {
			this.clases.add("bg-success");
			return this;
		}

		public Columna fondoExitoSi(boolean condicion) {
			if(condicion) return fondoExito();
			return this;
		}
		

		public Columna fondoAviso() {
			this.clases.add("bg-warning");
			return this;
		}
		
		public Columna fondoAvisoSi(boolean condicion) {
			if(condicion) return fondoAviso();
			return this;
		}
		
		public Columna fondoInfo() {
			this.clases.add("bg-info");
			return this;
		}
		
		public Columna fondoInfoSi(boolean condicion) {
			if(condicion) return fondoInfo();
			return this;
		}
		
		public Columna fondoGris() {
			this.style = "background-color:rgb(198,198,198)";
			return this;
		}
		
		public Columna fondoGrisSi(boolean condicion) {
			if(condicion) return fondoGris();
			return this;
		}
		
		
		// --------------------------------------
		// IContinente & IComponente
		// --------------------------------------
		
		@Override
		public String clasesParaHtml() {
			StringBuilder sb = new StringBuilder();
			for(String c: clases) sb.append(c).append(" ");
			return sb.toString();
		}

		@Override
		public Collection<IComponente> componentes() {
			return componentes;
		}
		
	}
	
	// ###############################################
	
	public Columnas() {}
	
	private ArrayList<Columnas.Columna> columnas = new ArrayList<Columnas.Columna>();
	
	// ---------------------------------------
	// Api fluido
	// ---------------------------------------

	public Columnas conColumna(Columna columna) {
		this.columnas.add(columna);
		return this;
	}
	
	
	// --------------------------------------
	// IContinente & IComponente
	// --------------------------------------

	@Override
	public String clasesParaHtml() {
		return "";
	}
	
	@Override
	public Collection<Columna> componentes() {
		return columnas;
	}
}
