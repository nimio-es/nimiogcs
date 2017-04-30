package es.nimio.nimiogcs.componentes;

import java.util.Collection;
import java.util.concurrent.Executor;

import es.nimio.nimiogcs.jpa.repositorios.IRegistroRepositorios;
import es.nimio.nimiogcs.modelo.IUsuario;

/**
 * El contexto base de ejecución.
 */
public interface IContextoEjecucionBase {

	// ----------------------------------------------
	// Búsqueda general de componentes
	// ----------------------------------------------
	
	/**
	 * Devuelve un componente del contexto de ejecución que cumpla la clase indicada.
	 * De haber más de uno, devolverá el primero.
	 */
	<T> T componente(Class<T> cz);

	/**
	 * Devuelve una colección con todos los componentes del contexto de ejecución que 
	 * sean del tipo indicado.
	 */
	<T> Collection<T> componentes(Class<T> cz);
	
	// ----------------------------------------------
	// Variables de entorno
	// ----------------------------------------------

	IVariablesEntorno variablesEntorno();
	
	// ----------------------------------------------
	// Usuario
	// ----------------------------------------------

	/**
	 * Devuelve una estructura que representa al usuario en la aplicación.
	 * @return
	 */
	IUsuario usuario();


	// ----------------------------------------------
	// Tareas
	// ----------------------------------------------

	/**
	 * Ejecutor de tareas asíncronas
	 * @return
	 */
	Executor executor();

	// ----------------------------------------------
	// Repositorios
	// ----------------------------------------------

	IRegistroRepositorios repos();
}
