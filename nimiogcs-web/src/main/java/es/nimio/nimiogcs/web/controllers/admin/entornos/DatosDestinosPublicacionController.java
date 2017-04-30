package es.nimio.nimiogcs.web.controllers.admin.entornos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.admin.entornos.PaginaDatosEntornos;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/ciclovida/entornos")
public class DatosDestinosPublicacionController {

	private IContextoEjecucion ce;
	
	@Autowired
	public DatosDestinosPublicacionController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ************************************************
	
	// ------------------------------------------
	// Datos
	// ------------------------------------------

	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModeloPagina read(@PathVariable String id) {

		// buscamos la entidad
		DestinoPublicacion entorno = ce.destinosPublicacion().findOne(id);
		if(entorno == null) throw new ErrorEntidadNoEncontrada();
		
		// vamos preparando la vista y el model
		return 
				ModeloPagina.nuevaPagina(
						new PaginaDatosEntornos(
								entorno, 
								ce.operaciones().artefactoConOperacionesEnCurso(entorno.getId()) != 0
						)
				);
	}

}
