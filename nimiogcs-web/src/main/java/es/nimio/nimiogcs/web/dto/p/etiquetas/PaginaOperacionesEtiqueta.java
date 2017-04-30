package es.nimio.nimiogcs.web.dto.p.etiquetas;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.dto.p.BuilderPaginadorOperaciones;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaOperacionesEtiqueta extends PaginaBaseEtiquetas {

	private Page<Operacion> operaciones;
	private Map<String, Usuario> conocidos;
	
	public PaginaOperacionesEtiqueta(EtiquetaProyecto etiqueta, boolean operacionesEnCurso, Page<Operacion> operaciones, Map<String, Usuario> conocidos) {
		super(TabActiva.OPERACIONES, etiqueta, operacionesEnCurso);
		this.operaciones = operaciones;
		this.conocidos = conocidos;
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> componentesPagina() {

		return Arrays.asList( 
				new IComponente[] {
						
						BuilderPaginadorOperaciones.fabricaPaginador(
								operaciones, conocidos, "proyectos/etiquetas/etiqueta/" + etiqueta.getId() + "/operaciones", "pag=%d")
					}
				);
	}

}
