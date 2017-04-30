package es.nimio.nimiogcs.web.controllers.proyectos.artefactos;

import java.util.HashMap;
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

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.operaciones.proyecto.QuitarRelacionarProyectoYArtefacto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/artefactos/quitar")
@SessionAttributes(names="estado")
public class QuitarArtefactoDeProyectoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public QuitarArtefactoDeProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(path="/{idRelacion}", method=RequestMethod.GET)
	public ModelAndView quitar(
			@PathVariable("idRelacion") String idRelacion) {
		
		// confirmamos, además, que la relación entre proyecto y artefacto también existe
		RelacionElementoProyectoArtefacto relacion = ce.relacionesProyectos().findOne(idRelacion);
		if(relacion==null) throw new ErrorEntidadNoEncontrada();
		
		// comprobamos que existe el proyecto
		Proyecto proyecto = (Proyecto)relacion.getElementoProyecto();
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();
		
		// pasamos el artefacto de la relación a una variable para tenerlo "más cerca"
		Artefacto artefacto = relacion.getArtefacto();
		
		// preparamos el formulario de confirmación
		String control = UUID.randomUUID().toString();
		String textoConfirmacion = "Quitar la relación entre el proyecto '" + proyecto.getNombre() + "' y el artefacto '";
		if(artefacto instanceof ITestaferroArtefacto) 
			textoConfirmacion += ((ITestaferroArtefacto)artefacto).getArtefactoAfectado().getNombre() + "' (como un evolutivo)";
		else
			textoConfirmacion += artefacto.getNombre() + "'";
		HashMap<String, String> estado = new HashMap<String, String>();
		estado.put("control", control);
		estado.put("idrel", idRelacion);
		
		// devolvemos la página de confirmación
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Quitar relación proyecto con artefacto")
				.conComponentes(
						
						// la licalización
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Proyectos", "/proyectos")
						.conEnlace(proyecto.getNombre(), "/proyectos/" + proyecto.getId())
						.conTexto("Artefactos afectados")
						.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
						.conTexto("Eliminar relación"),
						
						// el formulario de confirmación
						new Formulario()
						.urlAceptacion("/proyectos/artefactos/quitar")
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
						)
						.botoneraEstandar("/proyectos/" + proyecto.getId())
				)
		)
		.conModelo("estado", estado)
		.conModelo("datos", 
				new PeticionConfirmacionGeneral(
						control, 
						textoConfirmacion
				)
		);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String quitar(
			@ModelAttribute("datos") PeticionConfirmacionGeneral peticion, 
			@ModelAttribute("estado") HashMap<String, String> estado, 
			SessionStatus ss) {
		
		// confirmamos, además, que la relación entre proyecto y artefacto también existe
		RelacionElementoProyectoArtefacto relacion = ce.relacionesProyectos().findOne(estado.get("idrel"));
		if(relacion==null) throw new ErrorEntidadNoEncontrada();

		
		try {
			
			// si el valor de control de la petición no coincide con el del estado... ¡cachapop!
			if(!peticion.getCodigoControl().equalsIgnoreCase(estado.get("control")))
				throw new ErrorInconsistenciaDatos("No hay un valor de control conforme a lo esperado");
				
			// en caso contrario sí que podemos dar por finalizada la relación contractual entre partes
			new QuitarRelacionarProyectoYArtefacto(ce)
			.ejecutarCon(
					new QuitarRelacionarProyectoYArtefacto.PeticionEliminacionRelacion(relacion)
			);
			
		} finally {
			ss.setComplete();
		}
		
		return "redirect:/proyectos/" + relacion.getElementoProyecto().getId();
	}
}
