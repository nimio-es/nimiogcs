package es.nimio.nimiogcs.web.componentes.formularios;

import java.util.Collection;

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_formularios", id="grupo-seleccion")
public class GrupoFormularioSeleccion implements IComponente {

	private final String field;
	private final String etiqueta;
	private final String descripcion;
	private final Collection<NombreDescripcion> valores;
	
	public GrupoFormularioSeleccion(
			String field,
			String etiqueta,
			String descripcion,
			Collection<NombreDescripcion> valores) {
		
		this.field = field;
		this.etiqueta = etiqueta;
		this.descripcion = descripcion;
		this.valores = valores;
	}
	
	public String field() { return field; }
	public String etiqueta() { return etiqueta != null && !etiqueta.isEmpty() ? etiqueta + ":" : ""; }
	public String descripcion() { return descripcion; }
	public Collection<NombreDescripcion> valores() { return valores; }
	
	public boolean tieneDescripcion() { return descripcion != null && !descripcion.isEmpty(); }
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
