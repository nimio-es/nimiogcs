package es.nimio.nimiogcs.web.controllers.admin.entornos;

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

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.controllers.utiles.UtilidadCargarUsuariosPaginaOperaciones;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.admin.entornos.PaginaOperacionesEntornos;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/ciclovida/entornos/operaciones")
public class OperacionesDestinosPublicacionController {

	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private final static int NUMERO_REGISTROS_POR_PAGINA = 10;

	// ------------------------------------------------
	// Estado
	// ------------------------------------------------
	
	private IContextoEjecucion ce;
	
	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public OperacionesDestinosPublicacionController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ************************************************
	
	// ------------------------------------------
	// Datos
	// ------------------------------------------

	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModelAndView procesos(
			@PathVariable String id,
			@RequestParam(required=false, value="pag", defaultValue="1") Integer pag) {

		
		// primero es confirmar que existe una entidad con el id 
		DestinoPublicacion entorno = ce.destinosPublicacion().findOne(id);
		if(entorno == null) throw new ErrorEntidadNoEncontrada();
		
		// una vez encontrada la entidad, procedemos a preparar y lanzar la consulta
		Pageable peticion = new PageRequest(
				pag - 1, 
				NUMERO_REGISTROS_POR_PAGINA, 
				Direction.DESC, 
				"modificacion");
		
		Page<Operacion> operaciones = ce.operaciones().operacionesDeArtefacto(peticion, entorno.getId());
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(
				new PaginaOperacionesEntornos(
						entorno,
						ce.operaciones().artefactoConOperacionesEnCurso(entorno.getId()) > 0,
						operaciones,
						UtilidadCargarUsuariosPaginaOperaciones.cargarUsuariosPagina(ce, operaciones))
		);		
	}

}
