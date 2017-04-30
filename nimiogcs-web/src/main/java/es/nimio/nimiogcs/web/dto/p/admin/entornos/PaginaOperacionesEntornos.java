package es.nimio.nimiogcs.web.dto.p.admin.entornos;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.dto.p.BuilderPaginadorOperaciones;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaOperacionesEntornos extends PaginaBaseEntornos {

	private Map<String, Usuario> conocidos;
	private Page<Operacion> operaciones;
	
	public PaginaOperacionesEntornos(DestinoPublicacion entorno, boolean operacionesEnCurso, Page<Operacion> operaciones, Map<String, Usuario> conocidos) {
		super(TabActiva.OPERACIONES, entorno, operacionesEnCurso);
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
								operaciones, conocidos, "admin/ciclovida/entornos/operaciones/" + entorno.getId(), "pag=%d")
						
					}
				
				);
	}

}
