package es.nimio.nimiogcs.web.controllers.artefactos.dependencias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorSesionInconsistente;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConAlcance;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConModuloWeb;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaReferenciar;
import es.nimio.nimiogcs.jpa.specs.Artefactos;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.SaltoDeLinea;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.f.artefactos.dependencias.FormularioNuevaDependenciaConfigurarAlcance;
import es.nimio.nimiogcs.web.dto.f.artefactos.dependencias.FormularioNuevaDependenciaConfigurarContextRoot;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping("/artefactos/dependencias/estaticas/incluir")
@SessionAttributes(names="estado")
public class IncluirDependenciaArtefactoController {

	private static final String TIPO_DEPENDENCIA_MODULO_WEB = "MODULO WEB";

	private static final String TIPO_DEPENDENCIA_ALCANCE = "ALCANCE";

	private static final String TIPO_DEPENDENCIA_POSICIONAL = "POSICIONAL";

	private static final String SESSION_ATT_ESTADO = "estado";
	
	private IContextoEjecucion ce;
	
	@Autowired
	public IncluirDependenciaArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -------------------------------------------------------
	// Incluir dependencia
	// -------------------------------------------------------
	
	@RequestMapping(path="/{artefacto}", method=RequestMethod.GET)
	public ModelAndView iniciarProcesoIncluirDependencia(@PathVariable("artefacto") final String idArtefacto) {
		
		// cargamos el artefacto para confirmar que existe
		Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) 
			throw new ErrorEntidadNoEncontrada();
		
		// iniciamos el asistente
		EstadoInterno estado = new EstadoInterno();
		
		// el id del artefacto
		estado.setIdArtefacto(idArtefacto);
		
		// el paso en que nos encontramos
		estado.setPaso(1);
		
		// una lista para decir qué queda pendiente y otra para
		// las decisiones tomadas
		estado.setPendientes(new ArrayList<String>());
		estado.getPendientes().add("Elegir tipo de artefacto a vincular.");
		estado.getPendientes().add("Elegir el artefacto disponible.");
		estado.getPendientes().add("Configurar la relación de dependencia.");
		estado.getPendientes().add("Confirmar operación.");
		estado.setRealizados(new ArrayList<String>());
		
