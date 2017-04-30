package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;

public class Publicaciones {

	private Publicaciones() {}
	
	public static Specification<Publicacion> publicacionesEnEjecucion() {
		return new Specification<Publicacion>() {
			
			@Override
			public Predicate toPredicate(Root<Publicacion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.<String>get("estado"), K.X.EJECUCION);
			}
		};
	}
	
	/**
	 * Todas las publicaciones de un artefacto dado
	 */
	public static Specification<Publicacion> publicacionesDeUnArtefacto(final Artefacto artefacto) {
		
		return new Specification<Publicacion>() {
			
			@Override
			public Predicate toPredicate(Root<Publicacion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				// nos basamos en una subquery
				Subquery<PublicacionArtefacto> sq = query.subquery(PublicacionArtefacto.class);
				Root<PublicacionArtefacto> rs = sq.from(PublicacionArtefacto.class);
				Predicate ps =
						cb.and(
								cb.equal(rs.get("artefacto").get("id"), artefacto.getId()),
								cb.equal(rs.get("publicacion").get("id"), root.get("id"))
						);
				
				return cb.exists(sq.where(ps));
			}
		};
	}
	
	public static Specification<Publicacion> publicacionesDeUnDestino(final DestinoPublicacion destino) {
		
		return new Specification<Publicacion>() {
			@Override
			public Predicate toPredicate(Root<Publicacion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.<String>get("idDestinoPublicacion"), destino.getId());
			}
		};
	}
	
}
