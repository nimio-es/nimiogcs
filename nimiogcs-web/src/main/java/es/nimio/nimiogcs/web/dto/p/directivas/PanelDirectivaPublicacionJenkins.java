package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionJenkins;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaPublicacionJenkins extends PanelDirectivaBase<DirectivaPublicacionJenkins> {

	public PanelDirectivaPublicacionJenkins(DirectivaPublicacionJenkins directiva) {
		super(directiva);
	}
	
	public PanelDirectivaPublicacionJenkins(DirectivaPublicacionJenkins directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaPublicacionJenkins(DirectivaPublicacionJenkins directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("La publicación de éste tipo de artefacto usará la tarea:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Tarea:", directiva.getTarea())
		);
	}
}
