package es.nimio.nimiogcs.web.controllers.admin.tiposArtefactos;

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

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.tiposArtefactos.TiposArtefactosForm;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/admin/tipos")
public class NuevoTipoArtefactoController {
	
	private IContextoEjecucion ce;
	
	@Autowired
	public NuevoTipoArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	
	// ---------------------------------------------------
	// Alta
	// ---------------------------------------------------

	@RequestMapping(path="/nuevo", method=GET)
	public ModeloPagina register() {

		return paginaParaRegistro(true, null)
				.conModelo("datos", new TiposArtefactosForm());
	}
	
	@RequestMapping(value = "/nuevo", method = POST)
	public ModelAndView nuevo(
			@Valid @ModelAttribute("datos") TiposArtefactosForm datos, Errors errores) {
		
		// confirmamos que la validación es correcta
		if (errores.hasErrors())
			return paginaParaRegistro(true, null).conModelo("datos", datos);
		
		final TipoArtefacto datosGuardar = guardarDatosAlta(datos);
		
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				// guardamos el tipo 
				TipoArtefacto tipoGuardado = ce.tipos().saveAndFlush(datosGuardar);
				
				// y creamos la relación con la operación
				registraRelacionConOperacion(op, tipoGuardado);
				
				// fin
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "ALTA TIPO DE ARTEFACTO '" + datosGuardar.getId() + "'";
			}
		}.ejecutar();
		
		// si todo es ok, salimos redirigiendo a la vista con la lista
		return new ModelAndView("redirect:/admin/tipos/");
	}
	
	// ---------------------------------------------------
	// Edición
	// ---------------------------------------------------
	
	@RequestMapping(path="/editar/{id}", method=GET)
	public ModeloPagina editar(@PathVariable String id) {
		
		 TipoArtefacto entidad = ce.tipos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		
		return paginaParaRegistro(false,entidad)
				.conModelo(
						"datos", 
						new TiposArtefactosForm()
						.desde(entidad)
				);
	}	
	
	@RequestMapping(path="/editar", method=POST)
	public ModelAndView editar(@ModelAttribute("datos") TiposArtefactosForm datos, Errors errores) {

		// buscamos el parametro
		TipoArtefacto entidad = ce.tipos().findOne(datos.getId());
		if(entidad==null) throw new ErrorEntidadNoEncontrada();

		if(errores.hasErrors()) {
			return paginaParaRegistro(false,entidad).conModelo("datos", datos); 
		}

		// actualizamos la entidad 
		final TipoArtefacto datosGuardar = guardarDatos(datos);

		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				ce.tipos().saveAndFlush(datosGuardar);
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "MODIFICAR TIPOS DE ARTEFACTOS" + "(' " + datosGuardar.getId() + " ')";
			}
		}.ejecutar();
			
			// redirigimos a la página del listado
			return new ModelAndView("redirect:/admin/tipos/" + datos.getId());
	}	
	
	
	

	private ModeloPagina paginaParaRegistro(boolean alta,TipoArtefacto  tipo) {

		String textoFinalLocalizador = alta ? "Alta nuevo registro" : "Editar registro existente";
		String tituloPagina = alta ? "Alta nuevo tipo artefacto" : "Editar tipo artefacto '" + tipo.getId() + "'";
		String urlAceptar = alta ? "/admin/tipos/nuevo" : "/admin/tipos/editar";
		String urlCancelar = alta ? "/admin/tipos" : "/admin/tipos/" + tipo.getId();
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
												TiposArtefactosForm.class
										))
					)
				);
	}
	
	
	
	private Localizacion localizacionBase() {
		return new Localizacion(
				new ItemBasadoEnUrl("Home", "/"),
				new ItemBasadoEnUrl("Tipos", "/tipos"));
				
	}			

	private TipoArtefacto guardarDatos(TiposArtefactosForm datos) {
		TipoArtefacto entidad = ce.tipos().findOne(datos.getId());	
	  
		 entidad.setNombre(datos.getNombre());
		 entidad.setDeUsuario(true);
		 return (entidad);
	}
	
	private TipoArtefacto guardarDatosAlta(TiposArtefactosForm datos) {
		TipoArtefacto entidad = new TipoArtefacto();	
	  
		entidad.setId(datos.getId().toUpperCase()); 
		entidad.setNombre(datos.getNombre());
		entidad.setDeUsuario(datos.isDeUsuario());
		return (entidad);
	}	
}
