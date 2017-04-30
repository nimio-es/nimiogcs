package es.nimio.nimiogcs.operaciones.proyecto;

import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionProyecto;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoProyecto;
import es.nimio.nimiogcs.modelo.IUsuario;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

/**
 * Clase que implementa la lógica para crear la estructura de un nuevo proyecto
 */
public final class CrearEstructuraNuevoProyecto 
	extends ProcesoAsincronoModulo<CrearEstructuraNuevoProyecto.Peticion>{

	public CrearEstructuraNuevoProyecto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	/**
	 * Clase interna que recoge los datos de la petición
	 */
	public final static class Peticion {
		public final String nombreProyecto;
		public final String proyeccion; 
		public final String idRepositorio;
		public final String idArtefacto;
		public final IUsuario usuario;
		
		
		public Peticion(
				final String nombreProyecto,
				final String proyeccion,
				final String idRepositorio,
				final String idArtefacto,
				final IUsuario usuario) {

			this.nombreProyecto = nombreProyecto;
			this.proyeccion = proyeccion;
			this.idRepositorio = idRepositorio;
			this.idArtefacto = idArtefacto;
			this.usuario = usuario;
		}
	}
	
	@Override
	protected String nombreUnicoOperacion(CrearEstructuraNuevoProyecto.Peticion datos, ProcesoAsincrono op) {
		return "ALTA PROYECTO " + datos.nombreProyecto;
	}
	
	@Override
	protected void relacionarOperacionConEntidades(CrearEstructuraNuevoProyecto.Peticion datos, ProcesoAsincrono op) {
		// AQUÍ NADA
	}
	

	@Override
	protected void hazlo(final CrearEstructuraNuevoProyecto.Peticion datos, ProcesoAsincrono op) {
		
		// para poder pasar como parámetro en la ejecución anidada de otro proceso asíncrono
		final CrearEstructuraNuevoProyecto _this = this;
		
		// tenemos que dar de alta el proyecto en el registro
		final Proyecto proyecto = registraProyecto(datos, op);
		
		// lanzamos la creación de la rama del primer artefacto, separada en una tarea distinta para ser reutilizada
		// en otras ocasiones
		escribeMensaje("Lanzamos la operación de registro del artefacto en el proyecto.");
		new RelacionarProyectoYArtefacto(ce)
			.setUsuarioAnd(getUsuario())
			.ejecutarCon(
					new RelacionarProyectoYArtefacto.Peticion(proyecto, ce.artefactos().findOne(datos.idArtefacto)),
					
					new Function<RelacionarProyectoYArtefacto.Peticion, Boolean>() {

						@Override
						public Boolean apply(
								RelacionarProyectoYArtefacto.Peticion v) {
							
							// una vez registrado el artefacto (primero) procedemos a crear las estructuras de datos necesarias
							// tenemos que lanzar la creación de las estructuras de proyecto y código asociadas
							for(ITareaProyeccionProyecto pp: ce.contextoAplicacion().getBeansOfType(ITareaProyeccionProyecto.class).values()) {
								try {
									pp.ejecutarTareaProyeccion(
											
											datos.proyeccion, 
											proyecto, 
											_this,
											
											// si la tarea 
											new Function<ElementoBaseProyecto, Boolean>() {

												@Override
												public Boolean apply(ElementoBaseProyecto v) {
													proyecto.setEstado(EnumEstadoProyecto.Abierto);
													ce.proyectos().saveAndFlush(proyecto);
													return true;
												}
												
											},
											
											// en caso de fracaso, nada
											null);
								} catch (Exception e) {
									throw new ErrorInesperadoOperacion(
											"Error en el anidamiento de operaciones", 
											e
									);
								}
							}
							
							return true;
						}
						
					}
			);
		

	}

	/**
	 * Damos de alta el registro del proyecto
	 */
	private Proyecto registraProyecto(CrearEstructuraNuevoProyecto.Peticion datos, ProcesoAsincrono op){
		
		// creamos el registro base del proyecto
		// y lo almacenamos
		Proyecto proyecto = new Proyecto();
		proyecto.setNombre(datos.nombreProyecto);
		proyecto.setEstado(EnumEstadoProyecto.Desconocido);
		proyecto.setEnRepositorio(ce.repositorios().findOne(datos.idRepositorio));
		ce.proyectos().saveAndFlush(proyecto);
		
		// y lo volvemos a cargar para que recoga los EAGERS asociados
		proyecto = ce.proyectos().findOne(proyecto.getId());
		
		// para, a continuación, relacionarlo con la operación
		registraRelacionConOperacion(op, proyecto);
		
		// y lo devolvemos
		return proyecto;
	}
	
}
