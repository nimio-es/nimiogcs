package es.nimio.nimiogcs.web.controllers.proyectos.etiquetas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.publicacion.PublicarEtiqueta;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.InclusionFragmento;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.controllers.utiles.UtilidadMapeoModeloErroresSpring;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.f.publicacion.PublicacionArtefactosForm;
import es.nimio.nimiogcs.web.dto.f.publicacion.PublicacionArtefactosForm.EleccionArtefacto;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/etiquetas/publicar")
@SessionAttributes(names="estado")
public class PublicarEtiquetaController {

	private static final String PARAMETROS_CANAL = "PARAMETROS_CANAL";
	private IContextoEjecucion ce;
	
	@Autowired
	public PublicarEtiquetaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	// Claves del mapa
	// ----

	private static final String ID_PROYECTO = "ID-PROYECTO";
	private static final String ID_ETIQUETA = "ID-ETIQUETA";
	private static final String PASO = "PASO";
	private static final String CANAL_ELEGIDO = "CANAL";
	private static final String VALOR_CONTROL = "CONTROL";
	private static final String ARTEFACTOS_ELEGIDOS = "ARTEFACTOS";
	
	// ----

	// inicial
	@RequestMapping(path="/{idProyecto}/{idEtiqueta}", method=RequestMethod.GET)
	public String inicioAsistente(
			@PathVariable("idProyecto") String idProyecto, 
			@PathVariable("idEtiqueta") String idEtiqueta, 
			RedirectAttributes redirectAttributes) {

		HashMap<String, Serializable> estado = new HashMap<String, Serializable>();
		estado.put(ID_PROYECTO, idProyecto);
		estado.put(ID_ETIQUETA, idEtiqueta);
		estado.put(PASO, 1);
		redirectAttributes.addFlashAttribute("estado", estado);
		return "redirect:/proyectos/etiquetas/publicar";
	}
	
	// pasos
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView paso(@ModelAttribute("estado") Map<String, Serializable> estado) {
		
		if(estado==null)throw new ErrorEntidadNoEncontrada();
		
		return generarPresentacion(estado);
	}
	
	// elecciones
	@RequestMapping(path="/eleccion", method=RequestMethod.GET)
	public String eleccion(
			@RequestParam(name="eleccion",required=true) String eleccion,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			RedirectAttributes redirectAttributes) {
		
		int paso = (Integer)estado.get(PASO);
		
		if(paso==1) { 
			estado.put(CANAL_ELEGIDO, eleccion);
			estado.put(PASO, 2);
		}
		
		redirectAttributes.addFlashAttribute("estado", estado);
		return "redirect:/proyectos/etiquetas/publicar";
	}

	@RequestMapping(path="/eleccion/paso2", method=RequestMethod.POST)
	public ModelAndView eleccion_paseo2(
			@ModelAttribute("peticion") PublicacionArtefactosForm peticion,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			RedirectAttributes redirectAttributes) {
		
		// pedimos a los canales que validen la parte de las propiedades
		final ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = 
				canalesOrdenadosSegunPrioridad();
		for(Tuples.T2<Integer, ICanalPublicacion> tc: c_canales) 
			tc._2.validarPeticion(
					(String)estado.get(CANAL_ELEGIDO), 
					peticion, 
					UtilidadMapeoModeloErroresSpring.desde(errores)
			);
		
		// validamos que hayamos elegido al menos un artefacto
		int elegidos = 0;
		for(EleccionArtefacto ea: peticion.getArtefactos())
			elegidos += ea.isElegido() ? 1 : 0;
		
		// al menos un artefacto elegido
		if(elegidos==0) {
			errores.rejectValue(
					"artefactos", 
					"ALMENOSUNO", 
					"Debe elegir un artefacto a desplegar");
		}
			
		if(errores.hasErrors()) {
			return eleccionArtefactos(
					ce.proyectos().findOne((String)estado.get(ID_PROYECTO)), 
					ce.etiquetas().findOne((String)estado.get(ID_ETIQUETA)), 
					estado, 
					peticion);
		}
		
		// llegados a este punto, ya sabemos que hemos elegido, al menos, un artefacto
		// para publicar. Vamos a meter la lista de artefactos (sus identificadores)
		// en una lista que subiremos al estado e indicamos que nos vamos al tercer paso
		estado.put(PASO, 3);
		ArrayList<String> artefactos = new ArrayList<String>();
		for(PublicacionArtefactosForm.EleccionArtefacto eleccion :peticion.getArtefactos()) {
			if(eleccion.isElegido()) artefactos.add(eleccion.getIdArtefacto());
		}
		estado.put(PARAMETROS_CANAL, peticion.getParametrosCanal());
		estado.put(ARTEFACTOS_ELEGIDOS, artefactos);
			
		redirectAttributes.addFlashAttribute("estado", estado);
		return new ModelAndView("redirect:/proyectos/etiquetas/publicar");
	}
	
