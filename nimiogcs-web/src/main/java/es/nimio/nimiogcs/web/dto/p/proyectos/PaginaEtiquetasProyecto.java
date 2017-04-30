package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaEtiquetasProyecto extends PaginaBaseProyectos {

	private final Collection<ItemListaEtiquetas> etiquetas;
	private final int pagina, totalPaginas;

	public PaginaEtiquetasProyecto(
			Proyecto proyecto, 
			boolean operacionesEnCurso, 
			Collection<ItemListaEtiquetas> etiquetas,
			int pagina,
			int totalPaginas) {
		super(TabActiva.ETIQUETAS, proyecto, operacionesEnCurso);

		this.etiquetas = etiquetas;
		this.pagina = pagina;
		this.totalPaginas = totalPaginas;

		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------

	@Override
	protected List<IComponente> componentesPagina() {

		return Arrays
				.asList(new IComponente[] {

				new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(9)
								.conComponentes(
										etiquetas(pagina)
								)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(3)
								.conComponentes(acciones())
						) 
				}
		);
	}

	// -----------------------------------------------------
	// Partes
	// -----------------------------------------------------

	private PanelContinente etiquetas(int pagina) {

		// la tabla
		final TablaBasica tb = new TablaBasica(false,
				Arrays.asList(new TablaBasica.DefinicionColumna("Nombre", 5),
						new TablaBasica.DefinicionColumna("Fecha", 3), new TablaBasica.DefinicionColumna("Calidad", 1),
						new TablaBasica.DefinicionColumna("Acciones", 3)));

		for (ItemListaEtiquetas item : etiquetas) {
			
			final EtiquetaProyecto etiqueta = item.etiqueta();
			final boolean conPublicaciones = item.conPublicaciones();
			
			tb.conFila(
					new EnlaceSimple().conTexto(etiqueta.getNombre())
							.paraUrl("proyectos/etiquetas/etiqueta/" + etiqueta.getId()),
					new TextoSimple().conTexto(DateTimeUtils.formaReducida(etiqueta.getCreacion())),
					new GlyphIcon().exitoSi(etiqueta.calidadSuperada()).avisoSi(etiqueta.calidadPendiente())
							.peligroSi(!etiqueta.calidadPendiente() && !etiqueta.calidadSuperada())
							.pulgarArribaSi(etiqueta.calidadSuperada()).signoExclamacionSi(etiqueta.calidadPendiente())
							.pulgarAbajoSi(!etiqueta.calidadPendiente() && !etiqueta.calidadSuperada()),
					new Botonera().conAlineacionALaDerecha()
							.conBotonSi(!this.conOperacionesEnCurso,
									new BotonEnlace().conTexto("Publicar").conTamañoMuyPequeño()
											.paraUrl("/proyectos/etiquetas/publicar/" + proyecto.getId() + "/"
													+ etiqueta.getId()))
							.conBotonSi(
									!this.conOperacionesEnCurso && !conPublicaciones,
									new BotonEnlace().conTexto("Eliminar").conTamañoMuyPequeño()
											.paraUrl("/proyectos/etiquetas/eliminar/" + proyecto.getId() + "/"
													+ etiqueta.getId())
											.deTipoAviso()));
		}

		// devolvemos el panel con el paginador
		return new PanelContinente().paraTipoPrimario().conTitulo("Etiquetas").conComponente(

		new Paginador().conIndexador(new Indexador().conTotalPaginas(totalPaginas).enPagina(pagina)
				.usarPlantillaRedireccion("proyectos/etiquetas/" + proyecto.getId())
				.usarPlantillaDeParametros("pag=%d")).conContenido(tb));
	}

	private PanelContinente acciones() {

		return new PanelContinente().paraTipoDefecto().conTitulo("Acciones")
				.conComponentes(new Parrafo().conTexto("Con el siguiente enlace podrá añadir una nueva etiqueta:"),

		new EnlaceSimple("Etiquetar", "proyectos/etiquetas/nueva/" + proyecto.getId()).enColumna(12));
	}
	
	// ----
	
	public static final class ItemListaEtiquetas extends Tuples.T2<EtiquetaProyecto, Boolean> {

		private static final long serialVersionUID = 3909953576370607125L;

		public ItemListaEtiquetas(EtiquetaProyecto etiqueta, Boolean conPublicaciones) {
			super(etiqueta, conPublicaciones);
		}
		
		public EtiquetaProyecto etiqueta() { return _1; }
		public boolean conPublicaciones() { return _2; }
		
	}
}
