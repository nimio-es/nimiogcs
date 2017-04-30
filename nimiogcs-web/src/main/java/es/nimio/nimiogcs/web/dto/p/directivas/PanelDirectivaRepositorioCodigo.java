package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaRepositorioCodigo extends PanelDirectivaBase<DirectivaRepositorioCodigo> {

	public PanelDirectivaRepositorioCodigo(DirectivaRepositorioCodigo directiva) {
		super(directiva);
	}
	
	public PanelDirectivaRepositorioCodigo(DirectivaRepositorioCodigo directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaRepositorioCodigo(DirectivaRepositorioCodigo directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Configuración de acceso al código fuente en el repositorio de versionado de código:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Repositorio (id):", directiva.getRepositorio().getId()),
				MetodosDeUtilidad.parClaveValor("Repositorio (nombre):", directiva.getRepositorio().getNombre()),
				MetodosDeUtilidad.parClaveValorUrlFija(
						"URI repositorio:", 
						directiva.getRepositorio().getUriRaizRepositorio(), 
						directiva.getRepositorio().getUriRaizRepositorio()
				)
		);
		
		this.conComponenteSi(
				Strings.isNotEmpty(directiva.getParcialEstables()),
				MetodosDeUtilidad.parClaveValorUrlFija(
						"Ruta versión estable:", 
						directiva.getParcialEstables(),
						directiva.getRepositorio().getUriRaizRepositorio() + "/" + directiva.getParcialEstables()
				)				
		);

		this.conComponenteSi(
				Strings.isNotEmpty(directiva.getParcialEtiquetas()),
				MetodosDeUtilidad.parClaveValorUrlFija(
						"Ruta raíz etiquetas:", 
						directiva.getParcialEtiquetas(),
						directiva.getRepositorio().getUriRaizRepositorio() + "/" + directiva.getParcialEtiquetas()
				)				
		);
	}
	
}
