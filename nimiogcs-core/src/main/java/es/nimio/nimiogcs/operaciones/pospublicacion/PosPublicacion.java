package es.nimio.nimiogcs.operaciones.pospublicacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.operaciones.OperacionInternaBase;

/**
 * Operación a ejecutar cuando concluya un canal
 */
public class PosPublicacion 
	extends OperacionInternaBase<IContextoEjecucionBase, String, Boolean> {

	public PosPublicacion(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ----
	
	@Override
	protected String nombreUnicoOperacion(String idPublicacion, Operacion op) {
		return "LANZAR OPERACIONES DE POSPUBLICACIÓN DE LOS ARTEFACTOS PUBLICADOS POR EL CANAL '"
				+ ce.repos().publicaciones().buscar(idPublicacion).getCanal() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(String datos, Operacion op) {
		
	}

	@Override
	protected Boolean hazlo(String idPublicacion, Operacion op) throws ErrorInesperadoOperacion {

		// cargamos los datos de la publicación
		final Publicacion publicacion = ce.repos().publicaciones().buscar(idPublicacion);
		registraRelacionConOperacion(op, publicacion);
		registraRelacionConOperacion(
				op, 
				ce.repos().destinosPublicacion().buscar(publicacion.getIdDestinoPublicacion())
		);
		
		// recogemos los canales
		ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_precanales = canalesOrdenadosSegunPrioridad();
		
		// nos quedamos a partir del que acaba de ser usado
		boolean encontrado = false;
		ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = new ArrayList<Tuples.T2<Integer,ICanalPublicacion>>();
		for(Tuples.T2<Integer, ICanalPublicacion> tc: c_precanales) {
			if(tc._2.descripcionCanal().getNombre().equalsIgnoreCase(publicacion.getCanal())) { 
				encontrado = true;
			} else {
				if(!encontrado) continue;
				c_canales.add(tc);
			}
		}
		
		// para cada uno de los artefactos afectados en la publicación, tendremos
		// que ver si es necesario publicar en otro canal
		for(final PublicacionArtefacto pa: ce.repos().artefactosPublicados().elementosPublicados(publicacion)) {
			
			final Artefacto artefacto = pa.getArtefacto(); 
			final EtiquetaProyecto etiqueta = Strings.isNotEmpty(pa.getIdEtiqueta()) ? 
					ce.repos().elementosProyectos().etiquetas().buscar(pa.getIdEtiqueta()) 
					: null;
			if(etiqueta == null) continue;
			
			// vamos preguntando a cada uno de los elementos de la lista final de canales 
			// si es posible publicar a través de ella.
			for(Tuples.T2<Integer, ICanalPublicacion> tc: c_canales) {
				
				if(tc._2.posiblePublicarArtefacto(etiqueta, artefacto)!=null) {
					
					// lanzamos la publicación
					tc._2.ejecutarPublicacion(
							new IDatosPeticionPublicacion() {
								
								@Override
								public String getUsuario() {
									return  ce.usuario() != null ?
											ce.usuario().getNombre().toUpperCase()
											: "DESCONOCIDO";
								}
								
								@Override
								public Map<String, String> getParametrosCanal() {
									return new HashMap<String, String>();
								}
								
								@Override
								public EtiquetaProyecto getEtiqueta() {
									return etiqueta;
								}
								
								@Override
								public String getCanal() {
									return publicacion.getCanal();
								}
								
								@Override
								public Collection<Artefacto> getArtefactos() {
									return Arrays.asList(artefacto);
								}
							}
					);
					
					// y concluimos el trabajo con este artefacto
					break;
				}				
			}
		}
		
		return true;
	}

	// ---

	private ArrayList<Tuples.T2<Integer, ICanalPublicacion>> canalesOrdenadosSegunPrioridad() {

		// cargamos los publicadores en un array en el que, además, añadiremos la prioridad
		// para ordenarlo de esta forma
		final ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = new ArrayList<Tuples.T2<Integer,ICanalPublicacion>>();
		for(ICanalPublicacion cp: ce.componentes(ICanalPublicacion.class)) {
			DescripcionCanal dc = cp.descripcionCanal();
			c_canales.add(
					Tuples.tuple(dc.getPrioridad(), cp)
			);
		}
		Collections.sort(
				c_canales,
				new Comparator<Tuples.T2<Integer, ICanalPublicacion>>() {

					@Override
					public int compare(T2<Integer, ICanalPublicacion> o1, T2<Integer, ICanalPublicacion> o2) {
						return o1._1.compareTo(o2._1);
					}
				}
		);
		return c_canales;
	}
	
}
