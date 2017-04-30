package es.nimio.nimiogcs.web.controllers.admin.globales;

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
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.FiltroListadoParametrosGlobales;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping("/admin/globales")
public class ParametrosGlobalesController {
	private IContextoEjecucion ce;

	@Autowired
	public ParametrosGlobalesController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private final static int NUMERO_REGISTROS_POR_PAGINA = 15;

	// ------------------------------------------------
	// Listado
	// ------------------------------------------------
	@RequestMapping(method = GET)
	public ModelAndView index(
			@RequestParam(required = false, value = "identificador", defaultValue = "") String identificador,
			@RequestParam(required = false, value = "descripcion", defaultValue = "") String descripcion,
			@RequestParam(required = false, value = "pag", defaultValue = "1") Integer pag) {

		int paginaActual = pag != null ? pag : 1;
		Pageable pageable = new PageRequest(paginaActual - 1, NUMERO_REGISTROS_POR_PAGINA, Direction.ASC, "id");

		// Page<ParametroGlobal> pagina = ce.global().findAll(pageable);
		Page<ParametroGlobal> pagina = ce.global().findAll(crearSpecConsulta(descripcion.trim(), identificador.trim()),
				pageable);

		// calculamos la ruta de redirección
		StringBuffer sb = new StringBuffer("pag=%d");
		if (!identificador.isEmpty())
			sb.append("&identificador=").append(identificador);
		if (!descripcion.isEmpty())
			sb.append("&descripcion=").append(descripcion);
		// y devolvemos la paginación
		return ModeloPagina
				.nuevaPagina(new EstructuraPagina("Globales")
						.conComponentes(new Localizacion().conEnlace("Home", "/").conTexto("Administracion")
								.conEnlace("Globales", "/admin/globales"))
				.conComponentes(formularioFiltro(), fabricaPagiador(pagina, "admin/globales", sb.toString())))
				.conModelo("datos",
						new FiltroListadoParametrosGlobales()
								.conIdentificadorSi(!identificador.isEmpty(), identificador)
								.conDescripcionSi(!descripcion.isEmpty(), descripcion)

		);

	}

	private static Specification<ParametroGlobal> crearSpecConsulta(final String filtroDescripcion,
			final String filtroIdentificador) {

		return new Specification<ParametroGlobal>() {

			@Override
			public Predicate toPredicate(Root<ParametroGlobal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				// el patrón de búsqueda
				Predicate p = root.isNotNull();
				boolean filtrarDescripcion = (filtroDescripcion != null && !filtroDescripcion.isEmpty());
				boolean filtrarIdentificador = (filtroIdentificador != null && !filtroIdentificador.isEmpty());

				if (filtrarIdentificador) {
					String patronBusqueda = new StringBuilder().append("%").append(filtroIdentificador.toLowerCase())
							.append("%").toString();
					p = cb.and(p, cb.like(cb.lower(root.<String> get("id")), patronBusqueda));
				}

				if (filtrarDescripcion) {
					String patronBusqueda = new StringBuilder().append("%").append(filtroDescripcion.toLowerCase())
							.append("%").toString();
					p = cb.and(p, cb.like(cb.lower(root.<String> get("descripcion")), patronBusqueda));
				}
				return p;
			}
		};

	}

	@RequestMapping(method = POST)
	public String aplicarFiltro(@ModelAttribute("datos") FiltroListadoParametrosGlobales filtro) {

		boolean tieneIdentificador = filtro.getIdentificador() != null && !filtro.getIdentificador().isEmpty();
		boolean tieneDescripcion = filtro.getDescripcion() != null && !filtro.getDescripcion().isEmpty();

		boolean tieneAlguno = tieneDescripcion || tieneIdentificador;
		boolean tieneAmbos = tieneDescripcion && tieneIdentificador;
		StringBuffer parametros = new StringBuffer();

		if (tieneAlguno)
			parametros.append('?');
		if (tieneIdentificador)
			parametros.append("identificador=").append(filtro.getIdentificador());
		if (tieneAmbos)
			parametros.append('&');
		if (tieneDescripcion)
			parametros.append("descripcion=").append(filtro.getDescripcion());

		return "redirect:/admin/globales" + parametros.toString();
	}

	// ------------------------------------------------
	// Partes de la página
	// ------------------------------------------------

	private PanelContinente formularioFiltro() {
		return new PanelContinente()
				.conComponente(new Formulario("datos", "/admin/globales", "/admin/globales",
						AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(FiltroListadoParametrosGlobales.class,
								null)))
				.conTitulo("Filtrar la lista").paraTipoInfo().siendoContraible().empiezaContraido();
	}

	public static Paginador fabricaPagiador(Page<ParametroGlobal> parametrosGlobales, String urlBase,
			String parametros) {

		return new Paginador()
				.conIndexador(new Indexador().conTotalPaginas(parametrosGlobales.getTotalPages())
						.enPagina(parametrosGlobales.getNumber() + 1).usarPlantillaRedireccion(urlBase)
						.usarPlantillaDeParametros(parametros))
				.conContenido(new TablaBasica(true,
						Arrays.asList(new TablaBasica.DefinicionColumna[] {

				// --- ¿está todo bien?
								new TablaBasica.DefinicionColumna("Identificador", 1,
										Collections.list(Streams.of(parametrosGlobales.getContent())
												.map(new Function<ParametroGlobal, IComponente>() {

													@Override
													public IComponente apply(ParametroGlobal e) {
														return new EnlaceSimple(e.getId(),
																"admin/globales/editar/" + e.getId())

						;
													}

												}).getEnumeration())),

				// --- Contenido
								new TablaBasica.DefinicionColumna("Contenido", 2,
										Collections.list(Streams.of(parametrosGlobales.getContent())
												.map(new Function<ParametroGlobal, IComponente>() {

													@Override
													public IComponente apply(ParametroGlobal e) {

														String c = e.getContenido();
														int l = Strings.isNotEmpty(c) ? c.length() : 0;
														if (l > 200)
															l = 200;
														String sufijo = l == 200 ? "..." : "";
														String texto = Strings.isNotEmpty(c)
																? c.substring(0, l) + sufijo : "";

														return new TextoSimple().conTexto(texto);
													}

												}).getEnumeration())),

				// --- Descripcion
								new TablaBasica.DefinicionColumna("Descripcion", 4,
										Collections.list(Streams.of(parametrosGlobales.getContent())
												.map(new Function<ParametroGlobal, IComponente>() {

													@Override
													public IComponente apply(ParametroGlobal e) {
														return new TextoSimple().conTexto(e.getDescripcion());
													}

												}).getEnumeration())) })));
	}

}
