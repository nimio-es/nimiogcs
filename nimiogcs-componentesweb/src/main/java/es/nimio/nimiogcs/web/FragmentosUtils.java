package es.nimio.nimiogcs.web;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

public final class FragmentosUtils {

	/**
	 * Devuelve el grupo al que pertence este fragmento.
	 */
	public static String grupo(IComponente componente) {
		
		Fragmento fragmento = componente.getClass().getAnnotation(Fragmento.class);
		String grupo = fragmento != null ? 
				(fragmento.grupo() != null && !fragmento.grupo().isEmpty() ? fragmento.grupo() : "_componentes" )
				: "_componentes";
		return grupo;
	}

	
	/**
	 * Devuelve el fragmento que requiere éste componente basándose en la anotación Fragmento.
	 */
	public static String fragmento(IComponente componente) {
		
		Fragmento fragmento = componente.getClass().getAnnotation(Fragmento.class);
		String id = fragmento != null ? fragmento.id() : "desconocido";
		return id;
	}
	
	// no se podrá instanciar
	private FragmentosUtils() {	}
}
