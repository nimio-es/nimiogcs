package es.nimio.nimiogcs.web.componentes.basicos;

import es.nimio.nimiogcs.web.componentes.Fragmento;

@Fragmento(id="enlace-simple")
public class EnlaceSimple extends TextoSimple {

	private String url = "";
	private String parametros = "";
	
	public EnlaceSimple() {}
	
	public EnlaceSimple(String texto, String url) {
		super(texto);
		this.url = url;
	}

	// ----------------------------------------------
	// Lectura del estado
	// ----------------------------------------------
	
	public String url() { 
		return 
				url.startsWith("/") ?
						url
						: "/" + url; 
	}
	
	public String parametros() { return parametros.isEmpty() ? "" : "?" + parametros; }
	
	// ----------------------------------------------
	// Api fluido
	// ----------------------------------------------

	@Override
	public EnlaceSimple conTexto(String texto) {
		return (EnlaceSimple) super.conTexto(texto);
	}
	
	@Override
	public EnlaceSimple conLetraPeq() {
		return (EnlaceSimple) super.conLetraPeq();
	}
	
	public EnlaceSimple paraUrl(String url) {
		this.url = url;
		return this;
	}
	
	public EnlaceSimple conParametros(String parametros) {
		this.parametros = parametros;
		return this;
	}
}
