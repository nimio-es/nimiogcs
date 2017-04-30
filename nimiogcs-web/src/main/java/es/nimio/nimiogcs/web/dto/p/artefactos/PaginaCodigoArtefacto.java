package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceExterno;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaCodigoArtefacto extends PaginaBaseArtefactos {

	private List<RelacionElementoProyectoArtefacto> proyectosArtefacto;
	
	public PaginaCodigoArtefacto(
			Artefacto artefacto, 
			boolean operacionesEnCurso,
			List<RelacionElementoProyectoArtefacto> proyectosArtefacto) {
		super(TabActiva.CODIGO, artefacto, operacionesEnCurso);
		this.proyectosArtefacto = proyectosArtefacto;
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------

	private DirectivaRepositorioCodigo codigo() { 
		return P.of(artefacto).repositorioCodigo(); 
	}
	
	@Override
	protected List<IComponente> componentesPagina() {

		return Arrays.asList( 
				new IComponente[] {
						
						// --- panel con los datos de repositorio y estrategia de código
						new PanelContinente()
						.conTitulo("Repositorio y estrategia de código")
						.paraTipoDefecto()
						.siendoContraible()
						.conComponente(MetodosDeUtilidad.parClaveValor("Repositorio (nombre):", codigo().getRepositorio().getNombre()))
						.conComponente(MetodosDeUtilidad.parClaveValorUrlFija("URI repositorio:",codigo().getRepositorio().getUriRaizRepositorio(),codigo().getRepositorio().getUriRaizRepositorio() ))
						.conComponente(MetodosDeUtilidad.parClaveValorUrlFija("Subruta artefacto: ", codigo().getParcialEtiquetas(),codigo().getRepositorio().getUriRaizRepositorio()+codigo().getParcialEtiquetas() )),
						
						// --- lista de los proyectos y las ramas que están asociadas con el artefacto
						new PanelContinente()
						.conTitulo("Proyectos / Ramas")
						.paraTipoDefecto()
						.siendoContraible()
						.conComponente(tablaProyectosRamas())
				}
		);
	}
	
	private TablaBasica tablaProyectosRamas() {
		return new TablaBasica(
				false,
				Arrays.asList(
						new TablaBasica.DefinicionColumna(
								"Rama / Proyecto", 
								4,
								Collections.list(
										Streams.of(proyectosArtefacto)
										.map(new Function<RelacionElementoProyectoArtefacto, IComponente>() {

											@Override
											public IComponente apply(RelacionElementoProyectoArtefacto v) {
												return new EnlaceSimple()
														.conTexto(v.getElementoProyecto().getNombre().toLowerCase())
														.paraUrl("proyectos/" + v.getElementoProyecto().getId());
											}
											
										})
										.getEnumeration()
								)
						),
						
						new TablaBasica.DefinicionColumna(
								"Url", 
								8,
								Collections.list(
										Streams.of(proyectosArtefacto)
										.map(new Function<RelacionElementoProyectoArtefacto, IComponente>() {

											@Override
											public IComponente apply(RelacionElementoProyectoArtefacto v) {
												
												ProyeccionMavenDeProyecto pmvn = null;
												Proyecto p = (Proyecto)v.getElementoProyecto();
												for(UsoYProyeccionProyecto uso: p.getUsosYProyecciones())
													if(uso instanceof ProyeccionMavenDeProyecto) pmvn = (ProyeccionMavenDeProyecto)uso;
												
												if(pmvn==null) return new TextoSimple("-");
												
												return new EnlaceExterno()
														.conTexto(pmvn.getUrlRepositorio())
														.paraUrl(pmvn.getUrlRepositorio());
											}
											
										})
										.getEnumeration()
								)
						)						
				)
		);
	}
}
