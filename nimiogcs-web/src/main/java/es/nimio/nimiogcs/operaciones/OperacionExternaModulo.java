package es.nimio.nimiogcs.operaciones;

import es.nimio.nimiogcs.operaciones.OperacionExternaBase;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public abstract class OperacionExternaModulo<T, R> extends OperacionExternaBase<IContextoEjecucion, T, R>{

	public OperacionExternaModulo(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}
}