	// conclusión
	@SuppressWarnings("unchecked")
	@RequestMapping(path="/confirmar", method=RequestMethod.POST)
	public String confirmacion( 
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {

		// confirmamos que estamos ante una operación controlada
		if(!((String)estado.get(VALOR_CONTROL)).equalsIgnoreCase(confirmacion.getCodigoControl()))
			throw new ErrorInconsistenciaDatos(
					"Se ha alcanzado el punto de confirmación sin el valor de control correcto"
			);
		
		ArrayList<Artefacto> as = new ArrayList<Artefacto>();
		for(String a: (List<String>)estado.get(ARTEFACTOS_ELEGIDOS)) 
			as.add(ce.artefactos().findOne(a));
		
		// lanzamos la tarea de publicación
		new PublicarEtiqueta(ce)
			.ejecutar(
					new PublicarEtiqueta.PeticionPublicacion(
							(String)estado.get(CANAL_ELEGIDO), 
							(Map<String, String>)estado.get(PARAMETROS_CANAL),
							ce.etiquetas().findOne((String)estado.get(ID_ETIQUETA)),
							as, 
							ce.usuario().getNombre()
					)
			);
		
		
		// la tarea se da por completada y que borre todo el estado
		ss.setComplete();
		
		return "redirect:/proyectos/" + (String)estado.get(ID_PROYECTO);
	}
	
	// cancelación
	@RequestMapping(path="/cancelar", method=RequestMethod.GET)
	public String cancelar( 
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		// la tarea se da por completada y que borre todo el estado
		ss.setComplete();
		
		return "redirect:/proyectos/" + (String)estado.get(ID_PROYECTO);
	}
	
	
	// --------------------------------------
	// La representación de los pasos
	// --------------------------------------
	
	private ModelAndView generarPresentacion(Map<String, Serializable> estado) {
		
		// cargamos la etiqueta
		EtiquetaProyecto etiqueta = ce.etiquetas().findOne((String)estado.get(ID_ETIQUETA));
		if(etiqueta==null) throw new ErrorEntidadNoEncontrada();
		
		// y sacamos el proyecto
		Proyecto proyecto = etiqueta.getProyecto();
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// una vez confirmado que lo importante está, miramos el paso 
		// y nos redirigimos al que toque
		int paso = (Integer)estado.get(PASO);
		
		switch (paso) {
		
			case 1:  // elección del canal 
				return eleccionCanal(proyecto, etiqueta, estado);
				
			case 2: // elección de los artefactos
				return eleccionArtefactos(proyecto, etiqueta, estado, null);
				
			case 3: // confirmación
				return confirmacion(proyecto, etiqueta, estado);
		
			default: 
				return new ModelAndView("redirect:/proyectos/etiquetas/" + proyecto.getId());
		}
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Elección del canal
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	private ModelAndView eleccionCanal(
			Proyecto proyecto, 
			EtiquetaProyecto etiqueta, 
			Map<String, Serializable> estado) 
	{
		
		// ¿Debemos ofrecer todos los canales de publicación para cada artefacto?
		final boolean ofrecerTodos = ofrecerTodosLosCanales(); 
		
		// indizamos los descriptores para tenerlos a mano más rápidamente
		final Map<String, DescripcionCanal> i_canales = new HashMap<String, DescripcionCanal>();
		
		// Ahora recorremos todos los artefactos y, con ellos, vamos generando un mapa
		// donde se registre la lista de artefactos desplegables por cada canal
		final Map<String, List<Artefacto>> a_canales = new HashMap<String, List<Artefacto>>();
		
		final ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = 
				canalesOrdenadosSegunPrioridad();
		
		// lista de los artefactos que han sido excluidos por encontrarse en situación
		// de obsoletos (que el código que recogen no está sincronizado con la rama troncal)
		final ArrayList<Artefacto> excluidosPorObsoletos = new ArrayList<Artefacto>();
		
		// descargamos la lista de todos los artefactos afectados
		List<RelacionElementoProyectoArtefacto> rpas = 
				ce.relacionesProyectos()
				.findAll(
						ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(etiqueta)
				); 
		for(RelacionElementoProyectoArtefacto rpa: rpas) {

			final Artefacto testaferro = rpa.getArtefacto(); 
			final Artefacto base = testaferro instanceof ITestaferroArtefacto ?
					((ITestaferroArtefacto)testaferro).getArtefactoAfectado()
					: testaferro;

			if(testaferro instanceof ITestaferroArtefacto && !((ITestaferroArtefacto)testaferro).getSincronizadoEstable()) {
				excluidosPorObsoletos.add(base);
				// nos lo saltamos
				continue;
			}
					
			// preguntamos a todos los canales si el artefacto es tratable
			for(Tuples.T2<Integer, ICanalPublicacion> tc: c_canales) {
				ICanalPublicacion cp = tc._2;
				DescripcionCanal dc = cp.posiblePublicarArtefacto(etiqueta, base);
				if(dc!=null) {
					
					// preguntamos si es un canal con gestión de calidad o
					// si depende de que ya haya superado la calidad
					boolean calidadSuperada = etiqueta.getCalidadSuperada() != null ?
							etiqueta.getCalidadSuperada() 
							: false;
					if(!dc.getGestionaCalidad() && !calidadSuperada)
						continue; // nos lo saltamos
					
					if(!i_canales.containsKey(dc.getNombre())) {
						i_canales.put(dc.getNombre(), dc);
						
						// damos por hecho que si no existe en el indizador, no existe 
						// en el mapa de canales-artefactos
						a_canales.put(dc.getNombre(), new ArrayList<Artefacto>());
					}
					
					// añadimos el artefacto en la lista del canal
					a_canales.get(dc.getNombre()).add(base);
					
					// si hemos añadido el artefacto a un canal, ya no seguimos
					// siempre que no hayamos indicado que sí que los ofrezca
					if(!ofrecerTodos) break;
				}
			}
		}
		
		// si solo hay un canal en el que publicar, lo damos como elección directa
		if(a_canales.keySet().size() == 1) 
			return 
					new ModelAndView(
							"redirect:/proyectos/etiquetas/publicar/eleccion?eleccion=" 
									+ a_canales.keySet().toArray()[0].toString() 
					);
		
		// en caso contrario, vamos a crear lo que queremos mostrar en este paso de decisión
		// y construiremos la página de decisiones
		List<IComponente> eleccion = new ArrayList<IComponente>();
		
		// panel donde indicaremos que hay artefactos que no pueden ser publicados
		if(excluidosPorObsoletos.size() > 0) {
			
			String listaArtefactos = "";
			for(Artefacto ap: excluidosPorObsoletos) {
				String t = ap.getNombre() + " (" + ap.getTipoArtefacto().getNombre() + ")";
				listaArtefactos += listaArtefactos.length() > 0 ? ", " + t : t;
			}
			
			eleccion.add(
					new PanelContinente()
					.conTitulo("Artefactos obsoletos")
					.paraTipoAviso()
					.conComponentes(
							new Parrafo()
							.conTexto(
									"Existen componentes que ya no pueden ser publicados porque "
									+ "el código fuente de la etiqueta asociada no está en consonancia "
									+ "al código estable."
							),
							
							new Parrafo(" "),
							
							new Parrafo(listaArtefactos)
							.conLetraPeq()
							.deTipoPeligro()
							.enNegrita()
					)
			);
		}
		
		if(a_canales.keySet().size() == 0) { 
		
			eleccion.add(
					new Parrafo()
					.conTexto("No es posible publicar. O no hay canales soportados para los artefactos del proyecto o los artefactos del proyecto no son publicables per sé.")
					.deTipoPeligro()
					.enNegrita()
			);

		} else {

			// el párrafo principal
			eleccion.add(
					new Parrafo()
					.conTexto("Elija uno de los posibles canales de publicación para publicar la etiqueta.")
					.deTipoPrincipal()
			);
			eleccion.add(new Parrafo(""));
			
			// ahora vamos eligiendo cada canal, lo mostramos con su descripción
			// y la lista de elementos que podremos publicar por él.
			for(String ca: a_canales.keySet()) {

				// artefactos que podremos publicar
				String listaArtefactos = "";
				for(Artefacto ap: a_canales.get(ca)) {
					String t = ap.getNombre() + " (" + ap.getTipoArtefacto().getNombre() + ")";
					listaArtefactos += listaArtefactos.length() > 0 ? ", " + t : t;
				}
				
				// lo presentamos como un panel
				eleccion.add(
						new PanelContinente()
						.paraTipoDefecto()
						.conComponentesCabecera(
								new EnlaceSimple()
								.conTexto(ca)
								.paraUrl("proyectos/etiquetas/publicar/eleccion")
								.conParametros("eleccion=" + ca)
								.enNegrita()
						).conComponentes(
								new Parrafo()
								.conTexto(i_canales.get(ca).getDescripcion())
								.conLetraPeq(),
								
								new Parrafo(""),
								
								new Parrafo("Artefactos publicables por este canal: ")
								.conLetraPeq(),
								
								new Parrafo(listaArtefactos).conLetraPeq().enNegrita()
						)
				);
				
			}
		}

		// ya es momento de crear la página
		return 
				ModeloPagina.nuevaPagina(
						construyePagina(
								proyecto, 
								etiqueta, 
								eleccion, 
								estado
						)
				)
				.conModelo("estado", estado);
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Elección de los artefactos
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	private ModelAndView eleccionArtefactos(
			Proyecto proyecto, 
			EtiquetaProyecto etiqueta, 
			Map<String, Serializable> estado,
			PublicacionArtefactosForm peticion) 
	{
	
		// ¿Debemos ofrecer todos los canales de publicación para cada artefacto?
		final boolean ofrecerTodos = ofrecerTodosLosCanales(); 
		
		// el canal elegido
		final String canalElegido = (String)estado.get(CANAL_ELEGIDO);
		
		final List<IComponente> eleccion = new ArrayList<IComponente>();
		
		// lista de los artefactos que han sido excluidos por encontrarse en situación
		// de obsoletos (que el código que recogen no está sincronizado con la rama troncal)
		final ArrayList<Artefacto> excluidosPorObsoletos = new ArrayList<Artefacto>();
		
		PublicacionArtefactosForm datos = peticion;
		if(peticion==null) {
			// vamos preparando los datos del formulario
			 datos = new PublicacionArtefactosForm();
			
			final ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = 
						canalesOrdenadosSegunPrioridad();
			 
			// recogemos los parámetros adicionales que son necesarios solicitar
			for(Tuples.T2<Integer, ICanalPublicacion> tc: c_canales) {
				ICanalPublicacion cp = tc._2; 
				cp.datosPeticion(canalElegido, datos);
			}
	
			// descargamos la lista de todos los artefactos afectados
			final List<RelacionElementoProyectoArtefacto> rpas = 
					ce.relacionesProyectos().findAll(
							ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(etiqueta)
					);
			for(RelacionElementoProyectoArtefacto rpa: rpas) {
	
				Artefacto testaferro = rpa.getArtefacto();
				Artefacto base = testaferro instanceof ITestaferroArtefacto ?
						((ITestaferroArtefacto)testaferro).getArtefactoAfectado()
						: testaferro;
	
				// debemos excluir los artefactos que se traten en canales con 
				// una prioridad mayor.
				for(Tuples.T2<Integer, ICanalPublicacion> tc: c_canales) {
					ICanalPublicacion cp = tc._2;

					DescripcionCanal dc = cp.posiblePublicarArtefacto(
							etiqueta, 
							rpa.getArtefacto()
					);
					if(dc==null) continue; // saltamos al siguiente

					// preguntamos si es un canal con gestión de calidad o
					// si depende de que ya haya superado la calidad
					boolean calidadSuperada = etiqueta.getCalidadSuperada() != null ?
							etiqueta.getCalidadSuperada() 
							: false;
					if(!dc.getGestionaCalidad() && !calidadSuperada)
						continue; // nos lo saltamos
					
					if(dc.getNombre().equalsIgnoreCase(canalElegido)) {

						if(testaferro instanceof ITestaferroArtefacto && !((ITestaferroArtefacto)testaferro).getSincronizadoEstable()) {
							excluidosPorObsoletos.add(base);
							// nos lo saltamos
							continue;
						}
								
						datos.getArtefactos().add(
								new PublicacionArtefactosForm.EleccionArtefacto(
										base.getId(),
										base.getNombre(),
										base.getTipoArtefacto().getNombre(),
										false
								)
						);
					} else {
						
						// sin lugar a dudas hemos tropezado con un canal que tiene mayor prioridad
						// y que ya trata la publicación del elemento en curso, por lo que no podremos
						// ofrecerlo para publicar en el canal. O sí, siempre que lo hayamos configurado 
						if(!ofrecerTodos) break;
					}
				}
			}		
		}

		// panel donde indicaremos que hay artefactos que no pueden ser publicados
		if(excluidosPorObsoletos.size() > 0) {
			
			String listaArtefactos = "";
			for(Artefacto ap: excluidosPorObsoletos) {
				String t = ap.getNombre() + " (" + ap.getTipoArtefacto().getNombre() + ")";
				listaArtefactos += listaArtefactos.length() > 0 ? ", " + t : t;
			}
			
			eleccion.add(
					new PanelContinente()
					.conTitulo("Artefactos obsoletos")
					.paraTipoAviso()
					.conComponentes(
							new Parrafo()
							.conTexto(
									"Existen componentes que ya no pueden ser publicados porque "
									+ "el código fuente de la etiqueta asociada no está en consonancia "
									+ "al código estable."
							),
							
							new Parrafo(" "),
							
							new Parrafo(listaArtefactos)
							.conLetraPeq()
							.deTipoPeligro()
							.enNegrita()
					)
			);
		}
		
		// el párrafo de descripción
		eleccion.add(
				new Parrafo()
				.conTexto("Indique qué artefactos se publicarán como parte de esta petición y, si fuera necesario, los parámetros adicionales que deben indicarse en la solicitud.")
				.deTipoPrincipal()
		);
		
		// creamos el objeto de presentación: el formulario
		eleccion.add(
				new Formulario()
				.idModelo("peticion")
				.urlAceptacion("/proyectos/etiquetas/publicar/eleccion/paso2")
				.conComponentes(
						new InclusionFragmento("_formularios_nimio", "sel-multiple-artefactos")
				)
				.botoneraEstandar("/proyectos/etiquetas/publicar/cancelar")
		);
		
		// ya es momento de crear la página
		return 
				ModeloPagina.nuevaPagina(
						construyePagina(
								proyecto, 
								etiqueta, 
								eleccion, 
								estado
						)
				)
				.conModelo("estado", estado)
				.conModelo("peticion", datos);
	}
		
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Confirmación
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	@SuppressWarnings("unchecked")
	private ModelAndView confirmacion(
			Proyecto proyecto, 
			EtiquetaProyecto etiqueta, 
			Map<String, Serializable> estado) {
		
		// los componentes que mostraremos
		List<IComponente> eleccion = new ArrayList<IComponente>();
		
		eleccion.add(
				new Parrafo()
				.conTexto("Como último paso, por favor, confirme que quiere ejecutar la operación de publicación.")
		);
		
		
		// queda añadir un formulario con el panel de confirmación
		eleccion.add(
				new Formulario()
				.urlAceptacion("/proyectos/etiquetas/publicar/confirmar")
				.conComponentes(
						AyudanteCalculoEstructuraFormularioDesdePojo
						.altaDesdeDto(PeticionConfirmacionGeneral.class)
				)
				.botoneraEstandar("/proyectos/etiquetas/publicar/cancelar")
		);

		// calculamos los datos a mostrar en la confirmación
		String control = UUID.randomUUID().toString();
		String textoConfirmacion = "Se ha solicitado publicar, empleando el canal " 
				+ (String)estado.get(CANAL_ELEGIDO)
				+ ", los siguientes artefactos: ";
		String ars = "";
		for(String a: (List<String>)estado.get(ARTEFACTOS_ELEGIDOS)) {
			Artefacto art = ce.artefactos().findOne(a);
			ars += ars.length() > 0 ? ", " + art.getNombre() : art.getNombre();
		}
		textoConfirmacion += ars;
		
		// metemos el valor de control en el estado
		estado.put(VALOR_CONTROL, control);
		
		// ya es momento de crear la página
		return 
				ModeloPagina.nuevaPagina(
						construyePagina(
								proyecto, 
								etiqueta, 
								eleccion, 
								estado
						)
				)
				.conModelo("estado", estado)
				.conModelo(
						"datos",
						new PeticionConfirmacionGeneral(control, textoConfirmacion)
				);
	}
	
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Común
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	private EstructuraAbstractaPagina construyePagina(Proyecto proyecto, 
			EtiquetaProyecto etiqueta, 
			List<IComponente> componentesDecision, 
			Map<String, Serializable> estado) {

		int paso = (Integer)estado.get(PASO);
		
		// estructura base de la página
		return new EstructuraPagina("Publicar etiqueta")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Proyectos", "/proyectos")
						.conEnlace(proyecto.getNombre(), "/proyectos/" + proyecto.getId())
						.conEnlace("Etiquetas", "/proyectos/etiquetas/" + proyecto.getId())
						.conTexto(etiqueta.getNombre())
						.conTexto("Publicar")
				)
				.conComponentes(
						
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(9)
								.conComponentes(
										new PanelContinente()
										.conTitulo(tituloPaso(paso))
										.paraTipoPrimario()
										.conComponentes(
												componentesDecision
										)
								)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(3)
								.conComponentes(
										new PanelContinente()
										.conTitulo("Pasos realizados")
										.paraTipoExito()
										.conComponentes(
												paraPanelRealizados(estado)
										),
										
										new PanelContinente()
										.conTitulo("Pasos pendientes")
										.paraTipoAviso()
										.conComponentes(
												paraPanelPendiente(estado)
										)
								)
						)
				);
	}
	
	
	private String tituloPaso(int paso) {
		switch (paso) {
			case 1: return "Elección del canal de publicación";
			case 2: return "Elección de los artefactos a publicar";
			case 3: return "Confirmar";
			default: return "Perdido...";
		}
	}
	
	// TODO: lo siguiente puede hacerse mucho más fácilmente manteniendo una lista 
	//       con los textos o los cálculos a realizar
	
	private List<IComponente> paraPanelRealizados(Map<String, Serializable> estado) {
		
		int paso = (Integer)estado.get(PASO);
		
		List<IComponente> componentes = new ArrayList<IComponente>();
		
		if(paso == 1) {
			componentes.add(
					new Parrafo("Aún no has tomado ninguna decisión de valor.")
					.enNegrita()
					.deTipoAviso()
			);
		}
		
		if(paso > 1) {
			
			String canalElegido = (String)estado.get(CANAL_ELEGIDO);
			
			componentes.add(
					new Parrafo("Has seleccionado publicar por: " + canalElegido)
			);
			
			componentes.add(
					new Parrafo("")
			);
		}
		
		if(paso > 2) {
			
		}
		
		return componentes;
	}
	
	private List<IComponente> paraPanelPendiente(Map<String, Serializable> estado) {
		
		int paso = (Integer)estado.get(PASO);
		
		List<IComponente> componentes = new ArrayList<IComponente>();

		if(paso < 2) {
			
			componentes.add(
					new Parrafo("Elegir el canal de publicación.")
			);
			
			componentes.add(
					new Parrafo("")
			);
		}
		
		if(paso < 3) {
			
			componentes.add(
					new Parrafo("Elegir los artefactos a publicar.")
			);
			
			componentes.add(
					new Parrafo("")
			);
		}

		if(paso < 4) {
			
			componentes.add(
					new Parrafo("Confirmar la elección.")
			);
			
			componentes.add(
					new Parrafo("")
			);
		}

		return componentes;
	}
	
	private ArrayList<Tuples.T2<Integer, ICanalPublicacion>> canalesOrdenadosSegunPrioridad() {

		// cargamos los publicadores en un array en el que, además, añadiremos la prioridad
		// para ordenarlo de esta forma
		final ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = new ArrayList<Tuples.T2<Integer,ICanalPublicacion>>();
		for(ICanalPublicacion cp: ce.contextoAplicacion().getBeansOfType(ICanalPublicacion.class).values()) {
			DescripcionCanal dc = cp.descripcionCanal();
			if(dc.isElegible())
				c_canales.add(
						Tuples.tuple(dc.getPrioridad(), cp)
				);
		}
		Collections.sort(
				c_canales,
				new Comparator<Tuples.T2<Integer, ICanalPublicacion>>() {

					@Override
					public int compare(T2<Integer, ICanalPublicacion> o1, T2<Integer, ICanalPublicacion> o2) {
						return o1._1.compareTo(o2._1);
					}
				}
		);
		return c_canales;
	}
	
	private boolean ofrecerTodosLosCanales() {
		final ParametroGlobal pg = 
				ce.global()
				.findOne("CANAL.PUBLICACION.OFRECERTODOS");
		if(pg==null) return false;
		return pg.comoValorIgualA("1");
	}
}
