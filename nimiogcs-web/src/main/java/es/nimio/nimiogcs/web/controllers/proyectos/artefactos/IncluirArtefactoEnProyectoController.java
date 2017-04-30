	package es.nimio.nimiogcs.web.controllers.proyectos.artefactos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.operaciones.proyecto.AltaNuevaRelacionProyectoArtefacto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.SaltoDeLinea;
import es.nimio.nimiogcs.web.componentes.basicos.TextoCabecera1;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.artefactos.ConfirmarEleccionArtefactoRama;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.w.AsistenteAgregarNuevoArtefactoAProyecto;
import es.nimio.nimiogcs.web.dto.w.AsistenteAgregarNuevoArtefactoAProyecto.ConPaginacion;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/incluirartefacto")
@SessionAttributes("estado")
public class IncluirArtefactoEnProyectoController {

	private final IContextoEjecucion ce;
	
	@Autowired
	public IncluirArtefactoEnProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	
	@RequestMapping(path="/{proyecto}", method=RequestMethod.GET)
	public ModelAndView inicio_asistente(@PathVariable("proyecto") final String idProyecto) {

		// primero comprobamos que el proyecto existe
		Proyecto proyecto = ce.proyectos().findOne(idProyecto);
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// también creamos la primera versión del estado
		AsistenteAgregarNuevoArtefactoAProyecto estado = new AsistenteAgregarNuevoArtefactoAProyecto(idProyecto);
		
		return ModeloPagina.nuevaPagina(
				pagina_1(proyecto, estado)
		)
		.conModelo("estado", estado);
	}

	@RequestMapping(path="/origen/{origen}", method=RequestMethod.GET)
	public ModelAndView origen_elegido(
			@PathVariable("origen") final String origen,
			@ModelAttribute("estado") AsistenteAgregarNuevoArtefactoAProyecto estado,
			Model modelo) {
		
		// sacamos el proyecto del estado
		Proyecto proyecto = ce.proyectos().findOne(estado.getIdProyecto());
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// metemos en la caché del estado qué hemos decidido
		estado.getCache().put("ORIGEN", origen);
		
		// arrancamos en el estado el camino a seguir
		if(origen.equalsIgnoreCase(AsistenteAgregarNuevoArtefactoAProyecto.ORIGEN_DE_PROYECTO)) {
			estado.cerrarPaso1Con(AsistenteAgregarNuevoArtefactoAProyecto.ORIGEN_DE_PROYECTO);
		} else {
			estado.cerrarPaso1Con(AsistenteAgregarNuevoArtefactoAProyecto.ORIGEN_NUEVA_RAMA);
		}
		
		return ModeloPagina.nuevaPagina(
				construye_pagina(proyecto, estado, modelo)
		)
		.conModelo("estado", estado);
	}

	@RequestMapping(path="/tipo/{tipo}", method=RequestMethod.GET)
	public ModelAndView tipo_elegido(
			@PathVariable("tipo") final String tipo,
			@ModelAttribute("estado") AsistenteAgregarNuevoArtefactoAProyecto estado,
			Model modelo 
	) {
		
		// sacamos el proyecto del estado
		Proyecto proyecto = ce.proyectos().findOne(estado.getIdProyecto());
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// cargamos el tipo
		TipoArtefacto ta = ce.tipos().findOne(tipo);
		if(ta==null) throw new ErrorEntidadNoEncontrada();
		
		// arrancamos en el estado el camino a seguir
		estado.cerrarPasoCon(ta.getNombre());
		
		// y emtemos en la caché el id para las búsquedas
		estado.getCache().put("ID-TIPO", tipo);
		
		return ModeloPagina.nuevaPagina(
				construye_pagina(proyecto, estado, modelo)
		)
		.conModelo("estado", estado);
	}

	@RequestMapping(path="/buscarArtefacto", method=RequestMethod.GET)
	public ModelAndView buscar_artefacto(
			@RequestParam(name="pag", required=false, defaultValue="1") Integer pag,
			@ModelAttribute("estado") AsistenteAgregarNuevoArtefactoAProyecto estado,
			Model modelo 
	) {
		
		// sacamos el proyecto del estado
		Proyecto proyecto = ce.proyectos().findOne(estado.getIdProyecto());
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// actualizamos la página 
		((ConPaginacion)estado.getPasoActual()).setPaginaActual(pag);
		
		return ModeloPagina.nuevaPagina(
				construye_pagina(proyecto, estado, modelo)
		)
		.conModelo("estado", estado);
	}

