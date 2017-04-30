package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstructuraCodigo;
import es.nimio.nimiogcs.web.componentes.basicos.AreaPre;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

public final class PanelDirectivaEstructuraCodigo extends PanelDirectivaBase<DirectivaEstructuraCodigo> {

	public PanelDirectivaEstructuraCodigo(DirectivaEstructuraCodigo directiva) {
		super(directiva);
	}
	
	public PanelDirectivaEstructuraCodigo(DirectivaEstructuraCodigo directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaEstructuraCodigo(DirectivaEstructuraCodigo directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Los artefactos tendrán las siguiente estructura de carpetas inicial:"));
		
		this.conComponentes(
				new ContinenteSinAspecto()
				.conComponente(new TextoSimple("Carpetas: ").enNegrita())
				.enColumna(12)
		);
		
		this.conComponentes(
				new AreaPre(directiva.getCarpetas())
		);
	}
}
