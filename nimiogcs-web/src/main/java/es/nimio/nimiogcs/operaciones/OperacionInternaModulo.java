package es.nimio.nimiogcs.operaciones;

import es.nimio.nimiogcs.operaciones.OperacionInternaBase;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public abstract class OperacionInternaModulo<T, R> 
	extends OperacionInternaBase<IContextoEjecucion, T, R>{

	public OperacionInternaModulo(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}
}
