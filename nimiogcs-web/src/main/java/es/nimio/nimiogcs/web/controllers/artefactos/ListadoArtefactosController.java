package es.nimio.nimiogcs.web.controllers.artefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;
import es.nimio.nimiogcs.jpa.specs.Artefactos;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.controllers.utiles.UtilidadUsuarioAutenticado;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.artefactos.FiltroListado;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping("/artefactos")
public class ListadoArtefactosController {

	private IContextoEjecucion ce;
	
	@Autowired
	public ListadoArtefactosController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private final static int NUMERO_REGISTROS_POR_PAGINA = 10;

	// ------------------------------------------------
	// Listado
	// ------------------------------------------------

	@RequestMapping(method=GET)
	public ModeloPagina listado(
			@RequestParam(required=false, value="tipo", defaultValue="") String tipo, 
			@RequestParam(required=false, value="nombre", defaultValue="") String nombre,
			@RequestParam(required=false, value="pag", defaultValue="1") Integer pag
	) {
	
		Pageable peticion = new PageRequest(
				pag - 1, 
				NUMERO_REGISTROS_POR_PAGINA,
				new Sort("nombre"));
		
		Page<Artefacto> entidades = ce.artefactos()
				.findAll(
						Artefactos.artefactosDeTipoYConNombre(tipo, nombre), 
						peticion
				);
		
		// calculamos la ruta de redirección
		StringBuffer sb = new StringBuffer("pag=%d");
		if(!tipo.isEmpty()) sb.append("&tipo=").append(tipo);
		if(!nombre.isEmpty()) sb.append("&nombre=").append(nombre);
		
		// definimos el panel de altas
		IComponente panelAltas = panelAltas();
		PanelContinente panelListado = panelListado(sb, entidades);
		EstructuraAbstractaPagina pagina = 
				new EstructuraPagina("Artefactos")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conTexto("Artefactos")
				);
		
