package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;

public final class PaginaListadoPublicacionesProyecto extends PaginaBasePublicacionesProyecto {

	private ListaDatos datos;
	
	public PaginaListadoPublicacionesProyecto(
			Proyecto proyecto, 
			boolean operacionesEnCurso,
			ListaDatos datos) {
		super(SubTabPublicacionesActiva.LISTADO, proyecto, operacionesEnCurso);

		this.datos = datos;
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> subcomponentesPagina() {

		ArrayList<TablaBasica.DefinicionColumna> dfcs = new ArrayList<TablaBasica.DefinicionColumna>();
		dfcs.add(new TablaBasica.DefinicionColumna("", 1));
		dfcs.add(new TablaBasica.DefinicionColumna("Fecha", 1));
		dfcs.add(new TablaBasica.DefinicionColumna("Canal", 1));
		dfcs.add(new TablaBasica.DefinicionColumna("Estado", 1));
		dfcs.add(new TablaBasica.DefinicionColumna("Destino", 1));
		TablaBasica tb = new TablaBasica(true, dfcs);
		
		// rellenamos las líneas
		for(Publicacion p: datos.publicaciones().getContent()) {
		
			final ArrayList<IComponente> cl = new ArrayList<IComponente>();
			
			cl.add(
					new GlyphIcon()
					.pulgarArribaSi(correcta(p))
					.pulgarAbajoSi(error(p))
					.dedoIndiceDerechaSi(ejecucion(p))
					.exitoSi(correcta(p))
					.peligroSi(error(p))
					.avisoSi(ejecucion(p))
			);
			
			cl.add(
					new TextoSimple(DateTimeUtils.formaReducida(p.getCreacion()))
					.exitoSi(correcta(p))
					.peligroSi(error(p))
					.avisoSi(ejecucion(p))
			);
			
			cl.add(
					new TextoSimple(p.getCanal())
					.exitoSi(correcta(p))
					.peligroSi(error(p))
					.avisoSi(ejecucion(p))
			);
			
			cl.add(
					new TextoSimple(p.getEstado())
					.exitoSi(correcta(p))
					.peligroSi(error(p))
					.avisoSi(ejecucion(p))
			);

			DestinoPublicacion d = null;
			for(DestinoPublicacion dp: datos.destinos())
				if(dp.getId().equalsIgnoreCase(p.getIdDestinoPublicacion())) {
					d = dp;
					break;
				}
			cl.add(new TextoSimple(d != null ? d.getNombre() : ""));
			
			tb.conFila(cl);
		}	
		
		Paginador pg = new Paginador()
				.conIndexador(
						new Indexador()
						.conTotalPaginas(datos.publicaciones().getTotalPages())
						.enPagina(datos.publicaciones().getNumber() + 1)
						.usarPlantillaDeParametros("")
				)
				.conContenido(tb);
		
		return Arrays.asList(new IComponente[] {pg});
	}

	// ----
	
	public boolean correcta(Publicacion p) { return p.getEstado().equalsIgnoreCase(K.X.OK); }
	public boolean error(Publicacion p) { return p.getEstado().equalsIgnoreCase(K.X.ERROR); }
	public boolean ejecucion(Publicacion p) { return p.getEstado().equalsIgnoreCase(K.X.EJECUCION); }
	
	// ----
	
	public static final class ListaDatos 
		extends Tuples.T2<Collection<DestinoPublicacion>,Page<Publicacion>> {
		
		private static final long serialVersionUID = -7467275457434737310L;
		
		public ListaDatos(Collection<DestinoPublicacion> destinos, Page<Publicacion> publicaciones) {
			super(destinos, publicaciones);
		}

		public Collection<DestinoPublicacion> destinos() { return _1; }
		public Page<Publicacion> publicaciones() { return _2; }
		
	}
	
}
