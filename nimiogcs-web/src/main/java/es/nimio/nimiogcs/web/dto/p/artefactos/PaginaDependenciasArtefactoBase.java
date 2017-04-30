package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Tabs;

/**
 * Variante de la página para los artefactos JVM
 */
public abstract class PaginaDependenciasArtefactoBase extends PaginaBaseArtefactos {

	public enum TabDependenciasActiva {
		
		ESTATICAS("Estáticas"),
		ENCURSO("En curso");
		
		private String titulo;
		public String titulo() { return titulo; }
		
		private TabDependenciasActiva(String titulo) { this.titulo = titulo; } 
		
	}
	
	protected TabDependenciasActiva depActiva;
	
	public PaginaDependenciasArtefactoBase(
			TabDependenciasActiva tabDependencia,
			Artefacto artefacto, 
			boolean operacionesEnCurso) {
		super(
				TabActiva.DEPENDENCIAS, 
				"Dependencias '" + tabDependencia.titulo() + "' para el artefacto '" + artefacto.getNombre() + '"', 
				artefacto, 
				operacionesEnCurso);
		depActiva = tabDependencia;
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------

	@Override
	protected final List<IComponente> componentesPagina() {

		// ahora metemos las pestañas, que primero creamos y luego añadimos
		Tabs tabs = new Tabs()
				.conTab(TabDependenciasActiva.ESTATICAS.titulo(), "/artefactos/dependencias/estaticas/" + artefacto.getId(), depActiva == TabDependenciasActiva.ESTATICAS)
				.conTab(TabDependenciasActiva.ENCURSO.titulo(), "/artefactos/dependencias/encurso/" + artefacto.getId(), depActiva == TabDependenciasActiva.ENCURSO)
				.conComponentes(componentesTabDependencia());

		
		return Arrays.asList( new IComponente[] { tabs } );
	}
	
	protected abstract List<IComponente> componentesTabDependencia();
}
