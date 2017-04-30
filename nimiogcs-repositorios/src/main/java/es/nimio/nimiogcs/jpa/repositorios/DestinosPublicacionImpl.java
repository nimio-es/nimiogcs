package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;
import es.nimio.nimiogcs.jpa.repositorios.DestinosPublicacion;
import es.nimio.nimiogcs.jpa.specs.EntornosServidores;

final class DestinosPublicacionImpl 
	extends DestinosPublicacion {

	private final IContextoEjecucionRepositorios interno;

	public DestinosPublicacionImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}

	//--
	
	@Override
	public DestinoPublicacion buscar(String id) {
		return interno.destinosPublicacion().findOne(id);
	}

	@Override
	public <R extends DestinoPublicacion> R guardar(R entidad) {
		return interno.destinosPublicacion().save(entidad);
	}

	@Override
	public <R extends DestinoPublicacion> R guardarYVolcar(R entidad) {
		return interno.destinosPublicacion().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.destinosPublicacion().flush();
	}
	
	@Override
	public RelacionesConServidores servidores() {
		return new RelacionesConServidoresImpl(interno);
	}
	
	// ---
	
	static final class RelacionesConServidoresImpl extends DestinosPublicacion.RelacionesConServidores {

		private final IContextoEjecucionRepositorios interno;

		public RelacionesConServidoresImpl(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}
		
		@Override
		public RelacionEntornoServidor buscar(String id) {
			return interno.entornosServidores().findOne(id);
		}

		@Override
		public <R extends RelacionEntornoServidor> R guardar(R entidad) {
			return interno.entornosServidores().save(entidad);
		}

		@Override
		public <R extends RelacionEntornoServidor> R guardarYVolcar(R entidad) {
			return interno.entornosServidores().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.entornosServidores().flush();
		}
		
		@Override
		public Collection<RelacionEntornoServidor> deUnDestino(DestinoPublicacion destino) {
			return interno.entornosServidores().findAll(
					EntornosServidores.servidoresEntorno(destino)
			);
		}
	}
}
