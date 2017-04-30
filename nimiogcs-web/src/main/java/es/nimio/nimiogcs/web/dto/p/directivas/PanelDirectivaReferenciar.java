package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaReferenciar;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaReferenciar extends PanelDirectivaBase<DirectivaReferenciar> {

	public PanelDirectivaReferenciar(DirectivaReferenciar directiva) {
		super(directiva);
	}
	
	public PanelDirectivaReferenciar(DirectivaReferenciar directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaReferenciar(DirectivaReferenciar directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Tipos de artefactos que pueden ser referenciados por los artefactos de esta familia usando cada uno de los tipos de dependencia:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Como relación posicional:", join(directiva.getPosiblesPosicional())),
				MetodosDeUtilidad.parClaveValor("Como relación con alcance:", join(directiva.getPosiblesAlcance())),
				MetodosDeUtilidad.parClaveValor("Como relación módulo web:", join(directiva.getPosiblesWeb()))
		);
	}

	
	private String join(String[] valores) {
		String j = "";
		for(String v: valores) j += j.length() > 0 ? ", " + v : v;
		return j;
	}
}
