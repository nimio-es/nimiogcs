package es.nimio.nimiogcs.operaciones;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.utils.DateTimeUtils;

/**
 * Definición de lo que es una operación.
 *
 * @param <T>
 * @param <R>
 */
public abstract class OperacionBase<C extends IContextoEjecucionBase, T,R> {

	final private Logger logger = LoggerFactory
			.getLogger(getClass());
	
	final protected C ce;
	final protected StringBuilder msg = new StringBuilder();
	
	/**
	 * Constructor en el que se le suministra el contexto de ejecución de la aplicación
	 */
	public OperacionBase(C contextoEjecucion) {
		this.ce = contextoEjecucion;
	} 
	
	/**
	 * La operación de ejecución
	 */
	public abstract R ejecutar(T d) throws ErrorInesperadoOperacion;
	
	
	protected void debug(String mensaje) {
		logger.debug(mensaje);
	}
	
	protected void error(String mensaje, Throwable th) {
		logger.error(mensaje, th);
	}
	
	// ------------------------------------------
	// Para dejar constancia de los pasos dados
	// durante la ejecución. Como se supone
	// que es algo interno, se ofrecen operaciones
	// internas y una externa que variará un poco
	// a la hora de registrar los valores
	// ------------------------------------------

	protected void escribeMensaje(String mensaje) {
	
		msg.append("[")
		.append(DateTimeUtils.formaReducida(new Date()))
		.append("] ")
		.append(mensaje)
		.append("\n");
		
		debug("mensaje interno operación: " + mensaje);
	}
	
	public void escribeMensajeExterior(String mensaje) {
		
		msg.append("[")
		.append(DateTimeUtils.formaReducida(new Date()))
		.append("][EXT] ")
		.append(mensaje)
		.append("\n");
		
		debug("mensaje externo operación: " + mensaje);
	}
}
