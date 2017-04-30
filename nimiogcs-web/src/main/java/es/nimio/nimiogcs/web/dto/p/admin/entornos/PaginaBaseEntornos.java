package es.nimio.nimiogcs.web.dto.p.admin.entornos;

import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.EntornoConServidores;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Tabs;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

public abstract class PaginaBaseEntornos extends EstructuraPagina {

	protected TabActiva activa;
	protected DestinoPublicacion entorno;
	protected boolean conOperacionesEnCurso;
	
	public PaginaBaseEntornos(TabActiva activa, DestinoPublicacion entorno, boolean conOperacionesEnCurso) {
		this(activa, activa.titulo() + " para '" + entorno.getNombre() + "'", entorno, conOperacionesEnCurso);
	}
	
	public PaginaBaseEntornos(TabActiva activa, String titulo, DestinoPublicacion entorno, boolean conOperacionesEnCurso) {
		super(titulo);
		this.activa = activa;
		this.entorno = entorno;
		this.conOperacionesEnCurso = conOperacionesEnCurso;
	}


	// -----------------------------------------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------------------------------------
	
	protected boolean sinOperaciones() { return !conOperacionesEnCurso; }
	
	/**
	 * Fija la estructura base de las páginas de todos los artefactos
	 */
	protected void estructuraBaseArtefacto() {
		
		// empezamos por el localizador
		this.conComponentes(
				new Localizacion()
					.conEnlace("Home", "/")
					.conTexto("Administracion")
					.conTexto("Ciclo de Vida")
					.conEnlace("Destinos publicación", "/admin/ciclovida/entornos")
					.conTexto(entorno.getNombre())
					.conTexto(activa.titulo())
		);
		
		// ahora metemos las pestañas, que primero creamos y luego añadimos
		Tabs tabs = new Tabs()
				.conTab(TabActiva.DATOS.titulo(), "/admin/ciclovida/entornos/" + entorno.getId(), activa == TabActiva.DATOS)
				.conTabSi(entorno instanceof EntornoConServidores, TabActiva.SERVIDORES.titulo(), "/admin/ciclovida/entornos/servidores/" + entorno.getId(), activa == TabActiva.SERVIDORES)
				.conTab(TabActiva.OPERACIONES.titulo(), "/admin/ciclovida/entornos/operaciones/" + entorno.getId(), activa == TabActiva.OPERACIONES);
 
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
