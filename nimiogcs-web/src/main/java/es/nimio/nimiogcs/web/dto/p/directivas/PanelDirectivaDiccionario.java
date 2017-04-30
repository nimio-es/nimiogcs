package es.nimio.nimiogcs.web.dto.p.directivas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaDiccionario extends PanelDirectivaBase<DirectivaDiccionario> {

	public PanelDirectivaDiccionario(DirectivaDiccionario directiva) {
		super(directiva);
	}
	
	public PanelDirectivaDiccionario(DirectivaDiccionario directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaDiccionario(DirectivaDiccionario directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected String titulo() {
		return directiva.getDiccionario().getDescripcion();
	}
	
	@Override
	protected void construyeCuerpoPanel() {
		
		ArrayList<TipoDirectivaDiccionarioDefinicion> definiciones = new ArrayList<TipoDirectivaDiccionarioDefinicion>(directiva.getDiccionario().getDefinicionesDiccionario());
		Collections.sort(
				definiciones,
				new Comparator<TipoDirectivaDiccionarioDefinicion>() {

					@Override
					public int compare(TipoDirectivaDiccionarioDefinicion o1, TipoDirectivaDiccionarioDefinicion o2) {
						Integer i1 = o1.getPosicion();
						Integer i2 = o2.getPosicion();
						return i1.compareTo(i2);
					}
				}
		); 
		
		for(TipoDirectivaDiccionarioDefinicion dd: definiciones) {

			String valor = "";
			if (directiva.getValores().containsKey(dd.getClave())) valor = directiva.getValores().get(dd.getClave());
			
			this.conComponente(MetodosDeUtilidad.parClaveValor(dd.getEtiqueta() + ":", valor));
		}
	}
}
