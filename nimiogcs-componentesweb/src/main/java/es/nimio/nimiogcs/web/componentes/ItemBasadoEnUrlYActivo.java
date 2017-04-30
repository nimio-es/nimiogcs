package es.nimio.nimiogcs.web.componentes;

import es.nimio.nimiogcs.functional.Tuples;

public class ItemBasadoEnUrlYActivo extends Tuples.T4<String, String, String, Boolean> implements IItemBasadoEnUrl {

	private static final long serialVersionUID = 2659743748815386557L;

	public ItemBasadoEnUrlYActivo(String texto, String url, Boolean activo) {
		this(texto, url, "", activo);
	}
	
	public ItemBasadoEnUrlYActivo(String texto, String url, String parametros, Boolean activo) {
		super(texto, url.trim(), parametros.trim(), activo);
	}

	@Override
	public String texto() {
		return _1;
	}

	@Override
	public String url() {
		return _2;
	}
	
	@Override
	public String parametros() {
		return "?" + _3;
	}

	@Override
	public boolean soloTexto() {
		return _2 == null || _2.isEmpty();
	}

	@Override
	public boolean tieneParametros() { return this._3 != null && !this._3.isEmpty(); }
	
	public boolean activo() {
		return _4;
	}
}
