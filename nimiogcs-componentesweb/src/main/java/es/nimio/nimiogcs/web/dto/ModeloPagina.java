package es.nimio.nimiogcs.web.dto;

import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.Pagina;

/**
 * Especializa la estructura ModelAndView para que se ofrezca siempre una página concreta y se utilice
 */
public class ModeloPagina extends ModelAndView {

	/**
	 * Obtiene qué plantilla corresponde a una estructura de página sacándola de la propiedad.
	 */
	private static String obtenPlantillaAsociada(EstructuraAbstractaPagina pagina) {
		
		Pagina anotacion = pagina.getClass().getAnnotation(Pagina.class);
		if(anotacion == null) 
			throw new ModeloPagina.ErrorObtencionPlantilla();
		
		return anotacion.nombrePlantilla();
	}
	
	private ModeloPagina(EstructuraAbstractaPagina pagina) {
		super(obtenPlantillaAsociada(pagina));
		this.addObject("pagina", pagina);
	}
	
	
	// ---------------------------------------
	// API fluido
	// ---------------------------------------

	public static ModeloPagina nuevaPagina(EstructuraAbstractaPagina pagina) {
		ModeloPagina pag = new ModeloPagina(pagina);
		return pag;
	}
	
	public ModeloPagina conModelo(String nombre, Object modelo) {
		this.addObject(nombre, modelo);
		return this;
	}
	
	// --
	
	/**
	 * Error específico cuando no se consigue leer la plantilla asociada
	 */
	public static final class ErrorObtencionPlantilla extends RuntimeException {

		private static final long serialVersionUID = 4937838010703440264L;

		public ErrorObtencionPlantilla() {
			super("Se está definiendo una estructura de página que no tiene asociada una anotación donde se define la plantilla a utilizar");
		}
	}
	
}
