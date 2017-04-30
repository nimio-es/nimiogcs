package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.CongelarEvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaDatosArtefacto extends PaginaBaseArtefactos {

	private Collection<RelacionElementoProyectoArtefacto> relacionesProyectosArtefactos;
	long numeroRelacionesDependencia;
	private Collection<Tuples.T2<TipoDirectiva, String>> directivasOpcionales;
	
	public PaginaDatosArtefacto(
			Artefacto artefacto, 
			boolean conOperacionesEnCurso,
			Collection<RelacionElementoProyectoArtefacto> relacionesProyectosArtefactos,
			long numeroRelacionesDependencia,
			Collection<Tuples.T2<TipoDirectiva, String>> directivasOpcionales) {
		
		super(TabActiva.DATOS, artefacto, conOperacionesEnCurso);
		
		this.relacionesProyectosArtefactos = relacionesProyectosArtefactos;
		this.numeroRelacionesDependencia = numeroRelacionesDependencia;
		this.directivasOpcionales = directivasOpcionales;
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> componentesPagina() {
		
		// panel de acciones
		PanelContinente acciones = new PanelContinente()
				.conTitulo("Acciones")
				.paraTipoDefecto()
				.conComponenteSi(
						conOperacionesEnCurso && !esEvolutivo(),
						new Parrafo()
						.conTexto("Mientras existan operaciones en curso no se puede alterar los datos del artefacto")
						.deTipoAviso()
				)
				.conComponenteSi(
						sinOperaciones() && !esEvolutivo(), 
						new Parrafo().conTexto("Puede realizar las siguientes operaciones.")
				)
				.conComponenteSi(
						sinOperaciones() && !esEvolutivo(),
						new Botonera()
						.conAlineacionALaDerecha()
						.incrustarEnLinea()
						.conBotonSi(
								relacionesProyectosArtefactos.size() == 0 && numeroRelacionesDependencia == 0,
								new BotonEnlace()
								.conTexto("Borrar")
								.deTipoAviso()
								.paraUrl("/artefactos/eliminar/" + artefacto.getId())
								.conTamañoPequeño()
						)
				)
				.conComponenteSi(
						esEvolutivo(),
						new Parrafo()
						.conTexto("Un artefacto evolutivo solo puede ser eliminado como parte de la relación con un proyecto.")
						.deTipoAviso()
				)
				.conComponentesSi(
						relacionesProyectosArtefactos.size() > 0,
						new Parrafo(" "),
						new PanelContinente()
						.paraTipoPeligro()
						.conTitulo("Limitación !")
						.conLetraPeq()
						.siendoContraible()
						.conComponentes(
								new Parrafo()
									.conTexto("No se puede eliminar un artefacto que tiene proyectos en curso. Estos son los proyectos en los que aparece el artefacto:")
									.deTipoAviso()
									.conLetraPeq()
						)
						.conComponentes(
								Collections.list(
										Streams.of(relacionesProyectosArtefactos)
										.map(new Function<RelacionElementoProyectoArtefacto, IComponente>() {
		
											@Override
											public IComponente apply(RelacionElementoProyectoArtefacto v) {
												return new Columnas()
														.conColumna(
																new Columnas.Columna()
																.conAncho(12)
																.conComponentes(
																		new EnlaceSimple(
																		v.getElementoProyecto().getNombre(), 
																		"proyectos/" + v.getElementoProyecto().getId()
																		)
																		.conLetraPeq()
																)
																.sinFilas()
														);
											}
										})
										.getEnumeration()
								)
						)
				)
				.conComponentesSi(
						numeroRelacionesDependencia > 0,
						new Parrafo(" "),
						new PanelContinente()
						.paraTipoPeligro()
						.conTitulo("Limitación !")
						.conLetraPeq()
						.siendoContraible()
						.conComponentes(
								new Parrafo()
								.conTexto("No se puede eliminar un artefacto que participa como destino en definiciones de relación de dependencias con otros artefactos.")
								.deTipoAviso()
								.conLetraPeq(),
								new Parrafo()
								.conTexto("El total de relaciones de dependencias contabilizadas en las que el artefacto participa como destino asciende a: " + numeroRelacionesDependencia)
								.conLetraPeq()
						)
				);

		// panel de actividad y validez
		PanelContinente actividad = null;
		if(!conOperacionesEnCurso && !esEvolutivo() && !esCongelacion()) {

			Artefacto aV = artefacto;
			
			boolean noValido = !aV.getEstadoValidez();
			boolean validoPeroInactivo = aV.getEstadoValidez() && !aV.getEstadoActivacion();
			boolean validoYActivo = aV.getEstadoValidez() && aV.getEstadoActivacion();
			actividad = new PanelContinente()
					.conTitulo("Validez / Actividad")
					.paraTipoDefecto()
					.conComponenteSi(noValido, new Parrafo().conTexto("El elemento consta como inválido.").deTipoAviso())
					.conComponenteSi(validoPeroInactivo, new Parrafo().conTexto("El elemento se encuentra desactivado, por lo que no podrá utilizarse como dependencia desde otros elementos. Únicamente se mantendrán las referencias ya existentes.").deTipoInfo())
					.conComponenteSi(validoYActivo, new Parrafo().conTexto("El elemento está activo, con lo que puede utilizase como dependencias en otros elementos.").deTipoExito())
					.conComponente(
							new Botonera()
							.conAlineacionALaDerecha()
							.incrustarEnLinea()
							.conBotonSi(
									noValido, 
									new BotonEnlace()
									.conTexto("Asumir como válido")
									.conTamañoPequeño()
									.deTipoPeligro()
									.paraUrl("/artefactos/validar/" + artefacto.getId())
							)
							.conBotonSi(
									validoPeroInactivo,
									new BotonEnlace()
									.conTexto("Activar")
									.conTamañoPequeño()
									.paraUrl("/artefactos/activar/" + artefacto.getId())
							)
							.conBotonSi(
									validoYActivo,
									new BotonEnlace()
									.conTexto("Desactivar")
									.conTamañoPequeño()
									.deTipoAviso()
									.paraUrl("/artefactos/desactivar/" + artefacto.getId())
							)
					);
		}
		
		PanelContinente panelSincronizar = null;
		if(artefacto instanceof ITestaferroArtefacto) {
			
			final ITestaferroArtefacto testaferro = (ITestaferroArtefacto)artefacto;

			final DirectivaRamaCodigo drc = P.of(artefacto).ramaCodigo();
			if(drc != null) {
				
				panelSincronizar = new PanelContinente()
						.conTitulo("Recalcular sincronización")
						.paraTipoDefectoSi(testaferro.getSincronizadoEstable())
						.paraTipoAvisoSi(!testaferro.getSincronizadoEstable())
						.conComponenteSi(
								testaferro.getSincronizadoEstable(),
								new Parrafo()
								.conTexto("La representación evolutiva consta como sincronizada con la rama estable.")
								.deTipoPrincipal()
						)
						.conComponenteSi(
								!testaferro.getSincronizadoEstable(),
								new Parrafo()
								.conTexto("El representación evolutiva consta como no sincronizada con la rama estable.")
								.deTipoAviso()
						)
						.conComponenteSi(testaferro instanceof EvolucionArtefacto, new Parrafo(" "))
						.conComponenteSi(testaferro instanceof EvolucionArtefacto, 
								new EnlaceSimple()
								.conTexto("Revisar sincronización código")
								.paraUrl("artefactos/evolucion/comprobarsincronizacion/" + artefacto.getId())
								.enColumna(12)
						);
			}
		}
		
		// panel para lanzar el recálculo de dependencias
		PanelContinente recalculoDependencias = null;
		DirectivaCaracterizacion dc = PT.of(artefacto.getTipoArtefacto()).caracterizacion(); 
		if(dc!=null && dc.getLibreriaExterna()) {
			if(sinOperaciones()) {
				recalculoDependencias = new PanelContinente()
						.conTitulo("Recalcular dependencias")
						.paraTipoDefecto()
						.conComponentes(
								new Parrafo()
								.conTexto("El artefacto tiene declarado comportamiento de librería externa, por lo que podrá lanzar el recálculo de las depedencias cuando lo crea necesario pulsando el siguiente enlace:"),
								
								new Parrafo(" "),
								
								new ContinenteSinAspecto()
								.enColumna(12)
								.conComponentes(
										new GlyphIcon()
										.dedoIndiceDerecha()
										.info(),
										
										new TextoSimple(" "),
										
										new EnlaceSimple()
										.conTexto("Recalcular dependencias")
										.paraUrl("artefactos/" + artefacto.getId() + "/recalculardependencias")
								)
						);
			}
		}
		
		PanelContinente otrasDirectivas = null;
		if(directivasOpcionales != null && directivasOpcionales.size() > 0) {
			otrasDirectivas = new PanelContinente()
					.conTitulo("Directivas opcionales a añadir")
					.paraTipoDefecto()
					.conComponentes(
							new Parrafo()
							.conTexto("Al artefacto se le pueden añadir las siguientes directivas opcionales:"),
							
							new Parrafo(" ")
					);
			
			for(Tuples.T2<TipoDirectiva, String> ttd: directivasOpcionales) {
				
				TipoDirectiva td = ttd._1;
				String nombre = Strings.isNullOrEmpty(ttd._2) ?
						td.getNombre() 
						: td.getNombre() + ": @" + ttd._2;
				String url = Strings.isNullOrEmpty(ttd._2) ? 
						"artefactos/" + artefacto.getId() + "/directiva/" + td.getId() + "/incluir"
						: "artefactos/" + artefacto.getId() + "/directiva/DICCIONARIO/" + ttd._2 + "/incluir";
				
				otrasDirectivas.conComponente(
						new ContinenteSinAspecto()
						.conComponentes(
								new GlyphIcon().mas(),
								new TextoSimple("  "),
								new EnlaceSimple()
								.conTexto(nombre)
								.paraUrl(url)
						)
						.enColumna(12)
				);
			}
			
			otrasDirectivas.conComponente(new Parrafo(" "));
		}
		
		// dos columnas
		return Arrays.asList(
				new IComponente[] {
						
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(8)
								.conComponentes(
										componentesFicha()
								)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(4)
								.conComponentes(acciones)
								.conComponentesSi(actividad!= null, actividad)
								.conComponentesSi(recalculoDependencias != null, recalculoDependencias)
								.conComponentesSi(panelSincronizar != null, panelSincronizar)
								.conComponentesSi(otrasDirectivas!=null, otrasDirectivas)
						)
				}
		);
	}

	private boolean esEvolutivo() {
		return artefacto instanceof EvolucionArtefacto;
	}
	
	private boolean esCongelacion() {
		return artefacto instanceof CongelarEvolucionArtefacto;
	}
	
	private Collection<IComponente> componentesFicha() {
		
		List<IComponente> componentes = new ArrayList<IComponente>();
		
		// si estamos con un evolutivo, añadimos un panel con dicha información
		if(esEvolutivo()) {
			componentes.add(
					new PanelContinente()
					.conTitulo("Evolutivo")
					.paraTipoDefecto()
					.siendoContraible()
					.conComponente(
							new PanelInformativo()
							.tipoInfo()
							.conTexto( 
									"El artefacto es un evolutivo. Los evolutivos se vinculan a otros artefactos a "
									+ "los que representan dentro de un proyecto, pero en esencia son el artefacto "
									+ "al que están evolucionando y trasladan la mayoría de las directivas "
									+ "definidas para el elemento original."
							)
					)
					.conComponente(
							new PanelContinente()
							.conTitulo("Artefacto evolucionado")
							.paraTipoDefecto()
							.conComponentes(
									new Parrafo()
									.conTexto("Enlace al artefacto que se está evolucionando."),
									
									MetodosDeUtilidad.parClaveValorUrl(
											"Artefacto:", 
											((EvolucionArtefacto)artefacto).getArtefactoAfectado().getNombre(), 
											"artefactos/" + ((EvolucionArtefacto)artefacto).getArtefactoAfectado().getId())
							)
					)
					.conComponentes(FichaArtefactoBuilder.deconstruye(artefacto))
			);
		}
		
		// si estamos ante la congelación de una evolución, tendremos que hacerlo saber
		if(esCongelacion()) {
			componentes.add(
					new PanelContinente()
					.conTitulo("Congelación evolutivo")
					.paraTipoDefecto()
					.siendoContraible()
					.conComponente(
							new PanelInformativo()
							.tipoInfo()
							.conTexto( 
									"El artefacto representa la congelación de un evolutivo. Estos artefactos hacen "
									+ "de testaferro y se vinculan a otros artefactos a "
									+ "los que representan dentro de una etiqueta de proyecto, pero en esencia son el artefacto "
									+ "al que están evolucionando y trasladan la mayoría de las directivas "
									+ "definidas para el elemento original."
							)
					)
					.conComponente(
							new PanelContinente()
							.conTitulo("Artefacto con evolución congelada")
							.paraTipoDefecto()
							.conComponentes(
									new Parrafo()
									.conTexto("Enlace al artefacto cuya evolución se ha congelado."),
									
									MetodosDeUtilidad.parClaveValorUrl(
											"Artefacto:", 
											((CongelarEvolucionArtefacto)artefacto).getArtefactoAfectado().getNombre(), 
											"artefactos/" + ((CongelarEvolucionArtefacto)artefacto).getArtefactoAfectado().getId())
							)
					)
					.conComponentes(FichaArtefactoBuilder.deconstruye(artefacto))
			);
		}
		
		// añadimos la información del artefacto
		Artefacto paraDescomponer = esEvolutivo() || esCongelacion() ? 
				((ITestaferroArtefacto)artefacto).getArtefactoAfectado() 
				: artefacto;
		
		componentes.add(
				new PanelContinente()
				.conTitulo("Composición artefacto")
				.paraTipoDefecto()
				.siendoContraible()
				.empiezaContraidoSi(esEvolutivo() || esCongelacion())
				.conComponentes(FichaArtefactoBuilder.deconstruye(paraDescomponer))
		);
				
		
		// añadimos la información del tipo
		componentes.add(
				new PanelContinente()
				.conTitulo("Directivas del tipo de artefacto")
				.paraTipoDefecto()
				.siendoContraible()
				.empiezaContraido()
				.conComponentes(FichaArtefactoBuilder.deconstruye(paraDescomponer.getTipoArtefacto()))
		);
		
		
		return componentes;
	}


}
