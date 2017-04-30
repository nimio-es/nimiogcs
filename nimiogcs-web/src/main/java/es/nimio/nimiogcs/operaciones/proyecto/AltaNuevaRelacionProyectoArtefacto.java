package es.nimio.nimiogcs.operaciones.proyecto;

import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionProyecto;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public class AltaNuevaRelacionProyectoArtefacto 
	extends ProcesoAsincronoModulo<AltaNuevaRelacionProyectoArtefacto.Peticion> {

	public AltaNuevaRelacionProyectoArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	/**
	 * Petición
	 */
	public final static class Peticion extends Tuples.T2<String, String> {

		private static final long serialVersionUID = 6502273471196873677L;

		public Peticion(String idProyecto, String idArtefacto) {
			super(idProyecto, idArtefacto);
		}
		
		public String idProyecto() { return _1; }
		public String idArtefacto() { return _2; }
	}
	
	// -----

	@Override
	protected void relacionarOperacionConEntidades(Peticion datos, ProcesoAsincrono op) {
		Proyecto proyecto = ce.proyectos().findOne(datos.idProyecto());
		Artefacto artefacto = ce.artefactos().findOne(datos.idArtefacto());
		registraRelacionConOperacion(op, proyecto);
		registraRelacionConOperacion(op, artefacto);
	}

	@Override
	protected void hazlo(final Peticion datos, ProcesoAsincrono op) {
		
		final AltaNuevaRelacionProyectoArtefacto _this = this;
		
		final Proyecto proyecto = ce.proyectos().findOne(datos.idProyecto());
		final Artefacto artefacto = ce.artefactos().findOne(datos.idArtefacto());
		
		// se trata de relacionar el proyecto con el artefacto
		new RelacionarProyectoYArtefacto(ce)
		.setUsuarioAnd(this.getUsuario())
		.ejecutarCon(
				new RelacionarProyectoYArtefacto.Peticion(proyecto, artefacto),
				
				new Function<RelacionarProyectoYArtefacto.Peticion, Boolean>() {

					@Override
					public Boolean apply(
							RelacionarProyectoYArtefacto.Peticion v) {
						
						// una vez registrado el artefacto (primero) procedemos a crear las estructuras de datos necesarias
						// tenemos que lanzar la creación de las estructuras de proyecto y código asociadas
						for(ITareaProyeccionProyecto pp: ce.contextoAplicacion().getBeansOfType(ITareaProyeccionProyecto.class).values()) {
							try {
								pp.ejecutarTareaReconstruccionProyeccion(
										proyecto, 
										_this,
										null,
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
}