	@RequestMapping(path="/artefacto/{id}", method=RequestMethod.GET)
	public ModelAndView artefacto_elegido(
			@PathVariable("id") final String idArtefacto,
			@ModelAttribute("estado") AsistenteAgregarNuevoArtefactoAProyecto estado,
			Model modelo 
	) {
		
		// sacamos el proyecto del estado
		Proyecto proyecto = ce.proyectos().findOne(estado.getIdProyecto());
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// cargamos el artefacto
		Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();
		
		// arrancamos en el estado el camino a seguir
		estado.cerrarPasoCon(artefacto.getNombre());
		
		// metemos en la caché el id del artefacto
		estado.getCache().put("ID-ARTEFACTO", artefacto.getId());
		
		return ModeloPagina.nuevaPagina(
				construye_pagina(proyecto, estado, modelo)
		)
		.conModelo("estado", estado);
	}

	
	@RequestMapping(path="/confirmar", method=RequestMethod.POST)
	public ModelAndView confirmar(
			final @ModelAttribute("estado") AsistenteAgregarNuevoArtefactoAProyecto estado,
			Model modelo,
			SessionStatus ss) {
		
		// en realidad no necesitamos el formulario de confirmación, pues ya tenemos 
		// todo lo que nos interesa en el estado. A saber, si se trata de recoger
		// de un proyecto o usar una nueva ramificación.
		
		// cargamos el proyecto
		final Proyecto proyecto = ce.proyectos().findOne(estado.getIdProyecto());
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();

		// empezamos intentando averiguar qué hemos ido decidiendo
		final String origen = (String)estado.getCache().get("ORIGEN");
		if(origen.equalsIgnoreCase("ARTEFACTO")) {
			
			// el origen es usar un artefacto nuevo
			final String idArtefacto = (String)estado.getCache().get("ID-ARTEFACTO");
			final Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
			if(artefacto==null) throw new ErrorEntidadNoEncontrada();

			new AltaNuevaRelacionProyectoArtefacto(ce)
			.ejecutarCon(
					new AltaNuevaRelacionProyectoArtefacto.Peticion(
							estado.getIdProyecto(),
							idArtefacto
					)
			);
			
		} else {

			// el origen es un proyecto
		}
		
		// fin de la sesión
		ss.setComplete();
		
		// volvemos a la página del proyecto
		return new ModelAndView("redirect:/proyectos/" + estado.getIdProyecto());
	}
	
	
	
	// ---------------
	// Construcción de páginas
	// ---------------

	private EstructuraPagina base() {
		return new EstructuraPagina("Incluir artefacto en proyecto");
	}
	
	private EstructuraAbstractaPagina pagina_1(Proyecto proyecto, AsistenteAgregarNuevoArtefactoAProyecto estado) {
		
		return base()
				.conComponentes(
						crearLocalizacion(proyecto, "Paso 1: Elegir origen"),
						
						creaColumnas(
								estado, 
								Arrays.asList(
										new IComponente[] {
												new TextoCabecera1()
												.conTexto("Elegir el origen")
												.primario(),
												
												new Parrafo()
												.conTexto("Debe elegir de dónde tomará el artefacto:"),
												
												new Parrafo(""),
												
												new EnlaceSimple()
												.conTexto("TOMAR EL ARTEFACTO DIRECTAMENTE")
												.paraUrl("proyectos/incluirartefacto/origen/ARTEFACTO")
												.enNegrita()
												.enColumna(12),
												
												new Parrafo("Ramifica un nevo artefacto (si la estratega de evolución es por ramas) o coge la rama única.")
												.deTipoDefecto()
												.conLetraPeq(),
												
												new Parrafo(""),

												new EnlaceSimple()
												.conTexto("VINCULAR A ARTEFACTO DE OTRO PROYECTO")
												.paraUrl("proyectos/incluirartefacto/origen/PROYECTO")
												.enNegrita()
												.enColumna(12),
												
												new Parrafo("Utiliza la rama ya existente de otro proyecto. De esta forma, ambos proyectos evolucionará el código a la vez.")
												.deTipoDefecto()
												.conLetraPeq(),
												
												new Parrafo(""),

												new Parrafo("Pulse sobre la opción que desea.").conLetraPeq()
												
										}
								)
						)
				);
	}
	
	
	private EstructuraAbstractaPagina construye_pagina(Proyecto proyecto, AsistenteAgregarNuevoArtefactoAProyecto estado, Model modelo) {
	
		AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar accionActual = estado.getAccionActual();
		String punto = titulosAcciones.get(accionActual);
		
		List<IComponente> central = null;
		switch(accionActual) {
		
			case ElegirTipoArtefactoProyectable:
				central = elegir_tipo_proyectable();
				break;
		
			case ElegirArtefactoActivo: 
				central = elegir_artefacto_activo(estado);
				break;
				
			case ConfirmarOrigenNuevaRama:
				central = formulario_confirmar_rama(estado, modelo);
				break;
				
			default:
				central = norte_perdido(); 
		}
		
		return base()
				.conComponentes(
						crearLocalizacion(proyecto, punto),
						creaColumnas(estado, central)
				);
	}

