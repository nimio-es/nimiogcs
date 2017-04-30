package es.nimio.nimiogcs.web.dto.p.admin.entornos;

import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaDatosEntornos extends PaginaBaseEntornos {

	public PaginaDatosEntornos(
			DestinoPublicacion entorno, 
			boolean conOperacionesEnCurso) {
		
		super(TabActiva.DATOS, entorno, conOperacionesEnCurso);
		
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
						
						new PanelContinente()
						.paraTipoInfo()
						.conTitulo("Datos generales")
						.conComponente(
								new Parrafo()
								.conTexto("Datos generales del entorno")
						)
						.conComponentes(
								MetodosDeUtilidad.parClaveValor("Id:", entorno.getId()),
								MetodosDeUtilidad.parClaveValor("Nombre:", entorno.getNombre())
						)
				}
		);
	}


}
