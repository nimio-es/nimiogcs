package es.nimio.nimiogcs.web.componentes.seguridad;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_seguridad", id="seguridad-url")
public class BloqueSeguridadUrl implements IComponente {

	private IComponente componente;
	private String authorizeUrl = "";
	
	public IComponente getComponente() { return componente; }
	public String authorizeUrl() { return authorizeUrl; }
	
	// --
	
	public BloqueSeguridadUrl conComponente(IComponente componente) {
		this.componente = componente;
		return this;
	}
	
	public BloqueSeguridadUrl conAutorizacionUrl(String url) {
		this.authorizeUrl = url;
		return this;
	}
	
	@Override
	public String clasesParaHtml() {
		// Innecesario en este componente
		return null;
	}

}
