package es.nimio.nimiogcs.web.controllers.operaciones;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.operaciones.EditarEstadoOperacionForm;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/operaciones/terminar")
public class EditarOperacionController {

	private IContextoEjecucion ce;

	@Autowired
	public EditarOperacionController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// --
	
	// ---------------------------------------------------
	// Edición
	// ---------------------------------------------------
	
	@RequestMapping(path="/{id}", method=GET)
	public ModeloPagina editar(@PathVariable String id) {
		
		Operacion entidad = ce.operaciones().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		
		return paginaParaRegistro(entidad)
				.conModelo(
						"datos", 
						new EditarEstadoOperacionForm(id)
				);
	}
	
	
	@RequestMapping(method=POST)
	public ModelAndView editar(
			@ModelAttribute("datos") @Valid final EditarEstadoOperacionForm datos,
			Errors errores) {

		// buscamos el artefacto
		final Operacion entidad = ce.operaciones().findOne(datos.getIdOperacion());
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
				
		if(errores.hasErrors()) {
			return paginaParaRegistro(entidad).conModelo("datos", datos); 
		}
       
		 new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean d, Operacion op) {
				entidad.setMensajeError(datos.getTexto());
				entidad.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
				entidad.setFinalizado(true);
				ce.operaciones().saveAndFlush(entidad);
				return true;
			}
	
			@Override
			protected String generaNombreUnico() {
				
				return ("CERRAR OPERACION  " + "(' " + entidad.getId() + " ')");
			}
		}.ejecutar();
        	 
      	
		// redirigimos a la página del artefacto
		return new ModelAndView("redirect:/operaciones/" + entidad.getId());
	}
	
	private ModeloPagina paginaParaRegistro(Operacion operacion) {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Editar datos de operación")
					.conComponentes(
							
							new Localizacion()
							.conEnlace("Home", "/")
							.conEnlace("Operaciones", "/operaciones")
							.conEnlace(operacion.getId(), "/operaciones/" + operacion.getId())
							.conTexto("Editar"),
							
							new PanelInformativo()
							.tipoAviso()
							.conTexto(
									"La edición manual de una operación conlleva indicar que la misma acaba con error."
							),
							
							new Formulario()
							.urlAceptacion("/operaciones/terminar")
							.conComponentes(
									AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(EditarEstadoOperacionForm.class)
							)
							.botoneraEstandar("/operaciones/" + operacion.getId())
					)
				);
	}
}
