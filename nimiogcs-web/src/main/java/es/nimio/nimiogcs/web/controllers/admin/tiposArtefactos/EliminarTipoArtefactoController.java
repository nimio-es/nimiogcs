package es.nimio.nimiogcs.web.controllers.admin.tiposArtefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.tiposArtefactos.ConfirmarEliminacion;
import es.nimio.nimiogcs.web.dto.f.admin.tiposArtefactos.FichaTipoArtefactoBuilder;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping("/admin/tipos")
public class EliminarTipoArtefactoController {

private IContextoEjecucion ce;
	
	@Autowired
	public EliminarTipoArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	
	// ------------------------------------------------
	// Eliminar el artefacto
	// ------------------------------------------------

	// 1. presentamos la entidad para confirmar que se quiere eliminar
	@RequestMapping(path="/eliminar/{id}", method=GET)
	public ModeloPagina eliminar(@PathVariable String id) {
		
		TipoArtefacto entidad = leerEntidadYComprobarPosibleEliminacion(id);
		if(entidad==null)throw new ErrorEntidadNoEncontrada();
		
		return PaginaEliminarArtefacto(entidad)
				.conModelo("confirmacion", new ConfirmarEliminacion(entidad));
	}
	
	
	// 2. eliminamos
		@RequestMapping(path="/eliminar", method=POST)
		public String eliminar(@ModelAttribute("confirmacion") ConfirmarEliminacion confirmacion) {
			
			final TipoArtefacto entidad = leerEntidadYComprobarPosibleEliminacion(confirmacion.getIdTipo());
			
			new OperacionInternaInlineModulo(ce) {
				
				@Override
				protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {

					for(RelacionOperacion ropt: ce.relacionesOperaciones().operacionesDeUnArtefacto(entidad.getId())) {
						
						// indicamos las intenciones en la operación
						Operacion opp = ropt.getOperacion();
						opp.setMensajesEjecucion(
								(Strings.isNotEmpty(opp.getMensajesEjecucion()) ? opp.getMensajesEjecucion() : "")
								+ "\n\n["
								+ DateTimeUtils.formaReducida(new Date())
								+ "][EXT] Eliminar relación con tipo artefacto '" + entidad.getId() + "'\n\n" 
						);
						ce.operaciones().saveAndFlush(opp);
						
						// ya podemos eliminar la relación
						ce.relacionesOperaciones().delete(ropt);
					}
					
					// recogemos las directivas para luego poder borrarlas
					final ArrayList<DirectivaBase> directivasTipo = new ArrayList<DirectivaBase>(entidad.getDirectivasTipo()); 
					
					// se elimina la entidad
					ce.tipos().delete(entidad.getId());
					
					// y podemos eliminar las directivas asociadas
					for(DirectivaBase db: directivasTipo)
						ce.directivas().delete(db);
					
					return true;
				}
				
				@Override
				protected String generaNombreUnico() {
					return "ELIMINAR TIPOS DE ARTEFACTOS" + "(' " + entidad.getId() + " ')";
				}
			}.ejecutar();
			
			
			return "redirect:/admin/tipos";
		}


	private ModeloPagina PaginaEliminarArtefacto(TipoArtefacto  tipo) {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Eliminar tipo artefacto " + tipo.getId())
					.conComponentes(
							new Localizacion()
							.conEnlace("Home", "/")
							.conTexto("Administración")
							.conEnlace("Tipos artefactos", "/admin/tipos")
							.conEnlace(tipo.getNombre(), "/admin/tipos/" + tipo.getId())
							.conTexto("ELIMINAR")
					)
					.conComponentes(
						new PanelInformativo()
						.tipoPeligro()
						.conTexto(
							"Se dispone a eliminar el siguiente Tipo de artefacto. Esta operación es irreversible."
						)
			       ) 
			      .conComponentes(
			    		     FichaTipoArtefactoBuilder.componenteFichaGeneral(tipo)
			    		  )
			      .conComponentes(
			    		 		new Formulario(
			    		 				"confirmacion", 
			    		 				"/admin/tipos/eliminar", 
			    						"POST", 
			    						"/admin/tipos/" + tipo.getId(), 
			    						AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
			    								ConfirmarEliminacion.class	
			    						)
			    				  )
				   )
		 );
	}
	
	// -------------------------------------------------
	// Métodos privados
	// -------------------------------------------------

	private TipoArtefacto leerEntidadYComprobarPosibleEliminacion(String id) {

		// -- recogemos la entidad
		TipoArtefacto entidad = ce.tipos().findOne(id);
		if(entidad==null)throw new ErrorEntidadNoEncontrada();
		
		// -- confirmamos que no haya dependencias que la referencien
		if(FichaTipoArtefactoBuilder.totalArtefactosTipoArtefacto(ce, id) > 0)
			throw new ErrorIntentoOperacionInvalida();
		
		return entidad;
	}	
}
