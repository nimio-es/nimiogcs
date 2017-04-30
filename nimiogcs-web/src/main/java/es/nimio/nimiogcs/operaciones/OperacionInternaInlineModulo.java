package es.nimio.nimiogcs.operaciones;

import es.nimio.nimiogcs.operaciones.OperacionInternaInline;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public abstract class OperacionInternaInlineModulo extends OperacionInternaInline<IContextoEjecucion> {

	public OperacionInternaInlineModulo(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}
}
