package es.nimio.nimiogcs.web.dto.p.etiquetas;

import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Tabs;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

public abstract class PaginaBaseEtiquetas extends EstructuraPagina {

	protected TabActiva activa;
	protected EtiquetaProyecto etiqueta;
	protected boolean conOperacionesEnCurso;
	
	public PaginaBaseEtiquetas(TabActiva activa, EtiquetaProyecto etiqueta, boolean conOperacionesEnCurso) {
		this(activa, activa.titulo() + " para '" + etiqueta.getNombre() + "'", etiqueta, conOperacionesEnCurso);
	}
	
	public PaginaBaseEtiquetas(TabActiva activa, String titulo, EtiquetaProyecto etiqueta, boolean conOperacionesEnCurso) {
		super(titulo);
		this.activa = activa;
		this.etiqueta = etiqueta;
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
				.conEnlace(etiqueta.getProyecto().getNombre(), "/proyectos/" + etiqueta.getProyecto().getId())
				.conEnlace("Etiquetas", "/proyectos/etiquetas/" + etiqueta.getProyecto().getId())
				.conEnlace(etiqueta.getNombre(), "/proyectos/etiquetas/etiqueta/" + etiqueta.getId())
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
				.conTab(TabActiva.DATOS.titulo(), "/proyectos/etiquetas/etiqueta/" + etiqueta.getId(), activa == TabActiva.DATOS)
				.conTab(TabActiva.OPERACIONES.titulo(), "/proyectos/etiquetas/etiqueta/" + etiqueta.getId() + "/operaciones", activa == TabActiva.OPERACIONES);
 
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
