package es.nimio.nimiogcs.web.controllers.admin.tiposArtefactos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioBaseDirectiva;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaCaracterizacion;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaEstrategia;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaEstructuraCodigo;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaProyMaven;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaProyeccion;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaPubDeployer;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaPublicacionJenkins;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaReferenciar;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaVersionJava;
import es.nimio.nimiogcs.web.dto.f.directivas.RDFT;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/admin/tipos")
public class IncorporarYEditarDirectivaEnTipoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public IncorporarYEditarDirectivaEnTipoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------
	
	@RequestMapping(path="/{idTipo}/directiva/incorporar/{directiva}", method=RequestMethod.GET)
	public ModelAndView incorporar (
			@PathVariable("idTipo") String idTipo,
			@PathVariable("directiva") String directiva) {
		
		// cargamos el tipo
		TipoArtefacto tipo = ce.tipos().findOne(idTipo);
		if(tipo == null) throw new ErrorEntidadNoEncontrada();
		
		// confirmamos que el tipo no tenga ya la directiva
		for(DirectivaBase db: tipo.getDirectivasTipo())
			if(db.getDirectiva().getId().equalsIgnoreCase(directiva)) 
				throw new ErrorInconsistenciaDatos("El tipo ya cuenta con la directiva que se indica.");
		
		// cargamos la diretiva para confirmar que es posible agregarla a un tipo
		TipoDirectiva td = ce.tiposDirectivas().findOne(directiva);
		if(td == null) throw new ErrorEntidadNoEncontrada();
		if(!td.getParaTipoArtefacto()) throw new ErrorInconsistenciaDatos("No es una directiva de tipo"); 
		
		// construimos el formulario en base a la clase
		return construirPagina(false, tipo, directiva, null, null);
	}
	
	@RequestMapping(path="/{idTipo}/directiva/editar/{directiva}", method=RequestMethod.GET)
	public ModelAndView editar(
			@PathVariable("idTipo") String idTipo,
			@PathVariable("directiva") String directiva) {

		// cargamos el tipo
		TipoArtefacto tipo = ce.tipos().findOne(idTipo);
		if(tipo == null) throw new ErrorEntidadNoEncontrada();

		// confirmamos que el tipo sí tiene la directiva
		DirectivaBase de = null;
		for(DirectivaBase db: tipo.getDirectivasTipo())
			if(db.getDirectiva().getId().equalsIgnoreCase(directiva)) {
				de = db;
				break;
			}
		if(de==null) throw new ErrorEntidadNoEncontrada();
		
		// construimos el formulario en base a la clase
		return construirPagina(true, tipo, directiva, null, de);
	}

	// ::: cada directiva tedrá un método post específico, dado que el formuñario es tipado

	@RequestMapping(path="/{idTipo}/directiva/incorporar/caracterizacion", method=RequestMethod.POST)
	public ModelAndView incorporarCaracterizacion(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaCaracterizacion datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "CARACTERIZACION");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/evolucion", method=RequestMethod.POST)
	public ModelAndView incorporarEvolucion(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaEstrategia datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "EVOLUCION");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/estruct_codigo", method=RequestMethod.POST)
	public ModelAndView incorporarEstructuraCodigo(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaEstructuraCodigo datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "ESTRUCT_CODIGO");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/proyeccion", method=RequestMethod.POST)
	public ModelAndView incorporarProyeccion(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaProyeccion datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "PROYECCION");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/proyeccion_maven", method=RequestMethod.POST)
	public ModelAndView incorporarProyMaven(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaProyMaven datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "PROYECCION_MAVEN");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/publicacion_deployer", method=RequestMethod.POST)
	public ModelAndView incorporarPublicacionDeployer(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaPubDeployer datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "PUBLICACION_DEPLOYER");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/publicacion_jenkins", method=RequestMethod.POST)
	public ModelAndView incorporarPublicacionJenkins(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaPublicacionJenkins datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "PUBLICACION_JENKINS");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/referenciar", method=RequestMethod.POST)
	public ModelAndView incorporarReferenciar(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaReferenciar datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "REFERENCIAR");
	}

	@RequestMapping(path="/{idTipo}/directiva/incorporar/version_java", method=RequestMethod.POST)
	public ModelAndView incorporarVersionJava(
			@PathVariable("idTipo") String idTipo,
			@Valid @ModelAttribute("datos") FormularioDirectivaVersionJava datos,
			Errors errores) {
		
		return procederIncorporar(idTipo, datos, errores, "VERSION_JAVA");
	}


	
	// ------------------------------
	
	private ModeloPagina construirPagina(
			boolean edicion, 
			TipoArtefacto tipo, 
			String directiva, 
			FormularioBaseDirectiva datos, 
			DirectivaBase db) {

		Class<? extends FormularioBaseDirectiva> czf = RDFT.formularios().get(directiva);
		FormularioBaseDirectiva p;
		try {
			p = datos == null ? 
					czf.getConstructor((Class<?> [])null).newInstance((Object[])null)
					: datos;
		} catch (Exception e) {
			throw new ErrorInesperadoOperacion(e);
		}
		p.setIdEntidad(tipo.getId());
		p.setEsAlta(!edicion);
		if(edicion) { if(db!=null) p.datosDesde(db); }
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Incluir directiva en tipo")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conTexto("Administración")
						.conEnlace("Tipos de artefactos", "/admin/tipos")
						.conEnlace(tipo.getNombre(), "/admin/tipos/" + tipo.getId())
						.conTexto("Directivas")
						.conTexto(edicion? "Cambiar datos" : "Incluir")
						.conTexto(directiva)
				)
				.conComponentesSi(
						edicion,
						new PanelInformativo()
						.tipoPeligro()
						.conTexto(
								"Modificar los parámetros de una directiva que tiene artefactos ya creados puede tener consecuencias impredecibles. Actúe con prudencia."
						)
				)
				.conComponentes(		
						new Formulario()
						.urlAceptacion("/admin/tipos/" + tipo.getId() + "/directiva/incorporar/" + directiva.toLowerCase())
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(czf, seleccionables())
						)
						.botoneraEstandar("/admin/tipos/" + tipo.getId())
				)
		)
		.conModelo("datos", p); 
	}
	
	// Todos los posibles seleccionables que tendrán los formularios de directiva
	private Map<String, Collection<NombreDescripcion>> seleccionables() {
		
		Map<String, Collection<NombreDescripcion>> seleccionables = new HashMap<String, Collection<NombreDescripcion>>();
		
		// las posibles estrategias de evolución
		Collection<NombreDescripcion> estrategias = new ArrayList<NombreDescripcion>();
		estrategias.add(new NombreDescripcion(DirectivaEstrategiaEvolucion.VALOR_PROYECTO, DirectivaEstrategiaEvolucion.EXPLICACION_PROYECTO));
		estrategias.add(new NombreDescripcion(DirectivaEstrategiaEvolucion.VALOR_UNICA, DirectivaEstrategiaEvolucion.EXPLICACION_UNICA));
		seleccionables.put("estrategias", estrategias);
		
		// los comportamientos deployer
		Collection<NombreDescripcion> comportamientosDeployer = new ArrayList<NombreDescripcion>();
		ParametroGlobal pg = ce.global().findOne("PUBLICACION.DEPLOYER.COMPORTAMIENTOS");
		for(String lineaBruto: pg.getContenido().split("\n")) {
			String linea = lineaBruto.replace("\r", "");
			String[] cv = linea.split("=");
			comportamientosDeployer.add(new NombreDescripcion(cv[0], cv[1]));
		}
		seleccionables.put("comportamientos-deployer", comportamientosDeployer);
		
		return seleccionables;
	}
	
	private ModelAndView procederIncorporar(
			final String idTipo,
			final FormularioBaseDirectiva datosFormulario,
			final Errors errores,
			final String directiva) {
		
		// validamos que la petición y el tipo corresponden
		if(!idTipo.equalsIgnoreCase(datosFormulario.getIdEntidad())) 
			throw new ErrorInconsistenciaDatos(
					"No hay correspondencia entre la URL y los datos de la petición"
			);
		
		// también debe garantizarse que el tipo existe
		final TipoArtefacto ta = ce.tipos().findOne(idTipo);
		if(ta==null) throw new ErrorEntidadNoEncontrada();
		
		// hacemos una validación más exhaustiva
		datosFormulario.validar(ce, errores);
		
		// si hay errores, tenemos que volver a mostrar el formulario
		if(errores.hasErrors()) 
			return construirPagina(!datosFormulario.getEsAlta(), ta, directiva, datosFormulario, null);
		
		// podemos añadir, libremente, el tipo al formulario
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
				
				// relacionamos la operación con el tipo
				registraRelacionConOperacion(op, ta);
				
				// dependiendo de si estamos en alta o en modificación, 
				// crearemos una nueva o modificaremos la existente
				if(datosFormulario.getEsAlta()) {

					// pedimos al formulario que nos de el tipo exacto
					DirectivaBase nuevaDirectiva = datosFormulario.nueva(this.ce);
					
					// que almacenamos
					ce.directivas().saveAndFlush(nuevaDirectiva);
					
					// y que añadimos al tipo de artefacto
					ta.getDirectivasTipo().add(nuevaDirectiva);
					ce.tipos().saveAndFlush(ta);
				} else {
				
					// buscamos la directiva entre las del tipo
					DirectivaBase dm = null;
					for(DirectivaBase dd: ta.getDirectivasTipo()) {
						if(dd.getDirectiva().getId().equalsIgnoreCase(directiva)) dm = dd;
					}
					if(dm!=null) {
						
						datosFormulario.actualiza(this.ce, dm);
						ce.directivas().saveAndFlush(dm);
					}
				}
				
				// salimos
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				if(datosFormulario.getEsAlta())
					return "AÑADIR DIRECTIVA '" + directiva + "' AL TIPO DE ARTEFACTO '" + idTipo + "'";
				else
					return "CAMBIAR DATOS DIRECTIVA '" + directiva + "' DEL TIPO DE ARTEFACTO '" + idTipo + "'";
			}
		}.ejecutar();
		
		// si se ha superado todo, podemos redirigir a la página del tipo
		return new ModelAndView("redirect:/admin/tipos/" + idTipo);
	}
}
