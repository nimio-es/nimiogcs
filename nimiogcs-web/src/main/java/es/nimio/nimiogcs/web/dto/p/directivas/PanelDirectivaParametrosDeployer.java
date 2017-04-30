package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaParametrosDeployer;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaParametrosDeployer extends PanelDirectivaBase<DirectivaParametrosDeployer> {

	public PanelDirectivaParametrosDeployer(DirectivaParametrosDeployer directiva) {
		super(directiva);
	}
	
	public PanelDirectivaParametrosDeployer(DirectivaParametrosDeployer directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaParametrosDeployer(DirectivaParametrosDeployer directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Parámetros para la publicación empleando el canal de publicación Deployer:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Ruta en publicación:", directiva.getPathInRequest())
		);
	}
}
