package es.nimio.nimiogcs.operaciones.proyecto;

import java.util.ArrayList;
import java.util.Date;

import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionProyecto;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;
import es.nimio.nimiogcs.utils.DateTimeUtils;

public class QuitarRelacionarProyectoYArtefacto 
	extends ProcesoAsincronoModulo<QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion> {
	
	/**
	 * Recoge los parámetros requeridos para la ejecución del proceso
	 */
	public static final class PeticionEliminacionRelacion extends Tuples.T2<RelacionElementoProyectoArtefacto, Boolean> {

		private static final long serialVersionUID = 7955107481713615521L;

		public PeticionEliminacionRelacion(RelacionElementoProyectoArtefacto relacion) {
			this(relacion, false);
		}
		
		public PeticionEliminacionRelacion(RelacionElementoProyectoArtefacto relacion, Boolean ignorarReconstruccion) {
			super(relacion, ignorarReconstruccion);
		}

		// --
		
		public RelacionElementoProyectoArtefacto relacion() { return _1; }
		public boolean ignorarReconstruccion() { return _2; } 
	}
	
	// ----
	
	public QuitarRelacionarProyectoYArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ----
	
	private String nombreUnicoOperacion(RelacionElementoProyectoArtefacto relacion) {
		return "ELIMINAR RELACION DEL PROYECTO '" 
				+ relacion.getElementoProyecto().getNombre() 
				+ "' CON EL ARTEFACTO '" 
				+ relacion.getArtefacto().getNombre() 
				+ "'";
	}
	
	@Override
	protected String nombreUnicoOperacion(QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion peticion, ProcesoAsincrono op) {
		return nombreUnicoOperacion(peticion.relacion());
	}

	@Override
	protected void relacionarOperacionConEntidades(QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion peticion, ProcesoAsincrono op) {
		registraRelacionConOperacion(op, peticion.relacion().getElementoProyecto());

		// si es un evolutivo vamos a eliminar el artefacto, pero asociamos con su representado
		if(!(peticion.relacion().getArtefacto() instanceof ITestaferroArtefacto))
			registraRelacionConOperacion(op, peticion.relacion().getArtefacto());
		else 
			registraRelacionConOperacion(
					op, 
					((ITestaferroArtefacto)peticion.relacion().getArtefacto()).getArtefactoAfectado()
			);
	}


	@Override
	protected void hazlo(QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion peticion, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		// comprobamos que se cumplen las condiciones de extracción
		cumpleCondicionesAntesDeEliminacion(peticion.relacion());
		
		// queremos saber en cuántos proyectos, adecionales, tenemos el artefacto
		int numeroProyectos = ce.relacionesProyectos()
				.findAll(ArtefactosProyecto.relacionesDeUnArtefactoConProyectos(peticion.relacion().getArtefacto()))
				.size();
		
		escribeMensaje("El artefacto '" + peticion.relacion().getArtefacto().getNombre() + "' participa actualmente en " + numeroProyectos + " proyectos");
		
		// empezamos eliminando la rama de proyecto
		// (si la hubiera y si el evolutivo solo interviene en un proyecto)
		if(numeroProyectos == 1) eliminarRamaTrabajo(peticion.relacion());
		
		// eliminamos el artefacto (si es evolutivo) y la relación
		eliminarArtefacto(peticion.relacion(), numeroProyectos);

		// si no queremos proceder con la fase de reconstrucción, damos por concluida la operación
		if(peticion.ignorarReconstruccion()) return;
		
		// queda notificar a los usos que se proceda a la reconstrucción de las estructuras
		// tenemos que lanzar la re-creación de las estructuras de proyecto y código asociadas
		for(ITareaProyeccionProyecto pp: ce.contextoAplicacion().getBeansOfType(ITareaProyeccionProyecto.class).values()) {
				pp.ejecutarTareaReconstruccionProyeccion(
					peticion.relacion().getElementoProyecto(), 
					this,
					null,
					null);
		}
	}
	
	/**
	 * Confirma que no exista una relación previa entre el artefacto y el proyecto
	 */
	private void cumpleCondicionesAntesDeEliminacion(RelacionElementoProyectoArtefacto relacion) {
		
		// no hay motivo, de momento, para condicionar la eliminación
	}

	/**
	 * Eliminamos la rama de trabajo, si fuera necesario 
	 */
	private void eliminarRamaTrabajo(RelacionElementoProyectoArtefacto relacion) {

		Artefacto artefacto = relacion.getArtefacto();

		// si el artefacto no es un evolutivo, o no tiene código o es de rama única.
		// en cualquier caso no podríamos eliminar la rama de código
		if(!(artefacto instanceof ITestaferroArtefacto)) {
			escribeMensaje("No es un artefacto evolutivo. No podemos tocar la rama de código");
			return;
		}

		// si es un evolutivo y tiene rama de código, entonces procederemos a eliminar la rama
		DirectivaRamaCodigo rama = P.of(artefacto).ramaCodigo();
		if(rama!=null) {

			escribeMensaje("Se trata de un evolutivo y tiene rama de código.");

			// y si tiene rama, es que el artefacto tiene repositorio
			// y lo que estamos tratando es un evolutivo 
			DirectivaRepositorioCodigo codigo = P.of(((ITestaferroArtefacto)artefacto).getArtefactoAfectado()).repositorioCodigo();
			
			// queda perdirle a Subversion que elimine la rama
			Subversion svn = new Subversion(codigo.getRepositorio());
			
			escribeMensaje("Nos disponemos a eliminar la rama de código '" + rama.getRamaCodigo() + "'");
			if(svn.existeElementoRemoto(rama.getRamaCodigo())) {
				svn.eliminarCarpeta(rama.getRamaCodigo());
			} else {
				escribeMensaje("¡¡NO EXISTE!!");
			}
		}
	}
	
	/**
	 * Procedemos a eliminar el artefacto
	 */
	private void eliminarArtefacto(RelacionElementoProyectoArtefacto relacion, int numeroProyectos) {
		
		Artefacto artefacto = relacion.getArtefacto();
		
		final boolean esEvolutivo = artefacto instanceof ITestaferroArtefacto;
		final boolean unProyecto = numeroProyectos <= 1;
		
		// Solo tiene sentido eliminar un artefacto de la relación si es evolutivo
		// y no participa en varios proyectos
		final boolean evolutivoYProyecto = esEvolutivo && unProyecto; 
		if(evolutivoYProyecto) {
			
			escribeMensaje("Eliminamos el artefacto '" + artefacto.getNombre() + "' y todas sus relaciones");
			
			// quitamos la relación con operaciones en las que ha participado
			escribeMensaje("Eliminando relaciones del artefacto con las operaciones");
			for(RelacionOperacion ro: ce.relacionesOperaciones().operacionesDeUnArtefacto(artefacto.getId())) {

				// indicamos en la operación que hemos hecho una modificación de forma externa
				Operacion op = ro.getOperacion();
				op.setMensajesEjecucion(
						op.getMensajesEjecucion() 
						+ "\n[" 
						+ DateTimeUtils.formaReducida(new Date())
						+ "] " + nombreUnicoOperacion(relacion)
						+ "\n"						
				);
				ce.operaciones().save(op);
				
				// y borramos la relación
				ce.relacionesOperaciones().delete(ro);
			}
			
			// eliminamos las dependencias
			escribeMensaje("Eliminando las dependencias");
			ce.dependenciasArtefactos().delete(
					ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(artefacto.getId())
			);
			
			// no obstante, la eliminación del artefacto solo podremos hacerla 
			// cuando hayamos eliminado la relación con el proyecto
		}
		
		// en cualquier caso, también hay que eliminar la relación entre el proyecto
		// y el artefacto, sea evolutivo o no
		escribeMensaje("Eliminar registro relación y proyecto");
		ce.relacionesProyectos().delete(relacion);
		ce.relacionesProyectos().flush();

		// volvemos a preguntar para ver si ahora podemos eliminar el artefacto y hacer flushing de todo
		if(evolutivoYProyecto) {
			
			// copiamos la lista de ids de las directivas que vamos a eliminar
			ArrayList<String> idsDirectivasArtefacto = new ArrayList<String>();
			for(DirectivaBase db: artefacto.getDirectivasArtefacto()) {
				idsDirectivasArtefacto.add(db.getId());
			}
			
			// eliminamos el artefacto
			escribeMensaje("Borrar artefacto");
			ce.artefactos().delete(artefacto);

			// quitamos las directivas
			escribeMensaje("Eliminando directivas");
			for(String idDirectiva: idsDirectivasArtefacto) {
				ce.directivas().delete(idDirectiva);
			}
			
			// flushing
			ce.operaciones().flush();
			ce.relacionesOperaciones().flush();
			ce.dependenciasArtefactos().flush();
			ce.artefactos().flush();
			ce.directivas().flush();
		}
	}
}
