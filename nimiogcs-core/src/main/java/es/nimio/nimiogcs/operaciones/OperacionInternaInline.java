package es.nimio.nimiogcs.operaciones;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;

/**
 * Versión reducida que facilite la incorporación en línea como clase anónima
 */
public abstract class OperacionInternaInline<C extends IContextoEjecucionBase> extends OperacionInternaBase<C, Boolean, Boolean>{

	public OperacionInternaInline(C contextoEjecucion) {
		super(contextoEjecucion);
	}

	
	@Override
	protected void debug(String mensaje) {
		super.debug("[" + generaNombreUnico() + "] " + mensaje);
	}

	@Override
	protected void error(String mensaje, Throwable th) {
		super.error("[" + generaNombreUnico() + "] " + mensaje, th);
	}
	
	// --
	
	@Override
	protected void relacionarOperacionConEntidades(Boolean datos, Operacion op) {
		// damos una versión básica que no hace nada. Que se sobrecargue si se requiere.
	}

	@Override
	protected String nombreUnicoOperacion(Boolean datos, Operacion op) {
		// vamos a basarnos en un nuevo método que hay que sobrecargar para que el nombre tenga sentido
		return generaNombreUnico();
	}

	protected abstract String generaNombreUnico();
	
	// versión en la que no se le pasa ni el valor ficticio
	public Boolean ejecutar() { 
		return this.ejecutar(true); 
	}
}
