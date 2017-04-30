package es.nimio.nimiogcs.web.controllers.proyectos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.proyecto.proyeccion.IDescripcionesProyecciones;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.operaciones.proyecto.CrearEstructuraNuevoProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.alta.ConfirmacionAltaArtefactoForm;
import es.nimio.nimiogcs.web.dto.f.proyectos.alta.DatosPrincipalesAltaProyectoForm;
import es.nimio.nimiogcs.web.dto.f.proyectos.alta.FiltroEleccionArtefactoForm;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

@Controller
@RequestMapping("/proyectos/nuevo")
public class AltaNuevoProyectoController {

	IContextoEjecucion ce;
	
	@Autowired
	public AltaNuevoProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ---------------------------------------------------
	// Comienzo del alta
	// ---------------------------------------------------

	@PreAuthorize("hasAnyAuthority('Desarrollo', '0984-ArquitecturayTransformacion')")
	@RequestMapping(method=RequestMethod.GET)
	public ModeloPagina inicio() {
	
		return ModeloPagina.nuevaPagina(paginaAltaPaso1())
		.conModelo("datos", new DatosPrincipalesAltaProyectoForm());
	}

	@PreAuthorize("hasAnyAuthority('Desarrollo', '0984-ArquitecturayTransformacion')")
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView inicio(@ModelAttribute("datos") @Valid DatosPrincipalesAltaProyectoForm datos, Errors errores) {

		if(errores.hasErrors()) {
			return ModeloPagina.nuevaPagina(paginaAltaPaso1())
					.conModelo("datos", datos);
		}
		
		return new ModelAndView(
				"redirect:/proyectos/nuevo/paso2?proyecto=" + datos.getNombre().toUpperCase() 
						+ "&produce=" + datos.getProyeccion() 
						+ "&tipo=" + datos.getProyectable()
						+ "&repositorio=" + datos.getRepositorio());
	}

	// ----------------------------------------------------
	// Elegir el tipo que queremos para el primer artefacto
	// ----------------------------------------------------

