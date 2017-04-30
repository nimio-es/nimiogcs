package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.f.proyectos.FormularioEditarAnotaciones;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaEditarAnotacionesProyecto extends PaginaBaseProyectos {

	public PaginaEditarAnotacionesProyecto(
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
		
		// dos columnas
		return Arrays.asList(
				new IComponente[] {

						new Formulario()
						.urlAceptacion("/proyectos/anotaciones/" + proyecto.getId() + "/aceptar")
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(FormularioEditarAnotaciones.class)
						)
						.botoneraEstandar("/proyectos/anotaciones/" + proyecto.getId())
				}
		);
	}

}
