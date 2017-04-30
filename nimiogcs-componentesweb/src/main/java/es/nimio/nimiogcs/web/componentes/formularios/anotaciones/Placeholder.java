package es.nimio.nimiogcs.web.componentes.formularios.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placeholder que se mostrará en el input para el caso de una entrada de texto.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Placeholder {
	String value();
}
