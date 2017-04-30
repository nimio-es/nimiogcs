package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.ContenidoHtml;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaAnotacionesProyecto extends PaginaBaseProyectos {

	public PaginaAnotacionesProyecto(
			Proyecto proyecto, 
			boolean conOperacionesEnCurso) {
		
		super(TabActiva.ANOTACIONES, proyecto, conOperacionesEnCurso);
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> componentesPagina() {
		
		final String anotaciones = proyecto.getAnotaciones();
		
		// dos columnas
		return Arrays.asList(
				new IComponente[] {

						new PanelContinente()
						.conTitulo("Comentarios y observaciones generales del proyecto")
						.paraTipoPrimario()
						.conComponente(
								new ContenidoHtml()
								.conId("anotaciones")
								.conClase("fondoGris")
								.conHtml(
										com.github.rjeschke.txtmark.Processor.process(
												Strings.isNotEmpty(anotaciones)? anotaciones : ""
										)
								)
						)
						.conComponente(
								new Botonera()
								.conAlineacionALaDerecha()
								.conBoton(
										new BotonEnlace()
										.conTamañoMuyPequeño()
										.conTexto("Editar")
										.paraUrl("/proyectos/anotaciones/" + proyecto.getId() + "/editar")
								)
						)
						
				}
		);
	}

}
