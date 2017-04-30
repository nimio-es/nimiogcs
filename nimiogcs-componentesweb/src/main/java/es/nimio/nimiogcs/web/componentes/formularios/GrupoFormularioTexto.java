package es.nimio.nimiogcs.web.componentes.formularios;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

/**
 * Grupo de formulario para un campo de texto
 */
@Fragmento(grupo="_formularios", id="grupo-texto")
public class GrupoFormularioTexto implements IComponente {

	private final String field;
	private final String placeholder;
	private final String etiqueta;
	private final String descripcion;
	
	public GrupoFormularioTexto(String field, String placeholder, String etiqueta, String descripcion) {
		this.etiqueta = etiqueta;
		this.placeholder = placeholder;
		this.field = field;
		this.descripcion = descripcion;
	}

	public String field() { return field; }
	public String placeholder() { return placeholder; }
	public String etiqueta() { return etiqueta != null && !etiqueta.isEmpty() ? etiqueta + ":" : ""; }
	public String descripcion() { return descripcion; }
	
	public boolean tieneDescripcion() { return descripcion != null && !descripcion.isEmpty(); }
	
	@Override
	public String clasesParaHtml() { return ""; }
}
