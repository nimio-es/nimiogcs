package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;

public final class EntornosServidores {

	private EntornosServidores() {}
	
	/**
	 * Los servidores que pertenecen a un entorno determinado
	 */
	public static Specification<RelacionEntornoServidor> servidoresEntorno(final DestinoPublicacion entorno) {
		
		return new Specification<RelacionEntornoServidor>() {
			
			@Override
			public Predicate toPredicate(Root<RelacionEntornoServidor> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.equal(root.get("entorno").get("id"), entorno.getId());
			}
		};
		
	}
}
