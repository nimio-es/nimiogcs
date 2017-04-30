package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaRamaCodigo extends PanelDirectivaBase<DirectivaRamaCodigo> {

	public PanelDirectivaRamaCodigo(DirectivaRamaCodigo directiva) {
		super(directiva);
	}
	
	public PanelDirectivaRamaCodigo(DirectivaRamaCodigo directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaRamaCodigo(DirectivaRamaCodigo directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("El componente tiene la siguiente ramificación en el repositorio:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Rama/Etiqueta:", directiva.getRamaCodigo())
		);
	}
}
