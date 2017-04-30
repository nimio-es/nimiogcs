package es.nimio.nimiogcs.web.componentes.basicos;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

/**
 * Un simple salto de línea
 */
@Fragmento(id="salto-de-linea")
public class SaltoDeLinea implements IComponente {

	public SaltoDeLinea() {}
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
