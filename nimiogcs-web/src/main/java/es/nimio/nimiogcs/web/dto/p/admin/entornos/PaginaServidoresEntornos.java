package es.nimio.nimiogcs.web.dto.p.admin.entornos;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaServidoresEntornos extends PaginaBaseEntornos {

	final Collection<RelacionEntornoServidor> servidores;
	
	public PaginaServidoresEntornos(
			DestinoPublicacion entorno, 
			boolean conOperacionesEnCurso,
			Collection<RelacionEntornoServidor> servidores) {
		
		super(TabActiva.SERVIDORES, entorno, conOperacionesEnCurso);
		this.servidores = servidores;
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> componentesPagina() {
		
		// dos columnas
		return Arrays.asList(
				new IComponente[] {

						// Dos columnas
						new Columnas()
							// 1 con el listado de los servidores
							.conColumna(
									new Columnas.Columna()
									.conComponentes(
											componentesColumna1()
									)
									.conAncho(9)
							)
							.conColumna(
									new Columnas.Columna()
									.conComponentes(
											componentesColumna2()
									)
									.conAncho(3)
							)
						
				}
		);
	}

	private IComponente[] componentesColumna1() {
		
		return new IComponente[] {
				
				// panel que contiene la tabla de servidores
				new PanelContinente()
				.conTitulo("Servidores")
				.paraTipoInfo()
				.conComponenteSi(
						servidores.size() == 0,
						new Parrafo()
						.conTexto("No hay servidores vinculados al entorno.")
						.deTipoInfo()
				)
				.conComponenteSi(
						servidores.size() > 0,
						new TablaBasica(
								false,
								Arrays.asList(
										new TablaBasica.DefinicionColumna[] {
												
												new TablaBasica.DefinicionColumna(
														"Servidor", 
														8, 
														Collections.list(
																Streams.of(servidores)
																.map(
																		new Function<RelacionEntornoServidor, IComponente>() {

																			@Override
																			public IComponente apply(
																					RelacionEntornoServidor v) {
																				
																				return new EnlaceSimple()
																						.conTexto(v.getServidor().getNombre())
																						.paraUrl("admin/ciclovida/sitios/" + v.getServidor().getId());
																			}
																		}
																)
																.getEnumeration()
														)
												),
												
												new TablaBasica.DefinicionColumna(
														"Acciones", 
														4, 
														Collections.list(
																Streams.of(servidores)
																.map(
																		new Function<RelacionEntornoServidor, IComponente>() {

																			@Override
																			public IComponente apply(
																					RelacionEntornoServidor v) {
																				
																				return new Botonera()
																						.conAlineacionALaDerecha()
																						.conBoton(
																								new BotonEnlace(
																										"Desvincular", 
																										"admin/ciclovida/entornos/servidores/desvincular/" + entorno.getId() + "/" + v.getServidor().getId()
																								)
																								.deTipoAviso()
																								.conTamañoMuyPequeño()
																						);
																			}
																		}
																)
																.getEnumeration()
														)
												),
												
										}
								)
						)
				)
		};
	}
	
	
	private IComponente[] componentesColumna2() {
		
		return new IComponente[] {
				
				// panel donde se nos informará que podemos agregar otra relación con el entorno
				new PanelContinente()
				.paraTipoDefecto()
				.conTitulo("Agregar servidor")
				.conComponentes(
						
						new Parrafo()
						.conTexto("Puede agregar un nuevo servidor al entorno pulsando el siguiente enlace:"),
						
						new EnlaceSimple()
						.conTexto("Agregar servidor")
						.paraUrl("admin/ciclovida/entornos/servidores/vincular/" + entorno.getId())
						.enColumna(12)
				)
		};
	}
}
