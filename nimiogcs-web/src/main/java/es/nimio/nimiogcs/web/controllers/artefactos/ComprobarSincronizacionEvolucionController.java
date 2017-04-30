package es.nimio.nimiogcs.web.controllers.artefactos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.operaciones.artefactos.externa.ComprobarSincronizacionRamaEvolutiva;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/artefactos/evolucion/comprobarsincronizacion")
public class ComprobarSincronizacionEvolucionController {

	private IContextoEjecucion ce;
	
	@Autowired
	public ComprobarSincronizacionEvolucionController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ----
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModelAndView inicio(@PathVariable("id") String id) {

		final Artefacto artefacto = ce.artefactos().findOne(id);
		if(artefacto==null || !(artefacto instanceof EvolucionArtefacto))
			throw new ErrorEntidadNoEncontrada();
		
		final EvolucionArtefacto evolucion = (EvolucionArtefacto)artefacto;
		
		return pagina(evolucion)
				.conModelo(
						"datos", 
						new PeticionConfirmacionGeneral(
								id, 
								"Lanzar comprobación sincronía del código"
						)
				);
	}

	@RequestMapping(path="/{id}/aceptar", method=RequestMethod.POST)
	public String aceptar(
			@PathVariable("id") String id,
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion) {

		final Artefacto artefacto = ce.artefactos().findOne(id);
		if(artefacto==null || !(artefacto instanceof EvolucionArtefacto))
			throw new ErrorEntidadNoEncontrada();
		
		final EvolucionArtefacto evolucion = (EvolucionArtefacto)artefacto;

		new ComprobarSincronizacionRamaEvolutiva(ce)
		.ejecutarCon(
				new ComprobarSincronizacionRamaEvolutiva.Peticion(evolucion)
		);
		
		return "redirect:/artefactos/" + evolucion.getId(); 
	}

	
	// --
	
	private ModeloPagina pagina(EvolucionArtefacto evolucion) {
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Confirmar comprobación sincronización")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Artefactos", "/artefactos")
						.conEnlace(evolucion.getNombre(), "/artefactos/" + evolucion.getId())
						.conTexto("Comprobar sincronía código rama trabajo")
				)
				.conComponentes(
						new PanelContinente()
						.conTitulo("Confirmar operación")
						.paraTipoPrimario()
						.conComponente(
								new PanelInformativo()
								.conTexto("Se dispone a lanzar un proceso de comprobación de la sincronía del código de la rama de trabajo "
										+ "con la rama estable, como resultado de lo cual el registro quedará marcado indicando si está "
										+ "o no sincronizado.")
								.tipoInfo()
						)
						.conComponente(new Parrafo(" "))
						.conComponente(
								new Formulario()
								.urlAceptacion("/artefactos/evolucion/comprobarsincronizacion/" + evolucion.getId() + "/aceptar")
								.conComponentes(
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
								)
								.botoneraEstandar("/artefactos/" + evolucion.getId())
						)
				)
		);
	}
}