		// ¿una o dos columnas?
		if(panelAltas != null) {
			pagina
			.conComponentes(
					new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(9)
								.conComponentes(panelListado)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(3)
								.conComponentesSi(
										panelAltas != null,
										panelAltas)
						)
			);
		} else {
			pagina
			.conComponentes(panelListado);
		}
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(pagina)
		.conModelo(
				"datos", 
				new FiltroListado()
					.conTipoSi(!tipo.isEmpty(), tipo)
					.conTipoSi(tipo.isEmpty(), "TODOS")
					.conNombreSi(!nombre.isEmpty(), nombre)
		);
	}
	
	@RequestMapping(method=POST)
	public String aplicarFiltro(@ModelAttribute("datos") FiltroListado filtro) {
		
		boolean tieneTipo = filtro.getTipo() != null && !filtro.getTipo().isEmpty();
		boolean tieneNombre = filtro.getNombre() != null && !filtro.getNombre().isEmpty();
		boolean tieneAmbos = tieneTipo && tieneNombre;
		boolean tieneAlguno = tieneTipo || tieneNombre;
		
		StringBuffer parametros = new StringBuffer();
		if(tieneAlguno) parametros.append('?');
		if(tieneTipo) parametros.append("tipo=").append(filtro.getTipo());
		if(tieneAmbos) parametros.append('&');
		if(tieneNombre) parametros.append("nombre=").append(filtro.getNombre());
		
		return "redirect:/artefactos" + parametros.toString();
	}

	// ------------------------------------------------
	// Partes de la página
	// ------------------------------------------------

	private PanelContinente panelListado(StringBuffer sb, Page<Artefacto> entidades) {
		return 
				new PanelContinente()
				.conTitulo("Artefactos registrados")
				.conLetraPeq()
				.paraTipoPrimario()
				.conComponentes(
						formularioFiltro(),
						paginadorArtefactos(sb, entidades)														
				);
	}
	
	private PanelContinente formularioFiltro() {
		return new PanelContinente()
				.conComponente(
						new Formulario(
								"datos", 
								"/artefactos",
								"/artefactos",
								AyudanteCalculoEstructuraFormularioDesdePojo
								.altaDesdeDto(FiltroListado.class, diccionarios())
				)
		)
		.conComponentesCabecera(
				new ContinenteSinAspecto()
				.conComponente(new GlyphIcon().lupa())
				.conComponente(new TextoSimple("Filtrar la lista"))
		)
		.paraTipoInfo()
		.siendoContraible()
		.empiezaContraido();
	}
	
	private Paginador paginadorArtefactos(StringBuffer sb, Page<Artefacto> entidades) {
		
		final TablaBasica datos = 
				new TablaBasica(
					false, 
					Arrays.asList(
							new TablaBasica.DefinicionColumna("Nombre",2),
							new TablaBasica.DefinicionColumna("Aplicación",2),
							new TablaBasica.DefinicionColumna("Tipo",3),
							new TablaBasica.DefinicionColumna("Taxonomía",3),
							new TablaBasica.DefinicionColumna("Activo/Válido",2)
					)
				);
		
		
		// recorremos los artefactos rellenando los valores
		for(Artefacto artefacto: entidades.getContent()) {
			
			final ArrayList<IComponente> fila = new ArrayList<IComponente>();
			
			// el nombre
			fila.add(new EnlaceSimple(artefacto.getNombre(), "artefactos/" + artefacto.getId()));
			
			// la aplicación (si tiene)
			DirectivaBase d = P.of(artefacto).inventario();
			if(d!=null) {
				final DirectivaInventario l = (DirectivaInventario)d;
				final String idApp = l.getAplicacion().getId();
				final String nmApp = l.getAplicacion().getNombre();
				fila.add(new TextoSimple(idApp + " - " + nmApp));
			} else {
				fila.add(new TextoSimple("-"));
			}
			
			// el tipo
			fila.add(
					new EnlaceSimple()
					.conTexto(artefacto.getTipoArtefacto().getNombre())
					.paraUrl("artefactos")
					.conParametros("tipo=" + artefacto.getTipoArtefacto().getId())
			);
			
			// la taxonomía
			d = P.of(artefacto).taxonomia();
			if(d!=null) {
				fila.add(new TextoSimple(((DirectivaTaxonomia)d).getTaxonomia()));
			} else {
				fila.add(new TextoSimple("-"));
			}
			
			// activo / válido
			fila.add(
					new TextoSimple(
							artefacto.getEstadoValidez() ? 
									(artefacto.getEstadoActivacion() ? "Válido y activo" : "Válido, pero inactivo")
									: "Inválido"
					)
			);
			
			// se añade a la tabla
			datos.conFila(fila);
		}
		
		
		// devolvemos un paginador
		return 	
				new Paginador()
				.conIndexador(
						new Indexador()
						.conTotalPaginas(entidades.getTotalPages())
						.enPagina(entidades.getNumber() + 1)
						.usarPlantillaRedireccion("artefactos")
						.usarPlantillaDeParametros(sb.toString())
				)
				.conContenido(datos);
	}
	
	/**
	 * Crea el panel que informa qué artefactos podremos dar de alta
	 */
	private IComponente panelAltas() {
		
		if(!UtilidadUsuarioAutenticado.conAutoridad(ce.usuario(), "0984-ArquitecturayTransformacion")) 
			return null;
		
		ArrayList<IComponente> artefactosCreables = new ArrayList<IComponente>();
		
		// recorremos la lista de tipos de artefactos y vamos añadiendo a la lista el mecanismo
		// de alta de aquellos para los que hay una caracterización de artefacto (los que
		// determinan qué directivas lo definen).
		for(TipoArtefacto t: ce.tipos().findAll(new Sort("nombre")))
		{
			boolean tieneCaracterizacion = false;
			for(DirectivaBase db: t.getDirectivasTipo()) 
				if(db instanceof DirectivaCaracterizacion) {
					tieneCaracterizacion = true;
					break;
				}
			
			if(tieneCaracterizacion) {
				artefactosCreables.add(
						new ContinenteSinAspecto()
						.conComponentes(
								new GlyphIcon()
								.mas()
								.info(),
								
								new EnlaceSimple()
								.conTexto(t.getNombre())
								.paraUrl("artefactos/nuevo/" + t.getId())
						)
						.enColumna(12)
				);
			}
		}
		
		// si no hay nada que crear, simplemente devolvemos nulo
		if(artefactosCreables.size()==0) return null;
		
		return 
				new PanelContinente()
				.conComponentes(
						new Parrafo("Utilice los siguientes enlaces para dar de alta nuevos artefactos"),
						new Parrafo(" ")
				)
				.conComponentes(
						artefactosCreables
				)
				.conTitulo("Alta nuevo artefacto")
				.conLetraPeq()
				.paraTipoDefecto();
	}
	

	// ------------------------------------------------
	// Utiles
	// ------------------------------------------------

	private Map<String, Collection<NombreDescripcion>> diccionarios() {

		// los tipos de artefactos existentes
		Map<String, Collection<NombreDescripcion>> ds = new HashMap<String, Collection<NombreDescripcion>>();
		ds.put(
				"tipos",
				Collections.list(
						Streams.of(new NombreDescripcion("TODOS", "Todos"))
						.append(
								Streams.of(
										ce.tipos().findAll(new Sort("nombre"))
								)
								.map(new Function<TipoArtefacto, NombreDescripcion>() {
						
									@Override
									public NombreDescripcion apply(TipoArtefacto v) {
										return new NombreDescripcion(v.getId(), v.getNombre());
									}
									
								})
						)
						.getEnumeration()
				)
		);
		
		return ds;
	}
	
}
