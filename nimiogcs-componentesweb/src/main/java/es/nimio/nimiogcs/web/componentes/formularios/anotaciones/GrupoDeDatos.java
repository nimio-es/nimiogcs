package es.nimio.nimiogcs.web.componentes.formularios.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.ANNOTATION_TYPE)
public @interface GrupoDeDatos {

	String id();
	String nombre();
	String textoDescripcion() default "";
	int orden();
}
