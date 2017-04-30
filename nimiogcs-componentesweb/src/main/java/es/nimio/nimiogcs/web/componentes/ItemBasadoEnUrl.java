package es.nimio.nimiogcs.web.componentes;

import es.nimio.nimiogcs.functional.Tuples;

/**
 * Cada uno de los elementos de broadcrumb
 */
public final class ItemBasadoEnUrl extends Tuples.T3<String, String, String> implements IItemBasadoEnUrl {

	private static final long serialVersionUID = 8458068620597885093L;

	public ItemBasadoEnUrl(String texto) {
		this(texto, "");
	}
	
	public ItemBasadoEnUrl(String texto, String url) {
		this(texto, url, "");
	}
	
	public ItemBasadoEnUrl(String texto, String url, String parametros) {
		super(texto, url.trim(), parametros.trim());
	}

	@Override
	public String texto() { return this._1; }
	
	@Override
	public String url() { return this._2; }
	
	@Override
	public String parametros() { return "?" + this._3; }
	
	@Override
	public boolean soloTexto() { return this._2 == null || this._2.isEmpty(); }
	
	@Override
	public boolean tieneParametros() { return this._3 != null && !this._3.isEmpty(); }
}