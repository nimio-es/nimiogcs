package es.nimio.nimiogcs.modelo;

import java.util.Collection;

/**
 * Abstracción del usuario que utiliza la aplicación.
 */
public interface IUsuario {

	String getNombre();
	
	Collection<String> getAutorizaciones();
	
}
