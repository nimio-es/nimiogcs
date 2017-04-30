package es.nimio.nimiogcs.maven.componentes.proyecto.proyeccion;

import java.util.List;

import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.proyecto.proyeccion.IDescripcionesProyecciones;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.maven.KMaven;

@Component
public class DescripcionesProyeccionesMaven implements IDescripcionesProyecciones {

	@Override
	public void descripciones(List<NombreDescripcion> listaDescripciones) {
		listaDescripciones.add(new NombreDescripcion(KMaven.PROYECCION_NOMBRE, KMaven.PROYECCION_NOMBRE + ": " + KMaven.PROYECCION_DESCRIPCION));
	}
}
