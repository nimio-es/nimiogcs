package es.nimio.nimiogcs.web.controllers.operaciones;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.controllers.utiles.UtilidadCargarUsuariosPaginaOperaciones;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.operaciones.FiltroListadoOperaciones;
import es.nimio.nimiogcs.web.dto.p.BuilderPaginadorOperaciones;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;


@Controller
@RequestMapping("/operaciones")
public class OperacionesController {
	private IContextoEjecucion ce;

	@Autowired
	public OperacionesController(IContextoEjecucion ce) {
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
		public ModelAndView index(
				@RequestParam(required=false, value="descripcion", defaultValue="") String descripcion,
				@RequestParam(required=false, value="tipo", defaultValue="") String tipo,
				@RequestParam(required=false, value="estado", defaultValue="") String estado,	
				@RequestParam(required=false, value="usuario", defaultValue="") String usuario,
				@RequestParam(required=false, value="pag" , defaultValue="1") Integer pag) {
			
			int paginaActual = pag != null ? pag : 1;
			Pageable pageable = new PageRequest(
					paginaActual - 1, 
					NUMERO_REGISTROS_POR_PAGINA, 
					Direction.DESC, "tiempoInicio");
			
			Page<Operacion> pagina = 
					ce.operaciones().findAll(crearSpecConsulta(
							 descripcion.trim(), 
							 tipo.trim(),
							 estado.trim(), 
							 usuario.trim()
							 ), pageable);
					
			// calculamos la ruta de redirección
			StringBuffer sb = new StringBuffer("pag=%d");
			if(!descripcion.isEmpty()) sb.append("&descripcion=").append(descripcion);	
			if(!tipo.isEmpty()) sb.append("&tipo=").append(tipo);
			if(!estado.isEmpty()) sb.append("&estado=").append(estado);
			if(!usuario.isEmpty()) sb.append("&usuario=").append(usuario);
			
			// y devolvemos la paginación
			return ModeloPagina.nuevaPagina(
					new EstructuraPagina("Operaciones")
					.conComponentes(
							new Localizacion()
							.conEnlace("Home", "/")
							.conEnlace("Operaciones", "/operaciones")
					)
					.conComponentes(
							formularioFiltro(),
							BuilderPaginadorOperaciones.fabricaPaginador(
									pagina, 
									UtilidadCargarUsuariosPaginaOperaciones.cargarUsuariosPagina(ce, pagina),
									"operaciones", 
									sb.toString())
					)
			)
			.conModelo(
						"datos", 
						new FiltroListadoOperaciones()
						.conDescripcionSi(!descripcion.isEmpty(), descripcion)
						.conTipoSi(!tipo.isEmpty(), tipo)
						.conEstadoSi(!estado.isEmpty(), estado)
						.conEstadoSi(estado.isEmpty(), "TODOS")
						.conUsuarioSi(!usuario.isEmpty(), usuario)
			);
					
					
				}
		// ------------------------------------------------
		// Utiles
		// ------------------------------------------------

		private Map<String, Collection<NombreDescripcion>> diccionarios() {
			
			Map<String, Collection<NombreDescripcion>> ds = new HashMap<String, Collection<NombreDescripcion>>();
			ds.put(
					"estados",
					Arrays.asList(
						new NombreDescripcion[] {
								new NombreDescripcion("TODOS", "Todos"),
								new NombreDescripcion("ESPERANDO","Esperando"),
								new NombreDescripcion("EJECUCION", "Ejecucion"),
								new NombreDescripcion("OK", "Ok"),
								new NombreDescripcion("ERROR", "Error")
								
						}
					)
			);
			
			return ds;
		}

		private static Specification<Operacion> crearSpecConsulta(final String filtroDescripcion, 
				                                                  final String filtroTipo, 
				                                                  final String filtroEstado,
				                                                  final String filtroUsuario) {
			
			return new Specification<Operacion>() {

				@Override
				public Predicate toPredicate(Root<Operacion> root,
						CriteriaQuery<?> query, CriteriaBuilder cb) {

					// el patrón de búsqueda
					Predicate p = root.isNotNull();
					boolean filtrarDescripcion = (filtroDescripcion != null && !filtroDescripcion.isEmpty());
					boolean filtrarTipo = (filtroTipo != null && !filtroTipo.isEmpty());
					boolean filtrarEstado = (filtroEstado != null && !filtroEstado.isEmpty()&& !filtroEstado.equalsIgnoreCase("TODOS"));
					boolean filtrarUsuario = (filtroUsuario != null && !filtroUsuario.isEmpty());
					
					if (filtrarDescripcion){
						String patronBusqueda = new StringBuilder()
						.append("%")
						.append(filtroDescripcion.toLowerCase())
						.append("%")
						.toString();
					 p = cb.and(p, cb.like(cb.lower(root.<String>get("descripcion")), patronBusqueda));
					}
					
					if(filtrarTipo) {
						String patronBusquedaTipo = new StringBuilder()
						.append("%")
						.append(filtroTipo.toLowerCase())
						.append("%")
						.toString();
						p = cb.and(p, cb.like(cb.lower(root.<String>get("tipoOperacion")), patronBusquedaTipo));
					}
					if(filtrarEstado) {
						String patronBusquedaEstado = new StringBuilder()
						.append("%")
						.append(filtroEstado.toLowerCase())
						.append("%")
						.toString();
						p = cb.and(p, cb.like(cb.lower(root.<String>get("estadoEjecucionProceso")), patronBusquedaEstado));
					}
					
					if(filtrarUsuario) {
						String patronBusquedaUsuario = new StringBuilder()
						.append("%")
						.append(filtroUsuario.toLowerCase())
						.append("%")
						.toString();
						p = cb.and(p, cb.like(cb.lower(root.<String>get("usuarioEjecuta")), patronBusquedaUsuario));
					}
				
					return p;
				}
			};
			
		}
		
	
		@RequestMapping(method=POST)
		public String aplicarFiltro(@ModelAttribute("datos") FiltroListadoOperaciones filtro) {
			
			boolean tieneTipo = filtro.getTipo() != null && !filtro.getTipo().isEmpty();
			boolean tieneDescripcion = filtro.getDescripcion() != null && !filtro.getDescripcion().isEmpty();
			boolean tieneEstado = filtro.getEstado() != null && !filtro.getEstado().isEmpty();
			boolean tieneUsuario = filtro.getUsuario() != null && !filtro.getUsuario().isEmpty();
			
			boolean tieneAlguno = tieneTipo || tieneDescripcion || tieneEstado  || tieneUsuario;
			boolean tieneDescripcionTipo = tieneDescripcion && tieneTipo;
			boolean tieneDescripcionEstado= tieneDescripcion && tieneEstado;
			boolean tieneDescripcionUsuarioNoTipoNoEstado= (tieneDescripcion && tieneUsuario && !tieneTipo && !tieneEstado);
			boolean tieneTipoEstadoYNoDescripcion= (tieneTipo && tieneEstado && !tieneDescripcion);
			boolean tieneTipoUsuarioYNoEstado= (tieneTipo && tieneUsuario && !tieneEstado);
			boolean tieneEstadoUsuario= tieneEstado && tieneUsuario;
			
			StringBuffer parametros = new StringBuffer();
			
			if(tieneAlguno) parametros.append('?');
			if(tieneDescripcion) parametros.append("descripcion=").append(filtro.getDescripcion());
			if (tieneDescripcionTipo) parametros.append('&');
			if(tieneTipo) parametros.append("tipo=").append(filtro.getTipo());
			if(tieneDescripcionEstado) parametros.append('&');
			if(tieneTipoEstadoYNoDescripcion) parametros.append('&');
			if(tieneEstado) parametros.append("estado=").append(filtro.getEstado());
			if(tieneDescripcionUsuarioNoTipoNoEstado) parametros.append('&');
			if(tieneTipoUsuarioYNoEstado) parametros.append('&');
			if(tieneEstadoUsuario) parametros.append('&');
			if(tieneUsuario) parametros.append("usuario=").append(filtro.getUsuario());
			
			return "redirect:/operaciones" + parametros.toString();
		}
		
		// ------------------------------------------------
		// Partes de la página
		// ------------------------------------------------

		private PanelContinente formularioFiltro() {
			return new PanelContinente()
			.conComponente(
					new Formulario(
							"datos", 
							"/operaciones",
							"/operaciones",
							AyudanteCalculoEstructuraFormularioDesdePojo
							.altaDesdeDto(FiltroListadoOperaciones.class, diccionarios())
					)
			)
			.conTitulo("Filtrar la lista")
			.paraTipoInfo()
			.siendoContraible()
			.empiezaContraido();
		}
}
