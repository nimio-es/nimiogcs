package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.repositorios.Operaciones;

final class OperacionesImpl extends Operaciones {

	private final IContextoEjecucionRepositorios interno;
	
	public OperacionesImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}
	
	@Override
	public Operacion buscar(String id) {
		return interno.operaciones().findOne(id);
	}

	@Override
	public <R extends Operacion> R guardar(R entidad) {
		return interno.operaciones().save(entidad);	
	}
	
	@Override
	public <R extends Operacion> R guardarYVolcar(R entidad) {
		return interno.operaciones().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.operaciones().flush();
	}
	
	@Override
	public ProcesoEspera procesoEsperaConTicket(String ticket) {
		return interno.operaciones().procesoEsperaConTicket(ticket);
	}
	
	@Override
	public ProcesoEsperaRespuestaDeployer procesoEsperaRespuestaDeployerConEtiquetaPase(String idEtiqueta) {
		return interno.operaciones().procesoEsperaRespuestaDeployerConEtiquetaPase(idEtiqueta);
	}
	
	@Override
	public Relaciones relaciones() {
		return new OperacionesImpl.RelacionesOperacionesImpl(interno);
	}
	
	// -----
	
	static final class RelacionesOperacionesImpl extends Operaciones.Relaciones {

		private final IContextoEjecucionRepositorios interno;

		public RelacionesOperacionesImpl(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}
		
		@Override
		public RelacionOperacion buscar(String id) {
			return interno.relacionesOperaciones().findOne(id);
		}

		@Override
		public <R extends RelacionOperacion> R guardar(R entidad) {
			return interno.relacionesOperaciones().save(entidad);
		}

		@Override
		public <R extends RelacionOperacion> R guardarYVolcar(R entidad) {
			return interno.relacionesOperaciones().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.relacionesOperaciones().flush();
		}
		
		@Override
		public Collection<RelacionOperacion> relacionesDeUnaOperacion(String id) {
			return interno.relacionesOperaciones().relacionesDeUnaOperacion(id);
		}
	}	
	
}
