package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.dto.p.BuilderPaginadorOperaciones;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaOperacionesProyecto extends PaginaBaseProyectos {

	private Page<Operacion> operaciones;
	private Map<String, Usuario> conocidos;
	
	public PaginaOperacionesProyecto(Proyecto proyecto, boolean operacionesEnCurso, Page<Operacion> operaciones, Map<String, Usuario> conocidos) {
		super(TabActiva.OPERACIONES, proyecto, operacionesEnCurso);
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
								operaciones, conocidos, "proyectos/operaciones/" + proyecto.getId(), "pag=%d")
					}
				);
	}

}
