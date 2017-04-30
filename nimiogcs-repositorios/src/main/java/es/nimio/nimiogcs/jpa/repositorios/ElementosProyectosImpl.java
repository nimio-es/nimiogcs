package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.repositorios.ElementosProyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;

final class ElementosProyectosImpl extends ElementosProyecto {

	private final IContextoEjecucionRepositorios interno;

	public ElementosProyectosImpl(final IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}

	//--

	@Override
	public Proyectos proyectos() {
		return new ElementosProyectosImpl.Proyectos(interno);
	}

	@Override
	public Etiquetas etiquetas() {
		return new ElementosProyectosImpl.Etiquetas(interno);
	}

	@Override
	public RelacionesArtefactos relaciones() {
		return new ElementosProyectosImpl.RelacionArtefactos(interno);
	}

	@Override
	public UsosYProyecciones usos() {
		return new ElementosProyectosImpl.Usos(interno);
	}
	
	
	// ----
	
	final static class Proyectos extends ElementosProyecto.Proyectos {

		private final IContextoEjecucionRepositorios interno;

		public Proyectos(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}

		//--


		@Override
		public Proyecto buscar(String id) {
			return interno.proyectos().findOne(id);
		}

		@Override
		public <R extends Proyecto> R guardar(R entidad) {
			return interno.proyectos().save(entidad);
		}

		@Override
		public <R extends Proyecto> R guardarYVolcar(R entidad) {
			return interno.proyectos().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.proyectos().flush();
		}
		
		@Override
		public RelacionesArtefactos relaciones() {
			return new RelacionArtefactos(interno);
		}
		
		// ----
		
		final static class RelacionArtefactos extends ElementosProyecto.Proyectos.RelacionesArtefactos {

			private final IContextoEjecucionRepositorios interno;
			
			public RelacionArtefactos(final IContextoEjecucionRepositorios interno) {
				this.interno = interno;
			}
			
			@Override
			public RelacionElementoProyectoArtefacto buscar(String id) {
				return interno.relacionesProyectos().findOne(id);
			}

			@Override
			public <R extends RelacionElementoProyectoArtefacto> R guardar(R entidad) {
				return interno.relacionesProyectos().save(entidad);
			}

			@Override
			public <R extends RelacionElementoProyectoArtefacto> R guardarYVolcar(R entidad) {
				return interno.relacionesProyectos().saveAndFlush(entidad);
			}

			@Override
			public void volcar() {
				interno.relacionesProyectos().flush();
			}
		}
	}

	
	final static class Etiquetas extends ElementosProyecto.Etiquetas {

		private final IContextoEjecucionRepositorios interno;

		public Etiquetas(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}

		//--


		@Override
		public EtiquetaProyecto buscar(String id) {
			return interno.etiquetas().findOne(id);
		}

		@Override
		public <R extends EtiquetaProyecto> R guardar(R entidad) {
			return interno.etiquetas().save(entidad);
		}

		@Override
		public <R extends EtiquetaProyecto> R guardarYVolcar(R entidad) {
			return interno.etiquetas().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.etiquetas().flush();
		}
		
		@Override
		public RelacionesArtefactos relaciones() {
			return new RelacionArtefactos(interno);
		}
		
		// ----
		
		final static class RelacionArtefactos extends ElementosProyecto.Etiquetas.RelacionesArtefactos {

			private final IContextoEjecucionRepositorios interno;
			
			public RelacionArtefactos(final IContextoEjecucionRepositorios interno) {
				this.interno = interno;
			}
			
			@Override
			public RelacionElementoProyectoArtefacto buscar(String id) {
				return interno.relacionesProyectos().findOne(id);
			}

			@Override
			public <R extends RelacionElementoProyectoArtefacto> R guardar(R entidad) {
				return interno.relacionesProyectos().save(entidad);
			}

			@Override
			public <R extends RelacionElementoProyectoArtefacto> R guardarYVolcar(R entidad) {
				return interno.relacionesProyectos().saveAndFlush(entidad);
			}

			@Override
			public void volcar() {
				interno.relacionesProyectos().flush();
			}
			
			@Override
			public Collection<RelacionElementoProyectoArtefacto> artefactosAfectados(EtiquetaProyecto etiqueta) {
				return 
						interno
						.relacionesProyectos()
						.findAll(
								ArtefactosProyecto
								.relacionesProyectoArtefactoParaProyecto(etiqueta)
						);
			}
		}
	}
	
	final static class RelacionArtefactos extends ElementosProyecto.RelacionesArtefactos {

		private final IContextoEjecucionRepositorios interno;
		
		public RelacionArtefactos(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}

		@Override
		public Collection<RelacionElementoProyectoArtefacto> artefactosAfectados(
				ElementoBaseProyecto elementoProyecto) {
			return 
					interno
					.relacionesProyectos()
					.findAll(
							ArtefactosProyecto
							.relacionesProyectoArtefactoParaProyecto(elementoProyecto)
					);
		}

		@Override
		public RelacionElementoProyectoArtefacto buscar(String id) {
			return interno.relacionesProyectos().findOne(id);
		}

		@Override
		public <R extends RelacionElementoProyectoArtefacto> R guardar(R entidad) {
			return interno.relacionesProyectos().save(entidad);
		}

		@Override
		public <R extends RelacionElementoProyectoArtefacto> R guardarYVolcar(R entidad) {
			return interno.relacionesProyectos().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.relacionesProyectos().flush();
		}
		
	}
	
	final static class Usos extends ElementosProyecto.UsosYProyecciones {

		private final IContextoEjecucionRepositorios interno;

		public Usos(final IContextoEjecucionRepositorios interno) {
			this.interno = interno;
		}
		// --
		
		@Override
		public UsoYProyeccionProyecto buscar(String id) {
			return interno.usosProyecto().findOne(id);
		}

		@Override
		public <R extends UsoYProyeccionProyecto> R guardar(R entidad) {
			return interno.usosProyecto().save(entidad);
		}

		@Override
		public <R extends UsoYProyeccionProyecto> R guardarYVolcar(R entidad) {
			return interno.usosProyecto().saveAndFlush(entidad);
		}

		@Override
		public void volcar() {
			interno.usosProyecto().flush();
		}
	}
}
