package es.nimio.nimiogcs.web.componentes.formularios.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca el dato para que se muestre como un checkbox 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Checkbox {
	String texto() default "";
}
