package es.nimio.nimiogcs.web.controllers.artefactos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.operaciones.artefactos.RegistrarNuevoArtefacto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.artefactos.FormularioDatosBasicosArtefacto;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioBaseDirectiva;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaAlcances;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaCoordenadasMaven;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaDiccionario;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaEstrategia;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaInventario;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaRepositorioCodigo;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaTaxonomia;
import es.nimio.nimiogcs.web.dto.f.directivas.RDFT;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/artefactos/nuevo")
@SessionAttributes(names="estado")
public class NuevoArtefactoController {

	private static final String ESTADO_TIPO = "TIPO";
	private static final String ESTADO_PASOS = "PASOS";
	private static final String ESTADO_PAGINA = "PAGINA";
	
	private IContextoEjecucion ce;

	@Autowired
	public NuevoArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -------------
	
	@PreAuthorize("hasAuthority('0984-ArquitecturayTransformacion')")
	@RequestMapping(path="/{idTipo}", method=RequestMethod.GET)
	public ModelAndView nuevo(@PathVariable("idTipo") String idTipo) {
		
		// cargamos el tipo de artefacto
		TipoArtefacto tipo = ce.tipos().findOne(idTipo);
		if(tipo==null) throw new ErrorEntidadNoEncontrada();
		
		// confirmamos que tiene tipo directiva carecterizacion
		if(PT.of(tipo).directiva(DirectivaCaracterizacion.class)==null)
			throw new ErrorEntidadNoEncontrada();
		
		// si es un tipo existente y tiene caracterización
		// solo queda iniciar el asistente
		ModelAndView inicio = new ModelAndView("redirect:/artefactos/nuevo/asistente");
		inicio.addObject("estado", iniciaEstado(tipo));
		return inicio;
	}
	
	@PreAuthorize("hasAuthority('0984-ArquitecturayTransformacion')")
	@RequestMapping(path="/asistente", method=RequestMethod.GET)
	public ModelAndView asistente(
			@ModelAttribute("estado") Map<String, Serializable> estado) {
		
		return paginaPara(estado);
	}
	
	// :::: cada formulario debe tener su post especializado
	
	@RequestMapping(path="/{idTipo}/datosbasicos", method=RequestMethod.POST)
	public ModelAndView postDatos(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDatosBasicosArtefacto datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}
	
