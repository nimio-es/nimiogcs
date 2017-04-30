package es.nimio.nimiogcs.web.componentes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

@Fragmento(id="tabla-basica")
public class TablaBasica extends Tuples.T2<Boolean, List<TablaBasica.DefinicionColumna>> implements IComponente {

	private static final long serialVersionUID = 8046559395818738118L;

	// -------------------------------------------------
	// Estructuras auxiliares
	// -------------------------------------------------

	public static final class DefinicionColumna extends Tuples.T3<IComponente, Integer, ArrayList<IComponente>> {

		private static final long serialVersionUID = 3845299546581254765L;

		public DefinicionColumna(String cabecera, Integer ancho) {
			this(new TextoSimple(cabecera), ancho);
		}

		public DefinicionColumna(String cabecera, Integer ancho, ArrayList<IComponente> valores) {
			this(new TextoSimple(cabecera), ancho, valores);
		}
		
		public DefinicionColumna(IComponente cabecera, Integer ancho) {
			this(cabecera, ancho, new ArrayList<IComponente>());
		}
		
		public DefinicionColumna(IComponente cabecera, Integer ancho, ArrayList<IComponente> valores) {
			super(cabecera, ancho, valores);
		}

		public IComponente cabecera() { return _1; }
		public int ancho() { return _2; }
		public ArrayList<IComponente> componentes() { return _3; }
		
		// -----
		
		
		
	}

	// -------------------------------------------------
	// Construcción
	// -------------------------------------------------

	public TablaBasica() {
		this(false);
	}

	public TablaBasica(Boolean pequeño) {
		this(pequeño, new ArrayList<DefinicionColumna>());
	}
	
	public TablaBasica(Boolean pequeño, List<DefinicionColumna> columnas) {
		super(pequeño, columnas);
	}
	
	// -------------------------------------------------
	// API fluido
	// -------------------------------------------------

	/**
	 * Añade los componentes a cada una de las columnas.
	 * Si hay más componentes que columnas, se ignorará lo que falte.
	 * Si hay menos, se rellenará el resto con el componente vacío.
	 */
	public TablaBasica conFila(List<IComponente> componentes) {

		int numCol = 0;
		for(DefinicionColumna columna: this.columnas()) {
			
			// si no quedan componentes que añadir pondremos un texto simple en blanco
			// lo mismo aplica si el componente de la posición es nulo
			if ((componentes.size() < numCol + 1) || (componentes.get(numCol) == null)) {
				columna.componentes().add(new TextoSimple(""));
			}
			
			// cogemos el componente de la posición que toca
			IComponente componente = componentes.get(numCol);
			columna.componentes().add(componente);

			// y pasamos a la siguiente columna
			numCol++;
		}
		
		return this;
	}
	
	/**
	 * Añade los componentes a cada una de las columnas.
	 * Si hay más componentes que columnas, se ignorará lo que falte.
	 * Si hay menos, se rellenará el resto con el componente vacío.
	 */
	public TablaBasica conFila(IComponente... componentes) {
		return conFila(Arrays.asList(componentes));
	}
	
	/**
	 * Convierte la lista de listas de componentes en filas
	 */
	public TablaBasica conFilas(List<List<IComponente>> recorrido) {
		if(recorrido.size() == 0) return this;
		
		return conFila(recorrido.get(0))
				.conFilas(recorrido.subList(1, recorrido.size()));
	}
	
	// -------------------------------------------------
	// Lectura del estado
	// -------------------------------------------------

	public boolean letraPequeña() { return _1; }
	public List<DefinicionColumna> columnas() { return _2; }

	/**
	 * Devuelve el join de todas las columnas en la que cada registro es 
	 * una lista de los componentes que se tienen que presentar en cada fila.
	 */
	public List<List<IComponente>> filas() {

		// el resultado de las filas
		List<List<IComponente>> filas = new ArrayList<List<IComponente>>();
		
		// el número máximo de componentes en una columna determina el número
		// de filas
		int numfilas = Collections.max(
				Collections.list(
					Streams.of(columnas())
						.map(new Function<DefinicionColumna, Integer>() {
							@Override
							public Integer apply(DefinicionColumna columna) {
								return columna.componentes().size();
							}
						})
						.getEnumeration()
					));
		
		
		// contamos las filas
		for(int indiceFila = 0; indiceFila < numfilas; indiceFila++) {

			// preparamos la lista que representa la fila
			List<IComponente> fila = new ArrayList<IComponente>();
			
			// y recorremos las columnas para sacar el componente que toca
			for(DefinicionColumna columna: columnas()) {
				if(columna.componentes().size()>indiceFila) 
					fila.add(columna.componentes().get(indiceFila));
				else
					fila.add(new TextoSimple("", true));
			}
			
			// y añadimos al resultado final
			filas.add(fila);
		}
		
		// devolvemos lo que salga
		return filas;
	}
	
	@Override
	public String clasesParaHtml() {
		String clase = "table table-striped table-condensed";
		return letraPequeña() ? clase + " small" : clase; }
}
