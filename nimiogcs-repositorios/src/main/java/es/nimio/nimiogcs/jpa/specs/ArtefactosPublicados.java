package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;

public class ArtefactosPublicados {

	private ArtefactosPublicados() {}
	
	/**
	 * Todos los elementos incluidos en un proceso de publicación
	 * @param publicacion
	 * @return
	 */
	public static Specification<PublicacionArtefacto> elementosPublicados(final Publicacion publicacion) {
		return new Specification<PublicacionArtefacto>() {
			
			@Override
			public Predicate toPredicate(Root<PublicacionArtefacto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("publicacion").get("id"), publicacion.getId());
			}
		};
	}
	
	
	/**
	 * Todos los elementos publicados de una etiqueta determinada
	 */
	public static Specification<PublicacionArtefacto> elementosPublicadosEtiqueta(final EtiquetaProyecto etiqueta) {
		return new Specification<PublicacionArtefacto>() {
			
			@Override
			public Predicate toPredicate(Root<PublicacionArtefacto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.<String>get("idEtiqueta"), etiqueta.getId());
			}
		};
	}
	
}
