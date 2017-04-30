package es.nimio.nimiogcs.web.controllers.artefactos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.operaciones.artefactos.externa.BuscarDependenciasAdicionales;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping(path="/artefactos")
@SessionAttributes(names="estado")
public class RecalculoDependenciasController {

	private IContextoEjecucion ce;
	
	@Autowired
	public RecalculoDependenciasController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	
	@RequestMapping(path="/{idArtefacto}/recalculardependencias", method=RequestMethod.GET)
	public ModelAndView recalcularDependencias(
			@PathVariable("idArtefacto") String id) {
		
		// comprobamos que el artefacto existe
		Artefacto artefacto = ce.artefactos().findOne(id);
		if(artefacto == null) throw new ErrorEntidadNoEncontrada();
		
		// además, el perfil debe ser el de una librería externa
		if(!PT.of(artefacto.getTipoArtefacto()).caracterizacion().getLibreriaExterna()) 
			throw new ErrorIntentoOperacionInvalida();
		
		// preparamos el control y el formulario de aceptación
		String control = UUID.randomUUID().toString();
		Map<String, Serializable> estado = new HashMap<String, Serializable>();
		estado.put("CONTROL", control);
		estado.put("ARTEFACTO", id);
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Recalcular dependencias")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Artefactos", "/artefactos")
						.conEnlaceYParametros(artefacto.getTipoArtefacto().getNombre(), "/artefactos", "tipo="+artefacto.getTipoArtefacto().getId())
						.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
						.conTexto("Recalcular dependencias externas"),
						
						new PanelInformativo()
						.conTexto(
								"Si confirma la operación, se lanzará un proceso que intentará localizar y añadir al inventario de artefactos todas las "
								+ "dependencias externas de las que dependen el artefacto actual."
						)
						.tipoInfo(),
						
						new Formulario()
						.urlAceptacion("/artefactos/" + artefacto.getId() + "/recalculardependencias")
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
						)
						.botoneraEstandar("/artefactos/recalculardependencias/cancelar")
				)
		)
		.conModelo("estado", estado)
		.conModelo("datos", new PeticionConfirmacionGeneral(control, "Recalcular las dependencias del artefacto '" + artefacto.getNombre() + "'"));
	}
	
	@RequestMapping(path="/{idArtefacto}/recalculardependencias", method=RequestMethod.POST)
	public String recalcularDependencias(
			@PathVariable("idArtefacto") String id,
			@ModelAttribute("datos") PeticionConfirmacionGeneral datos,
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {

		// control
		String control = (String)estado.get("CONTROL");
		if(!control.equalsIgnoreCase(datos.getCodigoControl()))
			throw new ErrorIntentoOperacionInvalida();
		
		// cargamos el artefacto
		Artefacto artefacto = ce.artefactos().findOne(id);
		if(artefacto == null) throw new ErrorEntidadNoEncontrada();
		
		// además, el perfil debe ser el de una librería externa
		if(!PT.of(artefacto.getTipoArtefacto()).caracterizacion().getLibreriaExterna()) 
			throw new ErrorIntentoOperacionInvalida();
		
		// lanzamos la operación
		new BuscarDependenciasAdicionales(ce).ejecutarCon(artefacto);

		// damos por completa la operación
		ss.setComplete();
		
		// redirigimos al artefacto
		return "redirect:/artefactos/" + (String)estado.get("ARTEFACTO");
	}
	
	@RequestMapping(path="/recalculardependencias/cancelar", method=RequestMethod.GET)
	public String recalcularCancelar(
			@ModelAttribute("estado") Map<String, Serializable> estado,
			SessionStatus ss) {
		
		ss.setComplete();
		
		if(estado!=null) {
			String id = (String)estado.get("ARTEFACTO");
			return "redirect:/artefactos/" + id;
		}
		
		return "redirect:/artefactos";
	}

}
