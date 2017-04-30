package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionTipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;

public final class Operaciones {

	private Operaciones() {}
	
	/**
	 * Devuelve una especificación con todas las operacioes que aún no han finalizado
	 */
	public static Specification<Operacion> noFinalizadas() {
		
		return new Specification<Operacion>() {
			
			@Override
			public Predicate toPredicate(Root<Operacion> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.equal(root.get("finalizado"), K.L.NO);
			}
		};
	}
	
	public static Specification<Operacion> operacionesDeUnTipoArtefacto(final TipoArtefacto tipo) {
		
		return new Specification<Operacion>() {
			@Override
			public Predicate toPredicate(Root<Operacion> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				Root<RelacionOperacionTipoArtefacto> rt = cq.from(RelacionOperacionTipoArtefacto.class);
				return cb.equal(rt.get("tipoArtefacto").get("id"), tipo.getId());
			}
		};
	}

	/**
	 * Operaciones de una etiqueta o un proyecto
	 */
	public static Specification<Operacion> operacionesDeUnElementoProyecto(final ElementoBaseProyecto elementoProyecto) {
		
		return new Specification<Operacion>() {
			@Override
			public Predicate toPredicate(Root<Operacion> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				Root<RelacionOperacionElementoProyecto> rt = cq.from(RelacionOperacionElementoProyecto.class);
				return cb.equal(rt.get("elementoProyecto").get("id"), elementoProyecto.getId());
			}
		};
	}

}