	private List<IComponente> elegir_tipo_proyectable() {
		
		// buscamos entre los tipos aquellos que tenga una directiva que permite que sea proyecable
		ArrayList<TipoArtefacto> proyectables = new ArrayList<TipoArtefacto>();
		
		for(TipoArtefacto t: ce.tipos().findAll(new Sort(Direction.ASC, "nombre"))) {
			if(PT.of(t).proyeccion() != null) {
				proyectables.add(t);
			}
		}
		
		// ya estamos en disposición de preparar los componentes
		ArrayList<IComponente> r = new ArrayList<IComponente>();
		r.add(
				new TextoCabecera1()
				.conTexto("Elegir tipo proyectable")
				.primario()
		);
		r.add(new Parrafo(" "));
		
		if(proyectables.size()==0) {
			r.add(
					new Parrafo()
					.conTexto("No existen tipos proyectables, por lo que no se podrá agregar ningún artefacto al proyecto.")
					.deTipoPeligro()
					.enNegrita()
			);
		} else {
			
			r.add(
					new Parrafo()
					.conTexto("A continuación se ofrecen los tipos que son proyectables. Pulse sobre el que se corresponda con el artefacto que quiere localizar.")					
			);
			
			r.add(new Parrafo(" "));
			
			for(TipoArtefacto tp: proyectables) {
				r.add(
						new EnlaceSimple()
						.conTexto(tp.getNombre())
						.paraUrl("proyectos/incluirartefacto/tipo/" + tp.getId())
						.enNegrita()
						.enColumna(12)
				);
				r.add(new Parrafo(""));
			}
			
		}
		
		return r;
	}
	
	private List<IComponente> elegir_artefacto_activo(AsistenteAgregarNuevoArtefactoAProyecto estado) {
		
		// del paso anterio sacamos el tipo
		final String tipo = (String)estado.getCache().get("ID-TIPO");
		
		// del actual, que debe ser con paginación, sacamos la página en la que nos encontramos
		int pagina = ((ConPaginacion)estado.getPasoActual()).getPaginaActual();
		
		// ya podemos montar la petición
		Pageable peticion = new PageRequest(
				pagina - 1, 
				10, 
				Direction.ASC, 
				"nombre");
		
		Page<Artefacto> entidades = ce.artefactos()
				.findAll(
						new Specification<Artefacto>() {
							@Override
							public Predicate toPredicate(Root<Artefacto> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
								return cb.equal(root.get("tipo").get("id"), tipo);
							}
						},
						peticion
				);
		
		// creamos la tabla
		TablaBasica tb = new TablaBasica(
				false,
				Arrays.asList(
						new TablaBasica.DefinicionColumna("", 12)
				)
		);
		
		// añadimos todo el contenido de la página
		for(Artefacto a: entidades.getContent()) {
			tb.conFila(
					new EnlaceSimple()
					.conTexto(a.getNombre())
					.paraUrl("proyectos/incluirartefacto/artefacto/" + a.getId())
					.enNegrita()
			);
		}
		
		return Arrays.asList(
				new IComponente[] {
						new TextoCabecera1()
						.conTexto("Elegir artefacto")
						.primario(),
						
						new Parrafo(" "),
						
						new Parrafo("Elija el artefacto que quiere agregar al proyecto."),
						
						new Parrafo(" "),

						new Paginador()
						.conIndexador(
								new Indexador()
								.conTotalPaginas(entidades.getTotalPages())
								.enPagina(pagina)
								.usarPlantillaRedireccion("proyectos/incluirartefacto/buscarArtefacto")
								.usarPlantillaDeParametros("pag=%d")
						)
						.conContenido(tb)
				}
		);
	}
	
