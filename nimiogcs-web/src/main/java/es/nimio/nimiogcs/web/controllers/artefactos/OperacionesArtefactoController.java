package es.nimio.nimiogcs.web.controllers.artefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.controllers.utiles.UtilidadCargarUsuariosPaginaOperaciones;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaOperacionesArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/artefactos/operaciones")
public class OperacionesArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public OperacionesArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private final static int NUMERO_REGISTROS_POR_PAGINA = 15;

	// ------------------------------------------------
	// Listado
	// ------------------------------------------------

	@RequestMapping(path="{id}", method=GET)
	public ModeloPagina operaciones(
			@PathVariable String id,
			@RequestParam(required=false, value="pag", defaultValue="1") Integer pag) {
	
		// primero es confirmar que existe una entidad con el id 
		Artefacto entidad = ce.artefactos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		// una vez encontrada la entidad, procedemos a preparar y lanzar la consulta
		Pageable peticion = new PageRequest(
				pag - 1, 
				NUMERO_REGISTROS_POR_PAGINA, 
				Direction.DESC, 
				"tiempoInicio");
		
		Page<Operacion> operaciones = ce.operaciones().operacionesDeArtefacto(peticion, entidad.getId());
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(
				new PaginaOperacionesArtefacto(
						entidad,
						ce.operaciones().artefactoConOperacionesEnCurso(entidad.getId()) > 0,
						operaciones,
						UtilidadCargarUsuariosPaginaOperaciones.cargarUsuariosPagina(ce, operaciones))
		);
	}
	
}
