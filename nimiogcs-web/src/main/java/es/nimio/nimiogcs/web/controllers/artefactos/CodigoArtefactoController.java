package es.nimio.nimiogcs.web.controllers.artefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaCodigoArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/artefactos/codigo")
public class CodigoArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public CodigoArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Listado
	// ------------------------------------------------

	@RequestMapping(path="{id}", method=GET)
	public ModeloPagina codigo(@PathVariable String id) {
	
		// primero es confirmar que existe una entidad con el id 
		Artefacto artefacto = ce.artefactos().findOne(id);
		
		// cogemos, también, la directiva de código
		DirectivaRepositorioCodigo d = P.of(artefacto).repositorioCodigo();
		
		if(artefacto==null || d==null) throw new ErrorEntidadNoEncontrada();
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(
				new PaginaCodigoArtefacto(
						artefacto, 
						ce.operaciones().artefactoConOperacionesEnCurso(artefacto.getId()) > 0,
						ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesDeUnArtefactoConProyectos(artefacto)))
		);
	}
	
}
