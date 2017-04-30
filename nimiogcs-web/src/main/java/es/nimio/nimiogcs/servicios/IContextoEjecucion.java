package es.nimio.nimiogcs.servicios;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;

/**
 * Estamos pasando en cientos de sitios los mismos repositorios, las mismas operaciones, etc., etc.
 * Cada vez que queremos otra instancia de algo añadimos otro campo y/o modificamos el con constructor.
 * Esta clase/servicio nace con la sana intención de que se inyecten todos los repositorios y todos
 * los servicios/componentes que se vayan requiriendo de forma general y sea solamente ésta la que se
 * pase en los constructores que se necesiten. 
 */
public interface IContextoEjecucion 
	extends IContextoEjecucionBase, IContextoEjecucionRepositorios {

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Entorno
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * El entorno de la aplicación
	 */
	Environment environment();
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Contexto de aplicación
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Contexto de aplicación Spring
	 */
	ApplicationContext contextoAplicacion();
	
	/**
	 * Scheduler disponible 
	 */
	TaskScheduler scheduler();

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Petición entrante del Servlet
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * La petición original
	 */
	HttpServletRequest servletRequest();
}