		// redirigimos siempre al método genérico que usará el estado para saber qué hay que presentar
		ModelAndView mv = new ModelAndView("redirect:/artefactos/dependencias/estaticas/incluir");
		mv.addObject(SESSION_ATT_ESTADO, estado);
		return mv;
	}
	
	@RequestMapping(path="", method=RequestMethod.GET)
	public ModelAndView incluir(
			@ModelAttribute(SESSION_ATT_ESTADO) EstadoInterno estado) {
		
		if(estado==null) 
			throw new ErrorSesionInconsistente("No hay variable de estado");
		
		Artefacto artefacto = ce.artefactos().findOne(estado.getIdArtefacto());
		
		return crearPagina(artefacto, estado, null);
	}
	
	@RequestMapping(path="/eleccion/{paso}/{opcion}",  method=RequestMethod.GET)
	public ModelAndView eleccion(
			@PathVariable("paso") Integer pasoSupuesto,
			@PathVariable("opcion") String opcion,
			@ModelAttribute(SESSION_ATT_ESTADO) EstadoInterno estado) {
		
		if(estado==null) 
			throw new ErrorSesionInconsistente("No hay variable de estado");
		
		Artefacto artefacto = ce.artefactos().findOne(estado.getIdArtefacto());

		switch (estado.getPaso()) {
		
		case 1:
			// hemos elegido el tipo de artefacto y toca elegir los artefactos
			TipoArtefacto tipo = ce.tipos().findOne(opcion);
			estado.setPaso(2);
			estado.getRealizados().add("Elegido el tipo de artefacto '" + tipo.getNombre() + "'");
			estado.setIdTipoElegido(opcion);
			
			// también creamos una lista de artefactos que ya están vinculados y que
			// por tanto, no es necesario añadir nuevamente, como el propio artefacto
			// aunque no sea del mismo tipo
			estado.setExcluir(new ArrayList<String>());
			estado.getExcluir().add(artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado().getId() : estado.getIdArtefacto());
			for(Dependencia d: ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(estado.getIdArtefacto())) {
				estado.getExcluir().add(d.getRequerida().getId());
			}
			
			break;
		
		case 2:
			Artefacto artefactoElegido = ce.artefactos().findOne(opcion);
			
			// el tipo del artefacto tiene que coincidir con el elegido en el paso anterior
			if(!artefactoElegido.getTipoArtefacto().getId().equalsIgnoreCase(estado.getIdTipoElegido())) 
				throw new ErrorIntentoOperacionInvalida();
			
			// metemos en la lista de pasos datos l oque hemos hecho
			estado.getRealizados().add("Elegido el artefacto '" + artefactoElegido.getNombre() + "'");
			estado.setIdArtefactoElegido(opcion);
			
			// aquí podemos encontrarnos en dos situaciones, que 
			// el tipo elegido se corresponda a una dependencia posicional (que no hay que pedir más datos)
			// o que haya que solicitar datos adicionales
			Artefacto base = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
			DirectivaReferenciar dr = P.of(base).referenciar();
			if(Arrays.asList(dr.getPosiblesPosicional()).contains(estado.getIdTipoElegido())) {
				
				// podemos pasar directamente al final
				estado.setPaso(4);
				estado.setTipoDependencia(TIPO_DEPENDENCIA_POSICIONAL);
				estado.getRealizados().add("No se requiere configurar dependencia (dependencia 'POSICIONAL')");
				
			} else {
				
				// tenemos que pedir datos adicionales
				estado.setPaso(3);
				if(Arrays.asList(dr.getPosiblesAlcance()).contains(estado.getIdTipoElegido())) 
					estado.setTipoDependencia(TIPO_DEPENDENCIA_ALCANCE);
				else 
					estado.setTipoDependencia(TIPO_DEPENDENCIA_MODULO_WEB);
			}
			

		default:
			break;
		}
		
		ModelAndView mv = new ModelAndView("redirect:/artefactos/dependencias/estaticas/incluir");
		mv.addObject(SESSION_ATT_ESTADO, estado);
		return mv;
	}
	
	@RequestMapping(path="/alcance",  method=RequestMethod.POST)
	public ModelAndView aceptarAlcance(
			@ModelAttribute("datos") @Valid FormularioNuevaDependenciaConfigurarAlcance alcance,
			Errors errores,
			@ModelAttribute(SESSION_ATT_ESTADO) EstadoInterno estado) {
		
		Artefacto artefacto = ce.artefactos().findOne(estado.getIdArtefacto());
		
		if(errores.hasErrors()) 
			return crearPagina(artefacto, estado, alcance);
		
		// registramos el resultado del formulario como parte del estado y pasamos al último paso
		estado.setDependencia(alcance);
		estado.setPaso(4);
		estado.getRealizados().add(
				"Elegido el alcance '" 
				+ EnumAlcanceDependencia.valueOf(alcance.getAlcance()).getTextoDescripcion() 
				+ "' para la relación de dependencia."
		);
		
		ModelAndView mv = new ModelAndView("redirect:/artefactos/dependencias/estaticas/incluir");
		mv.addObject(SESSION_ATT_ESTADO, estado);
		return mv;
	}
	
	@RequestMapping(path="/web",  method=RequestMethod.POST)
	public ModelAndView aceptarAlcance(
			@ModelAttribute("datos") @Valid FormularioNuevaDependenciaConfigurarContextRoot web,
			Errors errores,
			@ModelAttribute(SESSION_ATT_ESTADO) EstadoInterno estado) {
		
		Artefacto artefacto = ce.artefactos().findOne(estado.getIdArtefacto());
		
		if(errores.hasErrors()) 
			return crearPagina(artefacto, estado, web);
		
		// registramos el resultado del formulario como parte del estado
		estado.setDependencia(web);
		estado.setPaso(4);
		estado.getRealizados().add(
				"Elegida la ruta de contexto '" 
				+ web.getRutaDeContexto() 
				+ "' para la relación de dependencia."
		);
		
		ModelAndView mv = new ModelAndView("redirect:/artefactos/dependencias/estaticas/incluir");
		mv.addObject(SESSION_ATT_ESTADO, estado);
		return mv;
	}
	
	@RequestMapping(path="/confirmar",  method=RequestMethod.POST)
	public String confirmar(
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion,
			@ModelAttribute(SESSION_ATT_ESTADO) EstadoInterno estado,
			SessionStatus ss) {
		
		Artefacto artefacto = ce.artefactos().findOne(estado.getIdArtefacto());
		Artefacto electo = ce.artefactos().findOne(estado.getIdArtefactoElegido());
		
		// en función de las elecciones, crearemos un tipo de dependencia u otro.
		// eso sí, todas las dependencias que manejamos ahora mismo son de tipo
		// posicional.
		DependenciaPosicional nuevaDependencia;
		if(TIPO_DEPENDENCIA_POSICIONAL.equalsIgnoreCase(estado.getTipoDependencia())) {
			
			nuevaDependencia = new DependenciaPosicional();
			
		} else if(TIPO_DEPENDENCIA_ALCANCE.equalsIgnoreCase(estado.getTipoDependencia())) {
			
			DependenciaConAlcance t = new DependenciaConAlcance();
			t.setAlcanceElegido(EnumAlcanceDependencia.valueOf(((FormularioNuevaDependenciaConfigurarAlcance)estado.getDependencia()).getAlcance()));
			nuevaDependencia = t;
			
		} else {  // solo queda que sea de tipo web
			
			DependenciaConModuloWeb t = new DependenciaConModuloWeb();
			t.setContextRoot(((FormularioNuevaDependenciaConfigurarContextRoot)estado.getDependencia()).getRutaDeContexto());
			nuevaDependencia = t;
		}
		
		// ahora queda decir en la dependencia cuáles son el origen y el destino
		// y calcular la posición, que siempre será la última
		nuevaDependencia.setDependiente(artefacto);
		nuevaDependencia.setRequerida(electo);
		final int ultimaPosicion = ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(artefacto.getId()).size() + 1;
		nuevaDependencia.setPosicion(ultimaPosicion);
		
		// guaramos la nueva relación de dependencia
		ce.dependenciasArtefactos().saveAndFlush(nuevaDependencia);
		
		ss.setComplete();
		return "redirect:/artefactos/dependencias/estaticas/" + artefacto.getId();
	}
	
	@RequestMapping(path="/cambiopagina",  method=RequestMethod.GET)
	public ModelAndView cambioPagina(
			@RequestParam(name="pag", required=false, defaultValue="1") Integer pagina,
			@ModelAttribute(SESSION_ATT_ESTADO) EstadoInterno estado) {

		// modificamos el valor en el estado y volvemos al principio
		estado.setPagina(pagina);
		
		ModelAndView mv = new ModelAndView("redirect:/artefactos/dependencias/estaticas/incluir");
		mv.addObject(SESSION_ATT_ESTADO, estado);
		return mv;
	}
	
	
	// ------- 
	
	private ModeloPagina crearPagina(Artefacto artefacto, EstadoInterno estado, Object datos) {

		Tuples.T2<PanelContinente, Object> panelActual = actual(estado.getPaso(), artefacto, estado);

		ModeloPagina mp = ModeloPagina.nuevaPagina(
				new EstructuraPagina("Incluir nueva relación de dependencia > Paso: " + estado.getPaso())
				.conComponentes(
						localizacion(estado.getPaso(), artefacto, estado.getPendientes()),
						
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(9)
								.conComponentes(panelActual._1)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(3)
								.conComponentes(
										realizado(estado.getPaso(), estado.getRealizados()),
										pendientes(estado.getPaso(), estado.getPendientes())
								)
						)
				)
		);
		
		if(datos!=null) 
			mp.conModelo("datos", datos); 
		else if(panelActual._2 != null) 
			mp.conModelo("datos", panelActual._2);
		mp.conModelo(SESSION_ATT_ESTADO, estado);
		
		return mp;		
	}
	
	private Localizacion localizacion(int paso, Artefacto artefacto, List<String> pendientes) {
		return new Localizacion()
				.conEnlace("Home", "/")
				.conEnlace("Artefactos", "/artefactos")
				.conEnlaceYParametros(artefacto.getTipoArtefacto().getNombre(), "/artefactos", "tipo=" + artefacto.getTipoArtefacto().getId())
				.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
				.conEnlace("Dependencias", "/artefactos/dependencias/estaticas/" + artefacto.getId())
				.conTexto("Añadir")
				.conTexto("Paso " + paso + ": " + pendientes.get(paso-1));
	}
	
	private Tuples.T2<PanelContinente, Object> actual(int paso, Artefacto artefacto, EstadoInterno estado) {
		
		Tuples.T2<Collection<IComponente>, Object> contenido = contenidoPagina(paso, artefacto, estado);
		
		return Tuples.tuple(
				new PanelContinente()
				.conTitulo("Paso " + paso + ": " + estado.getPendientes().get(paso-1))
				.paraTipoPrimario()
				.conComponentes(contenido._1),
				contenido._2
		);
	}
	
	private Tuples.T2<Collection<IComponente>, Object> contenidoPagina(int paso, Artefacto artefacto, EstadoInterno estado) {

		switch (paso) {

		case 1:
			return contenidoPagina1(artefacto);
			
		case 2:
			return contenidoPagina2(estado);
			
		case 3:
			return contenidoPagina3(artefacto, estado);
			
		case 4:
			return contenidoPagina4(artefacto, estado);

		default:
			return defaultCasePageContent();
		}
	}
	
	/**
	 * Contenido de una página a la que no debería llegarse nunca
	 */
	private Tuples.T2<Collection<IComponente>, Object> defaultCasePageContent() {
		return Tuples.tuple(
				(Collection<IComponente>)
				Arrays.asList(
						new IComponente[] {
							new Parrafo()
							.conTexto("¿Pero cómo hemos llegado aquí?")
							.enNegrita()
							.deTipoPeligro()
						}
				),
				null
		);
	}
	
	private Tuples.T2<Collection<IComponente>, Object> contenidoPagina1(Artefacto artefacto) {
		
		final ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		final Artefacto base = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		
		componentes.add(new Parrafo(" "));
		componentes.add(
				new Parrafo()
				.conTexto(
						"El artefacto '" + base.getNombre() + "' pertenece a la familia de artefactos de tipo '"
						+ base.getTipoArtefacto().getNombre() + "'. Los artefacto de tipo '"
						+ base.getTipoArtefacto().getNombre() + "' pueden referenciar a artefactos de "
						+ "los siguientes tipos:"
				)
				.enNegrita()
		);
		componentes.add(new Parrafo(" "));

		// pedimos la directiva de refercias y la procesamos
		final DirectivaReferenciar dr = P.of(base).referenciar();
		ArrayList<String> idTipos = new ArrayList<String>();
		for(String idd: dr.getPosiblesPosicional()) 
			idTipos.add(idd);
		for(String idd: dr.getPosiblesAlcance()) 
			idTipos.add(idd);
		for(String idd: dr.getPosiblesWeb()) 
			idTipos.add(idd);
		final ArrayList<TipoArtefacto> tipos = new ArrayList<TipoArtefacto>();
		for(String idTipo: idTipos) tipos.add(ce.tipos().findOne(idTipo));
		Collections.sort(
				tipos,
				new Comparator<TipoArtefacto>() {
					@Override
					public int compare(TipoArtefacto o1, TipoArtefacto o2) {
						return o1.getNombre().compareTo(o2.getNombre());
					}
				}
		);
		
		// pintamos las opciones
		for(TipoArtefacto tipo: tipos) {
			componentes.add(
					new ContinenteSinAspecto()
					.enColumna(12)
					.conComponentes(
							new GlyphIcon()
							.dedoIndiceDerecha(),
							
							new EnlaceSimple()
							.conTexto(tipo.getNombre())
							.paraUrl("artefactos/dependencias/estaticas/incluir/eleccion/1/" + tipo.getId())
							.enNegrita()
					)
			);
			
			componentes.add(new Parrafo(" "));
		}
		
		componentes.add(new Parrafo(" "));
		componentes.add(new Parrafo(" "));
		componentes.add(new Parrafo("Pulse sobre la opción deseada.").conLetraPeq());
		componentes.add(new Parrafo(" "));
		
		return Tuples.tuple((Collection<IComponente>) componentes, null);
	}
	
	/**
	 * Elección del artefacto que se quiere vincular
	 */
	private Tuples.T2<Collection<IComponente>, Object> contenidoPagina2(final EstadoInterno estado) {
		
		final ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		final Page<Artefacto> artefactos = prepareArtifactsPageToSelect(estado); 
		
		// El párrafo indicando que hay que pulsar sobre el artefacto
		componentes.add(new Parrafo(" "));
		componentes.add(new Parrafo("Pulse sobre el artefacto que quiera vincular.").deTipoPrincipal());
		componentes.add(new Parrafo(" "));
		
		componentes.add(
				new Paginador()
				.conIndexador(
						new Indexador()
						.conTotalPaginas(artefactos.getTotalPages())
						.enPagina(artefactos.getNumber() + 1)
						.usarPlantillaRedireccion("artefactos/dependencias/estaticas/incluir/cambiopagina")
						.usarPlantillaDeParametros("pag=%d")
				)
				.conContenido(
						composeTableWithArtifactsToSelect(
								prepareArtifactListToSelect(artefactos), 
								estado
						)
				)
		);
		
		return Tuples.tuple((Collection<IComponente>)componentes, null);
	}
	
	private Page<Artefacto> prepareArtifactsPageToSelect(final EstadoInterno estado) {

		// el tipo elegido
		final TipoArtefacto tipo = ce.tipos().findOne(estado.getIdTipoElegido());
		
		// la página con la lista actual de artefactos a mostrar
		if(estado.getPagina()==null || estado.getPagina()==0) 
			estado.setPagina(1);
		
		final Pageable peticion = new PageRequest(
				estado.getPagina() - 1, 
				20,
				new Sort("nombre"));
		
		final Page<Artefacto> artefactos = ce.artefactos().findAll(Artefactos.artefactosActivosDeUnTipo(tipo), peticion);
		return artefactos;
	}
	
	private ArrayList<Artefacto[]> prepareArtifactListToSelect(Page<Artefacto> artefactos) {
		
		final ArrayList<Artefacto[]> aas = new ArrayList<Artefacto[]>();
		int p = 0;
		for(Artefacto e: artefactos.getContent()) {
			int fila = p % 10;
			int col = p < 10 ? 0 : 1;
			// en el primer recorrido siempre hay que añadir la fila
			if(p < 10) {
				Artefacto[] o = new Artefacto[2];
				aas.add(o);
			}
			
			aas.get(fila)[col] = e;
			
			p++;
		}
		
		return aas;
	}
	
	private TablaBasica composeTableWithArtifactsToSelect(final ArrayList<Artefacto[]> aas, final EstadoInterno estado) {
		
		final TablaBasica tb = new TablaBasica(
				false,
				Arrays.asList(
						new TablaBasica.DefinicionColumna("Artefactos", 6),
						new TablaBasica.DefinicionColumna("Artefactos", 6)
				)
		);
		
		for(Artefacto[] aa: aas) {
			
			// primera columna
			final ContinenteSinAspecto c1 = new ContinenteSinAspecto()
					.conComponente(
							estado.getExcluir().contains(aa[0].getId()) ?
									new TextoSimple(aa[0].getNombre()).enNegrita()
									: new EnlaceSimple(
											aa[0].getNombre(), 
											"artefactos/dependencias/estaticas/incluir/eleccion/2/" + aa[0].getId()
									)
					);
			if(P.of(aa[0]).coordenadasMaven() != null) {
				c1.conComponentes(
						new SaltoDeLinea(),
						new TextoSimple(P.of(aa[0]).coordenadasMaven().getCoordenadaFinal()).conLetraPeq()
				);
			}

			// en la segunda columna puede que no haya nada.
			final ContinenteSinAspecto c2 = new ContinenteSinAspecto();
			if(aa[1] != null) {
				c2.conComponente(
							estado.getExcluir().contains(aa[1].getId()) ?
										new TextoSimple(aa[1].getNombre()).enNegrita()
										: new EnlaceSimple(
												aa[1].getNombre(), 
												"artefactos/dependencias/estaticas/incluir/eleccion/2/" + aa[1].getId()
										)
						);
				if(P.of(aa[1]).coordenadasMaven() != null) {
					c2.conComponentes(
							new SaltoDeLinea(),
							new TextoSimple(P.of(aa[1]).coordenadasMaven().getCoordenadaFinal()).conLetraPeq()
					);
				}
			}
			
			tb.conFila(c1,c2);
		}
		
		return tb;
	}
		
	private Tuples.T2<Collection<IComponente>, Object> contenidoPagina3(Artefacto artefacto, EstadoInterno estado) {
		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		// cargamos el artefacto elegido
		Artefacto artefactoElegido = ce.artefactos().findOne(estado.getIdArtefactoElegido());
		
		// caracerizamos el tipo de dependencia
		boolean esAlcance = TIPO_DEPENDENCIA_ALCANCE.equalsIgnoreCase(estado.getTipoDependencia());
				
		// preparamos los diccionarios
		Map<String, Collection<NombreDescripcion>> dics = new HashMap<String, Collection<NombreDescripcion>>();
		String urlAceptacion = "/artefactos/dependencias/estaticas/incluir/" + (esAlcance ? "alcance" : "web");
		Object formulario =  esAlcance ?
				new FormularioNuevaDependenciaConfigurarAlcance(artefacto, artefactoElegido) 
				: new FormularioNuevaDependenciaConfigurarContextRoot(artefacto, artefactoElegido);
		
		// en el caso de los alcances, hay que preparar un diccionario
		if(esAlcance) {
			
			// los alcances posibles
			DirectivaAlcances alcances = P.of(artefactoElegido).alcances();
			ArrayList<NombreDescripcion> ae = new ArrayList<Tuples.NombreDescripcion>();
			for(EnumAlcanceDependencia ad: alcances.getAlcances())
				ae.add(new NombreDescripcion(ad.toString(), ad.getTextoDescripcion()));
			dics.put("alcances", ae);
		}
		
		// creamos el formulario
		componentes.add(
				new Formulario()
				.urlAceptacion(urlAceptacion)
				.conComponentes(
						AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(formulario.getClass(), dics)
				)
				.botoneraEstandar("artefactos/dependencias/" + artefacto.getId())
		);
				
		return Tuples.tuple((Collection<IComponente>)componentes, formulario);
	}

	private Tuples.T2<Collection<IComponente>, Object> contenidoPagina4(Artefacto artefacto, EstadoInterno estado) {
		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();

		// añadimos un valor de control al estado
		estado.setControl(UUID.randomUUID().toString());
		
		// cargamos las entidades adicionales
		Artefacto base = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		Artefacto electo = ce.artefactos().findOne(estado.getIdArtefactoElegido());
		
		// resumen de todas las decisiones
		PanelContinente resumen = new PanelContinente()
				.conTitulo("Resumen elecciones")
				.paraTipoInfo();
		
		// lo primero es añadir un párrafo explicativo
		resumen.conComponente(new Parrafo(" "));
		resumen.conComponente(new Parrafo("A continuación se presenta un resumen de las características que tendrá la nueva relación de dependencia entre artefactos basado en las elecciones realizadas."));
		resumen.conComponente(new Parrafo(" "));
		resumen.conComponente(new Parrafo(" "));

		// el artefacto dependiente
		resumen.conComponente(
				MetodosDeUtilidad.parClaveValor(
						"1.", 
						"El artefacto origen de la relación de dependencia es '" + artefacto.getNombre() + "'. "
						+ (artefacto instanceof ITestaferroArtefacto ?
								" Este artefacto es una evolución del artefacto '" + base.getNombre() + "'."
								: ""), 
						1
				)
		);
		resumen.conComponente(new Parrafo(" "));
		
		// el artefacto requerido
		resumen.conComponente(
				MetodosDeUtilidad.parClaveValor(
						"2.",
						"El artefacto mencionado en el punto anterior requiere al artefacto '"
						+ electo.getNombre() + "', que es de tipo '" + electo.getTipoArtefacto().getNombre() + "'.",
						1
				)
		);
		resumen.conComponente(new Parrafo(" "));

		// el tipo de relación de dependencia
		resumen.conComponente(
				MetodosDeUtilidad.parClaveValor(
						"3.",
						"El tipo de relación de dependencia que se creará será de tipo: " + estado.getTipoDependencia(),
						1
				)
		);
		resumen.conComponente(new Parrafo(" "));
		
		// el valor de configuración de la relación (si es pertinente)
		if(!TIPO_DEPENDENCIA_POSICIONAL.equalsIgnoreCase(estado.getTipoDependencia())) {
			String textoValorDependencia;
			
			if(TIPO_DEPENDENCIA_ALCANCE.equalsIgnoreCase(estado.getTipoDependencia())) {
				textoValorDependencia = "Elegido el alcance '" 
						+ EnumAlcanceDependencia.valueOf(((FormularioNuevaDependenciaConfigurarAlcance)estado.getDependencia()).getAlcance()).getTextoDescripcion()
						+ "' para la relación de dependencia.";
			} else {
				textoValorDependencia = "Elegida la ruta de contexto '"
						+ ((FormularioNuevaDependenciaConfigurarContextRoot)estado.getDependencia()).getRutaDeContexto()
						+ "' para la relación de dependencia.";
			}
			
			resumen.conComponente(
					MetodosDeUtilidad.parClaveValor(
							"4.",
							textoValorDependencia,
							1
					)
			);
			resumen.conComponente(new Parrafo(" "));
		}
		
		// añadimos el panel a los componentes a presentar
		componentes.add(resumen);
		
		// y añadimos, además, el formulario de confirmación
		componentes.add(
				new Formulario()
				.urlAceptacion("/artefactos/dependencias/estaticas/incluir/confirmar")
				.conComponentes(AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class))
				.botoneraEstandar("/artefactos/dependencias/estaticas/" + artefacto.getId())
		);

		return Tuples.tuple(
				(Collection<IComponente>)componentes, 
				(Object)new PeticionConfirmacionGeneral(
						estado.getControl(),
						"Confirmar elecciones para nueva relación de dependencia.")
		);
	}
	
	private PanelContinente pendientes(int paso, List<String> pendientes) {

		// según el paso, indicamos qué queda por hacer
		final ArrayList<IComponente> textos = new ArrayList<IComponente>();
		for(int p = paso - 1; p < pendientes.size(); p++) {
			textos.add(
					new ContinenteSinAspecto()
					.conComponentes(
							new GlyphIcon()
							.dedoIndiceDerechaSi(p == (paso - 1))
							.banderaSi(p >= paso),
							
							new TextoSimple()
							.conTexto(pendientes.get(p))
					)
					.enColumna(12)
			);
		}
		
		return new PanelContinente()
				.conTitulo("Pendiente")
				.paraTipoAviso()
				.conComponentes(textos);	
	}
	
	private PanelContinente realizado(int paso, List<String> realizado) {
		
		final ArrayList<IComponente> textos = new ArrayList<IComponente>();
		if(paso == 1) {
			textos.add(
					new Parrafo()
					.conTexto("De momento no se ha realizado ninguna operación")
					.deTipoAviso()
			);
		} else {
			for(int p = 0; p < paso - 1; p++) {
				textos.add(
						new ContinenteSinAspecto()
						.conComponentes(
								new GlyphIcon()
								.ok(),
								
								new TextoSimple()
								.conTexto(realizado.get(p))
						)
						.enColumna(12)
				);
			}
		}
		
		return new PanelContinente()
				.conTitulo("Completado")
				.paraTipoExito()
				.conComponentes(textos);
	}
	
	// ===============
	
	static final class EstadoInterno implements Serializable {
		
		private static final long serialVersionUID = -2340801200690795957L;
		
		private String idArtefacto;
		private Integer paso;
		private ArrayList<String> pendientes;
		private ArrayList<String> realizados;
		
		// elecciones
		private String idTipoElegido;
		private String idArtefactoElegido;
		private String tipoDependencia;
		private Serializable dependencia;
		
		// auxiliares
		private Integer pagina;
		private ArrayList<String> excluir;
		private String control;
		
		public String getIdArtefacto() {
			return idArtefacto;
		}
		public void setIdArtefacto(String idArtefacto) {
			this.idArtefacto = idArtefacto;
		}
		public Integer getPaso() {
			return paso;
		}
		public void setPaso(Integer paso) {
			this.paso = paso;
		}
		public ArrayList<String> getPendientes() {
			return pendientes;
		}
		public void setPendientes(ArrayList<String> pendientes) {
			this.pendientes = pendientes;
		}
		public ArrayList<String> getRealizados() {
			return realizados;
		}
		public void setRealizados(ArrayList<String> realizados) {
			this.realizados = realizados;
		}
		public String getIdTipoElegido() {
			return idTipoElegido;
		}
		public void setIdTipoElegido(String idTipoElegido) {
			this.idTipoElegido = idTipoElegido;
		}
		public String getIdArtefactoElegido() {
			return idArtefactoElegido;
		}
		public void setIdArtefactoElegido(String idArtefactoElegido) {
			this.idArtefactoElegido = idArtefactoElegido;
		}
		public String getTipoDependencia() {
			return tipoDependencia;
		}
		public void setTipoDependencia(String tipoDependencia) {
			this.tipoDependencia = tipoDependencia;
		}
		public Serializable getDependencia() {
			return dependencia;
		}
		public void setDependencia(Serializable dependencia) {
			this.dependencia = dependencia;
		}
		public Integer getPagina() {
			return pagina;
		}
		public void setPagina(Integer pagina) {
			this.pagina = pagina;
		}
		public ArrayList<String> getExcluir() {
			return excluir;
		}
		public void setExcluir(ArrayList<String> excluir) {
			this.excluir = excluir;
		}
		public String getControl() {
			return control;
		}
		public void setControl(String control) {
			this.control = control;
		}
		
		
	}
}
