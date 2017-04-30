package es.nimio.nimiogcs.web.controllers.proyectos.etiquetas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.subtareas.proyecto.etiqueta.QASonarEtiquetaUsandoJenkins;

@Controller
@RequestMapping(path="/proyectos/op/qa/sonar/etiqueta")
public class LaunchTagSonarQAController {

	private final IContextoEjecucionBase ce;
	
	@Autowired
	public LaunchTagSonarQAController(final IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// --
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public String launch(@PathVariable("id") final String id) {
		
		// cargar la etiqueta
		EtiquetaProyecto tag = ce.repos().elementosProyectos().etiquetas().buscar(id);
		if(tag==null)
			throw new ErrorInconsistenciaDatos("Entidad no encontrada");
		
		// se lanza la tarea
		new QASonarEtiquetaUsandoJenkins(ce)
		.ejecutarCon(tag);
		
		return "redirect:/proyectos/etiquetas/etiqueta/" + id;
	}
	
}
