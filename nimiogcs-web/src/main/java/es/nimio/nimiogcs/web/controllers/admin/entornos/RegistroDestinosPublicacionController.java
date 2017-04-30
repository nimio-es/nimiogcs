package es.nimio.nimiogcs.web.controllers.admin.entornos;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Controller
@RequestMapping("/admin/ciclovida/entornos/register")
public class RegistroDestinosPublicacionController {

	// ------------------------------------------------
	// Estado
	// ------------------------------------------------
	
	private IContextoEjecucion ce;
	
	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public RegistroDestinosPublicacionController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ************************************************
	
	// ------------------------------------------
	// Registro
	// ------------------------------------------

	
	@RequestMapping(path="/", method=RequestMethod.GET)
	public ModelAndView register() {

		ModelAndView mv = new ModelAndView("/admin/ciclovida/entornos/alta");
		mv.addObject("entorno", new DestinoPublicacion());
		
		return mv;
	}
	
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public ModelAndView register(@Valid DestinoPublicacion entorno, Errors errores) {

		if(errores.hasErrors()) {
			ModelAndView mv = new ModelAndView("/admin/ciclovida/entornos/alta");
			mv.addObject("entorno", entorno);
			return mv;
		}
		
		ce.destinosPublicacion().saveAndFlush(entorno);

		return new ModelAndView("redirect:/admin/ciclovida/entornos/" + entorno.getNombre());
	}
}
