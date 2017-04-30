package es.nimio.nimiogcs.web.controllers.proyectos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.FormularioEditarAnotaciones;
import es.nimio.nimiogcs.web.dto.p.proyectos.PaginaAnotacionesProyecto;
import es.nimio.nimiogcs.web.dto.p.proyectos.PaginaEditarAnotacionesProyecto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/anotaciones")
public class AnotacionesProyectoController {

	private final IContextoEjecucion ce;
	
	@Autowired
	public AnotacionesProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModelAndView show(@PathVariable("id") String id) {

		// cargamos el proyecto
		Proyecto proyecto = ce.proyectos().findOne(id);
		if(proyecto == null) throw new ErrorEntidadNoEncontrada();
		
		return ModeloPagina.nuevaPagina(
				new PaginaAnotacionesProyecto(
						proyecto, 
						ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) != 0
				)
		);
	}
	
	@RequestMapping(path="/{id}/editar", method=RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		
		// cargamos el proyecto
		Proyecto proyecto = ce.proyectos().findOne(id);
		if(proyecto == null) throw new ErrorEntidadNoEncontrada();
		
		return 
				ModeloPagina.nuevaPagina(
						new PaginaEditarAnotacionesProyecto(
								proyecto, 
								ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) != 0
						)
				)
				.conModelo("datos", new FormularioEditarAnotaciones(proyecto));
	}
	
	@RequestMapping(path="/{id}/aceptar", method=RequestMethod.POST)
	public String aceptar(
			@PathVariable("id") String id, 
			@ModelAttribute("datos") final FormularioEditarAnotaciones formulario) {
		
		// cargamos el proyecto
		final Proyecto proyecto = ce.proyectos().findOne(id);
		if(proyecto == null) throw new ErrorEntidadNoEncontrada();
		
		// cargamos las anotaciones
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected void relacionarOperacionConEntidades(Boolean datos, Operacion op) {
				registraRelacionConOperacion(op, proyecto);
			}
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
				proyecto.setAnotaciones(formulario.getMarkdown());
				ce.proyectos().saveAndFlush(proyecto);
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "CAMBIOS EN EL REGISTRO DE ANOTACIONES DEL PROYECTO '" + proyecto.getNombre() + "'";
			}
		}.ejecutar();
		
		return "redirect:/proyectos/anotaciones/" + id;
	}
	
}
