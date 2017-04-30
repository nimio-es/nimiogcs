package es.nimio.nimiogcs.operaciones.proyecto.etiquetas;

import java.util.ArrayList;
import java.util.List;

import es.nimio.nimiogcs.componentes.proyecto.etiqueta.IPosCreacionEtiqueta;
import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionNuevaEtiqueta;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.CongelarEvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.operaciones.artefactos.externa.ComprobarSincronizacionRamaEvolutiva;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;

public class NuevaEtiquetaDeProyecto 
	extends ProcesoAsincronoModulo<Tuples.T2<Proyecto, String>>{

	public NuevaEtiquetaDeProyecto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	@Override
	protected String nombreUnicoOperacion(T2<Proyecto, String> datos, ProcesoAsincrono op) {
		return "CREAR ETIQUETA '" 
				+ datos._1.getNombre() 
				+ "-" 
				+ datos._2.toUpperCase() 
				+ "'";
	}

	@Override
	protected void relacionarOperacionConEntidades(T2<Proyecto, String> datos, ProcesoAsincrono op) {
		// de entrada el proyecto
		registraRelacionConOperacion(op, datos._1);
	}

	@Override
	protected void hazlo(T2<Proyecto, String> datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		//
		Proyecto proyecto = datos._1;
		String nombreEtiqueta = datos._2.toUpperCase();
		
		// Creamos la etiqueta
		final EtiquetaProyecto etiqueta = new EtiquetaProyecto();
		etiqueta.setNombre(proyecto.getNombre() + "-" + nombreEtiqueta);
		etiqueta.setProyecto(proyecto);
		ce.etiquetas().saveAndFlush(etiqueta);
		
		// registrar la relación
		registraRelacionConOperacion(op, etiqueta);
		
		// recorremos las relaciones del proyecto con artefactos y vamos
		// generando las versiones de congelación
		for(RelacionElementoProyectoArtefacto rpa: 
			ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto))) {
			
			// congelamos el artefacto
			CongelarEvolucionArtefacto congelado = congelarArtefacto(op, rpa.getArtefacto());
			
			// creamos la relación entre la etiqueta y
			// el artefacto congelado
			ce.relacionesProyectos().saveAndFlush(
					new RelacionElementoProyectoArtefacto(etiqueta, congelado)
			);
			
			// creamos la etiqueta en el repositorio
			crearEtiquetaEnRepositorio(proyecto, etiqueta, congelado);
			
			// lanzamos la comprobación de si es obsoleto o no el congelado
			new ComprobarSincronizacionRamaEvolutiva(ce)
			.setUsuarioAnd(this.getUsuario())
			.ejecutarCon(
					new ComprobarSincronizacionRamaEvolutiva.Peticion(congelado)
			);
		}
		
		// En este punto ya tenemos creadas las estructuras de datos que representan la etiqueta
		// Ahora lanzamos las taras de proyección, que crearán las estructuras de código necesarias
		final NuevaEtiquetaDeProyecto thiz = this;
		serializeTagProjectionExecutionTasks(
				new ArrayList<ITareaProyeccionNuevaEtiqueta>(ce.componentes(ITareaProyeccionNuevaEtiqueta.class)),
				new Function<ElementoBaseProyecto, Boolean>() {

					@Override
					public Boolean apply(ElementoBaseProyecto v) {

						// Con las estructuras de control y de código creadas queda lanzar las tareas que 
						// representen aquellas operaciones adicionales a realizar justo tras el etiquetado
						for(IPosCreacionEtiqueta componente: ce.componentes(IPosCreacionEtiqueta.class)) {
							if(componente.paraEjecutar(etiqueta))
								try {
									componente.subtarea()
									.setUsuarioAnd(thiz.getUsuario())
									.ejecutarCon(etiqueta);
								} catch (Exception e) {
									error("Error durante la ejecución de un subproceso", e);
								}
						}
						
						return true;
					}
					
				}
		).apply(etiqueta);
		
		
	}
	
	
	// -----------------------------------------------
	// Métodos utilidad
	// -----------------------------------------------

	private CongelarEvolucionArtefacto congelarArtefacto(ProcesoAsincrono op, Artefacto artefacto) {

		// por un lado congelamos un conjunto de relaciones y por el otro
		// tenemos que relacionar el artefacto real al que estamos congelando
		Artefacto artefactoACongelar = artefacto instanceof EvolucionArtefacto ?
				((EvolucionArtefacto)artefacto).getArtefactoAfectado() 
				: artefacto;
		
		// creamos la congelación
		CongelarEvolucionArtefacto congelado = new CongelarEvolucionArtefacto();
		congelado.setTipoArtefacto(ce.tipos().findOne("FREEZE"));
		congelado.setNombre("CONGELACIÓN DE '" + artefactoACongelar.getNombre() + "'");
		congelado.setArtefactoAfectado(artefactoACongelar);
		congelado.setSincronizadoEstable(P.of(artefactoACongelar).repositorioCodigo() == null);
		ce.artefactos().saveAndFlush(congelado);
		
		// relacionamos con la operación
		registraRelacionConOperacion(op, congelado);
		
		// lista de las directivas que se deben copiar/evolucionar
		ParametroGlobal pg = ce.global().findOne("ARTEFACTOS.DIRECTIVAS.EVOLUCIONABLES");
		if(pg!=null) {
			String[] de = pg.getContenido().trim().replace("\n", "").replace("\r", "").split(",");
			for(String nd: de) {
				DirectivaBase dn = P.of(artefactoACongelar).buscarDirectiva(nd);
				if(dn!=null) {
					DirectivaBase da = dn.replicar();
					DirectivaBase ps = ce.directivas().save(da);
					congelado.getDirectivasArtefacto().add(ps);
					ce.artefactos().save(congelado);
				}
			}
		}
		ce.directivas().flush();
		
		// replicamos las dependencias
		for(Dependencia de: ce.dependenciasArtefactos()
				.relacionesDependenciaDeUnArtefacto(artefacto.getId())) {
			
			Dependencia nd = de.reproducir();
			nd.setDependiente(congelado);
			ce.dependenciasArtefactos().save(nd);
		}
		ce.dependenciasArtefactos().flush();
		
		// recargamos para que esté todo
		return (CongelarEvolucionArtefacto)ce.artefactos().findOne(congelado.getId());
	}


	private void crearEtiquetaEnRepositorio(
			Proyecto proyecto,
			EtiquetaProyecto etiqueta,
			CongelarEvolucionArtefacto congelado) {
		
		// para crear una etiqueta tenemos que disponer de un artefacto con 
		// directiva de repositorio de código
		Artefacto base = congelado.getArtefactoAfectado();
		if(P.of(base).repositorioCodigo()!=null) {

			// tenemos que buscar en el proyecto la relación que referencia 
			//  el artefacto que estamos etiquetando
			Artefacto evolucionado = null;
			for(RelacionElementoProyectoArtefacto rpa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto))) {
				if(rpa.getArtefacto() instanceof EvolucionArtefacto) { 
					if(((EvolucionArtefacto)rpa.getArtefacto()).getArtefactoAfectado().getId().equalsIgnoreCase(base.getId())) {
						evolucionado = rpa.getArtefacto();
						break;
					}
				} else {
					if(rpa.getArtefacto().getId().equalsIgnoreCase(base.getId())) {
						evolucionado = rpa.getArtefacto();
						break;
					}
				}
			}
			
			// el elemento evolucionado debe disponer de la rama de código
			if(evolucionado!=null && P.of(evolucionado).ramaCodigo()==null)
				throw new ErrorInconsistenciaDatos(
						"Detectado un artefacto '" + evolucionado.getNombre() 
						+ "' que se supone que tiene código, pero que no tiene "
						+ "rama de trabajo");
			
			// de la base tenemos que sacar la raíz de trabajo
			DirectivaRepositorioCodigo lc = P.of(base).repositorioCodigo();
			
			// y del evolucionado sacaremos la rama de código
			DirectivaRamaCodigo lr = P.of(evolucionado).ramaCodigo();
			
			// tenemos que crear la etiqueta a partir de la rama de trabajo actual
			RepositorioCodigo repo = lc.getRepositorio();
			Subversion subversion = new Subversion(repo);
			
			// la ruta de la etiqueta
			String rutaEtiqueta = repo.getUriRaizRepositorio() 
					+ "/" + lc.getParcialEtiquetas()
					+ "/" + etiqueta.getNombre().toLowerCase();
			
			subversion.copiarCarpetas(
					lr.getRamaCodigo(), 
					rutaEtiqueta,
					"Etiqueta '" + etiqueta.getNombre() + "' "
							+ "del artefacto '" + base.getNombre() + "' "
							+ "para el proyecto '" + proyecto.getNombre() + "'"
			);
			
			// actualizamos el elemento congelado para dejar constancia de la etiqueta
			DirectivaRamaCodigo da = new DirectivaRamaCodigo();
			da.setRamaCodigo(rutaEtiqueta);
			ce.directivas().saveAndFlush(da);
			congelado.getDirectivasArtefacto().add(da);
			ce.artefactos().saveAndFlush(congelado);
		}
		
		// no hay directiva de rama de código, es innecesario
		// hacer una etiqueta
	}
	
	/**
	 * Serialize the execution of tasks that project the tag
	 */
	private Function<ElementoBaseProyecto, Boolean> serializeTagProjectionExecutionTasks(
			final List<ITareaProyeccionNuevaEtiqueta> subtareas,
			final Function<ElementoBaseProyecto, Boolean> finalFunction) {

		// this
		final NuevaEtiquetaDeProyecto thiz = this;
		
		// base case
		if(subtareas.isEmpty()) return finalFunction;
		if(subtareas.size() == 1)  
			return new Function<ElementoBaseProyecto, Boolean>() {

				@Override
				public Boolean apply(ElementoBaseProyecto et) {

					try {
						subtareas.get(0).ejecutarTareaProyeccion(
								(EtiquetaProyecto)et, 
								thiz, 
								finalFunction, 
								null);
					} catch (Exception e) {
						error("Error en la ejecución de un subproceso", e);
					}
					
					return true;
				}
			};
			
		// general case
		final ITareaProyeccionNuevaEtiqueta subtask = subtareas.get(0);
		final List<ITareaProyeccionNuevaEtiqueta> restOfSubtasks = subtareas.subList(1, subtareas.size());
		return new Function<ElementoBaseProyecto, Boolean>() {

			@Override
			public Boolean apply(ElementoBaseProyecto et) {

				try {
					subtask.ejecutarTareaProyeccion(
							(EtiquetaProyecto)et, 
							thiz, 
							serializeTagProjectionExecutionTasks(
									restOfSubtasks, 
									finalFunction
							), 
							null
					);
				} catch (Exception e) {
					error("Error en la ejecución de un subproceso", e);
				}
				
				return true;
			}
		};
	}
}
