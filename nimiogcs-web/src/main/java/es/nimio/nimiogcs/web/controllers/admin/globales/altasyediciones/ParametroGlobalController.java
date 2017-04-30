package es.nimio.nimiogcs.web.controllers.admin.globales.altasyediciones;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.globales.ParametrosGlobalesForm;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;


@Controller
@RequestMapping(path="/admin/globales")
public class ParametroGlobalController {

	private IContextoEjecucion ce;
	
	@Autowired
	public ParametroGlobalController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	
	// ---------------------------------------------------
	// Alta
	// ---------------------------------------------------

	@RequestMapping(path="/nuevo", method=GET)
	public ModeloPagina register() {

		return paginaParaRegistro(true, null)
				.conModelo("datos", new ParametrosGlobalesForm());
	}
	
	
	
	
	// ---------------------------------------------------
		// Edición
		// ---------------------------------------------------
		
		@RequestMapping(path="/editar/{id:.+}", method=GET)
		public ModeloPagina editar(@PathVariable String id) {
			
			 ParametroGlobal entidad = ce.global().findOne(id);
			if(entidad==null) throw new ErrorEntidadNoEncontrada();
			
			
			return paginaParaRegistro(false,entidad)
					.conModelo(
							"datos", 
							new ParametrosGlobalesForm()
							.desde(entidad, diccionariosNecesariosPagina())
					);
		}
		
		@RequestMapping(path="/editar", method=POST)
		public ModelAndView editar(@ModelAttribute("datos") ParametrosGlobalesForm datos, Errors errores) {

			// buscamos el parametro
			ParametroGlobal entidad = ce.global().findOne(datos.getIdGlobal());
			if(entidad==null) throw new ErrorEntidadNoEncontrada();

			if(errores.hasErrors()) {
				return paginaParaRegistro(false,entidad).conModelo("datos", datos); 
			}
	
			// actualizamos la entidad 
			final ParametroGlobal datosGuardar = guardarDatos(datos);
			 new OperacionInternaInlineModulo(ce) {
				
				@Override
				protected Boolean hazlo(Boolean datos, Operacion op) {
					ce.global().saveAndFlush(datosGuardar);
					return true;
				}
				
				@Override
				protected String generaNombreUnico() {
					return ("MODIFICAR PARAMETROS GLOBALES  " + "(' " + datosGuardar.getId() + " ')");
					
				}
			}.ejecutar();
				
			// redirigimos a la página del listado
			return new ModelAndView("redirect:/admin/globales");
		}
		
		
		private ParametroGlobal guardarDatos(ParametrosGlobalesForm datos) {
				ParametroGlobal entidad = ce.global().findOne(datos.getIdGlobal());	
			  
				 entidad.setContenido(datos.getContenido());
				 return (entidad);
			}


		private ModeloPagina paginaParaRegistro(boolean alta,ParametroGlobal  parametro) {

			String textoFinalLocalizador = alta ? "Alta nuevo registro" : "Editar registro existente";
			String tituloPagina = alta ? "Alta nuevo parametro Global" : "Editar parametro Global '" + parametro.getId() + "'";
			String urlAceptar = alta ? "/admin/globales" : "/admin/globales/editar";
			String urlCancelar = alta ? "/admin/globales" : "/admin/globales";
			AyudanteCalculoEstructuraFormularioDesdePojo.Operacion op = alta ? 
					AyudanteCalculoEstructuraFormularioDesdePojo.Operacion.ALTA
					: AyudanteCalculoEstructuraFormularioDesdePojo.Operacion.EDICION;
			
			return ModeloPagina.nuevaPagina(
					new EstructuraPagina(tituloPagina)
						.conComponentes(
								localizacionBase().conTexto(textoFinalLocalizador),
								new Formulario(
										"datos", 
										urlAceptar,
										"POST",
										urlCancelar, 
										AyudanteCalculoEstructuraFormularioDesdePojo
											.convertirDesdeDto(
													op,
													ParametrosGlobalesForm.class,
													diccionariosNecesariosPagina()
											))
						)
					);
		}
		
		
		
		private Localizacion localizacionBase() {
			return new Localizacion(
					new ItemBasadoEnUrl("Home", "/"),
					new ItemBasadoEnUrl("Parametros Globales", "/admin/globales"));
					
		}		
		private Map<String, Collection<NombreDescripcion>> diccionariosNecesariosPagina() {
			
			// mapa que pasaremos al ayudante para fabricar el formulario
			Map<String, Collection<NombreDescripcion>> diccionarioValores = 
					new HashMap<String, Collection<NombreDescripcion>>();
			



			
			return diccionarioValores;
		}	
}
