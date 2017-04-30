package es.nimio.nimiogcs.web.componentes;

import java.util.Collection;

/**
 * Componente que a su vez es un contenedor.
 */
public interface IContinente<T extends IComponente> extends IComponente {

	/**
	 * Lista de componentes internos.
	 */
	Collection<T> componentes();
}
