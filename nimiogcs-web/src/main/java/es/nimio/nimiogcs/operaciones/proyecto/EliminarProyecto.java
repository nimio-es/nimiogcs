package es.nimio.nimiogcs.operaciones.proyecto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionProyecto;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.operaciones.proyecto.QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

public final class EliminarProyecto 
	extends ProcesoAsincronoModulo<Proyecto> {

	public EliminarProyecto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}


	// ----

	@Override
	protected String nombreUnicoOperacion(Proyecto proyecto, ProcesoAsincrono op) {
		return "LANZAR LA ELIMINACIÓN DEL PROYECTO '"
				+ proyecto.getNombre()
				+ "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(Proyecto proyecto, ProcesoAsincrono op) {
		// Aunque luego desaparecerá, lo asociamos con el proyecto
		registraRelacionConOperacion(op, proyecto);
	}

	@Override
	protected void hazlo(final Proyecto proyecto, final ProcesoAsincrono op) {
		
		validarPosibilidadProceso(proyecto);
		
		// el proceso de eliminación de un proyecto es un proceso que requiere, 
		// primero, de la eliminación de todas las relaciones existentes y, cuando 
		// haya finalizado, se procederá a eliminar los datos propios del artefacto
		
		// preparamos la cadena de eliminación de estructuras en repositorio que se ejecutará cuando concluya 
		// la eliminación de todas las relaciones con artefactos.
		final Function<ElementoBaseProyecto, Boolean> _fr = construyeInvocacionRecurrenteEliminarProyecciones(
				new ArrayList<ITareaProyeccionProyecto>(ce.contextoAplicacion().getBeansOfType(ITareaProyeccionProyecto.class).values()),
				new Function<ElementoBaseProyecto, Boolean>() {

					@Override
					public Boolean apply(ElementoBaseProyecto ignorable) {
						return borrarDatosProyecto(proyecto, op);
					}
				},
				proyecto,
				op
		);
				
		
		construyeInvocacionRecurrente(
				ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto)),
				new Function<QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion, Boolean>() {

					@Override
					public Boolean apply(PeticionEliminacionRelacion ignorable) {
						return _fr.apply(null);
					}
					
				}, 
				op
		)
		.apply(null);
		
	}

	
	// ---
	
	private void validarPosibilidadProceso(Proyecto proyecto) {
		
		// un proyecto que tenga publicaciones no podrá ser borrado
		if(ce.publicaciones().publicacionesProyecto(proyecto.getId(), new PageRequest(0,1)).getContent().size() > 0)
			throw new ErrorIntentoOperacionInvalida("El proyecto tiene publicaciones asociadas.");
		
	}
	
	private boolean borrarDatosProyecto(final Proyecto proyecto, final ProcesoAsincrono op) {

		// teniendo presente que la etiqueta puede estar siendo modificada en otras operaciones, vamos a cargar de nuevo la entidad
		final Proyecto recargada = ce.proyectos().findOne(proyecto.getId());
		
		// borramos los usos (que no hayan sido borrados en otras operaciones)
		for(UsoYProyeccionProyecto upp: recargada.getUsosYProyecciones())
			ce.usosProyecto().delete(upp);
		ce.usosProyecto().flush();

		// tenemos que quitar la etiqueta de todas las operaciones donde esté
		for(RelacionOperacion rop: ce.relacionesOperaciones().operacionesDeUnElementoProyecto(proyecto.getId())) {
			
			// dejamos constancia en la operación
			final Operacion opo = rop.getOperacion();
			final String linea = "[" + DateTimeUtils.formaReducida(new Date()) + "][EXT] Quitando relación con etiqueta '" + proyecto.getNombre() + "' por eliminación.\n";
			String msg = opo.getMensajesEjecucion();
			msg += Strings.isNotEmpty(msg) ? msg + linea : linea;
			opo.setMensajesEjecucion(msg);
			ce.operaciones().save(opo);
			
			ce.relacionesOperaciones().delete(rop);
		}
		ce.relacionesOperaciones().flush();

		// borramos la propia etiqueta
		ce.proyectos().delete(proyecto);
		ce.proyectos().flush();
		
		return true;
	}
	
	private Function<ElementoBaseProyecto, Boolean> construyeInvocacionRecurrenteEliminarProyecciones(
			final List<ITareaProyeccionProyecto> tareasProyeccion,
			final Function<ElementoBaseProyecto, Boolean> funcionCola,
			final Proyecto protyecto,
			final ProcesoAsincrono op) {
		
		// condición de salida
		if(tareasProyeccion == null || tareasProyeccion.size() == 0) return funcionCola;
		
		// cogemos el primer elemento de la lista
		final ITareaProyeccionProyecto tpp = tareasProyeccion.get(0);
		
		// si no es requerido procesar la eliminación de la carpeta, lo ignoramos
		if(!tpp.confirmaProcede(protyecto)) return
				construyeInvocacionRecurrenteEliminarProyecciones(
						tareasProyeccion.size() > 1 ? tareasProyeccion.subList(1, tareasProyeccion.size()) : null,
						funcionCola,
						protyecto,
						op
				);
		
		final EliminarProyecto _thiz = this; 
		
		// devolvemos una función que encapsula la llamada al método que elimina la estructura de repositorio
		return new Function<ElementoBaseProyecto, Boolean>() {

			@Override
			public Boolean apply(ElementoBaseProyecto ignorable) {
				
				try {
					tpp.ejecutarTareaEliminarProyeccion(
							protyecto, 
							_thiz, 
							construyeInvocacionRecurrenteEliminarProyecciones(
									tareasProyeccion.size() > 1 ? tareasProyeccion.subList(1, tareasProyeccion.size()) : null,
											funcionCola,
											protyecto,
											op
							), 
							null
					);
				} catch (Exception e) {
					throw new ErrorInesperadoOperacion("Error en la invocación encadenada", e);
				}
				
				return true;
			}
			
		};
	}
	
	private Function<QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion, Boolean> construyeInvocacionRecurrente(
			final List<RelacionElementoProyectoArtefacto> relaciones,
			final Function<QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion, Boolean> funcionCola,
			final ProcesoAsincrono op) {

		// condición de salida
		if(relaciones == null || relaciones.size() == 0) return funcionCola;
		
		// cogemos la primera de la lista
		final RelacionElementoProyectoArtefacto r = relaciones.get(0);
		
		// thiz
		final EliminarProyecto _thiz = this;

		this.escribeMensaje("Encadenando la operación que desvincula la etiqueta del artefacto '" + r.getArtefacto().getNombre() + "'");
		
		
		
		// creamos una función que es capaz de llamar a la operación que quita la relación
		// de un artefacto con un proyecto (o etiqueta)
		return new Function<QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion,Boolean>() {

			@Override
			public Boolean apply(QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion ignorable) {
				
				try {
					new QuitarRelacionarProyectoYArtefacto(ce)
					.setUsuarioAnd(_thiz.getUsuario())
					.ejecutarCon(
							new QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion(r, true), 
							construyeInvocacionRecurrente(
									relaciones.size() > 1 ? relaciones.subList(1, relaciones.size()) : null, 
									funcionCola, 
									op
							)
					);
				} catch (Exception e) {
					throw new ErrorInesperadoOperacion(
							"Error durante la invocación del proceso de eliminación de una relación", 
							e
					);
				}
				
				return true;
			}
			
		};
	}
	
}
