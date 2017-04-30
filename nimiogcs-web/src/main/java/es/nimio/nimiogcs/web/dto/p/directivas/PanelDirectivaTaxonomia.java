package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaTaxonomia extends PanelDirectivaBase<DirectivaTaxonomia> {

	public PanelDirectivaTaxonomia(DirectivaTaxonomia directiva) {
		super(directiva);
	}
	
	public PanelDirectivaTaxonomia(DirectivaTaxonomia directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaTaxonomia(DirectivaTaxonomia directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("El artefacto queda organizado bajo la siguiente taxonomía:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Taxonomía:", directiva.getTaxonomia())
		);
	}
}
