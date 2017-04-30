package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Tabs;

public abstract class PaginaBasePublicacionesProyecto extends PaginaBaseProyectos {

	protected final SubTabPublicacionesActiva subtabActiva;
	
	public PaginaBasePublicacionesProyecto(SubTabPublicacionesActiva subtabActiva, Proyecto proyecto, boolean conOperacionesEnCurso) {
		super(TabActiva.PUBLICACIONES, proyecto, conOperacionesEnCurso);
		this.subtabActiva = subtabActiva;
	}

	// -----------------------------------------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------------------------------------

	@Override
	protected Localizacion localizacion() {
		return 
				super.localizacion()
				.conTexto(subtabActiva.titulo());
	}
	
	protected List<IComponente> componentesPagina() {
		
		// ahora metemos las pestañas, que primero creamos y luego añadimos
		Tabs tabs = new Tabs()
				.conTab(SubTabPublicacionesActiva.TABLA.titulo(), "/proyectos/publicaciones/impacto/" + proyecto.getId(), subtabActiva == SubTabPublicacionesActiva.TABLA)
				.conTab(SubTabPublicacionesActiva.LISTADO.titulo(), "/proyectos/publicaciones/lista/" + proyecto.getId(), subtabActiva == SubTabPublicacionesActiva.LISTADO)
				.conComponentes(subcomponentesPagina());
		
		return Arrays.asList( new IComponente[] { tabs } );
	}
	
	protected abstract List<IComponente> subcomponentesPagina();
	
	// -------
	
	public static enum SubTabPublicacionesActiva {
		
		TABLA("Impacto"),
		LISTADO("Listado");
		
		private final String titulo;
		
		public String titulo() { return titulo; }
		
		private SubTabPublicacionesActiva(String titulo) { this.titulo = titulo; }
	}
	
}