	private List<IComponente> formulario_confirmar_rama(AsistenteAgregarNuevoArtefactoAProyecto estado, Model modelo) {
		
		//
		String id = (String)estado.getCache().get("ID-ARTEFACTO");
		String nombre = (String)estado.getPasoAnterior().getValor();
		
		
		// creamos el mapa
		modelo.addAttribute("datos", new ConfirmarEleccionArtefactoRama(id, nombre));
		
		return Arrays.asList(
				new IComponente[] {
						
						new TextoCabecera1()
						.conTexto("Confirmar elección")
						.primario(),
						
						new Parrafo(" "),
						
						new Parrafo("Si confirma la operación, éste será el artefacto que se añada al proyecto:"),

						new Parrafo(" "),

						new Formulario()
						.urlAceptacion("/proyectos/incluirartefacto/confirmar")
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(ConfirmarEleccionArtefactoRama.class)
						)
						.botoneraEstandar("/proyectos/" + (String)estado.getCache().get("ID-ARTEFACTO"))
				}
		);
	}
	
	private List<IComponente> norte_perdido() {
		return Arrays.asList(
				new IComponente[] {
						new Parrafo()
						.conTexto("Esto no debería aparecer. O no lo he terminado o no he calculado la ruta correcta.")
						.enNegrita()
				}
		);
	}
	
	
	// ---------------
	// Auxiliares
	// ---------------

	private static final Map<AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar, String> titulosAcciones = new HashMap<AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar, String>();
	
	static {
		titulosAcciones.put(AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar.ElegirTipoArtefactoProyectable, "Paso 2: Elegir tipo de artefacto proyectable");
		titulosAcciones.put(AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar.ElegirArtefactoActivo, "Paso 3: Elegir artefacto activo");
		titulosAcciones.put(AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar.ConfirmarOrigenNuevaRama, "Paso 4: Confirmar artefacto elegido");
		titulosAcciones.put(AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar.ElegirProyectoActivo, "Paso 2: Elegir el proyecto");
		titulosAcciones.put(AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar.ElegirArtefactoDeProyecto, "Paso 3: Elegir un artefacto del proyecto");
		titulosAcciones.put(AsistenteAgregarNuevoArtefactoAProyecto.AccionARealizar.ConfirmarOrigenProyecto, "Paso 4: Confirmar artefacto elegido");
	}
	
	private Localizacion crearLocalizacion(Proyecto proyecto, String punto) {
		return new Localizacion()
				.conEnlace("Home", "/")
				.conEnlace("Proyectos", "/proyectos")
				.conEnlace(proyecto.getNombre(), "/proyectos/" + proyecto.getId())
				.conTexto("Incluir artefacto")
				.conTexto(punto);
	}
	
	private Columnas creaColumnas(AsistenteAgregarNuevoArtefactoAProyecto estado, List<IComponente> central) {

		final List<IComponente> realizado = new ArrayList<IComponente>();
		final List<IComponente> porRealizar = new ArrayList<IComponente>();
		
		// creamos la lista de lo realizado
		realizado.add(new Parrafo(" "));
		if(estado.listaDeLoRealizado().size() == 0) {
			realizado.add(
					new Parrafo("Aún no se ha tomado ninguna decisión.")
					.deTipoInfo()
					.enNegrita()
			);
		} else {
			for(String s: estado.listaDeLoRealizado()) {
				realizado.add(new TextoSimple(s).enColumna(12));
				realizado.add(new SaltoDeLinea());
			}
		}

		// creamos la lista de lo pendiente
		porRealizar.add(new Parrafo(" "));
		for(String s: estado.listaPendienteDeRealizar()) {
			porRealizar.add(new TextoSimple(s).enColumna(12));
			porRealizar.add(new SaltoDeLinea());
		}

		
		// el asistente se compone de tres columnas
		// la primera y la tercera cuentan el estado actual, para lo que necesitan del estado.
		// la del centro atiende a lo que se tiene que hacer
		return new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(9).
						conComponentes(
								new PanelContinente()
								.conTitulo("Decisiones")
								.paraTipoPrimario()
								.conComponentes(central)
						)
				)
				.conColumna(
						new Columnas.Columna()
						.conAncho(3)
						.conComponentes(
								new PanelContinente()
								.conTitulo("Pasos realizados")
								.paraTipoExito()
								.conComponentes(realizado),
								
								new PanelContinente()
								.conTitulo("Pendientes")
								.paraTipoAviso()
								.conComponentes(porRealizar)
						)
				);
	}
}
