package es.nimio.nimiogcs.web.controllers.admin.diccionarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
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
public class EliminarDiccionarioDirectivaController {

	private IContextoEjecucion ce;
	
	@Autowired
	public EliminarDiccionarioDirectivaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----

	@RequestMapping(path=Paths.Admin.Diccionarios.PTRN_REQ_ELIMINAR, method=RequestMethod.GET)
	public ModelAndView eliminar_get(@PathVariable("id") String id) {
		
		// confirmamos que existe el diccionario
		final TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(id);
		if(diccionario == null) throw new ErrorEntidadNoEncontrada();
		
		return 
				ModeloPagina.nuevaPagina(
						new EstructuraPagina("Eliminar diccionario")
						.conComponentes(
								Paths.Admin.Diccionarios.localizacionDatos(diccionario)
								.conTexto("Eliminar"),
								
								new PanelInformativo()
								.conTexto(
										"Se dispone a eliminar un diccionario de directiva. "
										+ "Este proceso no es reversible. Se puede controlar que un diccionario esté siendo usado ya en algún artefacto, pero no se puede confimar que se esté referenciando. En tales casos, la creación de un nuevo artefacto concluiría con un fallo.")
								.tipoAviso(),
								
								new Formulario()
								.urlAceptacion(Paths.Admin.Diccionarios.eliminar(diccionario))
								.conComponentes(
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
												PeticionConfirmacionGeneral.class
										)
								)
								.botoneraEstandar(Paths.Admin.Diccionarios.datos(diccionario)),
								
								new Parrafo(" ")
						)
				)
				.conModelo(
						"datos", 
						new PeticionConfirmacionGeneral(
								"ELIMINAR_DICCIONARIO", 
								"Eliminar diccionario '" + diccionario.getId() + "'" 
						)
				);
	}

	@RequestMapping(path=Paths.Admin.Diccionarios.PTRN_REQ_ELIMINAR, method=RequestMethod.POST)
	public String eliminar_post(
			@PathVariable("id") String id,
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion) {
		
		// confirmamos que existe el diccionario
		final TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(id);
		if(diccionario == null) throw new ErrorEntidadNoEncontrada();
		
		// ejecutamos
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				// primero comprobamos si el diccionario tiene usos
				if(ce.directivas().contarDirectivasDiccionario(diccionario.getId()) > 0)
					throw new ErrorInconsistenciaDatos("Imposible eliminar un diccionar que ya tiene directivas en uso.");
				
				// borramos las definiciones
				for(TipoDirectivaDiccionarioDefinicion df: diccionario.getDefinicionesDiccionario()) {
					ce.definicionesDiccionarios().delete(df);
				}
				
				ce.diccionariosDirectivas().delete(diccionario);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "ELIMINAR DICCIONARIO '" + diccionario.getId() + "'";
			}
		}.ejecutar();
		
		return Paths.Admin.Diccionarios.redirect();
	}
}
