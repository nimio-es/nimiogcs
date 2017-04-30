package es.nimio.nimiogcs.web.controllers.proyectos.etiquetas;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.componentes.proyecto.web.DefinicionOperacionPosible;
import es.nimio.nimiogcs.componentes.proyecto.web.IOperacionesPosiblesSobreProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.etiquetas.PaginaDatosEtiqueta;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/etiquetas/etiqueta")
public class DatosEtiquetaController {

	private IContextoEjecucion ce;

	@Autowired
	public DatosEtiquetaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -------------------------------------------------
	// Datos
	// -------------------------------------------------

	@RequestMapping(path="/{id}")
	public ModelAndView datos(@PathVariable String id) {
		
		EtiquetaProyecto etiqueta = ce.etiquetas().findOne(id);
		if(etiqueta == null) throw new ErrorEntidadNoEncontrada();
		
		return 
				ModeloPagina.nuevaPagina(
						new PaginaDatosEtiqueta(
						etiqueta, 
						ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(etiqueta)),
						tieneOperaciones(etiqueta),
						operacionesAdicionales(etiqueta))
				);
	}
	
	
	// -------------------------------------------------
	// Auxiliares
	// -------------------------------------------------

	/**
	 * Devuelve si el proyecto tiene operaciones
	 */
	private boolean tieneOperaciones(EtiquetaProyecto etiqueta) {
		return ce.operaciones().artefactoConOperacionesEnCurso(etiqueta.getId()) != 0;
	}
	
	/**
	 * Recupera el resto de operacines que se pueden realizar sobre un proyecto
	 */
	private Collection<DefinicionOperacionPosible> operacionesAdicionales(EtiquetaProyecto etiqueta) {
		
		final ArrayList<DefinicionOperacionPosible> ops = new ArrayList<DefinicionOperacionPosible>();
		
		for(IOperacionesPosiblesSobreProyecto proveedores: 
			ce.contextoAplicacion().getBeansOfType(IOperacionesPosiblesSobreProyecto.class).values()) {
			
			ops.addAll(proveedores.defineAcciones(etiqueta));
		}
		
		return ops;
	}
}
