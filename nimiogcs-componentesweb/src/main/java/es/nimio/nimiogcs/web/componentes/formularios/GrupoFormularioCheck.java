package es.nimio.nimiogcs.web.componentes.formularios;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_formularios", id="grupo-check")
public class GrupoFormularioCheck implements IComponente {

	private final String field;
	private final String etiqueta;
	private final String texto;
	private final String descripcion;
	
	public GrupoFormularioCheck(
			String field,
			String etiqueta,
			String texto,
			String descripcion) {
		
		this.field = field;
		this.etiqueta = etiqueta;
		this.texto = texto;
		this.descripcion = descripcion;
	}

	public String field() { return field; }
	public String etiqueta() { return etiqueta != null && !etiqueta.isEmpty() ? etiqueta + ":" : ""; }
	public String texto() { return texto; }
	public String descripcion() { return descripcion; }
	
	public boolean tieneDescripcion() { return descripcion != null && !descripcion.isEmpty(); }
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
