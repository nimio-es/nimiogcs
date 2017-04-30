package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaPublicacionDeployer extends PanelDirectivaBase<DirectivaPublicacionDeployer> {

	public PanelDirectivaPublicacionDeployer(DirectivaPublicacionDeployer directiva) {
		super(directiva);
	}
	
	public PanelDirectivaPublicacionDeployer(DirectivaPublicacionDeployer directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaPublicacionDeployer(DirectivaPublicacionDeployer directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Parámetros para la publicación empleando el canal de publicación Deployer:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Comportamiento:", directiva.getComportamiento()), 
				MetodosDeUtilidad.parClaveValor("Código elemento:", directiva.getCodigoElemento()),
				MetodosDeUtilidad.parClaveValor("Carpeta elemento:", directiva.getCarpetaElemento()),
				MetodosDeUtilidad.parClaveValor("Elemento target:", directiva.getElementoTarget()),
				MetodosDeUtilidad.parClaveValor("Directivas:", join(directiva.getDirectivas()))
		);
	}
	
	private String join(String[] datos) {
		String r = "";
		for(String d: datos)
			r += r.length() > 0 ? "," + d : d;
		return r;
	}

}
