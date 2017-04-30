package es.nimio.nimiogcs.web.controllers.admin.servidores;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Controller
@RequestMapping("/admin/ciclovida/sitios")
public class RegistroSitiosController {

	// ------------------------------------------------
	// Estado
	// ------------------------------------------------

	private final IContextoEjecucion ce;
	
	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public RegistroSitiosController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------
	// Registro (Servidor)
	// ------------------------------------------
	
	@RequestMapping(path="/register/servidor", method=RequestMethod.GET)
	public ModelAndView registerServidor() {

		ModelAndView mv = new ModelAndView("/admin/ciclovida/sitios/servidor/alta");
		mv.addObject("servidor", new ServidorJava());
		
		return mv;
	}
	
	@RequestMapping(path="/register/servidor", method=RequestMethod.POST)
	@Transactional
	public ModelAndView registerServidor(@Valid @ModelAttribute("servidor") ServidorJava servidor, Errors errores) {

		if(errores.hasErrors()) {
			ModelAndView mv = new ModelAndView("/admin/ciclovida/sitios/servidor/alta");
			mv.addObject("servidor", servidor);
			return mv;
		}

		ce.servidores().saveAndFlush(servidor);

		return new ModelAndView("redirect:/admin/ciclovida/sitios/" + servidor.getId());
	}

}
