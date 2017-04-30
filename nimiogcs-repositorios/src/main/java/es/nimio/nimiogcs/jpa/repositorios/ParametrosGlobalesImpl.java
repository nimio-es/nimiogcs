package es.nimio.nimiogcs.jpa.repositorios;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.repositorios.ParametrosGlobales;

final class ParametrosGlobalesImpl 
	extends ParametrosGlobales {

	private final IContextoEjecucionRepositorios interno;
	
	public ParametrosGlobalesImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}
	
	@Override
	public ParametroGlobal buscar(String id) {
		return interno.global().findOne(id);
	}

	@Override
	public <R extends ParametroGlobal> R guardar(R entidad) {
		return interno.global().save(entidad);
	}

	@Override
	public <R extends ParametroGlobal> R guardarYVolcar(R entidad) {
		return interno.global().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.global().flush();
	}
}
