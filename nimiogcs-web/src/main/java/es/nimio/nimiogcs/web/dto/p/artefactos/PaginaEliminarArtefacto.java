package es.nimio.nimiogcs.web.dto.p.artefactos;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.f.artefactos.ConfirmarEliminacion;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaEliminarArtefacto extends EstructuraPagina {

	private Artefacto artefacto;
	
	public PaginaEliminarArtefacto(
			Artefacto artefacto) {
		super("Eliminar artefacto '" + artefacto.getNombre() + "'");
		this.artefacto = artefacto;
		estructuraPagina();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------

	private void estructuraPagina() {
		
		// añadimos el localizador
		this.conComponentes(
				new Localizacion()
					.conEnlace("Home", "/")
					.conEnlace("Artefactos", "/artefactos")
					.conEnlaceYParametros(
							artefacto.getTipoArtefacto().getNombre(), 
							"/artefactos", 
							"tipo=" + artefacto.getTipoArtefacto())
					.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
					.conTexto("ELIMINAR")
		);

		// añadimos un panel informativo indicando que es una operación irreversible
		this.conComponentes(
				new PanelInformativo()
				.tipoPeligro()
				.conTexto(
						"Se dispone a eliminar el siguiente artefacto del registro. Esta operación es irreversible."
				)
		);
		
		// añadimos los datos del componente
		this.conComponentes(
				new PanelContinente()
				.conTitulo("Datos / estructura del artefacto")
				.paraTipoPeligro()
				.conComponentes(FichaArtefactoBuilder.deconstruye(artefacto))
		);
		
		// y el formulario para la confirmación de la eliminación del artefacto
		this.conComponentes(
				new Formulario(
						"confirmacion", 
						"/artefactos/eliminar", 
						"POST", 
						"/artefactos/" + artefacto.getId(), 
						AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
								ConfirmarEliminacion.class
						)
				)
		);
	}
	
}
