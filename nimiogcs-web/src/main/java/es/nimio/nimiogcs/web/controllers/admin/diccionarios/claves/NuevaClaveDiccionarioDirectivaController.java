package es.nimio.nimiogcs.web.controllers.admin.diccionarios.claves;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.diccionarios.FormularioClaveDiccionarioDirectiva;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path=Paths.Admin.Diccionarios.BASE)
public class NuevaClaveDiccionarioDirectivaController {

	private IContextoEjecucion ce;
	
	@Autowired
	public NuevaClaveDiccionarioDirectivaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(path=Paths.Admin.Diccionarios.Definiciones.PTRN_REQ_NUEVA, method=RequestMethod.GET)
	public ModelAndView nuevaClave(@PathVariable("diccionario") String id) {
		
		// confirmamos que existe el diccionario
		TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(id);
		if(diccionario == null) throw new ErrorEntidadNoEncontrada();
		
		return formulario(diccionario, null);
	}
	
	@RequestMapping(path=Paths.Admin.Diccionarios.Definiciones.PTRN_REQ_NUEVA, method=RequestMethod.POST)
	public ModelAndView nuevaClave(
			@PathVariable("diccionario") String id,
			@ModelAttribute("datos") @Valid final FormularioClaveDiccionarioDirectiva datosFormulario,
			Errors errores) {
		
		// confirmamos que existe el diccionario
		final TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(id);
		if(diccionario == null) throw new ErrorEntidadNoEncontrada();
		
		// confirma que no haya ya una clave con el valor indicado
		boolean existe = false;
		for(TipoDirectivaDiccionarioDefinicion df: diccionario.getDefinicionesDiccionario()) 
			existe = existe || df.getClave().equalsIgnoreCase(datosFormulario.getClave());
		if(existe) 
			errores.rejectValue("clave", "DUPLICADO", "Ya existe una clave con el valor que se intenta añadir");
		
		if(errores.hasErrors())
			return formulario(diccionario, datosFormulario);
		
		// insertamos el dato
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				TipoDirectivaDiccionarioDefinicion definicion = new TipoDirectivaDiccionarioDefinicion();
				definicion.setIdDiccionario(diccionario.getId());
				definicion.setClave(datosFormulario.getClave());
				definicion.setEtiqueta(datosFormulario.getEtiqueta());
				definicion.setPosicion(Integer.parseInt(datosFormulario.getPosicion()));
				definicion.setPatronControl(datosFormulario.getPatronControl());
				definicion.setBloqueDescripcion(datosFormulario.getBloqueDescripcion());
				ce.definicionesDiccionarios().saveAndFlush(definicion);
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "NUEVA CLAVE '" + datosFormulario.getClave() + "' DEL DICCIONARIO DE DIRECTIVA '" + diccionario.getDescripcion() + "'";
			}
		}.ejecutar();
		
		// volvemos a los datos
		return new ModelAndView(Paths.Admin.Diccionarios.redirectDatos(diccionario));
	}
	
	
	// -----
	
	private ModeloPagina formulario(TipoDirectivaDiccionario diccionario, FormularioClaveDiccionarioDirectiva datos) {
		
		FormularioClaveDiccionarioDirectiva enviar = datos != null ? datos : new FormularioClaveDiccionarioDirectiva();
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Nueva clave diccionario para directivas")
				.conComponentes(
						Paths.Admin.Diccionarios.Definiciones.localizacion(diccionario)
						.conTexto("Nueva clave"),
						
						new Formulario()
						.urlAceptacion(Paths.Admin.Diccionarios.Definiciones.nueva(diccionario))
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(FormularioClaveDiccionarioDirectiva.class)
						)
						.botoneraEstandar(Paths.Admin.Diccionarios.datos(diccionario))
				)
		)
		.conModelo("datos", enviar);
	}	
}
