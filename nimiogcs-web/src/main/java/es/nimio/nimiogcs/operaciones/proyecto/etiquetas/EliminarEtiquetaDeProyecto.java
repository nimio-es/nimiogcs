package es.nimio.nimiogcs.operaciones.proyecto.etiquetas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaProyeccionProyecto;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosPublicados;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.operaciones.proyecto.QuitarRelacionarProyectoYArtefacto;
import es.nimio.nimiogcs.operaciones.proyecto.QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.utils.DateTimeUtils;

public final class EliminarEtiquetaDeProyecto 
	extends ProcesoAsincronoModulo<EtiquetaProyecto> {

	public EliminarEtiquetaDeProyecto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}


	// ----

	@Override
	protected String nombreUnicoOperacion(EtiquetaProyecto etiqueta, ProcesoAsincrono op) {
		return "LANZAR LA ELIMINACIÓN DE LA ETIQUETA '"
				+ etiqueta.getNombre()
				+ "' DEL PROYECTO '"
				+ etiqueta.getProyecto().getNombre()
				+ "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(EtiquetaProyecto etiqueta, ProcesoAsincrono op) {
		
		// solamente relacionaremos con el proyecto
		registraRelacionConOperacion(op, etiqueta.getProyecto());
	}

	@Override
	protected void hazlo(final EtiquetaProyecto etiqueta, final ProcesoAsincrono op) {
		
		// el proceso de eliminación de una etiqueta es un proceso que requiere, 
		// primero, de la eliminación de todas las relaciones existentes y, cuando 
		// haya finalizado, se procederá a eliminar los datos propios del artefacto
		
		// preparamos la cadena de eliminación de estructuras en repositorio
		final Function<ElementoBaseProyecto, Boolean> _fr = construyeInvocacionRecurrenteEliminarProyecciones(
				new ArrayList<ITareaProyeccionProyecto>(ce.contextoAplicacion().getBeansOfType(ITareaProyeccionProyecto.class).values()),
				new Function<ElementoBaseProyecto, Boolean>() {

					@Override
					public Boolean apply(ElementoBaseProyecto ignorable) {
						return borrarDatosEtiqueta(etiqueta, op);
					}
				},
				etiqueta,
				op
		);
				
		
		construyeInvocacionRecurrente(
				ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(etiqueta)),
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
	
	private boolean borrarDatosEtiqueta(final EtiquetaProyecto etiqueta, final ProcesoAsincrono op) {

		// teniendo presente que la etiqueta puede estar siendo modificada en otras operaciones, vamos a cargar de nuevo la entidad
		final EtiquetaProyecto recargada = ce.etiquetas().findOne(etiqueta.getId());
		
		// borramos los usos (que no hayan sido borrados en otras operaciones)
		for(UsoYProyeccionProyecto upp: recargada.getUsosYProyecciones())
			ce.usosProyecto().delete(upp);
		ce.usosProyecto().flush();

		// hay que localizar todas las publicaciones que estén apuntando a esta etiqueta
		for(PublicacionArtefacto pa: ce.artefactosPublicados().findAll(ArtefactosPublicados.elementosPublicadosEtiqueta(recargada))) {
			pa.setIdEtiqueta(null);
			ce.artefactosPublicados().saveAndFlush(pa);
		}
		ce.artefactosPublicados().flush();
		
		// tenemos que quitar la etiqueta de todas las operaciones donde esté
		HashMap<String, Operacion> opsProcesadas = new HashMap<String, Operacion>();
		for(RelacionOperacion rop: ce.relacionesOperaciones().operacionesDeUnElementoProyecto(etiqueta.getId())) {
			
			// dejamos constancia en la operación
			final Operacion opo = rop.getOperacion();
			final String linea = "[" + DateTimeUtils.formaReducida(new Date()) + "][EXT] Quitando relación con etiqueta '" + etiqueta.getNombre() + "' por eliminación.\n";
			String msg = opo.getMensajesEjecucion();
			msg += Strings.isNotEmpty(msg) ? msg + linea : linea;
			opo.setMensajesEjecucion(msg);

			// las operaciones las vamos a necesitar en dos ocasiones
			opsProcesadas.put(opo.getId(), opo);
			
			ce.relacionesOperaciones().delete(rop);
		}
		
		// y también todas las relaciones de publicación donde se referencia esa etiqueta
		for(RelacionOperacionPublicacionArtefacto ropa: ce.relacionesOperaciones().operacionesDePublicacionDeUnaEtiqueta(etiqueta.getId())) {
			
			// dejamos constancia en la operación
			final String idOp = ropa.getOperacion().getId();
			Operacion opo = opsProcesadas.containsKey(idOp) ? opsProcesadas.get(idOp) : ropa.getOperacion();
			final String linea = "[" + DateTimeUtils.formaReducida(new Date()) + "][EXT] Quitando relación de publicación con etiqueta '" + etiqueta.getNombre() + "' por eliminación.\n";
			String msg = opo.getMensajesEjecucion();
			msg += Strings.isNotEmpty(msg) ? msg + linea : linea;
			opo.setMensajesEjecucion(msg);

			opsProcesadas.put(opo.getId(), opo);
			
			ce.relacionesOperaciones().delete(ropa);
		}
		
		// guardamos una sola vez las operaciones
		for(Operacion opf: opsProcesadas.values()) ce.operaciones().save(opf);
		
		//flushing
		ce.operaciones().flush();
		ce.relacionesOperaciones().flush();

		// borramos la propia etiqueta
		ce.etiquetas().delete(etiqueta);
		ce.etiquetas().flush();
		
		return true;
	}
	
	private Function<ElementoBaseProyecto, Boolean> construyeInvocacionRecurrenteEliminarProyecciones(
			final List<ITareaProyeccionProyecto> tareasProyeccion,
			final Function<ElementoBaseProyecto, Boolean> funcionCola,
			final EtiquetaProyecto etiqueta,
			final ProcesoAsincrono op) {
		
		// condición de salida
		if(tareasProyeccion == null || tareasProyeccion.size() == 0) return funcionCola;
		
		// cogemos el primer elemento de la lista
		final ITareaProyeccionProyecto tpp = tareasProyeccion.get(0);
		
		// si no es requerido procesar la eliminación de la carpeta, lo ignoramos
		if(!tpp.confirmaProcede(etiqueta)) return
				construyeInvocacionRecurrenteEliminarProyecciones(
						tareasProyeccion.size() > 1 ? tareasProyeccion.subList(1, tareasProyeccion.size()) : null,
						funcionCola,
						etiqueta,
						op
				);
		
		final EliminarEtiquetaDeProyecto _thiz = this; 
		
		// devolvemos una función que encapsula la llamada al método que elimina la estructura de repositorio
		return new Function<ElementoBaseProyecto, Boolean>() {

			@Override
			public Boolean apply(ElementoBaseProyecto ignorable) {
				
				try {
					tpp.ejecutarTareaEliminarProyeccion(
							etiqueta, 
							_thiz, 
							construyeInvocacionRecurrenteEliminarProyecciones(
									tareasProyeccion.size() > 1 ? tareasProyeccion.subList(1, tareasProyeccion.size()) : null,
											funcionCola,
											etiqueta,
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
		final EliminarEtiquetaDeProyecto _thiz = this;

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
							"Error durante la invocación del proceso de eliminación de una relación", e
					);
				}
				
				return true;
			}
			
		};
	}
	
}
