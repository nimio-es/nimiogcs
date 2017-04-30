package es.nimio.nimiogcs.componentes.publicacion.modelo;

import java.util.Collection;
import java.util.Map;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;

public interface IDatosPeticionPublicacion {

	/**
	 * Canal para el que se corresponde la petición
	 */
	String getCanal();

	/**
	 * Parámetros adicionales a usar en el canal.
	 * @return
	 */
	Map<String, String> getParametrosCanal();
	
	/**
	 * Usuario al que asociar la petición
	 */
	String getUsuario();
	
	/**
	 * Etiqueta para la que estamos solicitando la publicación.
	 */
	EtiquetaProyecto getEtiqueta();
	
	/**
	 * Colección con los artefactos que se ha querido publicar
	 */
	Collection<Artefacto> getArtefactos();
}
