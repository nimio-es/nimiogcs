package es.nimio.nimiogcs.web.dto.p;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;

public final class BuilderPaginadorOperaciones {

	private BuilderPaginadorOperaciones() {}
	
	/**
	 * Fabrica un paginador a partir de una página con las operaciones, la url de redirección y los parámetros
	 * @param operaciones
	 * @param urlBase
	 * @param parametros
	 * @return
	 */
	public static Paginador fabricaPaginador(Page<Operacion> operaciones, final Map<String, Usuario> conocidos, String urlBase, String parametros) {
		
		return new Paginador()
				.conIndexador(
						new Indexador()
						.conTotalPaginas(operaciones.getTotalPages())
						.enPagina(operaciones.getNumber() + 1)
						.usarPlantillaRedireccion(urlBase)
						.usarPlantillaDeParametros(parametros)
				)
				.conContenido(
						new TablaBasica(
								true,
								Arrays.asList(
										new TablaBasica.DefinicionColumna[] {

												// --- ¿está todo bien?
												new TablaBasica.DefinicionColumna(
														"",
														1,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion o) {
																				return new GlyphIcon()
																						.exitoSi(paraExito(o))
																						.peligroSi(paraPeligro(o))
																						.avisoSi(paraAviso(o))
																						.pulgarArribaSi(paraExito(o))
																						.pulgarAbajoSi(paraPeligro(o))
																						.dedoIndiceDerechaSi(paraAviso(o));
																			}
																	
																		}
																)
																.getEnumeration()
														)
												),
												
												// --- la fecha del último cambio
												new TablaBasica.DefinicionColumna(
														"Fecha",
														1,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion e) {
																				return new TextoSimple()
																						.conTexto(DateTimeUtils.formaReducida(e.getTiempoInicio()))
																						.enNegritaSi(!paraExito(e))
																						.exitoSi(paraExito(e))
																						.peligroSi(paraPeligro(e))
																						.avisoSi(paraAviso(e));
																			}
																	
																		}
																)
																.getEnumeration()
														)
												),
												
												// --- la nombre del proceso
												new TablaBasica.DefinicionColumna(
														"Proceso",
														4,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion e) {
																				return new EnlaceSimple(e.getNombre(),
																						"operaciones/" + e.getId())
																						.conTexto(e.getNombre())
																						.enNegritaSi(!paraExito(e))
																						.exitoSi(paraExito(e))
																						.peligroSi(paraPeligro(e))
																						.avisoSi(paraAviso(e));
																			}
		
																		}
																)
																.getEnumeration()
														)
												),
												
												// --- el tipo
												new TablaBasica.DefinicionColumna(
														"Tipo", 
														1,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion e) {
																				return new TextoSimple()
																						.conTexto(e.getTipoOperacion())
																						.enNegritaSi(!paraExito(e))
																						.exitoSi(paraExito(e))
																						.peligroSi(paraPeligro(e))
																						.avisoSi(paraAviso(e));
																			}
		
																		}
																)
																.getEnumeration()
														)
												),
												
												// --- el estado
												new TablaBasica.DefinicionColumna(
														"Estado", 
														1,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion e) {
																				return new TextoSimple()
																						.conTexto(e.getEstadoEjecucionProceso().toString())
																						.enNegritaSi(!paraExito(e))
																						.exitoSi(paraExito(e))
																						.peligroSi(paraPeligro(e))
																						.avisoSi(paraAviso(e));
																			}
		
																		}
																)
																.getEnumeration()
														)
												),
		
												// --- el usuario
												new TablaBasica.DefinicionColumna(
														"Usuario", 
														2,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion e) {
																				
																				String userName = e.getUsuarioEjecuta();
																				if(conocidos.containsKey(userName))
																					userName = conocidos.get(userName).getNombre();
																				
																				return new TextoSimple()
																						.conTexto(userName)
																						.enNegritaSi(!paraExito(e))
																						.exitoSi(paraExito(e))
																						.peligroSi(paraPeligro(e))
																						.avisoSi(paraAviso(e));
																			}
		
																		}
																)
																.getEnumeration()
														)
												),
		
												// --- la duración
												new TablaBasica.DefinicionColumna(
														"Duración", 
														1,
														Collections.list(
																Streams.of(operaciones.getContent())
																.map(
																		new Function<Operacion, IComponente>() {
		
																			@Override
																			public IComponente apply(
																					Operacion e) {
																				return new TextoSimple()
																						.conTexto(e.diferenciaTiempo())
																						.enNegritaSi(!paraExito(e))
																						.exitoSi(paraExito(e))
																						.peligroSi(paraPeligro(e))
																						.avisoSi(paraAviso(e));
																			}
		
																		}
																)
																.getEnumeration()
														)
												)
		
										}
								)
						)
				);
	}
	
	
	// ---------------------------------------------
	// Auxiliares
	// ---------------------------------------------
	
	private static boolean paraExito(Operacion o) {
		return o.getEstadoEjecucionProceso() == EnumEstadoEjecucionProceso.OK;
	}
	
	private static boolean paraPeligro(Operacion o) {
		return o.getEstadoEjecucionProceso() == EnumEstadoEjecucionProceso.ERROR;
	}
	
	
	private static boolean paraAviso(Operacion o) {
		return !paraExito(o) && !paraPeligro(o);
	}
}
