package es.nimio.nimiogcs.web.controllers.proyectos.etiquetas;

import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.operaciones.proyecto.etiquetas.NuevaEtiquetaDeProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.etiquetas.NuevaEtiquetaForm;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/etiquetas/nueva")
@SessionAttributes(names="control")
public class NuevaEtiquetaController {

	private IContextoEjecucion ce;
	
	@Autowired
	public NuevaEtiquetaController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	
	// -----------------------------------------
	// Métodos controler
	// -----------------------------------------
	
	@RequestMapping(path="/{idProyecto}", method=RequestMethod.GET)
	public ModelAndView alta(@PathVariable("idProyecto") String idProyecto) {
		
		// cargamos el proyecto
		Proyecto proyecto = ce.proyectos().findOne(idProyecto);
		if(proyecto == null) throw new ErrorEntidadNoEncontrada();

		// creamos el valor de control
		String control = UUID.randomUUID().toString();
		
		// creamos el formulario
		return ModeloPagina.nuevaPagina(
				formulario(proyecto)
		)
		.conModelo("datos", new NuevaEtiquetaForm(
				control, 
				idProyecto, 
				DateTimeUtils.convertirAFormaYYYYMMDDHHMM(new Date())
				)
		)
		.conModelo("control", control);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView alta(
			@ModelAttribute("datos") @Valid final NuevaEtiquetaForm formulario,
			Errors errores,
			@ModelAttribute("control") String control) {
		
		// cargamos el proyecto
		final Proyecto proyecto = ce.proyectos().findOne(formulario.getIdProyecto());
		if(proyecto == null) throw new ErrorEntidadNoEncontrada();

		// confirmamos el control
		if(!control.equalsIgnoreCase(formulario.getControl()))
			throw new ErrorInconsistenciaDatos("No se ha superado el control!");
		
		// si hay error, repintamos
		if(errores.hasErrors()) {
			return ModeloPagina.nuevaPagina(
					formulario(proyecto)
			)
			.conModelo("datos", formulario)
			.conModelo("control", control);
		}
		
		// lanzamos la creación de la etiqueta
		new NuevaEtiquetaDeProyecto(this.ce)
			.ejecutarCon(Tuples.tuple(proyecto, formulario.getNombre()));
		
		// redirigimos a la pestaña de etiquetas
		return new ModelAndView("redirect:/proyectos/etiquetas/" + proyecto.getId());
	}
	
	@RequestMapping(path="/cancelar/{idProyecto}", method=RequestMethod.GET)
	public String cancelar(@PathVariable("idProyecto") String idProyecto, SessionStatus ss) {
		ss.setComplete();
		return "redirect:/proyectos/etiquetas/" + idProyecto;
	}
	
	// -----------------------------------------
	// Páginas
	// -----------------------------------------
	
	private EstructuraAbstractaPagina formulario(Proyecto proyecto) {
		return new EstructuraPagina("Nueva etiqueta")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Proyectos", "/proyectos")
						.conEnlace(proyecto.getNombre(), "/proyectos/" + proyecto.getId())
						.conEnlace("Etiquetas", "/proyectos/etiquetas2/" + proyecto.getId())
						.conTexto("Nueva"),
						
						new Formulario()
						.urlAceptacion("/proyectos/etiquetas/nueva")
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(NuevaEtiquetaForm.class)
						)
						.botoneraEstandar("/proyectos/etiquetas/nueva/cancelar/" + proyecto.getId())
				);
	}
	
}
