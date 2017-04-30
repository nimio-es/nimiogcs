package es.nimio.nimiogcs.web.dto.p.proyectos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.componentes.proyecto.web.DefinicionOperacionPosible;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
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
public final class PaginaDatosProyecto extends PaginaBaseProyectos {

	private final boolean tieneEtiquetas;
	private final List<RelacionElementoProyectoArtefacto> artefactosAfectados;
	private final Collection<DefinicionOperacionPosible> operaciones;
	
	public PaginaDatosProyecto(
			Proyecto proyecto, 
			List<RelacionElementoProyectoArtefacto> artefactosAfectados,
			boolean tieneEtiquetas,
			boolean conOperacionesEnCurso,
			Collection<DefinicionOperacionPosible> operaciones) {
		
		super(TabActiva.DATOS, proyecto, conOperacionesEnCurso);
		
		this.tieneEtiquetas = tieneEtiquetas;
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
										
										MetodosDeUtilidad.parClaveValor("ID:", proyecto.getId()),
										MetodosDeUtilidad.parClaveValor("Nombre:", proyecto.getNombre()),
										MetodosDeUtilidad.parClaveValor("Repositorio:", proyecto.getEnRepositorio().getNombre())
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
								new TablaBasica.DefinicionColumna("Artefacto", 7),
								new TablaBasica.DefinicionColumna("Tipo", 2),
								new TablaBasica.DefinicionColumna("Acciones", 3)
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
							artefacto instanceof EvolucionArtefacto ?
									((EvolucionArtefacto)artefacto).getArtefactoAfectado().getTipoArtefacto().getNombre()
									: artefacto.getTipoArtefacto().getNombre()
					),
					
					new Botonera()
					.conAlineacionALaDerecha()
					.conBotonSi(
							!conOperacionesEnCurso && artefactosAfectados.size() > 1, 
							new BotonEnlace()
							.conTamañoMuyPequeño()
							.conTexto("Quitar")
							.deTipoAviso()
							.paraUrl("/proyectos/artefactos/quitar/" + r.getId() )
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
						.conTexto("Esta es la relación de artefactos afectados por el proyecto."),
						
						tablaArtefactos,
						
						new Botonera()
						.conAlineacionALaDerecha()
						.conBoton(
								new BotonEnlace()
								.conTamañoPequeño()
								.conTexto("Agregar otro artefacto al proyecto")
								.paraUrl("/proyectos/incluirartefacto/" + proyecto.getId())
						)
				);
	}
	
	private PanelContinente panelUsosYProyecciones() {

		// por cada uso incluiremos un subpanel
		List<IComponente> subpaneles = new ArrayList<IComponente>();
		if(proyecto.getUsosYProyecciones()!=null) {
			for(UsoYProyeccionProyecto uso: proyecto.getUsosYProyecciones()) {
				
				if(uso instanceof ProyeccionMavenDeProyecto) {
					
					subpaneles.add(
							new PanelContinente()
							.conTitulo("Proyección utilizando Maven")
							.paraTipoDefecto()
							.conComponentes(
									new Parrafo()
									.conTexto("La proyección Maven permite que el proyecto se contruya utilizando Maven desde línea de comando."),
									
									MetodosDeUtilidad.parClaveValorUrlFija(
											"Url proyecto:", 
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
						proyecto.getUsosYProyecciones() != null && proyecto.getUsosYProyecciones().size() > 0,
						subpaneles
				)
				.conComponenteSi(
						proyecto.getUsosYProyecciones() == null || proyecto.getUsosYProyecciones().size() == 0,
						new Parrafo()
						.conTexto("El proyecto no dispone de usos asociados a él.")
						.deTipoAviso()
				);
	}
	
	private List<IComponente> acciones() {
		
		ArrayList<IComponente> internos = new ArrayList<IComponente>();
		
		// el botón de borrado
		if(conOperacionesEnCurso) {
			internos.add(
					new Parrafo()
					.conTexto("Mientras el proyecto tenga operaciones en curso, no se podrá realizar ninguna operación de mantenimiento adicional")
					.deTipoAviso()
			);
		} else if(tieneEtiquetas) {
			internos.add(
					new Parrafo()
					.conTexto("Un proyecto que ya está etiquetado no podrá ser eliminado.")
					.deTipoAviso()
			);
		} else {
			internos.add(new Parrafo().conTexto("Para eliminar el proyecto pulse el botón:"));
			internos.add(
					new Botonera()
					.conAlineacionALaDerecha()
					.conBoton(
							new BotonEnlace()
							.conTamañoPequeño()
							.conTexto("Eliminar")
							.paraUrl("/proyectos/eliminar/" + proyecto.getId())
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
			r.add(new Parrafo().conTexto("El proyecto no admite o no tiene definidas otras operaciones que se puedan realizar con él."));
		} else {
			r.add(new Parrafo().conTexto("Puede lanzar las siguiente operaciones sobre el proyecto:"));
			for(DefinicionOperacionPosible op: operaciones) {
				r.add(
						new EnlaceSimple()
						.conTexto(op.getNombre())
						.paraUrl(op.getUrl() + "/" + proyecto.getId())
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
