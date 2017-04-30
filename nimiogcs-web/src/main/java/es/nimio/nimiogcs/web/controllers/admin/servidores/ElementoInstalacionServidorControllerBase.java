package es.nimio.nimiogcs.web.controllers.admin.servidores;

import java.util.Collection;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

abstract class ElementoInstalacionServidorControllerBase {

	// ------------------------------------------------
	// Constructor
	// ------------------------------------------------

	public ElementoInstalacionServidorControllerBase(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Estado
	// ------------------------------------------------

	protected final IContextoEjecucion ce;
	
	// ------------------------------------------
	// Métodos que deben sobrecargar el resto de
	// controladores para adaptar el comportamiento
	// ------------------------------------------
	
	protected abstract String grupo();
	protected abstract String subarea();
	protected abstract String tituloPagina(boolean alta);
	protected abstract Class<?> tipoDto(); 
	protected abstract Map<String, Collection<NombreDescripcion>> diccionariosNecesariosPagina(boolean alta);
	
	// ------------------------------------------
	// Métodos auxiliares
	// ------------------------------------------
	
	protected Localizacion localizacionParaSitio(String grupo, String titulo, ServidorJava servidor) {
		
		return new Localizacion(
				new ItemBasadoEnUrl("Home", "/"),
				new ItemBasadoEnUrl("Administración", ""),
				new ItemBasadoEnUrl("Sitios", "/admin/ciclovida/sitios"),
				new ItemBasadoEnUrl("Servidor", ""),
				new ItemBasadoEnUrl(servidor.getNombre(), "/admin/ciclovida/sitios/" + servidor.getNombre()),
				new ItemBasadoEnUrl("Conf. instalación", "/admin/ciclovida/sitios/servidor/instalacion/" + servidor.getId()),
				new ItemBasadoEnUrl(grupo, "/admin/ciclovida/sitios/servidor/instalacion/" + servidor.getId()),
				new ItemBasadoEnUrl(titulo, "")
			);
	}

	protected static ModelAndView redireccionInfoConfiguracionSitio(String id) {
		return new ModelAndView("redirect:/admin/ciclovida/sitios/servidor/instalacion/" + id);
	}
	
	protected ModeloPagina crearEstructuraPaginaRegistroElemento(boolean alta, Servidor sitio) {
		
		return ModeloPagina.nuevaPagina(
				
				new EstructuraPagina(tituloPagina(alta))
					.conComponentes(
							new IComponente[] {
									localizacionParaSitio(grupo(), tituloPagina(alta), (ServidorJava)sitio),
									new Formulario(
											"datos", 
											"/admin/ciclovida/sitios/servidor/" + subarea() + "/" + (alta ? "nuevo" : "editar"),
											"POST",
											"/admin/ciclovida/sitios/servidor/instalacion/" + sitio.getId(), 
											AyudanteCalculoEstructuraFormularioDesdePojo
												.convertirDesdeDto(
														alta ? 
															AyudanteCalculoEstructuraFormularioDesdePojo.Operacion.ALTA
															: AyudanteCalculoEstructuraFormularioDesdePojo.Operacion.EDICION,  
														tipoDto(),
														diccionariosNecesariosPagina(alta)))
								}
					)
			);
	}
	
}
