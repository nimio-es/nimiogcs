package es.nimio.nimiogcs.jpa.repositorios;

import es.nimio.nimiogcs.jpa.entidades.RegistroConIdCalculado;

public abstract class RepositorioBase<E extends RegistroConIdCalculado> {

	public abstract E buscar(String id);
	public abstract <R extends E> R guardar(R entidad);
	public abstract <R extends E> R guardarYVolcar(R entidad);
	public abstract void volcar();
}
