package es.nimio.nimiogcs.web.componentes.formularios;

import es.nimio.nimiogcs.web.componentes.Boton;
import es.nimio.nimiogcs.web.componentes.Fragmento;

@Fragmento(grupo="_formularios", id="boton-input")
public class BotonInput extends Boton {

	public BotonInput(String texto, String url) {
		super(texto, url);
	}

	public BotonInput(String texto, String url, String tipo, String tamaño) {
		super(texto, url, tipo, tamaño);
	}
}
