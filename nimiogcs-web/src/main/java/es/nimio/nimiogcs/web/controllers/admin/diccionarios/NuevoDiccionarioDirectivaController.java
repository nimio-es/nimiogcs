package es.nimio.nimiogcs.web.controllers.admin.diccionarios;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.diccionarios.FormularioDiccionarioDirectiva;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping(path=Paths.Admin.Diccionarios.NUEVO)
public class NuevoDiccionarioDirectivaController {

	private IContextoEjecucion ce;
	
	@Autowired
	public NuevoDiccionarioDirectivaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView nuevo() {
		return formulario(null);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView nuevo(
			@ModelAttribute("datos") @Valid final FormularioDiccionarioDirectiva datosFormulario,
			Errors errores) {
		
		// comprobamos si existe ya algún diccionario con el identificador
		if(Strings.isNotEmpty(datosFormulario.getId()))
			if(ce.diccionariosDirectivas().exists(datosFormulario.getId().toUpperCase()))
				errores.rejectValue("id", "DIPLICADO", "Existe un diccionario con el identificador indicado.");
		
		// si hay errores, volvemos al formulario
		if(errores.hasErrors()) 
			return formulario(datosFormulario);
		
		// pues ya podemos crear un nuevo diccionario
		final TipoDirectivaDiccionario diccionario = new TipoDirectivaDiccionario();
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				diccionario.setId(datosFormulario.getId().toUpperCase());
				diccionario.setDescripcion(datosFormulario.getDescripcion());
				ce.diccionariosDirectivas().saveAndFlush(diccionario);
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "CREAR NUEVO DICCIONARIO '" + datosFormulario.getId().toUpperCase() + "' PARA DIRECTIVAS";
			}
		}.ejecutar();
		
		// y redirijimos a la vista de datos del diccionario
		return new ModelAndView(Paths.Admin.Diccionarios.redirectDatos(diccionario));
	}

	
	// -----
	
	private ModeloPagina formulario(FormularioDiccionarioDirectiva datos) {
		
		FormularioDiccionarioDirectiva enviar = datos != null ? datos : new FormularioDiccionarioDirectiva();
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Nuevo diccionario para directivas")
				.conComponentes(
						new Localizacion()
						.conItem(Paths.TO_HOME)
						.conItem(Paths.Admin.TO_ADMIN)
						.conItem(Paths.Admin.Diccionarios.TO_DICCIONARIOS)
						.conTexto("Nuevo"),
						
						new Formulario()
						.urlAceptacion(Paths.Admin.Diccionarios.NUEVO)
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(FormularioDiccionarioDirectiva.class)
						)
						.botoneraEstandar(Paths.Admin.Diccionarios.BASE)
				)
		)
		.conModelo("datos", enviar);
	}
}
