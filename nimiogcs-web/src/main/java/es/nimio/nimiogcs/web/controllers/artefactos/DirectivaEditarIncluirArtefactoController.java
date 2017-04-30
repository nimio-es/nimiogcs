package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioBaseDirectiva;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaAlcances;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaDiccionario;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaInventario;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaParamDeployer;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaProyMaven;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaProyeccion;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaVersionJava;
import es.nimio.nimiogcs.web.dto.f.directivas.RDFT;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping(path="/artefactos")
public class DirectivaEditarIncluirArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public DirectivaEditarIncluirArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----

	@RequestMapping(path="/{idArtefacto}/directiva/{idDirectiva}/incluir", method=RequestMethod.GET)
	public ModelAndView incluirDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("idDirectiva") String idDirectiva) {
		
		// comprobar que existe el artefacto
		Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();
		
		// comprobar que la directiva existe
		TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne(idDirectiva);
		if(tipoDirectiva==null) throw new ErrorEntidadNoEncontrada();
		
		// debe, además, ser una de las directivas opcionales del tipo
		if(!Arrays.asList(PT.of(artefacto.getTipoArtefacto()).caracterizacion().getDirectivasOpcionales()).contains(idDirectiva))
			throw new ErrorIntentoOperacionInvalida();
		
		return formulario("incluir", artefacto, tipoDirectiva);
	}

	@RequestMapping(path="/{idArtefacto}/directiva/DICCIONARIO/{idDiccionario}/incluir", method=RequestMethod.GET)
	public ModelAndView incluirDirectivaDiccionario(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("idDiccionario") String idDiccionario) {
		
		// comprobar que existe el artefacto
		Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) 
			throw new ErrorEntidadNoEncontrada();
		
		// comprobar que la directiva existe
		TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne("DICCIONARIO");
		
		// comprobar que existe el diccionario
		TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(idDiccionario);
		if(diccionario==null) throw new ErrorEntidadNoEncontrada();
		
		// debe, además, ser una de las directivas opcionales del tipo
		if(!Arrays.asList(PT.of(artefacto.getTipoArtefacto()).caracterizacion().getDirectivasOpcionales()).contains("@" + idDiccionario))
			throw new ErrorIntentoOperacionInvalida();
		
		return formulario("incluir", artefacto, tipoDirectiva, idDiccionario);
	}
	
	
	@RequestMapping(path="/{idArtefacto}/directiva/{idDirectiva}/editar", method=RequestMethod.GET)
	public ModelAndView editarDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("idDirectiva") String idDirectiva) {
		
		// comprobar que existe el artefacto
		Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) 
			throw new ErrorEntidadNoEncontrada();
		
		// comprobar que la directiva existe
		TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne(idDirectiva);
		if(tipoDirectiva==null) 
			throw new ErrorEntidadNoEncontrada();
		
		// comprobamos que, además, el artefacto tiene dicha directiva
		DirectivaBase directiva = P.of(artefacto).buscarDirectiva(idDirectiva); 
		if(directiva==null) 
			throw new ErrorEntidadNoEncontrada();
		
		return formulario("editar", artefacto, tipoDirectiva, directiva);
	}
	
	@RequestMapping(path="/{idArtefacto}/directiva/DICCIONARIO/{idDiccionario}/editar", method=RequestMethod.GET)
	public ModelAndView editarDiccionario(
			@PathVariable("idArtefacto") final String idArtefacto,
			@PathVariable("idDiccionario") final String idDiccionario) {
	
		// comprobar que existe el artefacto
		Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();

		// comprobar que la directiva existe
		TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne("DICCIONARIO");
		
		// comprobar que existe el diccionario
		TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(idDiccionario);
		if(diccionario==null) throw new ErrorEntidadNoEncontrada();
		
		// buscamos la directiva
		DirectivaBase directiva = P.of(artefacto).diccionario(idDiccionario);
		if(directiva==null) 
			throw new ErrorEntidadNoEncontrada();
		
		return formulario("editar", artefacto, tipoDirectiva, idDiccionario, directiva);
	}
	
	
	// :: cada directiva debe tener un post de entrada distinto para poder tipar correctamente el valor
	
	@RequestMapping(path="/{idArtefacto}/directiva/ALCANCES/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaAlcances datos,
			Errors errores) {

		return registrar(accion, idArtefacto, "ALCANCES", datos, errores);
	}

	@RequestMapping(path="/{idArtefacto}/directiva/INVENTARIO/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaInventario datos,
			Errors errores) {

		return registrar(accion, idArtefacto, "INVENTARIO", datos, errores);
	}

	@RequestMapping(path="/{idArtefacto}/directiva/PARAMETROS_DEPLOYER/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaParamDeployer datos,
			Errors errores) {
		
		return registrar(accion, idArtefacto, "PARAMETROS_DEPLOYER", datos, errores);
	}
		
	@RequestMapping(path="/{idArtefacto}/directiva/PROYECCION/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaProyeccion datos,
			Errors errores) {

		return registrar(accion, idArtefacto, "PROYECCION", datos, errores);
	}

	@RequestMapping(path="/{idArtefacto}/directiva/PROYECCION_MAVEN/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaProyMaven datos,
			Errors errores) {

		return registrar(accion, idArtefacto, "PROYECCION_MAVEN", datos, errores);
	}

	@RequestMapping(path="/{idArtefacto}/directiva/VERSION_JAVA/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaVersionJava datos,
			Errors errores) {

		return registrar(accion, idArtefacto, "VERSION_JAVA", datos, errores);
	}

	@RequestMapping(path="/{idArtefacto}/directiva/DICCIONARIO/{accion}", method=RequestMethod.POST)
	public ModelAndView postDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("accion") String accion,
			@ModelAttribute("datos") @Valid FormularioDirectivaDiccionario datos,
			Errors errores) {

		return registrar(accion, idArtefacto, "DICCIONARIO", datos, errores);
	}

	// ---
	
	private ModeloPagina formulario(String accion, Artefacto artefacto, TipoDirectiva tipoDirectiva) {
		return formulario(accion, artefacto, tipoDirectiva, (DirectivaBase)null);
	}
	
	private ModeloPagina formulario(String accion, Artefacto artefacto, TipoDirectiva tipoDirectiva, String diccionario) {
		
		FormularioDirectivaDiccionario fdd = new FormularioDirectivaDiccionario();
		fdd.setDiccionario(diccionario);
		
		return formulario(accion, artefacto, tipoDirectiva, fdd);
	}
	
	private ModeloPagina formulario(String accion, Artefacto artefacto, TipoDirectiva tipoDirectiva, DirectivaBase directiva) {
		
		Class<? extends FormularioBaseDirectiva> czf = RDFT.formularios().get(tipoDirectiva.getId()); 
		FormularioBaseDirectiva fbd;
		try {
			fbd = czf.getConstructor((Class<?> [])null).newInstance((Object[])null);
		} catch (Exception e) {
			throw new ErrorInesperadoOperacion(e);
		}
		if(directiva!=null) fbd.datosDesde(directiva);
		
		return formulario(accion, artefacto, tipoDirectiva, fbd);
	}

	private ModeloPagina formulario(String accion, Artefacto artefacto, TipoDirectiva tipoDirectiva, String diccionario, DirectivaBase directiva) {
		
		FormularioDirectivaDiccionario fdd = new FormularioDirectivaDiccionario();
		fdd.setDiccionario(diccionario);
		fdd.datosDesde(directiva);
		
		return formulario(accion, artefacto, tipoDirectiva, fdd);
	}

	private ModeloPagina formulario(String accion, Artefacto artefacto, TipoDirectiva tipoDirectiva, FormularioBaseDirectiva fbd) {
		
		return estructuraFormulario(accion, artefacto, tipoDirectiva, fbd)
		.conModelo("datos", fbd);
	}
	

	private ModeloPagina estructuraFormulario(String accion, Artefacto artefacto, TipoDirectiva tipoDirectiva, FormularioBaseDirectiva fbd) {
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina(accion.equalsIgnoreCase("EDITAR") ? "Editar directiva artefacto" : "Incluir directiva")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Artefactos", "/artefactos")
						.conEnlaceYParametros(artefacto.getTipoArtefacto().getNombre(), "/artefactos", "tipo=" + artefacto.getTipoArtefacto().getId())
						.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
						.conTexto("Directivas")
						.conTexto(tipoDirectiva.getNombre())
						.conTexto(accion.equalsIgnoreCase("EDITAR") ? "Editar" : "Incluir"),
						
						new Formulario()
						.urlAceptacion("/artefactos/" + artefacto.getId() + "/directiva/" + tipoDirectiva.getId() + "/" + accion)
						.conComponentes(UtilidadGenerarFormularioDirectiva.generarFormulario(ce, fbd))
						.botoneraEstandar("/artefactos/" + artefacto.getId())
						
				)
		);
	}
	
	private ModelAndView registrar(final String accion, String idArtefacto, final String idDirectiva, final FormularioBaseDirectiva datosFormulario, Errors errores) {
		
		// comprobar que existe el artefacto
		final Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();
		
		// comprobar que la directiva existe
		final TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne(idDirectiva);
		if(tipoDirectiva==null) throw new ErrorEntidadNoEncontrada();
		
		// comprobamos que, además, el artefacto tiene dicha directiva
		if(accion.equalsIgnoreCase("EDITAR"))
			if(P.of(artefacto).buscarDirectiva(idDirectiva)==null) 
				throw new ErrorEntidadNoEncontrada();
		
		datosFormulario.validar(ce, errores);
		if(errores.hasErrors()) return formulario(accion, artefacto, tipoDirectiva, datosFormulario);
		
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected void relacionarOperacionConEntidades(Boolean datos, Operacion op) {
				registraRelacionConOperacion(op, artefacto);
			}
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
				
				if(accion.equalsIgnoreCase("EDITAR")) {
					DirectivaBase directiva = P.of(artefacto).buscarDirectiva(idDirectiva);
					datosFormulario.actualiza(ce, directiva);
					ce.directivas().saveAndFlush(directiva);
				} else {
					DirectivaBase directiva = datosFormulario.nueva(ce);
					ce.directivas().save(directiva);
					artefacto.getDirectivasArtefacto().add(directiva);
					ce.artefactos().saveAndFlush(artefacto);
				}
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				if(accion.equalsIgnoreCase("EDITAR"))
					return "MODIFICAR DATOS DIRECTIVA '" + tipoDirectiva.getNombre() + "' DEL ARTEFACTO '" + artefacto.getNombre() + "'";
				else
					return "INCLUIR NUEVA DIRECTIVA '" + tipoDirectiva.getNombre() + "' EN EL ARTEFACTO '" + artefacto.getNombre() + "'";
			}
		}.ejecutar();

		return new ModelAndView("redirect:/artefactos/" + idArtefacto);
	}
	
}
