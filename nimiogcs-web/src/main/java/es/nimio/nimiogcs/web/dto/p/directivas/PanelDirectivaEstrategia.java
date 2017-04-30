package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaEstrategia extends PanelDirectivaBase<DirectivaEstrategiaEvolucion> {

	public PanelDirectivaEstrategia(DirectivaEstrategiaEvolucion directiva) {
		super(directiva);
	}
	
	public PanelDirectivaEstrategia(DirectivaEstrategiaEvolucion directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaEstrategia(DirectivaEstrategiaEvolucion directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Este artefacto posee una configuración específica para construcción, cuya estrategia de evolución es:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Estrategia:", directiva.getEstrategia()),
				MetodosDeUtilidad.parClaveValor("Descripción:", directiva.getDescripcionEstrategia())
		);
		
		this.conComponentes(
				new Parrafo(" ").conLetraPeq(),
				new Parrafo()
				.conTexto("La evolución de las dependencias podrá consultarse en la pestaña 'Dependencias'.")
				.deTipoInfo()
				.conLetraPeq()
		);
	}
	
}
