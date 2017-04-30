package es.nimio.nimiogcs.jpa.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;

public final class ServidoresArtefactos {

	private ServidoresArtefactos() {}
	
	/**
	 * Especificación para devolver todos los artefactos de un servidor que son de tipo EAR
	 */
	public static Specification<RelacionServidorArtefacto> earsEnServidor(final Servidor servidor) {
		
		return artefactosDeTipoEnServidor(servidor, "EAR");
	}
	
	/**
	 * Especificación para devolver todos los artefactos de un servidor que son de tipo WAR
	 */
	public static Specification<RelacionServidorArtefacto> warsEnServidor(final Servidor servidor) {

		return artefactosDeTipoEnServidor(servidor, "WAR");
	}
	
	/**
	 * Especificación para devolver todos los artefactos de un servidor que son de tipo POM
	 */
	public static Specification<RelacionServidorArtefacto> pomsEnServidor(final Servidor servidor) {
		
		return artefactosDeTipoEnServidor(servidor, "POM");
	}
	
	/**
	 * Especificación para devolver todos los artefactos de un servidor que son de un tipo determinado
	 */
	public static Specification<RelacionServidorArtefacto> artefactosDeTipoEnServidor(final Servidor servidor, final String tipoArtefacto) {
		
		return new Specification<RelacionServidorArtefacto>() {

			@Override
			public Predicate toPredicate(
					Root<RelacionServidorArtefacto> root, 
					CriteriaQuery<?> cq,
					CriteriaBuilder cb) {

				return 	
						cb.and(
								cb.equal(root.get("servidor").get("id"), servidor.getId()),
								cb.equal(root.get("artefacto").get("tipo").get("id"), tipoArtefacto)
						);
			}
		};
	}
}
