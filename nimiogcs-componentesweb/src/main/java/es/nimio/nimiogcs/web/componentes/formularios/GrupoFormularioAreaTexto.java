package es.nimio.nimiogcs.web.componentes.formularios;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

/**
 * Grupo de formulario para un campo de texto
 */
@Fragmento(grupo="_formularios", id="grupo-area-texto")
public class GrupoFormularioAreaTexto implements IComponente {

	private final String field;
	private final String etiqueta;
	private final String descripcion;
	private final Integer lineas;
	
	public GrupoFormularioAreaTexto(String field, String etiqueta, Integer lineas, String descripcion) {
		this.etiqueta = etiqueta;
		this.field = field;
		this.descripcion = descripcion;
		this.lineas = lineas;
	}

	public String field() { return field; }
	public String etiqueta() { return etiqueta != null && !etiqueta.isEmpty() ? etiqueta + ":" : ""; }
	public String descripcion() { return descripcion; }
	public Integer lineas() { return lineas; }
	
	public boolean tieneDescripcion() { return descripcion != null && !descripcion.isEmpty(); }
	
	@Override
	public String clasesParaHtml() { return ""; }
}
