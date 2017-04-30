package es.nimio.nimiogcs.web.componentes.formularios.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica el grupo al que se asocia éste elemento de formulario.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface GrupoAsociado {
	String grupoContiene() default "";
	int ordenEnGrupo() default 1;
}
