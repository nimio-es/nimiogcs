package es.nimio.nimiogcs.web.componentes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Inherited
public @interface Pagina {
	String nombrePlantilla() default "plantilla_pagina_defecto";
}
