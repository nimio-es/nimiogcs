package es.nimio.nimiogcs.operaciones;

import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public abstract class ProcesoAsincronoModulo<T> extends ProcesoAsincronoBase<IContextoEjecucion, T> {

	public ProcesoAsincronoModulo(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}
}
