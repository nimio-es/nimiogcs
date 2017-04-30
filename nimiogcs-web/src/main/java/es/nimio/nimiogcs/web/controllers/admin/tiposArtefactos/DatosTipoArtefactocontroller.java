package es.nimio.nimiogcs.web.controllers.admin.tiposArtefactos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.specs.Artefactos;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.admin.tipos.PaginaDatosTipoArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/tipos")
public class DatosTipoArtefactocontroller {

	private IContextoEjecucion ce;
	
	@Autowired
	public DatosTipoArtefactocontroller(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ---------------------------------------------------
	// Datos
	// ---------------------------------------------------

	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModeloPagina datos(@PathVariable String id) {
		
		// buscamos la entidad
		TipoArtefacto entidad = ce.tipos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
					
		return ModeloPagina.nuevaPagina(
				new PaginaDatosTipoArtefacto(
						entidad, 
						ce.artefactos().count(Artefactos.artefactosDeUnTipo(entidad)), 
						ce.tiposDirectivas().findAll(new Sort("nombre")))
		);
	}
}
