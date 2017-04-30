package es.nimio.nimiogcs.subtareas.apiweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.stream.Stream;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.OperacionBase;
import es.nimio.nimiogcs.subtareas.apiweb.ComienzoPublicacionDeployerOperacion.IComienzoPublicacionDeployer.IDatosArtefacto;

public class ComienzoPublicacionDeployerOperacion 
	extends OperacionBase<IContextoEjecucionBase, ComienzoPublicacionDeployerOperacion.IComienzoPublicacionDeployer, String>{

	// ---------------------------------------
	// Construcción
	// ---------------------------------------
	
	public ComienzoPublicacionDeployerOperacion(IContextoEjecucionBase ce) {
		super(ce);
	}

	// ---------------------------------------
	// Tipo de la operación
	// ---------------------------------------

	/**
	 * Interfaz que debe implementar la estructura que se pase a este proceso u operación. 
	 */
	public static interface IComienzoPublicacionDeployer {

		/**
		 * Datos de cada uno de los artefactos que acompañen la petición.
		 */
		public static interface IDatosArtefacto {
			String getId();
			String getNombre();
			String getTicket();
		}

		String getEntorno();
		String getEtiquetaPase();
		String getUsuario();
		List<IDatosArtefacto> getListaArtefactos();
		boolean getEsMarchaAtras();
	}
	
	// ---------------------------------------
	// Validación
	// ---------------------------------------
	
	public void validar(IComienzoPublicacionDeployer datos, Errors errores) {
		
		// algunos alias
		String entorno = datos.getEntorno();
		String etiquetaPase = datos.getEtiquetaPase();
		
		// hacemos algunas validaciones
		if(entorno.equalsIgnoreCase("INTEGRACION") && (etiquetaPase == null || etiquetaPase.isEmpty()))
			errores.rejectValue("etiquetaPase", "TIQUE_NO_NULO", "Falta tique de la operación original para una publicación en integración");
		if(!Streams.of(datos.getListaArtefactos()).forAll(new Predicate<IComienzoPublicacionDeployer.IDatosArtefacto> () {
					@Override
					public boolean test(IComienzoPublicacionDeployer.IDatosArtefacto v) {
						boolean respuesta = v.getId() != null && !v.getId().isEmpty();
						respuesta = respuesta && v.getTicket() != null && !v.getTicket().isEmpty();
						return respuesta;
					}
				})) 
			errores.rejectValue("artefactos", "ARTEFACTOS_NO_VALIDOS", "Referencias a artefactos incompletas");
		
		// si es integración, buscamos la operación de espera que tenga la etiqueta de pase,
		if(entorno.equalsIgnoreCase("INTEGRACION") && etiquetaPase != null) {
			
			ProcesoEsperaRespuestaDeployer procesoTicket = ce.repos().operaciones().procesoEsperaRespuestaDeployerConEtiquetaPase(etiquetaPase);
			if(procesoTicket==null || procesoTicket.getFinalizado()) {
				errores.rejectValue("etiquetaPase", "PROCESO_NO_ACTIVO", "La etiqueta de pase no se corresponde con un proceso en espera activo.");
			}
			else {
				// tenemos que validar que todos los artefactos que no hay artefactos de la petición original que no están en la petición deployer
				final Stream<IComienzoPublicacionDeployer.IDatosArtefacto> datosArtefactos = Streams.of(datos.getListaArtefactos());
				final Stream<Artefacto> entidades = Streams.of(
						ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(procesoTicket.getId()))
						// nos quitamos la de proyecto y de etiqueta
						.filter(new Predicate<RelacionOperacion> () {

							@Override
							public boolean test(RelacionOperacion r) {
								
								// si es una relación con proyecto/etiqueta o entorno, directamente la excluimos
								return !(r instanceof RelacionOperacionElementoProyecto || r instanceof RelacionOperacionDestinoPublicacion);
							}
							
						})
						.map(new Function<RelacionOperacion, Artefacto>() {
							@Override
							public Artefacto apply(RelacionOperacion r) {
								
								return ((RelacionOperacionArtefacto)r).getEntidad();
							}
						});
				
				boolean cubreTodas = entidades.forAll(
						new Predicate<Artefacto> () {

								@Override
								public boolean test(final Artefacto e) {
									return datosArtefactos.exists(new Predicate<IComienzoPublicacionDeployer.IDatosArtefacto>() {
			
										@Override
										public boolean test(IComienzoPublicacionDeployer.IDatosArtefacto d) {
											return d.getId().equals(e.getId());
										}
										
									});
								}
					});
				
				if(!cubreTodas) errores.rejectValue(
						"artefactos", 
						"NO_TODOS_INT", 
						"No se han indicado todos los artefactos del pase a integración como parte de la operación");
			}

		} else {
		
			// cuando no es integración, hay que recorrer la lista de artefactos indicados y confirmar que existe en el ticket original 
			// TODO: pendiente de hacer para cuando pasemos a esta parte
		}
	}
	
	// ---------------------------------------
	// Ejecución
	// ---------------------------------------
	
	// .............................................................
	// Tipos auxiliares con la estructura de datos para la ejecución
	// .............................................................

	/**
	 * Asocia el ticket de la petición con la etiqueta del proyecto al que corresponde y la lista de artefactos de la misma
	 */
	final static class TicketPeticionEtiquetaArtefacto extends Tuples.T3<String, EtiquetaProyecto, List<Artefacto>> {

		private static final long serialVersionUID = 1468664297054564557L;

		public TicketPeticionEtiquetaArtefacto(String ticketPeticion, EtiquetaProyecto etiqueta, List<Artefacto> listaEntidades) {
			super(ticketPeticion, etiqueta, listaEntidades);
		}
		
		public String ticketPeticion() { return this._1; }
		public EtiquetaProyecto etiqueta() { return this._2; }
		public List<Artefacto> listaEntidades() { return this._3; }
	}
	
	/**
	 * Asocia un proyecto con una lista de peticiones y etiquetas y artefactos
	 */
	final static class ProyectoArtefactos extends Tuples.T2<Proyecto, List<TicketPeticionEtiquetaArtefacto>> {

		private static final long serialVersionUID = -1298389611380241858L;

		public ProyectoArtefactos(Proyecto proyecto,
				List<TicketPeticionEtiquetaArtefacto> listaArtefactos) {
			super(proyecto, listaArtefactos);
		}
		
		public Proyecto proyecto() { return this._1; }
		public List<TicketPeticionEtiquetaArtefacto> listaArtefactos() { return this._2; }
	}
	
	// .............................................................
	
	/**
	 * Dado el ticket de la petición original encuentra la etiqueta de proyecto y el propio proyecto
	 */
	private Tuples.T2<EtiquetaProyecto, Proyecto> recogeDatosEtiquetaYProyectoDesdeTicketPeticion(String ticketPeticion) {
		
		// buscamos el proceso de espera que tiene la petición original
		ProcesoEspera procesoOriginal = ce.repos().operaciones().procesoEsperaConTicket(ticketPeticion);
		if(procesoOriginal == null) 
			throw new ErrorInconsistenciaDatos("No puede existir un ticket de petición sin un proceso de espera asociado.");
		
		// recogemos todas las relaciones del mismo
		Collection<RelacionOperacion> relacionesProcesoOriginal = ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(procesoOriginal.getId());
		
		// buscamos dos destinos concretos, proyecto y etiqueta de proyecto
		Proyecto proyecto = null;
		EtiquetaProyecto etiquetaProyecto = null;
		for(RelacionOperacion r: relacionesProcesoOriginal) {
			if(r instanceof RelacionOperacionElementoProyecto) {
				RelacionOperacionElementoProyecto rp = (RelacionOperacionElementoProyecto)r;
				if(rp.getElementoProyecto() instanceof Proyecto) proyecto = (Proyecto) rp.getElementoProyecto();
				if(rp.getElementoProyecto() instanceof EtiquetaProyecto) etiquetaProyecto = (EtiquetaProyecto) rp.getElementoProyecto();
			}
		}
		
		// indistintamente de dónde se encontraran en la lista de relaciones, ambos datos deben hacer sido asignados
		if(proyecto == null || etiquetaProyecto == null) 
			throw new ErrorInconsistenciaDatos("Cualquier relación de publicación debe tener relacionado tanto el proyecto como la etiqueta de la que parte.");
		
		// devolvemos la estructura que necesitamos
		return Tuples.tuple(etiquetaProyecto, proyecto);
	}
	
	/**
	 * Ejecución
	 */
	@Override
	public String ejecutar(ComienzoPublicacionDeployerOperacion.IComienzoPublicacionDeployer datos)	throws ErrorInesperadoOperacion {

		
		// algunos alias
		String entorno = datos.getEntorno();
		String etiquetaPase = datos.getEtiquetaPase();
		String usuario = datos.getUsuario().toUpperCase();
		
		
		// El proceso consistirá en ir tomando cada uno de los distintos tickets de petición original que se indiquen 
		// e iremos, a partir de él, buscando la etiqueta de proyecto y el propio proyecto relacionado.
		// Para ahorrar búsquedas iremos cacheando los resultados de búsquedas anteriores, para lo que necesitaremos unos diccionarios.
		// 1. Diccionario que, dado un ticket de petición, cachearemos los datos de etiqueta y lista de artefactos
		final Map<String, TicketPeticionEtiquetaArtefacto> cacheTicketDatosEtiqueta = new HashMap<String, ComienzoPublicacionDeployerOperacion.TicketPeticionEtiquetaArtefacto>();
		
		// 2. Diccionario que, dado un código de proyecto, dice si ya está o no cacheado.
		final Map<String, ProyectoArtefactos> cacheProyectoDatosProyecto = new HashMap<String, ComienzoPublicacionDeployerOperacion.ProyectoArtefactos>();
		
		// También necesitamos la lista con todos los proyectos que afecta el pase
		final List<ProyectoArtefactos> datosPublicacion = new ArrayList<ComienzoPublicacionDeployerOperacion.ProyectoArtefactos>();
		
		// recorremos los artefactos de la petición 
		for(IDatosArtefacto da: datos.getListaArtefactos()) {
			String ticketOriginal = da.getTicket();
			String idEntidad = da.getId();
			
			// siempre vamos a necesitar la entidad o artefacto, por lo que procedemos a cargarlo antes de hacer otro tipo de comprobaciones
			Artefacto artefacto = ce.repos().artefactos().buscar(idEntidad);
			
			// el caso más sencillo será cuando tengamos cacheada la estructura que correlacionad ticket con etiqueta y lista de artefactos
			if(cacheTicketDatosEtiqueta.containsKey(ticketOriginal)) {
				
				// en ese caso simplemente añadimos la entidad a la lista de artefactos ya registrada
				cacheTicketDatosEtiqueta.get(ticketOriginal).listaEntidades().add(artefacto);
				
			} else {
				
				// en caso contrario tenemos que obtener la petición del ticket original (proceso de espera respuesta Deployer)
				// donde tendremos relacionada tanto la etiqueta de proyecto como el propio proyecto
				Tuples.T2<EtiquetaProyecto, Proyecto> t = recogeDatosEtiquetaYProyectoDesdeTicketPeticion(ticketOriginal);
				
				// como el ticket no estaba cacheado, vamos a necesitar crear la estructura de datos del proceso
				TicketPeticionEtiquetaArtefacto datosTicketPeticion = new TicketPeticionEtiquetaArtefacto(ticketOriginal, t._1, new ArrayList<Artefacto>());
				datosTicketPeticion.listaEntidades().add(artefacto);
				
				// ahora queremos añadir la nueva lista de artefactos de una etiqueta al proyecto
				// para lo que confirmamos si ya está cacheado
				if(cacheProyectoDatosProyecto.containsKey(t._2.getId())) {
					
					// se añade a los datos vinculados al proyecto
					cacheProyectoDatosProyecto.get(t._2.getId()).listaArtefactos().add(datosTicketPeticion);
					
				} else {
					
					// primera aparición del proyecto, por lo que tendremos que crear la estructura de datos del proyecto
					ProyectoArtefactos datosProyectoArtefactos = new ProyectoArtefactos(t._2, new ArrayList<TicketPeticionEtiquetaArtefacto>());
					datosProyectoArtefactos.listaArtefactos().add(datosTicketPeticion);
					
					// como es nuevo, añadimos a la lista de datos de la publicación
					datosPublicacion.add(datosProyectoArtefactos);
					
					// y también lo cacheamos para la siguiente operación
					cacheProyectoDatosProyecto.put(t._2.getId(), datosProyectoArtefactos);
				}
				
				// cacheamos la estructura de ticket
				cacheTicketDatosEtiqueta.put(ticketOriginal, datosTicketPeticion);
			}
		}
		
		// antes de procesar la estructura, vamos a cargar la entidad entorno, dado que la necesitamos para
		// relacionarla con el proceso de publicación
		DestinoPublicacion entidadEntorno = ce.repos().destinosPublicacion().buscar(entorno);

		// ...........................................
		// CREAMOS EL REGISTRO DE PUBLICACIÓN
		// ...........................................
		
		Publicacion publicacion = new Publicacion();
		publicacion.setCanal("DEPLOYER");
		publicacion.setIdDestinoPublicacion(entorno);
		publicacion.setMarchaAtras(datos.getEsMarchaAtras());
		publicacion.enEjecucion();
		publicacion = ce.repos().publicaciones().guardarYVolcar(publicacion);
		
		// recorremos los artefactos referenciados para crear el registro
		for(ProyectoArtefactos da: datosPublicacion) {
			
			for(TicketPeticionEtiquetaArtefacto ea: da.listaArtefactos()) {

				EtiquetaProyecto etiqueta = ea.etiqueta();
				for(Artefacto artefacto: ea.listaEntidades()) {
					
					Artefacto base = artefacto instanceof ITestaferroArtefacto ?
							((ITestaferroArtefacto)artefacto).getArtefactoAfectado()
							: artefacto;
							
					PublicacionArtefacto artefactoPublicado = new PublicacionArtefacto();
					artefactoPublicado.setPublicacion(publicacion);
					artefactoPublicado.setArtefacto(base);
					artefactoPublicado.setIdEtiqueta(etiqueta.getId());
					artefactoPublicado.setNombreEtiqueta(etiqueta.getNombre());
					artefactoPublicado.setProyecto(etiqueta.getProyecto());
					artefactoPublicado.setDirecto(true);
					ce.repos().artefactosPublicados().guardar(artefactoPublicado);
				}
				ce.repos().artefactosPublicados().volcar();
			}
		}
		
		// ...........................................
		// REGISTRAMOS LA OPERACIÓN
		// ...........................................
		
		// creamos la operación que representa la publicación en deployer
		ProcesoEsperaPublicacionDeployer procesoDeployer = new ProcesoEsperaPublicacionDeployer(
				"OPERACIÓN PUBLICACIÓN DEPLOYER EN '" + entorno + "' (" + etiquetaPase + ")", 
				etiquetaPase, 
				entorno, 
				30 * 60);
		procesoDeployer.setUsuarioEjecuta(usuario);
		procesoDeployer = ce.repos().operaciones().guardarYVolcar(procesoDeployer);		
		
		// asociamos a la publicación
		ce.repos().operaciones().relaciones().guardar(new RelacionOperacionPublicacion(procesoDeployer, publicacion));
		
		// asociamos el entorno
		ce.repos().operaciones().relaciones().guardar(new RelacionOperacionDestinoPublicacion(procesoDeployer, entidadEntorno));
		
		// y recorremos la estructura de datos de publicación para relacionar también con el proceso deployer
		for(ProyectoArtefactos pa: datosPublicacion) {
			
			// el propio proyecto
			ce.repos().operaciones().relaciones().guardar(new RelacionOperacionElementoProyecto(procesoDeployer, pa.proyecto()));
			
			// y cada una de las etiquetas
			for(TicketPeticionEtiquetaArtefacto tpea: pa.listaArtefactos()) {
				
				// la etiqueta
				ce.repos().operaciones().relaciones().guardar(new RelacionOperacionElementoProyecto(procesoDeployer, tpea.etiqueta()));
				
				// y los artefactos
				for(Artefacto ec: tpea.listaEntidades()) {

					ce.repos().operaciones().relaciones().guardar(
							new RelacionOperacionPublicacionArtefacto(
									procesoDeployer, ec, tpea.ticketPeticion(), tpea.etiqueta()
							)
					);
				} 
			}
		}
		
		// queda hacer un flush para terminar
		ce.repos().operaciones().relaciones().volcar();
		
		// si estamos en integración habrá que terminar la tarea de espera previa
		if(entorno.equalsIgnoreCase("INTEGRACION")) {
			ProcesoEsperaRespuestaDeployer procesoTicket = ce.repos().operaciones().procesoEsperaRespuestaDeployerConEtiquetaPase(etiquetaPase);
			if(procesoTicket!=null) {
				procesoTicket.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.OK);
				procesoTicket.setFinalizado(true);
				ce.repos().operaciones().guardarYVolcar(procesoTicket);
			}
		}
		
		// salimos con las "manos vacías"
		return "";
	}
}
