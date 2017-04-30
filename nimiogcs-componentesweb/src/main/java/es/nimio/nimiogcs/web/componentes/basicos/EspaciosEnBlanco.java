package es.nimio.nimiogcs.web.componentes.basicos;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

/**
 * Un simple salto de línea
 */
@Fragmento(id="espacios")
public class EspaciosEnBlanco implements IComponente {

	int numero = 1;
	
	public EspaciosEnBlanco() {}

	public EspaciosEnBlanco(int numero) { this.numero = numero; }

	public EspaciosEnBlanco conEspacios(int numero) { this.numero = numero; return this; }
	
	
	public String texto() {
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i <= numero; i++) sb.append("&nbsp;");
		return sb.toString();
	}
	
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
