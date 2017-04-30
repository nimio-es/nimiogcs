package es.nimio.nimiogcs.web.controllers.admin.diccionarios.claves;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path=Paths.Admin.Diccionarios.BASE)
public class EliminarClaveDiccionarioDirectivaController {


	private IContextoEjecucion ce;
	
	@Autowired
	public EliminarClaveDiccionarioDirectivaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(path=Paths.Admin.Diccionarios.Definiciones.PTRN_REQ_ELIMINAR, method=RequestMethod.GET)
	public ModelAndView borrar_get(
			@PathVariable("diccionario") String idDiccionario,
			@PathVariable("id") String id) {
		
		// debe existir el diccionario y la definición
		final TipoDirectivaDiccionario tdd = ce.diccionariosDirectivas().findOne(idDiccionario);
		if(tdd==null) throw new ErrorEntidadNoEncontrada();
		final TipoDirectivaDiccionarioDefinicion tddd = ce.definicionesDiccionarios().findOne(id);
		if(tddd==null) throw new ErrorEntidadNoEncontrada();
		
		return 
				ModeloPagina.nuevaPagina(
						new EstructuraPagina("Eliminar definición de diccionario")
						.conComponentes(
								Paths.Admin.Diccionarios.Definiciones.localizacionDatos(tdd, tddd)
								.conTexto("Eliminar"),
								
								new PanelInformativo()
								.conTexto("Se dispone a eliminar una clave de un diccionar de directiva. Este proceso no lleva control de existencia de usos. Aquellas instancias de la directiva que ya estén definidas con esta clave dejarán de incorporarla en las operaciones, pero no se eliminarán los valores existentes.")
								.tipoAviso(),
								
								new Formulario()
								.urlAceptacion(Paths.Admin.Diccionarios.Definiciones.eliminar(tdd, tddd))
								.conComponentes(
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
												PeticionConfirmacionGeneral.class
										)
								)
								.botoneraEstandar(Paths.Admin.Diccionarios.datos(tdd)),
								
								new Parrafo(" ")
						)
				)
				.conModelo(
						"datos", 
						new PeticionConfirmacionGeneral(
								"ELIMINAR_DEFINICION_DICCIONARIO", 
								"Eliminar la definición '" + tddd.getClave() + "' del diccionario '" + tdd.getId() + "'" 
						)
				);
	}
	
	
	@RequestMapping(path=Paths.Admin.Diccionarios.Definiciones.PTRN_REQ_ELIMINAR, method=RequestMethod.POST)
	public String borrar_post(
			@PathVariable("diccionario") String idDiccionario,
			@PathVariable("id") String id,
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion) {
		
		// debe existir el diccionario y la definición
		final TipoDirectivaDiccionario tdd = ce.diccionariosDirectivas().findOne(idDiccionario);
		if(tdd==null) throw new ErrorEntidadNoEncontrada();
		final TipoDirectivaDiccionarioDefinicion tddd = ce.definicionesDiccionarios().findOne(id);
		if(tddd==null) throw new ErrorEntidadNoEncontrada();

		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				ce.definicionesDiccionarios().delete(tddd);
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "ELIMINAR CLAVE '" + tddd.getClave() + "' DEL DICCIONARIO DE DIRECTIVA '" + tdd.getDescripcion() + "'";
			}
		}.ejecutar();
		
		return Paths.Admin.Diccionarios.redirectDatos(tdd);
	}	
}
