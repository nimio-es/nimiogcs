package es.nimio.nimiogcs.componentes.publicacion.modelo;

import java.util.HashMap;

public interface IPeticionPublicacion {

	HashMap<String, String> getParametrosCanal();
	
	void setParametrosCanal(HashMap<String, String> peticion);
}
