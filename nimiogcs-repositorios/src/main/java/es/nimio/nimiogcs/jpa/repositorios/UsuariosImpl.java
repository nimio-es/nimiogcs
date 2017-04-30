package es.nimio.nimiogcs.jpa.repositorios;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.jpa.repositorios.Usuarios;

final class UsuariosImpl extends Usuarios {

	private final IContextoEjecucionRepositorios interno;
	
	public UsuariosImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}
	
	@Override
	public Usuario buscar(String id) {
		return interno.usuarios().findOne(id);
	}

	@Override
	public <R extends Usuario> R guardar(R entidad) {
		return interno.usuarios().save(entidad);
	}

	@Override
	public <R extends Usuario> R guardarYVolcar(R entidad) {
		return interno.usuarios().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.usuarios().flush();
	}

}
