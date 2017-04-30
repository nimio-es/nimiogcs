package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaCaracterizacion extends PanelDirectivaBase<DirectivaCaracterizacion> {

	public PanelDirectivaCaracterizacion(DirectivaCaracterizacion directiva) {
		super(directiva);
	}
	
	public PanelDirectivaCaracterizacion(DirectivaCaracterizacion directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaCaracterizacion(DirectivaCaracterizacion directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	
	@Override
	protected void construyeCuerpoPanel() {
		
		this.conComponente(new Parrafo("Caracterización de la familia de artefactos:"));
		
		DirectivaCaracterizacion ct = (DirectivaCaracterizacion)this.directiva;
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Directivas definen artefacto (requeridas):", join(ct.getDirectivasRequeridas())),
				MetodosDeUtilidad.parClaveValor("Directivas opcionales artefacto:", join(ct.getDirectivasOpcionales())),
				MetodosDeUtilidad.parClaveValor("Generar coordenadas Maven:", ct.getGenerarCoordenadasMaven() ? "SÍ" : "NO"),
				MetodosDeUtilidad.parClaveValor("Empaquetado generación:", ct.getEmpaquetadoCoordenada()),
				MetodosDeUtilidad.parClaveValor("Es Libería Externa:", ct.getLibreriaExterna() ? "SÍ" : "NO"),
				MetodosDeUtilidad.parClaveValor("Plantilla validación nombre:", ct.getPlantillaValidacionNombre()) 
		);
	}
	
	
	private String join(String[] valores) {
		String j = "";
		for(String v: valores) j += j.length() > 0 ? ", " + v : v;
		return j;
	}
}
