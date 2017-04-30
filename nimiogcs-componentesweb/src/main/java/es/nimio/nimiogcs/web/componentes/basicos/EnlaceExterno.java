package es.nimio.nimiogcs.web.componentes.basicos;

import es.nimio.nimiogcs.web.componentes.Fragmento;

@Fragmento(id="enlace-externo")
public class EnlaceExterno extends TextoSimple {

	private String url = "";
	
	public EnlaceExterno() {}
	
	// ----------------------------------------------
	// Lectura del estado
	// ----------------------------------------------
	
	public String url() { return url; }
	
	// ----------------------------------------------
	// Api fluido
	// ----------------------------------------------

	@Override
	public EnlaceExterno conTexto(String texto) {
		return (EnlaceExterno) super.conTexto(texto);
	}
	
	@Override
	public EnlaceExterno conLetraPeq() {
		return (EnlaceExterno) super.conLetraPeq();
	}
	
	public EnlaceExterno paraUrl(String url) {
		this.url = url;
		return this;
	}
}
