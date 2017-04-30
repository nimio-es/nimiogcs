package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;

public final class ArtefactosProyecto {

	private ArtefactosProyecto() {}
	
	/**
	 * Relaciones que tiene un proyecto con los artefactos que afecta
	 */
	public static Specification<RelacionElementoProyectoArtefacto> relacionesProyectoArtefactoParaProyecto(final ElementoBaseProyecto elemento) {
		
		return new Specification<RelacionElementoProyectoArtefacto>() {

			@Override
			public Predicate toPredicate(Root<RelacionElementoProyectoArtefacto> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {

				// y que tengan el id del propio proyecto
				return  cb.equal(root.get("elementoProyecto").<String>get("id"), elemento.getId());
			}
		};
	}

	/**
	 * Devuelve todos los proyectos en los que interviene el artefacto
	 */
	public static Specification<RelacionElementoProyectoArtefacto> relacionesDeUnArtefactoConProyectos(final Artefacto artefacto) {
		
		return new Specification<RelacionElementoProyectoArtefacto>() {

			@Override
			public Predicate toPredicate(Root<RelacionElementoProyectoArtefacto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				// queremos solamente los de tipo proyecto
				Predicate pp = cb.equal(root.type(), cb.literal(Proyecto.class));
				
				// la parte directa (cuando el artefacto está referenciado directamente por el proyecto)
				Predicate pd = cb.equal(root.get("artefacto").get("id"), artefacto.getId());
				
				// la parte indirecta (cuando existe una relación con un artefacto evolutivo y éste afecta al artefacto que andamos buscando)
				Subquery<EvolucionArtefacto> sq = query.subquery(EvolucionArtefacto.class);
				Root<EvolucionArtefacto> re = sq.from(EvolucionArtefacto.class);
				Predicate ps = 
						cb.and(
								cb.equal(root.get("artefacto").get("id"), re.get("id")),
								cb.equal(re.get("artefacto").get("id"), artefacto.getId())
						);
				Predicate pi = cb.exists(sq.where(ps));
				
				// devolvemos la composición
				return cb.and(pp, cb.or(pd, pi));
			}
			
		};
	}
}
