package es.nimio.nimiogcs.web.controllers.admin.chequeo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.operaciones.chequeo.ChequeoConexionServiciosExternos;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping(path="/admin/chequeo")
public class ChequeoConexionesController {

	private IContextoEjecucion ce;
	
	@Autowired
	public ChequeoConexionesController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView get() {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Chequeo conexiones")
				.conComponentes(
					new Localizacion()
					.conEnlace("Home", "/")
					.conTexto("Administración")
					.conTexto("Chequear conexiones"),
					
					new PanelInformativo()
					.tipoInfo()
					.conTexto(
							"Se lanzará un proceso que irá comprobando las conexiones "
							+ "de los distintos módulos. El resultado del proceso habrá de"
							+ "consultarse en el registro del proceso u operación."
					),
					
					new Formulario()
					.urlAceptacion("/admin/chequeo")
					.conComponentes(
							AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
					)
					.botoneraEstandar("/")
				)
		)
		.conModelo(
				"datos", 
				new PeticionConfirmacionGeneral(
						"SINCONTROL", 
						"Lanzar ejecución proceso de comprobación de las conexiones"
				)
		);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String post() {
		
		// lanzamos la operación de comprobación
		new ChequeoConexionServiciosExternos(ce).ejecutarCon(true);
		
		// volvemos a la página de inicio
		return "redirect:/";
	}
	
}
