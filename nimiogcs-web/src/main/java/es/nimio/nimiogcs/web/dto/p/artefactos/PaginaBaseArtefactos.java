package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.List;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Tabs;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

public abstract class PaginaBaseArtefactos extends EstructuraPagina {

	protected TabActiva activa;
	protected Artefacto artefacto;
	protected boolean conOperacionesEnCurso;
	
	public PaginaBaseArtefactos(TabActiva activa, Artefacto artefacto, boolean conOperacionesEnCurso) {
		this(activa, activa.titulo() + " para '" + artefacto.getTipoArtefacto().getNombre() + "'", artefacto, conOperacionesEnCurso);
	}
	
	public PaginaBaseArtefactos(TabActiva activa, String titulo, Artefacto artefacto, boolean conOperacionesEnCurso) {
		super(titulo);
		this.activa = activa;
		this.artefacto = artefacto;
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
					.conEnlace("Artefactos", "/artefactos")
					.conEnlaceYParametros(
							artefacto.getTipoArtefacto().getNombre(), 
							"/artefactos", 
							"tipo=" + artefacto.getTipoArtefacto().getId())
					.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
					.conTexto(activa.titulo())
		);
		
		// ahora metemos las pestañas, que primero creamos y luego añadimos
		Tabs tabs = new Tabs()
				.conTab(TabActiva.DATOS.titulo(), "/artefactos/" + artefacto.getId(), activa == TabActiva.DATOS)
				.conTab(TabActiva.DEPENDENCIAS.titulo(), "/artefactos/dependencias/" + artefacto.getId(), activa == TabActiva.DEPENDENCIAS)
				.conTabSi(P.of(artefacto).repositorioCodigo() != null, TabActiva.CODIGO.titulo(), "/artefactos/codigo/" + artefacto.getId(), activa == TabActiva.CODIGO)
				.conTabSi(tieneDirectivaPublicacion(artefacto),  TabActiva.PUBLICACIONES.titulo(), "/artefactos/publicaciones/" + artefacto.getId(), activa == TabActiva.PUBLICACIONES)
				.conTab(TabActiva.OPERACIONES.titulo(), "/artefactos/operaciones/" + artefacto.getId(), activa == TabActiva.OPERACIONES);
 
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
	
	private boolean tieneDirectivaPublicacion(Artefacto artefacto) {
		return 
				P.of(artefacto).publicacionDeployer() != null
				|| P.of(artefacto).publicacionJenkins() != null;
	}
	
}
