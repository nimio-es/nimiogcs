package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.CongelarEvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.modelo.enumerados.EnumEstadoValidezYActividad;

public final class Artefactos {

	private Artefactos() {}
	
	/**
	 * Busca artefactos con un nombre determinado
	 */
	public static final Specification<Artefacto> artefactoConNombre(final String nombre) {
		return new Specification<Artefacto>() {
			@Override
			public Predicate toPredicate(Root<Artefacto> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.equal(cb.upper(root.<String>get("nombre")), nombre.toUpperCase());
			}
		};		
	}
	
	/**
	 * Lista todos los artefactos de un tipo determinado
	 */
	public static final Specification<Artefacto> artefactosDeUnTipo(final TipoArtefacto tipo) {
		return new Specification<Artefacto>() {
			@Override
			public Predicate toPredicate(Root<Artefacto> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.equal(root.get("tipo").get("id"), tipo.getId());
			}
		};
	}

	/**
	 * Lista todos los artefactos activos de un tipo determinado
	 */
	public static final Specification<Artefacto> artefactosActivosDeUnTipo(final TipoArtefacto tipo) {
		return new Specification<Artefacto>() {
			@Override
			public Predicate toPredicate(Root<Artefacto> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.and(
						cb.equal(root.get("tipo").get("id"), tipo.getId()),
						cb.equal(root.get("estadoValidezActivacion"), EnumEstadoValidezYActividad.ValidoActivo)
				);
			}
		};
	}

	
	/**
	 * Devuelve una especificación capaz de filtrar por el tipo y por el nombre.
	 * A usar en la página con el litado de artefactos.
	 */
	public static Specification<Artefacto> artefactosDeTipoYConNombre(
			final String filtroTipo, 
			final String filtroNombre) {
		
		
		return new Specification<Artefacto>() {

			@Override
			public Predicate toPredicate(Root<Artefacto> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				Predicate p = root.isNotNull();
				
				boolean filtrarNombre = (filtroNombre != null && !filtroNombre.isEmpty());
				boolean filtrarTipo = (filtroTipo != null && !filtroTipo.isEmpty() && !filtroTipo.equalsIgnoreCase("TODOS"));
				
				// filtrar por nombre, solo cuando se ha indicado que se filtre
				if(filtrarNombre) {
					// el patrón de búsqueda para el nombre
					String patronBusqueda = new StringBuilder()
						.append("%")
						.append(filtroNombre.toLowerCase())
						.append("%")
						.toString();
				
					p = cb.and(p, cb.like(cb.lower(root.<String>get("nombre")), patronBusqueda));
				}
				
				// filtrar por tipo, cuando se pide que se incluyan únicamente los de ese tipo
				if(filtrarTipo) {
					p = cb.and(p, cb.equal(root.get("tipo").get("id"), filtroTipo));
				}
				
				return p;
			}
		};
	}	
	
	/**
	 * Devuelve todos los testaferros asociados a un artefacto
	 */
	public static Specification<Artefacto> testaferrosDeUnArtefacto(final Artefacto artefacto) {
		
		return new Specification<Artefacto>() {

			@Override
			public Predicate toPredicate(Root<Artefacto> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				Subquery<EvolucionArtefacto> sqe = query.subquery(EvolucionArtefacto.class);
				Root<EvolucionArtefacto> re = sqe.from(EvolucionArtefacto.class);
				Predicate pe = cb.equal(re.get("artefacto").get("id"), artefacto.getId());

				Subquery<CongelarEvolucionArtefacto> sqc = query.subquery(CongelarEvolucionArtefacto.class);
				Root<CongelarEvolucionArtefacto> rc = sqc.from(CongelarEvolucionArtefacto.class);
				Predicate pc = cb.equal(rc.get("artefacto").get("id"), artefacto.getId());
				
				return cb.or(pe, pc);
			}
			
		};
	}
}
