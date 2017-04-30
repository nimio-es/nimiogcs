package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.SaltoDeLinea;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaTablaPublicacionesProyecto extends PaginaBasePublicacionesProyecto {

	private TablaDatos datos;
	
	public PaginaTablaPublicacionesProyecto(
			Proyecto proyecto, 
			boolean operacionesEnCurso,
			TablaDatos datos) {
		super(SubTabPublicacionesActiva.TABLA, proyecto, operacionesEnCurso);

		this.datos = datos;
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> subcomponentesPagina() {

		// columnas y tabla
		ArrayList<TablaBasica.DefinicionColumna> dscs = new ArrayList<TablaBasica.DefinicionColumna>();
		dscs.add(new TablaBasica.DefinicionColumna(new EnlaceSimple("Artefactos", "artefactos"), 3));
		for(DestinoPublicacion dp: datos.destinos()) {
			dscs.add(
					new TablaBasica.DefinicionColumna(
							new EnlaceSimple(dp.getNombre(), "admin/ciclovida/entornos/" + dp.getId()),
							2
					)
			);
		}
		TablaBasica tb = new TablaBasica(true, dscs);
		
		// las filas y las columnas
		int fila = 0;
		for(ArtefactoProyecto ap: datos.artefactos()) {
			
			ArrayList<IComponente> cf = new ArrayList<IComponente>();
			
			cf.add(ap.comoComponenteWeb());
			
			for(int j = 0; j < datos.destinos().size(); j++) 
				cf.add(datos.celdas()[fila][j].comoComponenteWeb());
			
			tb.conFila(cf);
			
			fila++;
		}
		
		return Arrays.asList(
				new IComponente[] 
						{
								new PanelContinente()
								.conTitulo("Resumen de publicaciones del proyecto")
								.paraTipoPrimario()
								.conComponentes(
										new Parrafo()
										.conTexto("Tabla resumen que recoge las publicaciones de este proyecto para cada uno de los destinos de publicación registrados."),
										new Parrafo(" "),
										tb,
										new Parrafo(" "),
										new Parrafo("Aquí no se indica si el destino lo ocupa otro proyecto.")
										.deTipoAviso()
										.conLetraPeq()
										
								)
						}
		);
	}
	
	
	// ----
	
	public static final class TablaDatos 
		extends Tuples.T3<Collection<DestinoPublicacion>, Collection<ArtefactoProyecto>, DatoCelda[][]> {

		private static final long serialVersionUID = -971916907068051694L;

		public TablaDatos(Collection<DestinoPublicacion> destinos, Collection<ArtefactoProyecto> artefactos, DatoCelda[][] valores) {
			super(destinos, artefactos, valores);
		}
		
		public Collection<DestinoPublicacion> destinos() { return _1; }
		public Collection<ArtefactoProyecto> artefactos() { return _2; }
		public DatoCelda[][] celdas() { return _3; }
	}
	
	public static final class ArtefactoProyecto extends Tuples.T2<Artefacto, Artefacto> {

		private static final long serialVersionUID = 5111704008005396923L;

		public ArtefactoProyecto(Artefacto base, Artefacto evolutivo) {
			super(base, evolutivo);
		}
		
		public Artefacto base() { return _1; }
		public Artefacto evolutivo() { return  _2; }
		public boolean mismo() { return _1.getId().equalsIgnoreCase(_2.getId()); }
		
		public IComponente comoComponenteWeb() {
			if(mismo()) {
				return 
						new EnlaceSimple(base().getNombre(), "artefactos/" + base().getId());
			} 

			return 
					new ContinenteSinAspecto()
					.conComponentes(
							new EnlaceSimple(base().getNombre(), "artefactos/publicaciones/" + base().getId()),
							
							new SaltoDeLinea(),

							new EnlaceSimple(evolutivo().getNombre(), "artefactos/" + evolutivo().getId())
							.conLetraPeq()
					);
		}
	}

	public static abstract class DatoCelda {
		public abstract IComponente comoComponenteWeb();
	}
	
	public static final class DatoCeldaNoAplica extends DatoCelda {
		public IComponente comoComponenteWeb() {
			return new TextoSimple("No aplica");
		}
	}
	
	public static final class DatoCeldaSinPublicacion extends DatoCelda {
		public IComponente comoComponenteWeb() {
			return new TextoSimple("--");
		}
	}
	
	public static final class DatoCeldaPublicacion extends DatoCelda {
		
		private final PublicacionArtefacto publicacion; 
		private final EtiquetaProyecto etiqueta;
		
		public DatoCeldaPublicacion(PublicacionArtefacto publicacion, EtiquetaProyecto etiqueta) {
			this.publicacion = publicacion;
			this.etiqueta = etiqueta;
		}
		
		public IComponente comoComponenteWeb() {
			return 
					new ContinenteSinAspecto()
					.conComponentes(
							new TextoSimple(DateTimeUtils.formaReducida(publicacion.getPublicacion().getCreacion())),
							new SaltoDeLinea(),
							new EnlaceSimple(etiqueta.getNombre(), "proyectos/etiquetas/" + etiqueta.getProyecto().getId()),
							new SaltoDeLinea(),
							new TextoSimple("(" + publicacion.getPublicacion().getCanal() + ")")
							.conLetraPeq()
					);
		}
	}
	
}
