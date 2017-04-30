package es.nimio.nimiogcs.web.componentes.paginacion;

import java.util.ArrayList;
import java.util.Collection;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_paginacion", id="indexador_paginacion")
public class Indexador implements IComponente {

	/**
	 * Clase interna que representa cada uno de los elementos
	 * de selección de página
	 */
	final static class DescripcionElementoIndice extends Tuples.T5<Boolean, Boolean, String, String, String>{
		
		private static final long serialVersionUID = 3441447046944653646L;

		public DescripcionElementoIndice(
				boolean esElipsis,
				boolean electo,
				String texto,
				String url,
				String parametros) {
			super(esElipsis, electo, texto, url, parametros);
		}
		
		public boolean esElipsis() { return _1; }
		public boolean electo() { return _2; }
		public String texto() { return _3; }
		public String urlBase() { return _4; }
		public String parametros() { return _5; }
	}
	
	/**
	 * Número de elementos que se mostrarán (excluyendo las 
	 * elipsis de principio y fin, si fueran necesarias)
	 */
	private Integer totalIndizadores = 10;
	
	private Integer posicionActual = 1;
	private Integer totalPaginas = 1;
	private String plantillaUrl = "%d";
	private String plantillaParametros = "";
	
	// ---------------------------------------------
	// API fluido
	// ---------------------------------------------
	
	public Indexador conTotalPaginas(int total) {
		this.totalPaginas = total;
		return this;
	}
	
	public Indexador mostrarNumIndizadores(int numIndizadores) {
		this.totalIndizadores = numIndizadores;
		return this;
	}
	
	public Indexador enPagina(int paginaActual) {
		this.posicionActual = paginaActual; 
		return this;
	}
	
	public Indexador usarPlantillaRedireccion(String plantilla) {
		this.plantillaUrl = plantilla;
		return this;
	}
	
	public Indexador usarPlantillaDeParametros(String plantilla) {
		this.plantillaParametros = "?" + plantilla;
		return this;
	}
	
	public Collection<DescripcionElementoIndice> elementosIndice() {
		
		ArrayList<DescripcionElementoIndice> listaElementos = new ArrayList<Indexador.DescripcionElementoIndice>();
		
		// el primero será, siempre, siempre, el valor 1
		listaElementos.add(
				new DescripcionElementoIndice(
						false, 
						posicionActual == 1, 
						"1", 
						String.format(plantillaUrl, 1),
						String.format(plantillaParametros, 1)));
		
		// calculamos el rango de valores que deben mostrarse entre el primero y el último
		int valoresIntermedio = totalIndizadores - 2;
		int porLadoIzquierdo = valoresIntermedio / 2;
		int porLadoDerecho = valoresIntermedio - porLadoIzquierdo;
		int menorValorIntermedio = posicionActual - porLadoIzquierdo;
		if(menorValorIntermedio <= 1) {
			porLadoDerecho += (2 - menorValorIntermedio);
			menorValorIntermedio = 2; // cuando estamos muy próximos al 1
		}
		int mayorValorIntermedio = posicionActual + porLadoDerecho;
		if(mayorValorIntermedio>=totalPaginas) mayorValorIntermedio = totalPaginas - 1;
		
		// el segundo será una elipsis cuando estemos en un rango de valores lejano
		if(menorValorIntermedio > 2) listaElementos.add(
				new DescripcionElementoIndice(
						true, 
						false, 
						"...", 
						"",
						""
				)
		);
		
		// añadimos el rango de valores intermedio
		for(Integer v = menorValorIntermedio; v <= mayorValorIntermedio; v++) {
			listaElementos.add(
					new DescripcionElementoIndice(
							false, 
							v == posicionActual, 
							v.toString(), 
							String.format(plantillaUrl, v),
							String.format(plantillaParametros, v)
					)
			);
		}
		
		// si es necesario, se añade la elipsis al final
		if(mayorValorIntermedio < totalPaginas - 1) listaElementos.add(
				new DescripcionElementoIndice(
						true, 
						false, 
						"...", 
						"", 
						""
				)
		);
		
		// el último valor de la lista será la última página
		// que se mostrará solamente si hay más de una página
		if(totalPaginas > 1)
			listaElementos.add(
					new DescripcionElementoIndice(
							false, 
							posicionActual == totalPaginas, 
							totalPaginas.toString(), 
							String.format(plantillaUrl, totalPaginas),
							String.format(plantillaParametros, totalPaginas)
					)
			);
		
		return listaElementos;
	}
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
