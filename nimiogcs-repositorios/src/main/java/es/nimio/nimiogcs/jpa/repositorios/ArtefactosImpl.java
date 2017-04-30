package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.repositorios.Artefactos;

final class ArtefactosImpl extends Artefactos {

	private final IContextoEjecucionRepositorios interno;
	
	public ArtefactosImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}

	@Override
	public Artefacto buscar(String id) {
		return interno.artefactos().findOne(id);
	}

	@Override
	public <R extends Artefacto> R guardar(R entidad) {
		return interno.artefactos().save(entidad);
	}

	@Override
	public <R extends Artefacto> R guardarYVolcar(R entidad) {
		return interno.artefactos().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.artefactos().flush();
	}
	
	@Override
	public Dependencias dependencias() {
		return new DependenciasImpl(interno);
	}

	// ---
	
	static final class DependenciasImpl extends Artefactos.Dependencias {

		private final IContextoEjecucionRepositorios interno;
		
		public DependenciasImpl(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}
		
		@Override
		public Dependencia buscar(String id) {
			return interno.dependenciasArtefactos().findOne(id);
		}

		@Override
		public <R extends Dependencia> R guardar(R entidad) {
			return interno.dependenciasArtefactos().save(entidad);
		}

		@Override
		public <R extends Dependencia> R guardarYVolcar(R entidad) {
			return interno.dependenciasArtefactos().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.dependenciasArtefactos().flush();
		}
		
		@Override
		public Collection<Dependencia> deArtefacto(String idArtefacto) {
			return interno.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(idArtefacto);
		}
	}
}
