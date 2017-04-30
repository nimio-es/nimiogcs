package es.nimio.nimiogcs.web.controllers.utiles;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public final class UtilidadCargarUsuariosPaginaOperaciones {

	private UtilidadCargarUsuariosPaginaOperaciones() {}
	
	/**
	 * Carga los datos de los usuarios de una página de operaciones en preparación para lo que 
	 * necesita la presentación de la lista de operaciones.
	 */
	public static Map<String, Usuario> cargarUsuariosPagina(IContextoEjecucion ce, Page<Operacion> operaciones) {
		
		final Map<String, Usuario> mapaUsuarios = new HashMap<String, Usuario>();
		
		for(Operacion operacion: operaciones.getContent()) {
			
			String userName = operacion.getUsuarioEjecuta().toUpperCase();
			
			// si no está ya almacenado lo intentamos cargar
			if(!mapaUsuarios.containsKey(userName)) {
				Usuario usuario = ce.usuarios().findOne(userName);
				if(usuario!=null) {
					mapaUsuarios.put(userName, usuario);
				}
			}
		}
		
		return mapaUsuarios;
	}
}
