package es.nimio.nimiogcs.web.controllers.proyectos.etiquetas;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosPublicados;
import es.nimio.nimiogcs.jpa.specs.ProyectosEtiquetas;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.proyectos.PaginaEtiquetasProyecto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/etiquetas")
public class EtiquetasProyectoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public EtiquetasProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// --------------------------------------
	// Lista
	// --------------------------------------
	
	@RequestMapping(path="/{idProyecto}", method=RequestMethod.GET)
	public ModelAndView etiquetas(
			@PathVariable("idProyecto") String idProyecto,
			@RequestParam(name="pag", required=false, defaultValue="1") Integer pag) {
		
		// cargamos el proyecto
		Proyecto proyecto = ce.proyectos().findOne(idProyecto);
		if(proyecto==null) throw new ErrorEntidadNoEncontrada();

		// cargamos las etiquetas
		Page<EtiquetaProyecto> etiquetas = cargarEtiquetas(proyecto, pag);

		// creamos la página
		return ModeloPagina.nuevaPagina(
				new PaginaEtiquetasProyecto(
						proyecto, 
						tieneOperaciones(proyecto), 
						listaEtiquetasPagina(etiquetas),
						pag,
						etiquetas.getTotalPages()
				)
		);
	}
	
	// --------------------------------------
	// Utilidades
	// --------------------------------------

	private Collection<PaginaEtiquetasProyecto.ItemListaEtiquetas> listaEtiquetasPagina(Page<EtiquetaProyecto> etiquetas) {

		ArrayList<PaginaEtiquetasProyecto.ItemListaEtiquetas> items = new ArrayList<PaginaEtiquetasProyecto.ItemListaEtiquetas>();
		
		for(EtiquetaProyecto etiqueta: etiquetas.getContent()) {
			
			items.add(
					new PaginaEtiquetasProyecto.ItemListaEtiquetas(
							etiqueta, 
							!ce.artefactosPublicados().findAll(ArtefactosPublicados.elementosPublicadosEtiqueta(etiqueta)).isEmpty()
					)
			);
		}

		return items;
	}
	
	private Page<EtiquetaProyecto> cargarEtiquetas(Proyecto proyecto, int pagina) {
		
		Pageable peticion = new PageRequest(
				pagina - 1, 
				10, 
				Direction.DESC, 
				"creacion");
		
		return ce.etiquetas()
				.findAll(
						ProyectosEtiquetas.etiquetasDeProyecto(proyecto),
						peticion
				);
	}
	
	/**
	 * Devuelve si el proyecto tiene operaciones
	 */
	private boolean tieneOperaciones(Proyecto proyecto) {
		return ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) != 0;
	}
}
