package es.nimio.nimiogcs.web.controllers.artefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.operaciones.artefactos.EliminarArtefacto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.artefactos.ConfirmarEliminacion;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaEliminarArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping("/artefactos/eliminar")
public class EliminarArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public EliminarArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Eliminar el artefacto
	// ------------------------------------------------

	// 1. presentamos la entidad para confirmar que se quiere eliminar
	@RequestMapping(path="/{id}", method=GET)
	public ModeloPagina eliminar(@PathVariable String id) {
		
		Artefacto entidad = leerEntidadYComprobarPosibleEliminacion(id);
		
		return ModeloPagina.nuevaPagina(
				new PaginaEliminarArtefacto(entidad)
			)
			.conModelo("confirmacion", new ConfirmarEliminacion(entidad));
	}
	
	// 2. eliminamos
	@RequestMapping(method=POST)
	public String eliminar(@ModelAttribute("confirmacion") ConfirmarEliminacion confirmacion) {
		
		Artefacto entidad = leerEntidadYComprobarPosibleEliminacion(confirmacion.getIdArtefacto());
		
		new EliminarArtefacto<Artefacto>(ce).ejecutar(entidad);
		
		return "redirect:/artefactos";
	}
	
	
	// -------------------------------------------------
	// Métodos privados
	// -------------------------------------------------

	private Artefacto leerEntidadYComprobarPosibleEliminacion(String id) {

		// -- recogemos la entidad
		Artefacto entidad = ce.artefactos().findOne(id);
		if(entidad==null)throw new ErrorEntidadNoEncontrada();
		
		// -- confirmamos que no haya dependencias que la referencien
		if(ce.dependenciasArtefactos().totalRelacionesDependenciaConDestinoElArtefacto(id) > 0)
			throw new ErrorIntentoOperacionInvalida();
		
		// -- confirmamos que no tengamos proyectos asociados
		if(ce.relacionesProyectos().existeProyectoAfectaArtefacto(id) != 0)
			throw new ErrorIntentoOperacionInvalida();
		
		return entidad;
	}
}
