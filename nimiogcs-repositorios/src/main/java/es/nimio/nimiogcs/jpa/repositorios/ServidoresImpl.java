package es.nimio.nimiogcs.jpa.repositorios;

import java.util.ArrayList;
import java.util.Collection;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorAplicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorLibreriaCompartida;
import es.nimio.nimiogcs.jpa.repositorios.Servidores;
import es.nimio.nimiogcs.jpa.specs.ServidoresArtefactos;

final class ServidoresImpl extends Servidores {

	private final IContextoEjecucionRepositorios interno;
	
	public ServidoresImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}
	
	@Override
	public RelacionesConArtefactos artefactosInstalados() {
		return new RelacionesConArtefactosImpl(interno);
	}

	@Override
	public Servidor buscar(String id) {
		return interno.servidores().findOne(id);
	}

	@Override
	public <R extends Servidor> R guardar(R entidad) {
		return interno.servidores().save(entidad);
	}

	@Override
	public <R extends Servidor> R guardarYVolcar(R entidad) {
		return interno.servidores().saveAndFlush(entidad);
	}

	@Override
	public void volcar() {
		interno.servidores().flush();
	}

	// --
	
	static final class RelacionesConArtefactosImpl extends Servidores.RelacionesConArtefactos {

		private final IContextoEjecucionRepositorios interno;
		
		public RelacionesConArtefactosImpl(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}

		@Override
		public Collection<RelacionServidorLibreriaCompartida> poms(Servidor servidor) {
			final ArrayList<RelacionServidorLibreriaCompartida> poms = 
					new ArrayList<RelacionServidorLibreriaCompartida>();
			for(RelacionServidorArtefacto ra: interno.servidoresArtefactos().findAll(
					ServidoresArtefactos.pomsEnServidor(servidor))) {
				poms.add((RelacionServidorLibreriaCompartida)ra);
			}
			return poms;
		}

		@Override
		public Collection<RelacionServidorAplicacion> ears(Servidor servidor) {
			final ArrayList<RelacionServidorAplicacion> ears = 
					new ArrayList<RelacionServidorAplicacion>();
			for(RelacionServidorArtefacto ra: interno.servidoresArtefactos().findAll(
					ServidoresArtefactos.earsEnServidor(servidor))) {
				ears.add((RelacionServidorAplicacion)ra);
			}
			return ears;
		}
		
		@Override
		public Collection<RelacionServidorAplicacion> aplicaciones(Servidor servidor) {
			final ArrayList<RelacionServidorAplicacion> aplicaciones = 
					new ArrayList<RelacionServidorAplicacion>();
			for(RelacionServidorArtefacto ra: interno.servidoresArtefactos().findAll(
					ServidoresArtefactos.earsEnServidor(servidor))) {
				aplicaciones.add((RelacionServidorAplicacion)ra);
			}
			for(RelacionServidorArtefacto ra: interno.servidoresArtefactos().findAll(
					ServidoresArtefactos.warsEnServidor(servidor))) {
				aplicaciones.add((RelacionServidorAplicacion)ra);
			}
			return aplicaciones;
		}

		@Override
		public RelacionServidorArtefacto buscar(String id) {
			return interno.servidoresArtefactos().findOne(id);
		}

		@Override
		public <R extends RelacionServidorArtefacto> R guardar(R entidad) {
			return interno.servidoresArtefactos().save(entidad);
		}

		@Override
		public <R extends RelacionServidorArtefacto> R guardarYVolcar(R entidad) {
			return interno.servidoresArtefactos().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.servidoresArtefactos().flush();
		}
	}
}
