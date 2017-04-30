package es.nimio.nimiogcs.web.controllers.maven.proyectos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.maven.subtareas.proyecto.RecrearArchivosPomProyeccionMavenProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/op/maven/recrear")
@SessionAttributes("control")
public class OperacionRecrearEstructuraMavenController {

	private IContextoEjecucion ce;
	
	@Autowired
	public OperacionRecrearEstructuraMavenController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModelAndView inicio(@PathVariable("id") String idElemento) {
		
		// comprobamos que el proyecto o la etiqueta existen
		ElementoBaseProyecto elemento = ce.proyectos().findOne(idElemento);
		if(elemento == null) elemento = ce.etiquetas().findOne(idElemento);
		if(elemento == null) throw new ErrorEntidadNoEncontrada();
		
		// confirmamos también que tenga un uso Maven
		if(!tieneUsoMaven(elemento)) throw new ErrorEntidadNoEncontrada();
		
		// datos de control
		Map<String, String> control = new HashMap<String, String>(); 
		control.put("CONTROL", UUID.randomUUID().toString());
		control.put("ID-ELEMENTO", idElemento);
		
		// creamos la estrucutra de página para la confirmación
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Confirmar regenerar estructura Maven")
				.conComponentes(
						localizacion(elemento),
						new Parrafo("Se procederá a regenerar la estructura Maven. Para que surta efecto, se requiere la actualización en la copia de trabajo una vez concluya la tarea correspondiente."),
						new Parrafo(" "),
						new Formulario()
						.urlAceptacion("/proyectos/op/maven/recrear")
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
						)
						.botoneraEstandar("/proyectos/" + idElemento)
				)
		)
		.conModelo("control", control)
		.conModelo(
				"datos",
				new PeticionConfirmacionGeneral(
						control.get("CONTROL"),
						"Regenerar la estructura Maven " 
						+ (elemento instanceof Proyecto ? "del proyecto" : "de la etiqueta")
						+ " '" + elemento.getNombre() + "'"
				)
		);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView confirmacion(
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion,
			@ModelAttribute("control") Map<String, String> control,
			SessionStatus ss) {
		
		if(!control.get("CONTROL").equalsIgnoreCase(confirmacion.getCodigoControl())) 
			throw new ErrorInesperadoOperacion("Código de control incorrecto");

		String idElemento = control.get("ID-ELEMENTO");
		ElementoBaseProyecto elemento = ce.proyectos().findOne(idElemento);
		if(elemento==null) elemento = ce.etiquetas().findOne(idElemento);
		
		// lanzamos la tarea
		new RecrearArchivosPomProyeccionMavenProyecto(ce)
			.ejecutarCon(elemento);
		
		// cerramos la operación
		ss.setComplete();
		
		// volvemos al proyecto
		if(elemento instanceof Proyecto) return new ModelAndView("redirect:/proyectos/" + idElemento);
		else return new ModelAndView("redirect:/proyectos/etiquetas/etiqueta/" + idElemento);
	}
	
	// ------------------
	// Auxiliares
	// ------------------

	private Localizacion localizacion(ElementoBaseProyecto elemento) {
		Localizacion l = new Localizacion()
				.conEnlace("Home", "/")
				.conEnlace("Proyectos", "/proyectos");
				
		if(elemento instanceof Proyecto) l.conEnlace(elemento.getNombre(), "/proyectos/" + elemento.getId());
		else {
			EtiquetaProyecto etiqueta = (EtiquetaProyecto)elemento;
			l.conEnlace(etiqueta.getProyecto().getNombre(), "/proyectos/" + etiqueta.getProyecto().getId())
			.conEnlace("Etiquetas", "/proyectos/etiquetas/" + etiqueta.getProyecto().getId())
			.conEnlace(etiqueta.getNombre(), "/proyectos/etiquetas/etiqueta/" + etiqueta.getId());
		}
		
		return l.conTexto("Regenerar estructura Maven");
	}
	
	public boolean tieneUsoMaven(ElementoBaseProyecto elemento) {
		for(UsoYProyeccionProyecto uso: elemento.getUsosYProyecciones())
			if(uso instanceof ProyeccionMavenDeProyecto) return true;
		return false;
	}
}