	@RequestMapping(path="/{idTipo}/inventario", method=RequestMethod.POST)
	public ModelAndView postInventario(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaInventario datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	

	@RequestMapping(path="/{idTipo}/taxonomia", method=RequestMethod.POST)
	public ModelAndView postTaxonomia(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaTaxonomia datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	

	@RequestMapping(path="/{idTipo}/alcances", method=RequestMethod.POST)
	public ModelAndView postAlcances(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaAlcances datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	

	@RequestMapping(path="/{idTipo}/coor_maven", method=RequestMethod.POST)
	public ModelAndView postCoordenadasMaven(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaCoordenadasMaven datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	

	@RequestMapping(path="/{idTipo}/evolucion", method=RequestMethod.POST)
	public ModelAndView postEvolucion(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaEstrategia datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	

	@RequestMapping(path="/{idTipo}/repo-codigo", method=RequestMethod.POST)
	public ModelAndView postRepositorio(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaRepositorioCodigo datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	

	@RequestMapping(path="/{idTipo}/diccionario", method=RequestMethod.POST)
	public ModelAndView postDiccionario(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaDiccionario datos,
			Errors errores,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		return procesarPaso(datos, errores, estado, ss);
	}	
	
	// -------------
	
	/**
	 * Inicia la información de estado del asistente para que se pueda comenzar 
	 * a navegar por las diferentes opciones
	 */
	private Map<String, Serializable> iniciaEstado(TipoArtefacto tipo) {
		
		Map<String, Serializable> estado = new HashMap<String, Serializable>();
		
		// siempre empezaremos por la página 1
		estado.put(ESTADO_PAGINA, (Integer)1);
		
		// también indicamos el identificador del tipo, para tenerlo en todos los pasos
		estado.put(ESTADO_TIPO, tipo.getId());
		
		// preparamos los pasos
		ArrayList<NuevoArtefactoController.Paso> pasos = new ArrayList<NuevoArtefactoController.Paso>();
		
		// el primero siempre será 
		pasos.add(
				new Paso(
						"datosbasicos", 
						"Datos básicos", 
						new FormularioDatosBasicosArtefacto(tipo.getId())
				)
		);
		
		// se trata, ahora, de ir cogiendo cada una de las directivas definidas en la caracterización
		DirectivaCaracterizacion dc = PT.of(tipo).caracterizacion();
		for(String idTipoDir: dc.getDirectivasRequeridas()) {
			
			// hay que distinguir entre los formularios de directivas básicas de las directivas de diccionario
			if(idTipoDir.startsWith("@")) {

				String idDiccionario = idTipoDir.replace("@", "");
				
				// cargamos el diccionario
				TipoDirectivaDiccionario tdd = ce.diccionariosDirectivas().findOne(idDiccionario);
				
				FormularioDirectivaDiccionario fdd = new FormularioDirectivaDiccionario();
				fdd.setDiccionario(idDiccionario);
				
				pasos.add(
						new Paso(
								"diccionario",
								"Directiva: " + tdd.getDescripcion(),
								fdd
						)
				);
				
			} else {
				
				Class<? extends FormularioBaseDirectiva> czf = RDFT.formularios().get(idTipoDir);
				if(czf != null) { 
					FormularioBaseDirectiva f;
					try {
						f = (FormularioBaseDirectiva)czf.getConstructor((Class<?> [])null).newInstance((Object[])null);
					} catch (Exception e) {
						throw new ErrorInesperadoOperacion(e);
					}
					pasos.add(
							new Paso(
									idTipoDir.toLowerCase(),
									"Directiva: " + ce.tiposDirectivas().findOne(idTipoDir).getNombre(),
									f
							)
					);
				}
			}
		}
		
		// y añadimos la colección a la variable de estado
		estado.put(ESTADO_PASOS, pasos);
		
		return estado;
	}
	
	private ModelAndView procesarPaso(
			FormularioDatosBasicosArtefacto datos,
			Errors errores,
			Map<String, Serializable> estado,
			SessionStatus ss) {
		
		// pedimos un aumento en el control de validación
		datos.validar(ce, errores);
		
		// esta caso solo puede ser el primero (los datos)
		// por lo que nos ahorramos otras comprobaciones
		@SuppressWarnings("unchecked")
		Paso datosPaso = (Paso)((ArrayList<NuevoArtefactoController.Paso>)estado.get(ESTADO_PASOS)).get(0);
		datosPaso.setFormulario(datos);
		
		// si hay errores, volvemos al formulario
		if(errores.hasErrors()) 
			return paginaPara(estado);
		
		// e incrementamos el paso
		estado.put(ESTADO_PAGINA, (Integer)2);
		
		// redirigimos
		ModelAndView redi = new ModelAndView("redirect:/artefactos/nuevo/asistente");
		redi.addObject("estado", estado);
		
		return redi;
	}
	
	private ModelAndView procesarPaso(
			FormularioBaseDirectiva datos,
			Errors errores,
			Map<String, Serializable> estado,
			SessionStatus ss) {
		
		// revalidamos
		datos.validar(ce, errores);
		
		// cogemos el paso y los datos asociados
		int paso = (Integer)estado.get(ESTADO_PAGINA);
		@SuppressWarnings("unchecked")
		ArrayList<NuevoArtefactoController.Paso> pasos = (ArrayList<NuevoArtefactoController.Paso>)estado.get(ESTADO_PASOS);
		Paso datosPaso = pasos.get(paso - 1);
		
		// rellenamos los cambios del formulario
		datosPaso.setFormulario(datos);
		
		// si hay errores, volvemos al formulario
		if(errores.hasErrors())
			return paginaPara(estado);
		
		// ahora tenemos que confirmar si estamos o no al final de la lista de pasos pendientes
		if(paso == pasos.size()) {
			
			// pedimos el artefacto
			Artefacto artefacto = ((FormularioDatosBasicosArtefacto)pasos.get(0).getFormulario()).nueva(ce);
			
			// generamos las directivas
			Collection<DirectivaBase> directivas = new ArrayList<DirectivaBase>();
			for(int p = 1; p < pasos.size(); p++)
				directivas.add(((FormularioBaseDirectiva)pasos.get(p).getFormulario()).nueva(ce));
			
			// lanzamos el proceso
			new RegistrarNuevoArtefacto<Artefacto>(ce)
			.ejecutar(Tuples.tuple(artefacto, directivas));
			
			// pues ya hemos terminado
			ss.setComplete();
			return new ModelAndView("redirect:/artefactos/" + artefacto.getId());
		}
		
		// si no hemos llegado al final, entonces hay que avanzar otro paso
		estado.put(ESTADO_PAGINA, (Integer)(paso+1));
		
		// redirigimos
		ModelAndView redi = new ModelAndView("redirect:/artefactos/nuevo/asistente");
		redi.addObject("estado", estado);
		
		return redi;
	}
	
	private ModeloPagina paginaPara(Map<String, Serializable> estado) {
		
		int paso = (Integer)estado.get(ESTADO_PAGINA);
		@SuppressWarnings("unchecked")
		Paso datosPaso = (Paso)((ArrayList<NuevoArtefactoController.Paso>)estado.get(ESTADO_PASOS)).get(paso - 1);
		
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina(datosPaso.getTextoPaso())
				.conComponentes(
						
						// la localización
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Artefactos", "/artefactos")
						.conTexto(ce.tipos().findOne((String)estado.get(ESTADO_TIPO)).getNombre())
						.conTexto("Nuevo")
						.conTexto("Paso " + paso + ": " + datosPaso.getTextoPaso()),
						
						// la estructura de página
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conComponentes(
										estructuraFormulario(estado)
								)
								.conAncho(9)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(3)
								.conComponentes(
										pasosDados(estado),
										pasosPendientes(estado)
								)
						)
				)
		)
		.conModelo("datos", datosPaso.getFormulario())
		.conModelo("estado", estado);
	}
	
	private PanelContinente estructuraFormulario(Map<String, Serializable> estado) {
		
		int paso = (Integer)estado.get(ESTADO_PAGINA);
		@SuppressWarnings("unchecked")
		Paso datosPaso = (Paso)((ArrayList<NuevoArtefactoController.Paso>)estado.get(ESTADO_PASOS)).get(paso - 1);
		
		return new PanelContinente()
				.conTitulo("Paso " + paso + ": " + datosPaso.getTextoPaso())
				.paraTipoPrimario()
				.conComponente(
						new Formulario()
						.urlAceptacion("/artefactos/nuevo/" + ((String)estado.get(ESTADO_TIPO)) + "/" + datosPaso.getSubruta())
						.conComponentes(
								
								// aquí debemos diferenciar entre los formuñarios de las directivas y el formulario de datos básicos
								datosPaso.getFormulario() instanceof FormularioDatosBasicosArtefacto ?
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(FormularioDatosBasicosArtefacto.class)
										: UtilidadGenerarFormularioDirectiva.generarFormulario(
												ce, 
												(FormularioBaseDirectiva)datosPaso.getFormulario()
										)
						)
						.botoneraEstandar("/artefactos/nuevo/cancelar")
				);
	}
	
	private PanelContinente pasosDados(Map<String, Serializable> estado) {
		
		int paso = (Integer)estado.get(ESTADO_PAGINA);
		@SuppressWarnings("unchecked")
		ArrayList<NuevoArtefactoController.Paso> pasos = (ArrayList<NuevoArtefactoController.Paso>)estado.get(ESTADO_PASOS);
		
		// según el paso, indicamos qué tenemos hecho
		ArrayList<IComponente> textos = new ArrayList<IComponente>();
		if(paso == 1) {
			textos.add(
					new Parrafo()
					.conTexto("De momento no se ha realizado ninguna operación")
					.deTipoAviso()
			);
		} else {
			for(int p = 0; p < paso - 1; p++) {
				NuevoArtefactoController.Paso datosPaso = pasos.get(p);
				textos.add(
						new ContinenteSinAspecto()
						.conComponentes(
								new GlyphIcon()
								.ok(),
								
								new TextoSimple()
								.conTexto(datosPaso.getTextoPaso())
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
	
	private PanelContinente pasosPendientes(Map<String, Serializable> estado) {

		int paso = (Integer)estado.get(ESTADO_PAGINA);
		@SuppressWarnings("unchecked")
		ArrayList<NuevoArtefactoController.Paso> pasos = (ArrayList<NuevoArtefactoController.Paso>)estado.get(ESTADO_PASOS);

		// según el paso, indicamos qué queda por hacer
		ArrayList<IComponente> textos = new ArrayList<IComponente>();
		for(int p = paso - 1; p < pasos.size(); p++) {
			NuevoArtefactoController.Paso datosPaso = pasos.get(p);
			textos.add(
					new ContinenteSinAspecto()
					.conComponentes(
							new GlyphIcon()
							.dedoIndiceDerecha(),
							
							new TextoSimple()
							.conTexto(datosPaso.getTextoPaso())
					)
					.enColumna(12)
			);
		}
		
		return new PanelContinente()
				.conTitulo("Pendiente")
				.paraTipoAviso()
				.conComponentes(textos);
	}
	
	// -------------
	
	private static final class Paso implements Serializable {

		private static final long serialVersionUID = -7466839974436287984L;

		private String subruta;
		private String textoPaso;
		private Serializable formulario;
		
		public Paso(String subruta, String textoPaso, Serializable formulario) {
			this.subruta = subruta;
			this.textoPaso = textoPaso;
			this.formulario = formulario;
		}
		
		public String getSubruta() {
			return subruta;
		}
		
		public String getTextoPaso() {
			return textoPaso;
		}
		
		public Object getFormulario() {
			return formulario;
		}
		
		public void setFormulario(Serializable f) {
			this.formulario = f;
		}
		
	}
}
