package es.nimio.nimiogcs.web.componentes.formularios.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica los campos que solo pueden ser editados en el alta. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface NoEditable {
	
	/**
	 * Cuándo, además de no ser editable, deberá ocultarse.
	 */
	boolean ocultar() default false;
}
