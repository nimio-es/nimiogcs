package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;

public final class ProyectosEtiquetas {

	public static Specification<EtiquetaProyecto> etiquetasDeProyecto(final Proyecto proyecto) {
		return new Specification<EtiquetaProyecto>() {
			@Override
			public Predicate toPredicate(Root<EtiquetaProyecto> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.equal(root.get("proyecto").get("id"), proyecto.getId());
			}
		};
	}
	
	private ProyectosEtiquetas() {}
}
