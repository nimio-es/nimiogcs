package es.nimio.nimiogcs.web.dto.p.admin.tipos;

import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Tabs;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

public abstract class PaginaBaseTipoArtefactos extends EstructuraPagina {

	protected TabActiva activa;
	protected TipoArtefacto tipoArtefacto;
	
	public PaginaBaseTipoArtefactos(TabActiva activa, TipoArtefacto tipoArtefacto) {
		this(activa, activa.titulo() + " para '" + tipoArtefacto.getNombre() + "'", tipoArtefacto);
	}
	
	public PaginaBaseTipoArtefactos(TabActiva activa, String titulo, TipoArtefacto artefacto) {
		super(titulo);
		this.activa = activa;
		this.tipoArtefacto = artefacto;
	}


	// -----------------------------------------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------------------------------------
	
	/**
	 * Fija la estructura base de las páginas de todos los artefactos
	 */
	protected void estructuraBaseArtefacto() {
		
		// empezamos por el localizador
		this.conComponentes(
				new Localizacion()
					.conEnlace("Home", "/")
					.conTexto("Administración")
					.conEnlace("Tipos de artefactos", "/admin/tipos")
					.conEnlace(tipoArtefacto.getNombre(), "/admin/tipos/" + tipoArtefacto.getId())
					.conTexto(activa.titulo())
		);
		
		// ahora metemos las pestañas, que primero creamos y luego añadimos
		Tabs tabs = new Tabs()
				.conTab(TabActiva.DATOS.titulo(), "/admin/tipos/" + tipoArtefacto.getId(), activa == TabActiva.DATOS);
 
		tabs.conComponentes(componentesPagina());
		
		// lo añadimos a la lista de componentes
		this.conComponentes(tabs);
	}
	

	/**
	 * Donde se decide qué hay que construir
	 */
	protected abstract List<IComponente> componentesPagina();
	
}
