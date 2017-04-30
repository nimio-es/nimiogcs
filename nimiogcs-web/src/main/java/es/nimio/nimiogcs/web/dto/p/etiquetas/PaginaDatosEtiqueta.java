package es.nimio.nimiogcs.web.dto.p.etiquetas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.componentes.proyecto.web.DefinicionOperacionPosible;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaDatosEtiqueta extends PaginaBaseEtiquetas {

	private final List<RelacionElementoProyectoArtefacto> artefactosAfectados;
	private final Collection<DefinicionOperacionPosible> operaciones;
	
	public PaginaDatosEtiqueta(
			EtiquetaProyecto etiqueta, 
			List<RelacionElementoProyectoArtefacto> artefactosAfectados,
			boolean conOperacionesEnCurso,
			Collection<DefinicionOperacionPosible> operaciones) {
		
		super(TabActiva.DATOS, etiqueta, conOperacionesEnCurso);
		
		this.artefactosAfectados = artefactosAfectados;
		this.operaciones = operaciones;
		
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
						
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(8)
								.conComponentes(
										ficha()
								)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(4)
								.conComponentes(acciones())
								.conComponentes(operacionesAdicionales())
								
						)
				}
		);
	}

	private List<IComponente> ficha() {
		return 
				Arrays.asList(
						new IComponente[] {
								
								// datos generales
								new PanelContinente()
								.conTitulo("Datos generales")
								.paraTipoDefecto()
								.siendoContraible()
								.conComponentes(
										new Parrafo()
										.conTexto("Datos base del proyecto."),
										
										MetodosDeUtilidad.parClaveValor("ID:", etiqueta.getId()),
										MetodosDeUtilidad.parClaveValor("Nombre:", etiqueta.getNombre()),
										MetodosDeUtilidad.parClaveValorUrl(
												"Proyecto:", 
												etiqueta.getProyecto().getNombre(),
												"proyectos/" + etiqueta.getProyecto().getId()
										)
								),
								
								// artefactos afectados
								panelArtefactosAfectados(), 
								
								// paneles usos y proyecciones
								panelUsosYProyecciones()
						}
				);
	}
	
	private PanelContinente panelArtefactosAfectados() {
		
		// creamos la tabla
		
		final TablaBasica tablaArtefactos = 
				new TablaBasica(
						false,
						Arrays.asList(
								new TablaBasica.DefinicionColumna("Artefacto", 9),
								new TablaBasica.DefinicionColumna("Tipo", 3)
						)
				);
		
		// rellenamos las filas
		for(RelacionElementoProyectoArtefacto r: artefactosAfectados) {
			
			// lo que nos interesa es el artefacto
			final Artefacto artefacto = r.getArtefacto();

			tablaArtefactos.conFila(
					new EnlaceSimple()
					.conTexto(artefacto.getNombre())
					.paraUrl("artefactos/dependencias/" + artefacto.getId()),
					
					new TextoSimple()
					.conTexto(
							artefacto instanceof ITestaferroArtefacto ?
									((ITestaferroArtefacto)artefacto).getArtefactoAfectado().getTipoArtefacto().getNombre()
									: artefacto.getTipoArtefacto().getNombre()
					)
			);
		}
		
		// queda devolver el panel
		return 
				new PanelContinente()
				.conTitulo("Artefactos afectados")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponentes(
						new Parrafo()
						.conTexto("Esta es la relación de artefactos congelados por la etiqueta."),
						
						tablaArtefactos
				);
	}
	
	private PanelContinente panelUsosYProyecciones() {

		// por cada uso incluiremos un subpanel
		List<IComponente> subpaneles = new ArrayList<IComponente>();
		if(etiqueta.getUsosYProyecciones()!=null) {
			for(UsoYProyeccionProyecto uso: etiqueta.getUsosYProyecciones()) {
				
				if(uso instanceof ProyeccionMavenDeProyecto) {
					
					subpaneles.add(
							new PanelContinente()
							.conTitulo("Proyección utilizando Maven")
							.paraTipoDefecto()
							.conComponentes(
									new Parrafo()
									.conTexto("La proyección Maven permite que la etiqueta se contruya utilizando Maven desde línea de comando."),
									
									MetodosDeUtilidad.parClaveValorUrlFija(
											"Url etiqueta:", 
											((ProyeccionMavenDeProyecto)uso).getUrlRepositorio(),
											((ProyeccionMavenDeProyecto)uso).getUrlRepositorio()
									)
							)
					);				
				}
			}
		}
		
		// panel conteniendo los subpaneles
		return new PanelContinente()
				.paraTipoDefecto()
				.conTitulo("Usos y proyecciones")
				.siendoContraible()
				.conComponentesSi(
						etiqueta.getUsosYProyecciones() != null && !etiqueta.getUsosYProyecciones().isEmpty(),
						subpaneles
				)
				.conComponenteSi(
						etiqueta.getUsosYProyecciones() == null || etiqueta.getUsosYProyecciones().isEmpty(),
						new Parrafo()
						.conTexto("La etiqueta no dispone de usos asociados a ella.")
						.deTipoAviso()
				);
	}
	
	private List<IComponente> acciones() {
		
		ArrayList<IComponente> internos = new ArrayList<IComponente>();
		
		// el botón de borrado
		if(conOperacionesEnCurso) {
			internos.add(
					new Parrafo()
					.conTexto("Mientras la etiqueta tenga operaciones en curso, no se podrá realizar ninguna operación de mantenimiento adicional")
					.deTipoAviso()
			);
		} else {
			internos.add(new Parrafo().conTexto("Para eliminar la etiqueta pulse el botón:"));
			internos.add(
					new Botonera()
					.conAlineacionALaDerecha()
					.conBoton(
							new BotonEnlace()
							.conTamañoPequeño()
							.conTexto("Publicar")
							.paraUrl("/proyectos/etiquetas/publicar/" + etiqueta.getProyecto().getId() + "/" + etiqueta.getId())
					)
					.conBoton(
							new BotonEnlace()
							.conTamañoPequeño()
							.conTexto("Eliminar")
							.paraUrl("/proyectos/etiquetas/eliminar/" + etiqueta.getProyecto().getId() + "/" + etiqueta.getId())
							.deTipoAviso()
					)
			);
		}
		 
		return 
				Arrays.asList(
						new IComponente[] {
								new PanelContinente()
								.conTitulo("Acciones posibles")
								.paraTipoDefecto()
								.conComponentes(internos)
						}
				);
	}
	
	private List<IComponente> operacionesAdicionales() {
		
		ArrayList<IComponente> r = new ArrayList<IComponente>();
	
		if(operaciones.size() == 0) {
			r.add(new Parrafo().conTexto("La etiqueta no admite o no tiene definidas otras operaciones que se puedan realizar con ella."));
		} else {
			r.add(new Parrafo().conTexto("Puede lanzar las siguiente operaciones sobre la etiqueta:"));
			for(DefinicionOperacionPosible op: operaciones) {
				r.add(
						new EnlaceSimple()
						.conTexto(op.getNombre())
						.paraUrl(op.getUrl() + "/" + etiqueta.getId())
						.enColumna(12)
				);
			}
		}
		
		return 
				Arrays.asList(
						new IComponente[] {
								new PanelContinente()
								.conTitulo("Otras operaciones")
								.paraTipoDefecto()
								.conComponentes(r)
						}
				);
	}

}
