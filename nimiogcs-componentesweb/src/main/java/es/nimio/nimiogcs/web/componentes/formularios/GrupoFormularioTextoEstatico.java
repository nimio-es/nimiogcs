package es.nimio.nimiogcs.web.componentes.formularios;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_formularios", id="grupo-texto-estatico")
public class GrupoFormularioTextoEstatico implements IComponente {

	private final String field;
	private String etiqueta = "";
	
	public GrupoFormularioTextoEstatico(String field) {
		this.field = field;
	}

	public String field() { return this.field; }
	public String etiqueta() { return etiqueta != null && !etiqueta.isEmpty() ? etiqueta + ":" : ""; }
	
	// --------------------------------------
	// API fluido
	// --------------------------------------

	public GrupoFormularioTextoEstatico conEtiqueta(String etiqueta) { this.etiqueta = etiqueta; return this; }
	
	// --------------------------------------
	// IComponente
	// --------------------------------------
	
	@Override
	public String clasesParaHtml() { return ""; }
}
