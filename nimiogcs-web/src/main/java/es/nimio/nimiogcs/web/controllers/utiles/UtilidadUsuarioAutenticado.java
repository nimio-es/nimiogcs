package es.nimio.nimiogcs.web.controllers.utiles;

import es.nimio.nimiogcs.modelo.IUsuario;

public final class UtilidadUsuarioAutenticado {

	private UtilidadUsuarioAutenticado() {}
	
	public static boolean conAutoridad(IUsuario user, String autoridad) {
		
		for(String ga: user.getAutorizaciones()) {
			if(ga.equalsIgnoreCase(autoridad))
				return true;
		}
		
		return false;
	}
}
