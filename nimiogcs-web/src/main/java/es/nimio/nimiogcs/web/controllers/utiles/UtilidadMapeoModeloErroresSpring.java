package es.nimio.nimiogcs.web.controllers.utiles;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.componentes.publicacion.modelo.IErrores;

/**
 *
 * Pensado para mapear la validación de errores cuando lo que se quiere 
 * es manejar una interfaza del CORE en lugar de la clase de errores
 * de Spring Framework
 *
 */
public final class UtilidadMapeoModeloErroresSpring {

	private UtilidadMapeoModeloErroresSpring() {}
	
	public static final IErrores desde(final Errors errores) {

		return new IErrores() {
			
			@Override
			public void rechazarValor(String campo, String idError, String mensaje) {
				errores.rejectValue(campo, idError, mensaje);
			}
		}; 
	}
}
