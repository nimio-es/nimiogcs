package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaDependenciasEnCursoArtefacto extends PaginaDependenciasArtefactoBase {

	
	public PaginaDependenciasEnCursoArtefacto(
			Artefacto artefacto, 
			boolean operacionesEnCurso) {
		super(TabDependenciasActiva.ENCURSO, artefacto, operacionesEnCurso);
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------

	@Override
	protected List<IComponente> componentesTabDependencia() {
		
		return Arrays.asList( 
				new IComponente[] {
						new Parrafo().conTexto("Esto hay que hacerlo").deTipoPeligro()
				}
		);

	}
}
