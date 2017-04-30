package es.nimio.nimiogcs.subtareas.publicacion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.BiFunction;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T3;
import es.nimio.nimiogcs.functional.Tuples.T5;
import es.nimio.nimiogcs.functional.stream.Stream;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;
import es.nimio.nimiogcs.servicios.FactoriaServicioWebDeployer;
import es.nimio.nimiogcs.servicios.externos.Subversion;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.Elemento;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.EtiquetaFuncional;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.Extension;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ObjectFactory;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAI;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAIPortType;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.TipoElemento;
import es.nimio.nimiogcs.subtareas.publicacion.PublicacionDeployer.ErrorRechazoOperacionDeployer.EnumPasoEnvioDeployer;
import es.nimio.nimiogcs.utils.DateTimeUtils;

public class PublicacionDeployer 
	extends ProcesoAsincronoBase<IContextoEjecucionBase, IDatosPeticionPublicacion> {

	// -----------------------------------------------------------
	// Estructuras auxiliares
	// -----------------------------------------------------------

	/**
	 * Excepción que solamente tiene sentido en este ámbito, dado que aquí 
	 * es donde nos comunicamos con Deployer
	 */
	static class ErrorRechazoOperacionDeployer extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2514954765706858586L;

		public static enum EnumPasoEnvioDeployer {
			SUBIDA,
			ENCOLADO
		}
		
		public ErrorRechazoOperacionDeployer(EnumPasoEnvioDeployer paso, Integer codigoError) {
			super(
					"Error en la comunicación con Deployer en el paso '" 
					+ paso.toString() 
					+ "'" 
					+ ((codigoError != null) ? " y código de error '" + codigoError.toString() + "'" : ""));
		}
	}
	
	// ------------------
	
	public PublicacionDeployer(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}
	
	// ---

	@Override
	protected String nombreUnicoOperacion(IDatosPeticionPublicacion datos, ProcesoAsincrono op) {
		return "PUBLICAR POR DEPLOYER LA ETIQUETA '" + datos.getEtiqueta().getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(IDatosPeticionPublicacion datos, ProcesoAsincrono op) {
		// relacionamos con la etiqueta, el proyecto de la etiqueta y con todos los artefactos
		registraRelacionConOperacion(op, datos.getEtiqueta());
		registraRelacionConOperacion(op, datos.getEtiqueta().getProyecto());
		
		for(Artefacto a: datos.getArtefactos())
			registraRelacionConOperacion(op, a);
	}

	@Override
	protected void hazlo(IDatosPeticionPublicacion datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		// sacamos los parámetros del canal
		final String codigoPROI = datos.getParametrosCanal().get("Código PROI");
		
		// ticket
		final String instante = DateTimeUtils.convertirAFormaYYMMDDHHMMSS(new Date());
		final String version = "NOV" + instante;
		final String ticket = new StringBuilder(datos.getEtiqueta().getNombre())
			.append(instante)
			.toString();
		
		// queremos calcular la información necesaria de cada elemento que se solicita 
		// para poder construir correctamente el elemento de la petición deployer
		final ServicePAI servicioDeployer = ce.componente(FactoriaServicioWebDeployer.class).creaServicio();
		final ServicePAIPortType deployer = servicioDeployer.getServicePAIHttpSoap12Endpoint();

		// tomamos el código PRIO de la madre, siempre que no sea distinto de XX
		final String proiMadre = Strings.isNullOrEmpty(codigoPROI) ? 
				"" 
				: deployer.validarPROI(datos.getUsuario(), codigoPROI);
		

		// cargamos las entidades
		Stream<Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>> entidades = 
				buscarEntidadesYCalcularDatosAuxiliares(datos.getArtefactos(), deployer);
		
		// calculamos el mapa de etiquetas funcionales asociados a las aplicaciones
		final Map<String, EtiquetaFuncional> mapaAppEtiquetaFuncional = relacionAppConEtiquetaFuncional(entidades, deployer);
		
		// mapa que relaciona id de tipo con la estructura correspondiente
		int idGrupo = Integer.parseInt(ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.GRUPOELEMENTOS").getComoValorSimple());
		final Map<Integer, Tuples.T2<TipoElemento, Extension>> mapaIdConTipoeElemento = cargarTiposElementos(idGrupo, deployer);
		
		// creamos la estructura de descriptores en subversion
		final Stream<Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String>> entidadesConDescriptores =
				crearDescriptores(
						datos.getEtiqueta().getProyecto(), 
						datos.getEtiqueta(), 
						ticket, 
						mapaIdConTipoeElemento, 
						entidades
				);
		
		// a por la lista de elementos
		final Stream<Elemento> elementosSolicitud = elementosAPublicar(
				codigoPROI, 
				proiMadre, 
				datos.getEtiqueta().getProyecto().getNombre(), 
				version, 
				datos.getUsuario(), 
				ticket, 
				mapaAppEtiquetaFuncional, 
				mapaIdConTipoeElemento, 
				entidadesConDescriptores, 
				deployer
		);

		
		// Comenzamos con la parte de solicitud
		// Pedimos una etiqueta
		String etiquetaPase = deployer.getEtiquetaPase("INT");

		Elemento primero = elementosSolicitud.getHead();
		int retCode = deployer.subirElemento(primero, etiquetaPase, true);
		
		if(retCode != 1) throw new ErrorRechazoOperacionDeployer(EnumPasoEnvioDeployer.SUBIDA, retCode);
		
		// incorporamos los demás
		for (Elemento elemento: elementosSolicitud.skip(1)) {
			
			retCode = deployer.subirElemento(elemento, etiquetaPase, false);
			if(retCode != 1) throw new ErrorRechazoOperacionDeployer(EnumPasoEnvioDeployer.SUBIDA, retCode);
		}
		
		// si todo marcha bien, podemos hacer el envío
		boolean queueOk = deployer.enviarElementosACola(
				Collections.list(elementosSolicitud.getEnumeration()),
				"LOC", "INT", etiquetaPase, "DISTRIBUIR_EXPLOTAR", datos.getUsuario());

		if(!queueOk) throw new ErrorRechazoOperacionDeployer(EnumPasoEnvioDeployer.ENCOLADO, null);
		
		// Llegados a este punto, resta crear un proceso de espera hasta que 
		// confirmen que la publicaciÃ³n ha comenzado en Deployer
		ProcesoEspera procesoEspera = new ProcesoEsperaRespuestaDeployer(
				"ESPERA RESPUESTA PUBLICACIÓN DEPLOYER, TIQUE: " + ticket,
				ticket, 
				etiquetaPase);
		procesoEspera.setUsuarioEjecuta(datos.getUsuario().toUpperCase());
		procesoEspera.setMinutosEspera(30);
		
		// reasignamos porque es la forma que tiene SpringJPA de darnos una entidad persistida.
		procesoEspera = ce.repos().operaciones().guardarYVolcar(procesoEspera); 

		// y lo relacionamos con los elementos que corresponde
		ce.repos().operaciones().relaciones().guardar(
				new RelacionOperacionElementoProyecto(procesoEspera, datos.getEtiqueta().getProyecto())
		);
		ce.repos().operaciones().relaciones().guardar(
				new RelacionOperacionElementoProyecto(procesoEspera, datos.getEtiqueta())
		);
		
		for(Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String> entidad: entidadesConDescriptores) {
			ce.repos().operaciones().relaciones().guardar(
					new RelacionOperacionPublicacionArtefacto(procesoEspera, entidad._1, ticket, datos.getEtiqueta())
			);
		} 
		ce.repos().operaciones().relaciones().volcar();
		
		// como también queremos asociar al entorno que estamos impactando
		// lo intentamos localizar y lo asociamos
		DestinoPublicacion entorno = ce.repos().destinosPublicacion().buscar("INTEGRACION");
		if(entorno!=null)
			ce.repos().operaciones().relaciones().guardarYVolcar(
					new RelacionOperacionDestinoPublicacion(procesoEspera, entorno)
			);
		
	}

	
	// ------------------------------------------------------------------------------------------
	// Métodos auxiliares / utilidad
	// ------------------------------------------------------------------------------------------

	/**
	 * Devuelve un stream con los datos cargados de las entidades que se han pasado como parámetro a la tarea.
	 * Pero además, devuelve un indicador que dice si la entidad es inventariable o no (que está asociada a una aplicación)
	 * y el identificador de tipo que se le debe poner, así como una lista de posibles extensiones que puede
	 * usar el elemento. Con todo ello se fabrica una tupla de 5 elementos que es devuelta en un recubrimiento 
	 * de Stream.
	 */
	private Stream<Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>> buscarEntidadesYCalcularDatosAuxiliares (
			final Collection<Artefacto> artefactos, 
			final ServicePAIPortType deployer) {
		
		return Streams.of(artefactos)

				// comprobar si es inventariable
				.map(new Function<Artefacto, Tuples.T3<Artefacto, Boolean, DirectivaInventario>> () {

					@Override
					public T3<Artefacto, Boolean, DirectivaInventario> apply(
							Artefacto entidad) {
						DirectivaInventario entidadDeAplicacion = P.of(entidad).inventario();
						return Tuples.tuple(entidad, entidadDeAplicacion != null, entidadDeAplicacion);
					}
				})
				// calculamos el identificador de tipo en función de las condiciones de la entidad
				// y las extensiones posibles
				.map(new Function<
						Tuples.T3<Artefacto, Boolean, DirectivaInventario>,
						Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>>() {

							@Override
							public Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>> apply(
									Tuples.T3<Artefacto, Boolean, DirectivaInventario> t) {

								int idTipoElemento = Integer.parseInt(P.of(t._1).publicacionDeployer().getCodigoElemento());
								List<Extension> extensiones = deployer.getExtensiones(idTipoElemento);
								return Tuples.tuple(t._1, t._2, t._3, idTipoElemento, extensiones);
							}
					
				});
	}
	
	/**
	 * Con objeto de acelerar a posteriori el proceso de construcción de los elementos a enviar, hacemos una búsqueda
	 * de las etiquetas funcionales que tienen las aplicaciones de cada uno de los artefactos que vamos a publicar. Como 
	 * puede haber varios artefactos asociados a la misma aplicación, dicha búsqueda la realizaremos solamente una vez
	 * y "cachearemos" en un mapa. 
	 */
	private Map<String, EtiquetaFuncional> relacionAppConEtiquetaFuncional(
			final Stream<Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>> datosEntidades, 
			final ServicePAIPortType deployer) {
		
		return datosEntidades
				
				// solamente las que pueden tener aplicación asociada (las inventariables)
				.filter(new Predicate<Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>> () {

					@Override
					public boolean test(
							T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>> entidad) {
						return entidad._2;
					}
					
				})
				
				// recorremos las resultantes insertando solamente una vez la etiqueta funcional en el mapa
				.foldLeft(
						new HashMap<String, EtiquetaFuncional>(), 
						new BiFunction<
								Map<String, EtiquetaFuncional>, 
								Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>, 
								Map<String, EtiquetaFuncional>> () {

									@Override
									public Map<String, EtiquetaFuncional> apply(
											Map<String, EtiquetaFuncional> mapa,
											T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>> entidad) {

										String aplicacion = entidad._3.getAplicacion().getId();

										// y solo si en el mapa ya no está
										if(!mapa.containsKey(aplicacion)) {
										
											EtiquetaFuncional etiquetaFuncional = 
													Streams.of(deployer.getEtiquetasFuncionales(aplicacion))
														.filter(new Predicate<EtiquetaFuncional>() {
															@Override
															public boolean test(
																	EtiquetaFuncional etiquetaFuncional) {
																return etiquetaFuncional.getIdEtiqueta().getValue().startsWith("EF_GLOBAL");
															}
														})
														.getHead();
											
											if(etiquetaFuncional!=null) {
												mapa.put(aplicacion, etiquetaFuncional);
											}
													
										}
										
										return mapa;
									}
							
						});
		
	}
	
	/**
	 * Es necesario recuperar información del tipo, para lo que se ha puesto un grupo donde se relacionarán únicamente
	 * los tipos de elemento que están relacionados con Nimio. Se usará esta información para construir un mapa que permita
	 * luego, durante la fase de construcción de los elementos, recuperar los datos del tipo de forma rápida.
	 */
	private Map<Integer, Tuples.T2<TipoElemento, Extension>> cargarTiposElementos(
			final int idGrupo,
			final ServicePAIPortType deployer) {
		
		return Streams.of(deployer.getTipoElementosGrup(idGrupo))
				.foldLeft(
						new HashMap<Integer, Tuples.T2<TipoElemento, Extension>>(), 
						new BiFunction<Map<Integer, Tuples.T2<TipoElemento, Extension>>, TipoElemento, Map<Integer, Tuples.T2<TipoElemento, Extension>>> () {

							@Override
							public Map<Integer, Tuples.T2<TipoElemento, Extension>> apply(
									Map<Integer, Tuples.T2<TipoElemento, Extension>> mapa, TipoElemento te) {
								
								mapa.put(te.getId(), Tuples.tuple(te, deployer.getExtensiones(te.getId()).get(0)));
								return mapa;
							}
							
						});
		
	}
	
	/**
	 * Tomando los datos de las entidades que queremos publicar, y la etiqueta, construimos los descriptores que tenemos que facilitar. 
	 * El resultado devuelve un stream con una tupla de 6, donde se incluye la ruta del archivo descriptor dentro de subversion.
	 * @throws IOException 
	 */
	private Stream<Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String>> crearDescriptores(
			final Proyecto proyecto,
			final EtiquetaProyecto etiqueta,
			final String ticket,
			final Map<Integer, Tuples.T2<TipoElemento, Extension>> mapaIdConTipoeElemento,
			final Stream<Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>>> datosEntidades) {
		
		// recogemos los parámetros de conexión desde los parámetros globales
		final String busSvnUrl = ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.URL").getComoValorSimple();
		final String busSvnUsr = ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.USUARIO").getComoValorSimple();
		final String busSvnPwd = ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.PASSWORD").getComoValorSimple();
		
		// de momento mantenemos los datos de repositorio a pelo en código
		final Subversion subversion = new Subversion(busSvnUrl, busSvnUsr, busSvnPwd);
		
		// creamos la subcarpeta de la etiqueta
		subversion.crearCarpeta(ticket);
		
		// hacemos un checkout de la misma en un directorio temporal
		// nos traemos la raíz a local, para poder construir toda la estructura de carpetas que se necesitan
		// para compilar el proyecto
		File rutaLocal =
				new File(
					new StringBuilder(
							ce.variablesEntorno().propiedadRequerida("file.tmp.folder")
					)
					.append(File.separatorChar)
					.append(etiqueta.getNombre())
					.append(File.separatorChar)
					.append("publicacion_")
					.append(ticket)
					.toString());

		// creamos la carpeta
		rutaLocal.mkdirs();
		rutaLocal.deleteOnExit();
		
		// para el checkout empleamos la ruta total
		String rutaPublicacionDescriptor = 
				new StringBuilder(busSvnUrl)
				.append('/')
				.append(ticket)
				.toString();
		
		// creamos la copia de trabajo en la carpeta temporal
		subversion.checkout(rutaPublicacionDescriptor, rutaLocal);
		
		// comenzamos el proceso
		List<Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String>> entidadesConDescriptor = new ArrayList<Tuples.T6<Artefacto,Boolean,DirectivaInventario,Integer,List<Extension>,String>>();
		for(Tuples.T5<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>> tuplaEntidad: datosEntidades) {
			
			// alias para facilitar la lectura
			int idTipo = tuplaEntidad._4;
			
			// la estructura de tipo y la extensión asociada
			final Tuples.T2<TipoElemento, Extension> t = mapaIdConTipoeElemento.get(idTipo);
			if(t==null) throw new ErrorInconsistenciaDatos("No hay correspondencia en los datos de tipo Deployer con el id de tipo '" + idTipo + "'");
			final TipoElemento tipoElemento = t._1;
			final Extension extension = t._2;
			
			// mantendremos la subruta que aporta el proppio Deployer
			final String subrutaTipoDeployer = tipoElemento.getDirBase().getValue();
			
			String nombreArchivoDescriptor = new StringBuilder(tuplaEntidad._1.getNombre().toLowerCase())
				.append('.')
				.append(String.format("%04d",idTipo))
				.append(extension.getExtension().getValue())
				.toString();
			
			// la subcarpeta del tipo
			File subcarpetaTipo = new File(
					new StringBuilder(rutaLocal.toString())
							.append(File.separatorChar)
							.append(P.of(tuplaEntidad._1).publicacionDeployer().getCarpetaElemento())
							.toString());
			
			// si no está creada la carpeta por otro descriptor, la creamos
			// y la añadimos al repositorio para que ya conste que hay que subirla
			if(!subcarpetaTipo.exists()) { 
				subcarpetaTipo.mkdir();
				subversion.addArchivoLocalACopiaTrabajo(subcarpetaTipo);
			}
			
			// y por fin cogemos el descriptor del tipo
			File fileDescriptor = new File(
						new StringBuilder(subcarpetaTipo.toString())
						.append(File.separatorChar)
						.append(nombreArchivoDescriptor)
						.toString());

			// preparamos la estructura de datos a escribir
			StringBuilder sb = new StringBuilder();
			sb.append("NIMIO-ID-ARTEFACTO=").append(tuplaEntidad._1.getId()).append('\n');
			sb.append("NIMIO-NOMBRE-ARTEFACTO=").append(tuplaEntidad._1.getNombre()).append('\n');
			sb.append("NIMIO-ETIQUETA=").append(etiqueta.getNombre()).append('\n');
			sb.append("NIMIO-TICKET=").append(ticket).append('\n');
			sb.append("NIMIO-TIPO=").append(tuplaEntidad._1.getTipoArtefacto().getId()).append('\n');
			sb.append("DEPLOYER-TIPO=").append(idTipo).append('\n');

			sb.append("BUILD-REPO=").append(proyecto.getEnRepositorio().rutaTotalPublicacion()).append("/mvn/").append(etiqueta.getNombre().toLowerCase()).append('\n');
			sb.append("BUILD-TIPO=").append("MAVEN").append('\n');
			
			// además de lo anterior vamos a introducir la subruta del target
			Artefacto base = tuplaEntidad._1 instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)tuplaEntidad._1).getArtefactoAfectado() : tuplaEntidad._1;
			DirectivaPublicacionDeployer dph = PT.of(base.getTipoArtefacto()).publicacionDeployer();
			sb.append("BUILD-TARGET=")
			.append(base.getNombre().toLowerCase())
			.append("/target/")
			.append(dph.getElementoTarget().replace("@artefacto@", base.getNombre().toLowerCase()))
			.append("\n");
			
			// ruta que se añade como parte de la comunicación
			final String rutaIntermedia = subrutaTipoDeployer + 
					(Strings.isNotEmpty(dph.getCarpetaElemento()) ? "/" + dph.getCarpetaElemento() : "");

			// continuamos con los impactos (dónde hay que publicar)
			UtilidadExtenderDescriptorTipoWar.extenderDescriptor(sb, ce, etiqueta, tuplaEntidad._1, rutaIntermedia);
			UtilidadExtenderDescriptorTipoPom.extenderDescriptor(sb, ce, etiqueta, tuplaEntidad._1, rutaIntermedia);
			UtilidadExtenderDescriptorTipoLibreriaJava.extenderDescriptor(sb, ce, etiqueta, tuplaEntidad._1, rutaIntermedia);
			UtilidadExtenderDescriptorTipoEstaticos.extenderDescriptor(sb, ce, etiqueta, tuplaEntidad._1, rutaIntermedia);
			UtilidadExtenderDescriptorTipoGeneral.extenderDescriptor(sb, ce, etiqueta, tuplaEntidad._1, rutaIntermedia);

			// también vamos a recorrer la lista de directivas y diccionarios y vamos a añadir lo que ahí se indique
			for(String idDirectiva: dph.getDirectivas()) {
				
				DirectivaBase db = null;
				String prefijo = "";
				
				// diferenciamos entre diccionario y tipo base
				if(idDirectiva.startsWith("@")) {

					String idDiccionario = idDirectiva.replace("@", "");
					
					db = P.of(tuplaEntidad._1).diccionario(idDiccionario);
					if(db==null) P.of(base).diccionario(idDiccionario);
					if(db==null) PT.of(base.getTipoArtefacto()).diccionario(idDiccionario);
					
					if(db!=null) prefijo = idDiccionario;
					
				} else {
					
					// empezamos buscando en el artefacto
					db = P.of(tuplaEntidad._1).buscarDirectiva(idDirectiva);
					if(db==null) db = P.of(base).buscarDirectiva(idDirectiva);
					if(db==null) db = PT.of(base.getTipoArtefacto()).buscarDirectiva(idDirectiva);
					
					if(db!=null) prefijo = db.getDirectiva().getId();
				}
				
				// si hemos dado con algo, lo ponemos
				if(db!=null) {
					for(Entry<String, String> e: db.getMapaValores().entrySet()) {
						sb.append(prefijo.toUpperCase())
						.append("-")
						.append(e.getKey().replace("_", "-").replace(".","-"))
						.append("=")
						.append(e.getValue())
						.append("\n");
					}
				}
			}
			
			
			
			// escribimos los datos
			try {
				FileOutputStream fw = new FileOutputStream(fileDescriptor, false);
				try {
					// decoramos el canal de salida indicando que debe ser utf-8
					OutputStreamWriter os = new OutputStreamWriter(
							fw,
							Charset.forName("UTF-8").newEncoder()
					);
					try {
						os.write(sb.toString());
					} finally {
						os.flush();
						os.close();
					}				
					
				} finally {
					fw.flush();
					fw.close();
				}
			} catch(IOException ioex) {
				throw new ErrorInesperadoOperacion(ioex);
			}

			// se añade al repositorio
			subversion.addArchivoLocalACopiaTrabajo(fileDescriptor);
			
			// y al flujo de salida
			entidadesConDescriptor.add(tuplaEntidad.toT6ending(ticket + "/" + nombreArchivoDescriptor));
		}
		
		// queda hacer el commit
		subversion.commit(rutaLocal, "Subiendo descriptores para la publicación de la etiqueta '" + etiqueta.getNombre() + "' con el ticket '" + ticket + "'");
		
		// el stream final
		return Streams.of(entidadesConDescriptor);
	}
	
	/**
	 * A partir del stream de entidades y estructuras auxiliares calculados previamente, procedemos a crear la estructura del elemento de la petición. 
	 */
	private Stream<Elemento> elementosAPublicar(
			final String codigoPROI,
			final String proiMadre,
			final String nombreProyecto,
			final String version,
			final String usuario,
			final String ticket,
			final Map<String, EtiquetaFuncional> mapaAppEtiquetaFuncional,
			final Map<Integer, Tuples.T2<TipoElemento, Extension>> mapaIdConTipoeElemento,
			final Stream<Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String>> datosEntidades,
			final ServicePAIPortType deployer) {
		
		ArrayList<Elemento> elementosAPublicarResult =
			Collections.list(
				datosEntidades
				.map(new Function<Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String>, Elemento>() {

					@Override
					public Elemento apply(
							Tuples.T6<Artefacto, Boolean, DirectivaInventario, Integer, List<Extension>, String> t) {

						// alias para facilitar la lectura
						final int idTipo = t._4;
						final String rutaArtefacto = 
								P.of(t._1).parametrosDeployer() != null ?
										P.of(t._1).parametrosDeployer().getPathInRequest()
										: "";
						final String carpetaTipo = P.of(t._1).publicacionDeployer().getCarpetaElemento();
						
						// la estructura de tipo y la extensión asociada
						TipoElemento tipoElemento = mapaIdConTipoeElemento.get(idTipo)._1;
						Extension extension = mapaIdConTipoeElemento.get(idTipo)._2;
						
						// final nombre del archivo será 
						String nombreArchivo = new StringBuilder(t._1.getNombre().toLowerCase())
							.append('.')
							.append(String.format("%04d",idTipo))
							.append(extension.getExtension().getValue())
							.toString();
							
						// objeto factoría asociado al servicio
						ObjectFactory factory = new ObjectFactory();
						
						// creamos la estructura del tipo
						Elemento elemento = new Elemento();
						if(t._2) {
							String aplicacion = t._3.getAplicacion().getId();
							elemento.setCodigoAplicacion(
									factory.createElementoCodigoAplicacion(aplicacion));

							if(mapaAppEtiquetaFuncional.containsKey(aplicacion))
								elemento.setEtiqueta(
										factory.createElementoEtiqueta(mapaAppEtiquetaFuncional.get(aplicacion).getIdEtiqueta().getValue()));

						}

						// los mismos datos que sacamos de la estructura tipo de elemento
						elemento.setDirBase(tipoElemento.getDirBase());
						elemento.setDirBaseInt(tipoElemento.getDirBaseInt());
						elemento.setDirBasePre(tipoElemento.getDirBasePre());
						elemento.setElementoDescription(tipoElemento.getDescripcion());
						elemento.setIdTipoElemento(idTipo);
						
						elemento.setNombre(factory.createElementoNombre(nombreArchivo));
						elemento.setOrigen(factory.createElementoOrigen("NIMIO"));
						elemento.setPath(
								factory.createElementoPath(
										new StringBuilder("/")
										.append(rutaArtefacto)
										.toString()
								)
						);
						elemento.setPathCVS(
								factory.createElementoPathCVS(
										new StringBuilder(ticket)
										.append("/")
										.append(carpetaTipo)
										.append('/').
										toString()
								)
						);
						elemento.setExtension(extension.getExtension());

						if(!Strings.isNullOrEmpty(codigoPROI)) {
							elemento.setProyecto(factory.createElementoProyecto(codigoPROI));
							elemento.setProiMadre(factory.createElementoProiMadre(proiMadre));
						}
						
						elemento.setProjectName(factory.createElementoProjectName(nombreProyecto));
						elemento.setUsuario(factory.createElementoUsuario(usuario));
						elemento.setDatabase(factory.createElementoDatabase(""));
						elemento.setVersion(factory.createElementoVersion(version));
						elemento.setTipoScript(factory.createElementoTipoScript("S"));
						
						return elemento;
					}
					
				})
				.getEnumeration()
			);
 
		// los devolvemos ya pasados por el método de acomodación para el script
		return Streams.of(
				deployer.getTipoElementoByTipo( elementosAPublicarResult )
			);
	}
	
}
