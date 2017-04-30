package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaPublicacionesArtefacto extends PaginaBaseArtefactos {

	private Page<Publicacion> publicaciones;
	private Collection<DestinoPublicacion> destinos;
	private Collection<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>> registros;
	
	public PaginaPublicacionesArtefacto(
			Artefacto artefacto, 
			boolean conOperacionesEnCurso,
			Page<Publicacion> publicaciones,
			Collection<DestinoPublicacion> destinos,
			Collection<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>> registros) {
		
		super(TabActiva.PUBLICACIONES, artefacto, conOperacionesEnCurso);
		
		this.publicaciones = publicaciones;
		this.destinos = destinos;
		this.registros = registros;
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> componentesPagina() {
	
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		// en función de tener o no cosas que mostrar pondrá un panel o 
		// una tabla con las publicaciones
		if(publicaciones.getContent().size() > 0) {
		
			// las columas
			List<TablaBasica.DefinicionColumna> columnas = new ArrayList<TablaBasica.DefinicionColumna>();
			columnas.add(new TablaBasica.DefinicionColumna("", 1));  // -- las fijas
			columnas.add(new TablaBasica.DefinicionColumna("Fecha", 1));
			columnas.add(new TablaBasica.DefinicionColumna("Canal", 1));
			columnas.add(new TablaBasica.DefinicionColumna("Estado", 1));
			for(DestinoPublicacion dp: destinos) 
				columnas.add(
						new TablaBasica.DefinicionColumna(
							new EnlaceSimple(dp.getNombre(), "admin/ciclovida/entornos/" + dp.getId()), 
							2
						)
				);
			
			// la tabla
			TablaBasica tb = new TablaBasica(true, columnas);
			
			// las filas
			for(Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]> r: registros) {
				
				final boolean conError = conError(r._3);
				final boolean correcta = correcta(r._3);
				final boolean ejecucion = ejecucion(r._3);
				
				ArrayList<IComponente> componentesFila = new ArrayList<IComponente>();
				
				// el dedito
				componentesFila.add(
						new GlyphIcon()
						.pulgarArribaSi(correcta)
						.pulgarAbajoSi(conError)
						.dedoIndiceDerechaSi(ejecucion)
						.exitoSi(correcta)
						.peligroSi(conError)
						.avisoSi(ejecucion)
				);
				
				// fecha
				componentesFila.add(
						new TextoSimple(DateTimeUtils.formaReducida(r._1))
						.avisoSi(ejecucion)
						.exitoSi(correcta)
						.peligroSi(conError)
				);
				
				// canal
				componentesFila.add(
						new TextoSimple().conTexto(r._2)
						.avisoSi(ejecucion)
						.exitoSi(correcta)
						.peligroSi(conError)
				);
				
				// estado
				componentesFila.add(
						new TextoSimple(r._3)
						.avisoSi(ejecucion)
						.exitoSi(correcta)
						.peligroSi(conError)
				);
	
				final int destinos = r._4.length; 
				for(int i=0; i < destinos; i++) {
					final boolean usarEnlace = r._4[i] != null;
					if(r._5[i]!=null) {
						if(usarEnlace) 
							componentesFila.add(
									new EnlaceSimple()
									.conTexto(r._5[i])
									.paraUrl("proyectos/etiquetas/" + r._4[i].getProyecto().getId())
									.avisoSi(ejecucion)
									.exitoSi(correcta)
									.peligroSi(conError)
							);
						else 
							componentesFila.add(
									new TextoSimple(r._5[i])
									.avisoSi(ejecucion)
									.exitoSi(correcta)
									.peligroSi(conError)
							);
					} else {
						componentesFila.add(
								new TextoSimple(" ")
						);
					}
				}
				
				// añadimos la fila
				tb.conFila(componentesFila);
			}
			
			// creamos la páginación
			componentes.add(
					new Paginador()
					.conIndexador(
							new Indexador()
							.conTotalPaginas(publicaciones.getTotalPages())
							.enPagina(publicaciones.getNumber() + 1)
							.usarPlantillaRedireccion("/artefactos/publicaciones/" + artefacto.getId())
							.usarPlantillaDeParametros("pag=%d")
					)
					.conContenido(tb)
			);
			
		} else {
			
			componentes.add(
					new PanelInformativo()
					.conTexto("No consta ninguna publicación para este artefacto.")
					.tipoInfo()
			);
		}
		
		// dos columnas
		return componentes;
	}
	
	private boolean conError(String estado) {
		return estado.equalsIgnoreCase(K.X.ERROR);
	}
	
	private boolean correcta(String estado) {
		return estado.equalsIgnoreCase(K.X.OK);
	}
	
	private boolean ejecucion(String estado) {
		return estado.equalsIgnoreCase(K.X.EJECUCION);
	}

}
