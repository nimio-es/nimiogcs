package es.nimio.nimiogcs.web.controllers.proyectos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Arrays;
import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceExterno;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.FiltroListado;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping("/proyectos")
public class ListadoProyectosController {

	private IContextoEjecucion ce;
	
	@Autowired
	public ListadoProyectosController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private final static int NUMERO_REGISTROS_POR_PAGINA = 15;

	// ------------------------------------------------
	// Listado
	// ------------------------------------------------

	@RequestMapping(method=GET)
	public ModeloPagina listado( 
			@RequestParam(required=false, value="nombre", defaultValue="") String nombre,
			@RequestParam(required=false, value="pag", defaultValue="1") Integer pag
	) {
	
		Pageable peticion = new PageRequest(
				pag - 1, 
				NUMERO_REGISTROS_POR_PAGINA, 
				Direction.ASC, 
				ElementoBaseProyecto.NOMBRE_CAMPO_NOMBRE);
		
		Page<Proyecto> entidades = ce.proyectos()
				.findAll(
						crearSpecConsulta(nombre), 
						peticion
				);
		
		// calculamos la ruta de redirección
		StringBuffer sb = new StringBuffer("pag=%d");
		if(!nombre.isEmpty()) sb.append("&nombre=").append(nombre);
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Proyectos")
					.conComponentes(
							new Localizacion()
							.conEnlace("Home", "/")
							.conEnlace("Proyectos", "/proyectos")
					)
					.conComponentes(
							new Columnas()
								.conColumna(
										new Columnas.Columna()
										.conAncho(10)
										.conComponentes(
												new PanelContinente()
												.conComponentes(
														formularioFiltro(),
														paginadorProyectos(sb, entidades)
												)
												.conTitulo("Proyectos registrados")
												.conLetraPeq()
												.paraTipoPrimario()
										)
								)
								.conColumna(
										new Columnas.Columna()
										.conAncho(2)
										.conComponentes(
											new PanelContinente()
											.conComponentes(
													new Parrafo("Utilice el siguiente enlace para dar de alta nuevos proyectos"),
													new Columnas.Columna()
													.conAncho(12)
													.conComponentes(
																new EnlaceSimple("Alta", "proyectos/nuevo")
													)
													.sinFilas()
											)
											.conTitulo("Alta nuevo proyecto")
											.conLetraPeq()
											.paraTipoDefecto()
										)
								)
					)
		)
		.conModelo(
				"datos", 
				new FiltroListado()
					.conNombreSi(!nombre.isEmpty(), nombre)
		);
	}
	
	@RequestMapping(method=POST)
	public String aplicarFiltro(@ModelAttribute("datos") FiltroListado filtro) {
		
		boolean tieneNombre = filtro.getNombre() != null && !filtro.getNombre().isEmpty();
		
		StringBuffer parametros = new StringBuffer();
		if(tieneNombre) parametros.append("?nombre=").append(filtro.getNombre());
		
		return "redirect:/proyectos" + parametros.toString();
	}

	// ------------------------------------------------
	// Partes de la página
	// ------------------------------------------------

	private PanelContinente formularioFiltro() {
		return new PanelContinente()
				.conComponentes(
						new Formulario(
								"datos", 
								"/proyectos",
								"/proyectos",
								AyudanteCalculoEstructuraFormularioDesdePojo
								.altaDesdeDto(FiltroListado.class, null)
				)
		)
		.conTitulo("Filtrar la lista")
		.paraTipoInfo()
		.siendoContraible()
		.empiezaContraido();
	}
	
	private Paginador paginadorProyectos(StringBuffer sb, Page<Proyecto> entidades) {
		return 							new Paginador()
				.conIndexador(
						new Indexador()
						.conTotalPaginas(entidades.getTotalPages())
						.enPagina(entidades.getNumber() + 1)
						.usarPlantillaRedireccion("proyectos")
						.usarPlantillaDeParametros(sb.toString())
				)
				.conContenido(
						new TablaBasica(
								false,
								Arrays.asList(
										new TablaBasica.DefinicionColumna[] {
												// --- el nombre
												new TablaBasica.DefinicionColumna(
														"Nombre",
														4,
														Collections.list(
																Streams.of(entidades.getContent())
																.map(
																		new Function<Proyecto, IComponente>() {

																			@Override
																			public IComponente apply(
																					Proyecto e) {
																				return new EnlaceSimple(
																						e.getNombre(),
																						"proyectos/" + e.getId()
																				);
																			}
																	
																		}
																)
																.getEnumeration()
														)
												),
												
												// --- el tipo
												new TablaBasica.DefinicionColumna(
														"Estado", 
														3,
														Collections.list(
																Streams.of(entidades.getContent())
																.map(
																		new Function<Proyecto, IComponente>() {

																			@Override
																			public IComponente apply(
																					Proyecto e) {
																				return new TextoSimple()
																						.conTexto(e.getEstado().toString());
																			}

																		}
																)
																.getEnumeration()
														)
												),
												
												// --- la taxonomía
												new TablaBasica.DefinicionColumna(
														"Url trabajo", 
														5,
														Collections.list(
																Streams.of(entidades.getContent())
																.map(
																		new Function<Proyecto, IComponente>() {

																			@Override
																			public IComponente apply(
																					Proyecto e) {
																				return new EnlaceExterno()
																						.conTexto(e.getUrlTrabajoCodigo())
																						.paraUrl(e.getUrlTrabajoCodigo());
																			}

																		}
																)
																.getEnumeration()
														)
												)

										}
								)
						)
				);
	}
	

	// ------------------------------------------------
	// Utiles
	// ------------------------------------------------

	private static Specification<Proyecto> crearSpecConsulta(
			final String filtroNombre) {
		
		return new Specification<Proyecto>() {

			@Override
			public Predicate toPredicate(Root<Proyecto> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				// parte constante de la query
				Predicate pq = root.isNotNull();
				
				// filtrar por nombre, solo cuando se ha indicado que se filtre
				if(filtroNombre != null && !filtroNombre.isEmpty()) {
					// el patrón de búsqueda para el nombre
					String patronBusqueda = new StringBuilder()
						.append("%")
						.append(filtroNombre.toLowerCase())
						.append("%")
						.toString();
				
					pq = cb.and(pq, cb.like(cb.lower(root.<String>get("nombre")), patronBusqueda));
				}
				
				return pq;
			}
		};
		
	}
}
