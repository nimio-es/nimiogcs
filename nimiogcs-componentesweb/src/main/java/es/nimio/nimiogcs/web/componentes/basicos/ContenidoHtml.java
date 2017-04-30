package es.nimio.nimiogcs.web.componentes.basicos;

import java.util.ArrayList;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(id="contenido-html")
public class ContenidoHtml implements IComponente {

	private final ArrayList<String> clases = new ArrayList<String>();
	private String id = "";
	private String html = "";
	
	// --

	public String id() { return id; }
	public String html() { return html; }
	
	// --

	public ContenidoHtml conId(String id) { this.id = id;  return this; }
	public ContenidoHtml conClase(String clase) { this.clases.add(clase); return this; }
	public ContenidoHtml conHtml(String html) { this.html = html; return this; }
	
	// --
	
	@Override
	public String clasesParaHtml() {
		String c = "";
		for(String cc: clases) c += c.length() > 0 ? " " + cc : cc;
		return c;
	}
}
