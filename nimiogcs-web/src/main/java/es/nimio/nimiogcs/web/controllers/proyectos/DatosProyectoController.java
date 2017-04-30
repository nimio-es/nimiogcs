package es.nimio.nimiogcs.web.controllers.proyectos;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.componentes.proyecto.web.DefinicionOperacionPosible;
import es.nimio.nimiogcs.componentes.proyecto.web.IOperacionesPosiblesSobreProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.proyectos.PaginaDatosProyecto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos")
public class DatosProyectoController {

	private IContextoEjecucion ce;

	@Autowired
	public DatosProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -------------------------------------------------
	// Datos
	// -------------------------------------------------

	@RequestMapping(path="/{id}")
	public ModelAndView datos(@PathVariable String id) {
		
		Proyecto proyecto = ce.proyectos().findOne(id);
		if(proyecto == null) throw new ErrorEntidadNoEncontrada();
		
		return 
				ModeloPagina.nuevaPagina(
						new PaginaDatosProyecto(
						proyecto, 
						ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto)),
						tieneEtiquetas(proyecto),
						tieneOperaciones(proyecto),
						operacionesAdicionales(proyecto))
				);
	}
	
	
	// -------------------------------------------------
	// Auxiliares
	// -------------------------------------------------

	/**
	 * Devuelve si el proyecto tiene operaciones
	 */
	private boolean tieneOperaciones(Proyecto proyecto) {
		return ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) != 0;
	}
	
	/**
	 * Devuelve si un proyecto ya ha sido etiquetado
	 */
	private boolean tieneEtiquetas(final Proyecto proyecto) {
		return ce.etiquetas().findAll(
				new Specification<EtiquetaProyecto>() {

					@Override
					public Predicate toPredicate(Root<EtiquetaProyecto> root, CriteriaQuery<?> cq,
							CriteriaBuilder cb) {
						
						return cb.equal(root.get("proyecto").get("id"), proyecto.getId());
					}
					
				}
		).size() > 0;
	}
	
	/**
	 * Recupera el resto de operacines que se pueden realizar sobre un proyecto
	 */
	private Collection<DefinicionOperacionPosible> operacionesAdicionales(Proyecto proyecto) {
		
		final ArrayList<DefinicionOperacionPosible> ops = new ArrayList<DefinicionOperacionPosible>();
		
		for(IOperacionesPosiblesSobreProyecto opsProvider: ce.componentes(IOperacionesPosiblesSobreProyecto.class)) {
			ops.addAll(opsProvider.defineAcciones(proyecto));
		}
		
		return ops;
	}
}
