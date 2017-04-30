package es.nimio.nimiogcs.jpa.repositorios;

import java.util.List;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.repositorios.ArtefactosPublicados;

final class ArtefactosPublicadosImpl
	extends ArtefactosPublicados {

	private final IContextoEjecucionRepositorios interno;

	public ArtefactosPublicadosImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}
	
	@Override
	public PublicacionArtefacto buscar(String id) {
		return interno.artefactosPublicados().findOne(id);
	}

	@Override
	public <R extends PublicacionArtefacto> R guardar(R entidad) {
		return interno.artefactosPublicados().save(entidad);
	}

	@Override
	public <R extends PublicacionArtefacto> R guardarYVolcar(R entidad) {
		return interno.artefactosPublicados().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.artefactosPublicados().flush();
	}
	
	
	@Override
	public List<PublicacionArtefacto> elementosPublicados(Publicacion publicacion) {
		return interno.artefactosPublicados().findAll(
				es.nimio.nimiogcs.jpa.specs.ArtefactosPublicados.elementosPublicados(publicacion)
		);
	}
}
