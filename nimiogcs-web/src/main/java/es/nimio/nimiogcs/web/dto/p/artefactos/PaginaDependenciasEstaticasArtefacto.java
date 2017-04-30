package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConAlcance;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConModuloWeb;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.SaltoDeLinea;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.enlinea.FormularioEnLinea;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaDependenciasEstaticasArtefacto extends PaginaDependenciasArtefactoBase {

	private List<Dependencia> requeridos;
	private List<Dependencia> dependientes;
	
	public PaginaDependenciasEstaticasArtefacto(
			Artefacto artefacto, 
			boolean operacionesEnCurso,
			List<Dependencia> requeridos,
			List<Dependencia> dependientes) {
		super(TabDependenciasActiva.ESTATICAS, artefacto, operacionesEnCurso);
		
		this.requeridos = requeridos;
		this.dependientes = dependientes; 
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------

	@Override
	protected List<IComponente> componentesTabDependencia() {

		boolean esUnEvolutivoORamaUnica = 
				artefacto instanceof EvolucionArtefacto;
		
		DirectivaEstrategiaEvolucion directivaEvolucion = P.of(artefacto).evolucion(); 
		if(directivaEvolucion != null) 
			esUnEvolutivoORamaUnica = 
					esUnEvolutivoORamaUnica 
							|| directivaEvolucion.esUnica();
		
		if(esUnEvolutivoORamaUnica) return componentesArtefactoConEstrategiaUnicaOEvolutivo();
		else return componentesArtefactoConEstrategiaEvolutivaPorProyecto();
	}

	// -----------------------------------------------------
	// utilidades
	// -----------------------------------------------------
	
	private List<IComponente> componentesArtefactoConEstrategiaUnicaOEvolutivo() {
		
		ArrayList<IComponente> r = new ArrayList<IComponente>();

		// cuando es un evolutivo de un artefacto o un artefacto que no evoluciona 
		// se ofrecerá los cambios en la lista de dependencias desde él msmo.
		String mensajeInfo = artefacto instanceof EvolucionArtefacto ?
				"Se trata de una evolución de un artefacto. En esta sección gestionaremos la evolución de las dependencias."
				: "Estamos tratando con un artefacto con una estrategia de evolución basada en rama única (todos los proyectos comparten misma definición). En esta sección gestinaremos los cambios en las dependencias.";
		r.add(
				new PanelInformativo()
				.tipoInfo()
				.conTexto(mensajeInfo)
		);
		
		// tenemos que añadir una tabla con acciones adicionales que nos permita hacer cambios en el orden 
		// y en el alcance o context root, cuando sea pertinente.
		r.add(panelEdicionDependencias());
		
		// si resulta que no es un evolutivo, tenemos que ofrecer, igualmente, el panel
		// de dependientes y si es un evolutivo, añadimos la información de la dependencia
		// original
		if(!(artefacto instanceof EvolucionArtefacto)) r.add(panelDependientes());
	
		return r;
	}
	
	
	private List<IComponente> componentesArtefactoConEstrategiaEvolutivaPorProyecto() {

		ArrayList<IComponente> r = new ArrayList<IComponente>();
		
		r.add(
				new PanelContinente()
				.conTitulo("Requeridos")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponente(
						new Parrafo()
						.conTexto("Lista de otros artefactos que son requeridos por la versión consolidada del artefacto.")
				)
				.conComponenteSi(
						requeridos.size() == 0,
						new PanelInformativo()
						.tipoAviso()
						.conTexto( 
								"La versión consolidada de este artefacto no ha definido artefactos requeridos para su construcción."
						)
				)
				.conComponenteSi(
						requeridos.size() > 0,
						creaTablaRelacionesDependencia(sacaElArtefactoRequerido(requeridos))
				) 
		);

		r.add(panelDependientes());
		
		return r;
	}
	
	private PanelContinente panelEdicionDependencias() {
		
		final TablaBasica relaciones = 
				new TablaBasica(
						false,
						Arrays.asList(
								new TablaBasica.DefinicionColumna("Pos.", 1),
								new TablaBasica.DefinicionColumna("Nombre", 4),
								new TablaBasica.DefinicionColumna("Tipo", 2),
								new TablaBasica.DefinicionColumna("Alcance / ContextRoot", 1),
								new TablaBasica.DefinicionColumna("", 2),
								new TablaBasica.DefinicionColumna("Acciones", 2)
						)
				); 
		
		// vamos añadiendo cada dependencia
		for(Dependencia dependencia: requeridos) {
			
			final List<IComponente> fila = new ArrayList<IComponente>();
			
			// la posición
			fila.add(
					new TextoSimple()
					.conTexto(
							dependencia instanceof DependenciaPosicional? 
									Integer.toString(((DependenciaPosicional)dependencia).getPosicion())
									: "0"
					)
			);
			
			// el nombre
			fila.add(convierteRelacionArtefactoEnComponente(dependencia.getRequerida()));
			
			// el tipo
			fila.add(new TextoSimple(dependencia.getRequerida().getTipoArtefacto().getNombre()));
			
			// el alcance o el contexto
			fila.add(convierteAlcanceOContextoEnComponente(Tuples.tuple(dependencia.getRequerida(), dependencia)));
		
			// el formulario de cambio de posición
			if(dependencia instanceof DependenciaPosicional) {
				fila.add(
						new FormularioEnLinea()
						.conTextoEntrada(Integer.toString(((DependenciaPosicional)dependencia).getPosicion()))
						.conTextoBoton("Ok")
						.paraUrlEnvio("/artefactos/dependencias/estaticas/mover/" + artefacto.getId() + "/" + dependencia.getRequerida().getId())
				);
			} else {
				fila.add(new TextoSimple(""));
			}
			
			// las acciones
			fila.add(
					new Botonera()
					// 1 un botón para modificar
					.conBoton(
							new BotonEnlace()
							.conTamañoMuyPequeño()
							.conTexto("Editar")
							.paraUrl("/artefactos/dependencias/estaticas/editar/" + artefacto.getId() + "/" + dependencia.getRequerida().getId())
					)
					// 2 un botón par quitar
					.conBoton(
							new BotonEnlace()
							.conTamañoMuyPequeño()
							.deTipoAviso()
							.conTexto("Eliminar")
							.paraUrl("/artefactos/dependencias/estaticas/quitar/" + artefacto.getId() + "/" + dependencia.getRequerida().getId())
					)
			);
			
			// añadimos la línea a la tabla
			relaciones.conFila(fila);
		}
		
		
		// devolvemos el panel de edición
		return new PanelContinente()
				.conTitulo("Edición / Configuración actual")
				.paraTipoPrimario()
				.conComponente(relaciones)
				.conComponente(
						new Botonera()
						.conAlineacionALaDerecha()
						.conBoton(
								new BotonEnlace()
								.conTamañoPequeño()
								.conTexto("Añadir una dependencia")
								.paraUrl("/artefactos/dependencias/estaticas/incluir/" + artefacto.getId())
						)
				);
		
	}
	
	private PanelContinente panelDependientes() {
		return new PanelContinente()
				.conTitulo("Dependientes")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponente(
						new Parrafo()
						.conTexto("Lista de otros artefactos cuyas versiones estables requieren de este artefacto.")
				)
				.conComponenteSi(
						dependientes.size() == 0,
						new PanelInformativo()
						.tipoAviso()
						.conTexto( 
								"No hay artefactos que dependan de éste. Al menos en sus respectivas versiones estables."
						)
				)
				.conComponenteSi(
						dependientes.size() > 0,
						creaTablaRelacionesDependencia(sacaElArtefactoDependiente(dependientes))
				);
	}
	
	private Collection<Tuples.T2<Artefacto, Dependencia>> sacaElArtefactoRequerido(Collection<Dependencia> relaciones) {
		ArrayList<Tuples.T2<Artefacto, Dependencia>> requeridos = new ArrayList<Tuples.T2<Artefacto, Dependencia>>();
		
		for(Dependencia d: relaciones) requeridos.add(Tuples.tuple(d.getRequerida(), d));

		return requeridos;
	}
	
	public Collection<Tuples.T2<Artefacto, Dependencia>> sacaElArtefactoDependiente(Collection<Dependencia> relaciones) {
		ArrayList<Tuples.T2<Artefacto, Dependencia>> dependientes = new ArrayList<Tuples.T2<Artefacto, Dependencia>>();
		
		for(Dependencia d: relaciones) dependientes.add(Tuples.tuple(d.getDependiente(), d));

		return dependientes;
	}
 

	private TablaBasica creaTablaRelacionesDependencia(Collection<Tuples.T2<Artefacto, Dependencia>> artefactos) {

		TablaBasica t = new TablaBasica();
		
		// definimos las columnas
		t.columnas().addAll(
				Arrays.asList(
						new TablaBasica.DefinicionColumna("Nombre", 6),
						new TablaBasica.DefinicionColumna("Tipo", 3),
						new TablaBasica.DefinicionColumna("Alcance / ContextRoot", 3)
				)
		);
		
		// vamos añadiendo los elementos
		for(Tuples.T2<Artefacto, Dependencia> ed: artefactos) {
			
			IComponente columna1 = convierteRelacionArtefactoEnComponente(ed._1);
			
			// Segunda columna
			IComponente columna2 = new TextoSimple()
									.conTexto(ed._1.getTipoArtefacto().getNombre());
			
			// Tercera columna
			IComponente columna3 = convierteAlcanceOContextoEnComponente(ed);
			
			// Añadimos a la tabla
			t.conFila(columna1, columna2, columna3);
		}
		
		return t;
	}

	private IComponente convierteRelacionArtefactoEnComponente(Artefacto artefacto) {
		// la primera columna deberá mostrar el nombre (como enlace)
		// y la coordenada maven (si es referenciable como tal)
		IComponente referencia = new EnlaceSimple()
								.conTexto(artefacto.getNombre())
								.paraUrl("artefactos/dependencias/estaticas/" + artefacto.getId());
		
		// la coordenada maven
		IComponente coordenada = null;
		if(P.of(artefacto).coordenadasMaven() != null) {

			DirectivaCoordenadasMaven lente = P.of(artefacto).coordenadasMaven(); 
			
			String texto = 
					lente.getIdGrupo().trim() 
					+ ":" 
					+ lente.getIdArtefacto().trim() 
					+ ":"
					+ lente.getVersion().trim()
					+ ":"
					+ lente.getEmpaquetado();
			
			coordenada = new TextoSimple()
						.conTexto(texto);
		}

		IComponente columna1 = new ContinenteSinAspecto()
				.conComponente(referencia)
				.conComponenteSi(coordenada!=null, new SaltoDeLinea())
				.conComponenteSi(coordenada!=null, coordenada);
		return columna1;
	}

	private IComponente convierteAlcanceOContextoEnComponente(Tuples.T2<Artefacto, Dependencia> ed) {
		String texto3 = "-";
		if(ed._2 instanceof DependenciaConAlcance) 
			texto3 = ((DependenciaConAlcance)ed._2).getAlcanceElegido().getTextoDescripcion();
		if(ed._2 instanceof DependenciaConModuloWeb)
			texto3 = "/" + ((DependenciaConModuloWeb)ed._2).getContextRoot();
		IComponente columna3 = new TextoSimple().conTexto(texto3);
		return columna3;
	}
}
