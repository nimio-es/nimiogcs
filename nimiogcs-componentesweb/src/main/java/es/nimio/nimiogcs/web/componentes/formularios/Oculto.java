package es.nimio.nimiogcs.web.componentes.formularios;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_formularios", id="oculto")
public class Oculto implements IComponente {

	private final String field;
	public Oculto(String field) { this.field = field; }
	
	public String field() { return this.field; }
	
	@Override
	public String clasesParaHtml() { return ""; }
}
