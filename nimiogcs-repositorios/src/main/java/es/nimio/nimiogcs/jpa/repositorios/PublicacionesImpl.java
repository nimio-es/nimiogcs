package es.nimio.nimiogcs.jpa.repositorios;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.repositorios.Publicaciones;

final class PublicacionesImpl extends Publicaciones {

	private final IContextoEjecucionRepositorios interno;

	public PublicacionesImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}
	
	@Override
	public Publicacion buscar(String id) {
		return interno.publicaciones().findOne(id);
	}

	@Override
	public <R extends Publicacion> R guardar(R entidad) {
		return interno.publicaciones().save(entidad);
	}

	@Override
	public <R extends Publicacion> R guardarYVolcar(R entidad) {
		return interno.publicaciones().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.publicaciones().flush();
	}

}