	@RequestMapping(path="/paso2", method=RequestMethod.GET)
	public ModeloPagina paso2(
			@RequestParam(required=true) final String proyecto,
			@RequestParam(required=true) final String produce,
			@RequestParam(required=true) final String tipo,
			@RequestParam(required=true) final String repositorio,
			@RequestParam(defaultValue="", required=false) final String nombre,
			@RequestParam(defaultValue="1", required=false) final Integer pag) {
	
		
		Page<Artefacto> artefactos = artefactos(pag, tipo, nombre);
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Alta de proyecto (paso 2)")
				.conComponentes(
						localizacion("Paso 2: Elegir el primer artefacto"),
						
						// panel con los datos
						new PanelContinente()
						.conTitulo("Configuración inicial")
						.paraTipoDefecto()
						.siendoContraible()
						.conComponentes(
								new Parrafo()
								.conTexto("Configuración elegida en el paso anterior."),
								
								MetodosDeUtilidad.parClaveValor("Nombre proyecto:", proyecto),
								MetodosDeUtilidad.parClaveValor("Para asociar uso:", produce),
								MetodosDeUtilidad.parClaveValor("Tipo artefacto inicial:", ce.tipos().findOne(tipo).getNombre()),
								MetodosDeUtilidad.parClaveValor("Repositorio de proyecto:", ce.repositorios().findOne(repositorio).getNombre())
						),
						
						// panel del filtro
						new PanelContinente()
						.conTitulo("Filtro")
						.paraTipoInfo()
						.siendoContraible()
						.empiezaContraido()
						.conComponente(
								new Formulario()
								.urlAceptacion("/proyectos/nuevo/paso2")
								.conComponentes(
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(FiltroEleccionArtefactoForm.class)
								)
								.botoneraEstandar("/proyectos/nuevo/paso2?proyecto=" + proyecto + "&produce=" + produce + "&tipo=" + tipo)
						),

						// la selección
						new PanelContinente()
						.conTitulo("Artefactos disponibles")
						.paraTipoDefecto()
						.conComponente(
								new Paginador()
								.conIndexador(
										new Indexador()
										.conTotalPaginas(artefactos.getTotalPages())
										.enPagina(pag)
										.usarPlantillaRedireccion("proyectos/nuevo/paso2")
										.usarPlantillaDeParametros(
												"proyecto=" + proyecto 
												+ "&produce=" + produce 
												+ "&tipo=" + tipo
												+ "&repositorio=" + repositorio
												+ "&pag=%d&nombre=" + nombre)
								)
								.conContenido(
										
										new TablaBasica(
												false,
												Arrays.asList(
														new TablaBasica.DefinicionColumna[] {
																new TablaBasica.DefinicionColumna(
																		"Artefacto",
																		10,
																		Collections.list(
																				
																				Streams.of(artefactos.getContent())
																				.map(
																						new Function<Artefacto, IComponente>() {
		
																							@Override
																							public IComponente apply(
																									Artefacto v) {
																								
																								return new TextoSimple()
																										.conTexto(v.getNombre());
																							}
																					
																						}
																				)
																				.getEnumeration()
																		)
																),
																
																new TablaBasica.DefinicionColumna(
																		"Elegir",
																		2,
																		Collections.list(
																				
																				Streams.of(artefactos.getContent())
																				.map(
																						new Function<Artefacto, IComponente>() {
		
																							@Override
																							public IComponente apply(
																									Artefacto v) {
																								
																								return new BotonEnlace()
																										.conTamañoMuyPequeño()
																										.conTexto("Elegir")
																										.paraUrl("/proyectos/nuevo/confirmar")
																										.conParametros(
																												"proyecto=" + proyecto 
																												+ "&produce=" + produce
																												+ "&repositorio=" + repositorio
																												+ "&artefacto=" + v.getId()
																										);
																							}
																					
																						}
																				)
																				.getEnumeration()
																		)
																),
														}
												)
										)
										
								)
						)
				)
		)
		.conModelo("datos", new FiltroEleccionArtefactoForm(proyecto, tipo, produce, nombre));
	}

	// ----------------------------------------------------
	// Aceptar el alta
	// ----------------------------------------------------

	@RequestMapping(path="/confirmar", method=RequestMethod.GET)
	public ModeloPagina confirmar(
			@RequestParam(required=true) final String proyecto,
			@RequestParam(required=true) final String produce,
			@RequestParam(required=true) final String repositorio,
			@RequestParam(required=true) final String artefacto) {
		
		
		return ModeloPagina.nuevaPagina(paginaAltaConfirmacion())
				.conModelo(
						"datos", 
						new ConfirmacionAltaArtefactoForm(
								proyecto, 
								produce, 
								repositorio, 
								ce.repositorios().findOne(repositorio).getNombre(),
								artefacto, 
								ce.artefactos().findOne(artefacto).getNombre()
						)
				);
	}

	@RequestMapping(path="/confirmar", method=RequestMethod.POST)
	public ModelAndView confirmar(@ModelAttribute("datos") ConfirmacionAltaArtefactoForm alta) {

		// hacemos la magia para dar de alta el proyecto
		new CrearEstructuraNuevoProyecto(ce).ejecutarCon(
				new CrearEstructuraNuevoProyecto.Peticion(
						alta.getNombre(), 
						alta.getProyeccion(), 
						alta.getIdRepositorio(), 
						alta.getIdArtefacto(), 
						ce.usuario())
		);
		
		return new ModelAndView("redirect:/proyectos");
	}

	// ---------------------------------------------------
	// Consultas
	// ---------------------------------------------------
	
	private Page<Artefacto> artefactos(final int pag, final String tipo, final String nombre) {
		
		// Vamos a solicitar los artefactos que son del tipo indicado
		Pageable peticion = new PageRequest(
				pag - 1,
				10,
				new Sort(Direction.ASC, "nombre")
		);
		
		
		return ce.artefactos().findAll(
				new Specification<Artefacto>() {
					
					@Override
					public javax.persistence.criteria.Predicate toPredicate(Root<Artefacto> root, CriteriaQuery<?> query,
							CriteriaBuilder cb) {

						// siempre vamos a tener que filtrar por el tipo
						javax.persistence.criteria.Predicate p = cb.equal(root.get("tipo").get("id"), tipo);
						
						// lo siguiente es ver si añadimos el nombre a la búsqueda
						if(Strings.isNotEmpty(nombre)) {
							
							String filtro = new StringBuilder("%")
									.append(nombre.toLowerCase())
									.append("%")
									.toString();
							
							p = cb.and(p, cb.like(cb.lower(root.<String>get("nombre")),filtro));
						}
						
						return p;
					}
				}, 
				peticion);
		
	}
	
	// ---------------------------------------------------
	// Diccionarios
	// ---------------------------------------------------
	
	private Collection<NombreDescripcion> getUsos() {

		List<NombreDescripcion> proyecciones = new ArrayList<NombreDescripcion>();
		
		for(IDescripcionesProyecciones py: ce.contextoAplicacion().getBeansOfType(IDescripcionesProyecciones.class).values()) {
			py.descripciones(proyecciones);
		}
		
		Collections.sort(
				proyecciones,
				new Comparator<NombreDescripcion>() {

					@Override
					public int compare(NombreDescripcion o1, NombreDescripcion o2) {
						return o1._2.compareTo(o2._2);
					}
				}
		);
		
		return proyecciones;
	}
	
	private Collection<NombreDescripcion> getProyectables() {
		
		return Collections.list(
				Streams.of(ce.tipos().findAll(new Sort("nombre")))
				.filter(new Predicate<TipoArtefacto>() {

					@Override
					public boolean test(TipoArtefacto v) {
						return PT.of(v).proyeccion() != null;
					}
				})
				.map(new Function<TipoArtefacto, NombreDescripcion>() {

					@Override
					public NombreDescripcion apply(TipoArtefacto v) {
						return new NombreDescripcion(v.getId(), v.getNombre());
					}
				})
				.getEnumeration()
		);
	}
	
	private Collection<NombreDescripcion> gerRepositorios() {
		
		return Collections.list(
				Streams.of(ce.repositorios().findAll(new Sort("nombre")))
				.filter(new Predicate<RepositorioCodigo>() {

					@Override
					public boolean test(RepositorioCodigo v) {
						return v.getParaProyectos();
					}
					
				})
				.map(new Function<RepositorioCodigo, NombreDescripcion>() {

					@Override
					public NombreDescripcion apply(RepositorioCodigo v) {
						return new NombreDescripcion(v.getId(), v.getNombre());
					}
					
				})
				.getEnumeration()
		);
	}
	
	private Map<String, Collection<NombreDescripcion>> diccionarios() {
		HashMap<String, Collection<NombreDescripcion>> diccionarios = new HashMap<String, Collection<NombreDescripcion>>();
		diccionarios.put("usos", getUsos());
		diccionarios.put("proyectables", getProyectables());
		diccionarios.put("repositorios", gerRepositorios());
		
		return diccionarios;
	}
	
	// ****************************************************
	// WIDGETS
	// ****************************************************
	
	private Localizacion localizacion(String paso) {
		return new Localizacion()
				.conEnlace("Home", "/")
				.conEnlace("Proyectos", "/proyectos")
				.conTexto("Alta")
				.conTexto(paso);
	}
	
	private EstructuraPagina paginaAltaPaso1() { 
		return (EstructuraPagina) new EstructuraPagina("Alta de proyecto (paso 1)")
		.conComponentes(
				localizacion("Paso 1: Decisiones generales"),
				
				new Formulario()
				.urlAceptacion("/proyectos/nuevo")
				.conComponentes(
						AyudanteCalculoEstructuraFormularioDesdePojo
						.altaDesdeDto(
								DatosPrincipalesAltaProyectoForm.class, diccionarios())
				)
				.botoneraEstandar("/proyectos")
		);
	}

	private EstructuraPagina paginaAltaConfirmacion() { 
		return (EstructuraPagina) new EstructuraPagina("Confirmación")
		.conComponentes(
				localizacion("Paso 3: Confirmar datos"),
				
				new Formulario()
				.urlAceptacion("/proyectos/nuevo/confirmar")
				.conComponentes(
						AyudanteCalculoEstructuraFormularioDesdePojo
						.altaDesdeDto(
								ConfirmacionAltaArtefactoForm.class)
				)
				.botoneraEstandar("/proyectos")
		);
	}

}
