package es.nimio.nimiogcs.web.controllers.artefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/artefactos")
public class CambiosActividadArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public CambiosActividadArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Aceptar una entidad inválida
	// ------------------------------------------------

	@RequestMapping(path="/validar/{id}", method=GET)
	public String aceptarValidez(
			@PathVariable final String id) {

		final Artefacto artefacto = buscarEntidad(id);
		
		// ejecución en línea
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				artefacto.setEstadoValidez(true);
				ce.artefactos().saveAndFlush(artefacto);

				ce.relacionesOperaciones().saveAndFlush(
						new RelacionOperacionArtefacto(op, artefacto)
				);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "ACEPTAR VALIDEZ '" + artefacto.getNombre() + "'";
			}
			
		}.ejecutar();
		
		return redirigir(artefacto);
	}
	
	
	// ------------------------------------------------
	// Activar una entidad válida pero inactiva
	// ------------------------------------------------

	@RequestMapping(path="/activar/{id}", method=GET)
	public String activar(@PathVariable final String id) {

		final Artefacto artefacto = buscarEntidad(id);

		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				artefacto.setEstadoActivacion(true);
				ce.artefactos().saveAndFlush(artefacto);

				ce.relacionesOperaciones().saveAndFlush(
						new RelacionOperacionArtefacto(op, artefacto)
				);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "HABILITAR '" + artefacto.getNombre() + "'";
			}
		}.ejecutar();
		
		return redirigir(artefacto);
	}
	
	
	// ------------------------------------------------
	// Desactivar una entidad válida y activa
	// ------------------------------------------------

	@RequestMapping(path="/desactivar/{id}", method=GET)
	public String desactivar(@PathVariable final String id) {

		final Artefacto artefacto = buscarEntidad(id);

		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {

				artefacto.setEstadoActivacion(false);
				ce.artefactos().saveAndFlush(artefacto);
				
				ce.relacionesOperaciones().saveAndFlush(
						new RelacionOperacionArtefacto(op, artefacto)
				);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "INHABILITAR '" + artefacto.getNombre() + "'";
			}
		}.ejecutar();
		
		return redirigir(artefacto);
	}
	
	// -------------------------------------------------
	// privados
	// -------------------------------------------------

	/**
	 * Mismo método para todas las operaciones y así lanzamos la excepción solamente una vez
	 */
	private Artefacto buscarEntidad(String id) {
		
		Artefacto entidad = ce.artefactos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		return (Artefacto)entidad;
	}
	
	private String redirigir(Artefacto entidad) {
		
		return "redirect:/artefactos/" + entidad.getId();
	}
}
