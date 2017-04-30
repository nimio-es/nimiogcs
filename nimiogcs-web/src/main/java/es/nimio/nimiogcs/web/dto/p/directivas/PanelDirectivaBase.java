package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

abstract class PanelDirectivaBase<T extends DirectivaBase> extends PanelContinente {

	protected T directiva;
	
	public PanelDirectivaBase(T directiva) {
		super();
		
		this.directiva = directiva;
		
		// lo que es común en cuanto a título y forma
		this.conTitulo(titulo());
		this.siendoContraible();
		this.paraTipoDefecto();
		
		// el cuerpo
		construyeCuerpoPanel();
	}
	
	public PanelDirectivaBase(T directiva, String urlEditar) {
		this(directiva);
		
		// -- añadimos al pie la url para editar
		this.conComponentesPie(
				new ContinenteSinAspecto()
				.conComponentes(
						new EnlaceSimple("Editar", urlEditar)
				)
				.alineacionDerecha()
		);
	}

	public PanelDirectivaBase(T directiva, String urlEditar, String urlQuitar) {
		this(directiva);
		
		// -- añadimos al pie la url para editar
		this.conComponentesPie(
				new ContinenteSinAspecto()
				.conComponentes(
						new EnlaceSimple("Editar", urlEditar),
						new TextoSimple(" "),
						new EnlaceSimple("Quitar", urlQuitar).peligro()
				)
				.alineacionDerecha()
		);
	}
	

	// ----

	protected String titulo() {
		return directiva.getDirectiva().getNombre();
	}
	
	/**
	 * Se encarga de construir el cuerpo del panel acorde a la directiva
	 * que representa.
	 */
	protected abstract void construyeCuerpoPanel();
}
