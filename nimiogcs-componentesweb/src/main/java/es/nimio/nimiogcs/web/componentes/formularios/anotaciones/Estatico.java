package es.nimio.nimiogcs.web.componentes.formularios.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica los datos que se mostrarán como texto estático. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Estatico {
	
	/**
	 * Mostrar en la operación de alta
	 */
	boolean enAlta() default true;
	
	/**
	 * Mostrar durante la operación de edición.
	 */
	boolean enEdicion() default true;
}
