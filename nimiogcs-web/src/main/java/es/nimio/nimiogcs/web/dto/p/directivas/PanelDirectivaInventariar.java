package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaInventariar extends PanelDirectivaBase<DirectivaInventario> {

	public PanelDirectivaInventariar(DirectivaInventario directiva) {
		super(directiva);
	}
	
	public PanelDirectivaInventariar(DirectivaInventario directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaInventariar(DirectivaInventario directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("El artefacto queda asociado a la siguiente aplicación empresarial:"));
		this.conComponente(MetodosDeUtilidad.parClaveValor("Aplicación (ID)", ((DirectivaInventario)directiva).getAplicacion().getId()));
		this.conComponente(MetodosDeUtilidad.parClaveValor("Aplicación (Nombre)", ((DirectivaInventario)directiva).getAplicacion().getNombre()));
	}
}
