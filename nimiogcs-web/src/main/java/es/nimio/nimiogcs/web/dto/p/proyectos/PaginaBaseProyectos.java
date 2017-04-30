package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Tabs;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

public abstract class PaginaBaseProyectos extends EstructuraPagina {

	protected TabActiva activa;
	protected Proyecto proyecto;
	protected boolean conOperacionesEnCurso;
	
	public PaginaBaseProyectos(TabActiva activa, Proyecto proyecto, boolean conOperacionesEnCurso) {
		this(activa, activa.titulo() + " para '" + proyecto.getNombre() + "'", proyecto, conOperacionesEnCurso);
	}
	
	public PaginaBaseProyectos(TabActiva activa, String titulo, Proyecto proyecto, boolean conOperacionesEnCurso) {
		super(titulo);
		this.activa = activa;
		this.proyecto = proyecto;
		this.conOperacionesEnCurso = conOperacionesEnCurso;
	}


	// -----------------------------------------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------------------------------------
	
	protected boolean sinOperaciones() { return !conOperacionesEnCurso; }
	
	protected Localizacion localizacion() {
		return new Localizacion()
				.conEnlace("Home", "/")
				.conEnlace("Proyectos", "/proyectos")
				.conEnlace(proyecto.getNombre(), "/proyectos/" + proyecto.getId())
				.conTexto(activa.titulo());
	}
	
	/**
	 * Fija la estructura base de las páginas de todos los artefactos
	 */
	protected void estructuraBaseArtefacto() {
		
		// empezamos por el localizador
		this.conComponentes(localizacion());
		
		// ahora metemos las pestañas, que primero creamos y luego añadimos
		Tabs tabs = new Tabs()
				.conTab(TabActiva.DATOS.titulo(), "/proyectos/" + proyecto.getId(), activa == TabActiva.DATOS)
				.conTab(TabActiva.ETIQUETAS.titulo(), "/proyectos/etiquetas/" + proyecto.getId(), activa == TabActiva.ETIQUETAS)
				.conTab(TabActiva.PUBLICACIONES.titulo(), "/proyectos/publicaciones/" + proyecto.getId(), activa == TabActiva.PUBLICACIONES)
				.conTab(TabActiva.ANOTACIONES.titulo(), "/proyectos/anotaciones/" + proyecto.getId(), activa == TabActiva.ANOTACIONES)
				.conTab(TabActiva.OPERACIONES.titulo(), "/proyectos/operaciones/" + proyecto.getId(), activa == TabActiva.OPERACIONES);
 
		// añadimos los componentes que correspondan a la pestaña activa
		tabs.conComponenteSi(
				conOperacionesEnCurso, 
				new PanelInformativo()
				.tipoAviso()
				.conTexto( 
						"Hay una o varias operaciones actualmente en curso. "
						+ "Las nuevas operaciones que se podrán realizar estarán condicionadas "
						+ "a la finalización de las operaciones que ya se encuentran en curso."
				)
		);
		
		tabs.conComponentes(componentesPagina());
		
		// lo añadimos a la lista de componentes
		this.conComponentes(tabs);
	}
	

	/**
	 * Donde se decide qué hay que construir
	 */
	protected abstract List<IComponente> componentesPagina();
	
}
