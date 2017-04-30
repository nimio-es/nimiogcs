package es.nimio.nimiogcs.repositorios;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacionArtefacto;

public interface RepositorioRelacionesOperaciones 
	extends 
		JpaRepository<RelacionOperacion, String>,
		JpaSpecificationExecutor<RelacionOperacion> {

	/**
	 * Colección de relaciones en las que interviene una operación
	 */
	@Query("SELECT r FROM RelacionOperacion r WHERE r.operacion.id = :id")
	Collection<RelacionOperacion> relacionesDeUnaOperacion(@Param("id") String id);
	
	/**
	 * Todas las operaciones de un artefacto 
	 */
	@Query("SELECT r FROM RelacionOperacionArtefacto r WHERE r.entidad.id =  :id")
	Collection<RelacionOperacionArtefacto> operacionesDeUnArtefacto(@Param("id") String id);
	
	/**
	 * Todas las relaciones de operaciones de una etiqueta o un proyecto
	 */
	@Query("SELECT r FROM RelacionOperacionElementoProyecto r WHERE r.elementoProyecto.id = :id")
	Collection<RelacionOperacionElementoProyecto> operacionesDeUnElementoProyecto(@Param("id") String id);

	/**
	 * Las relaciones de publicación de operaciones donde aparece una etiqueta de proyecto
	 */
	@Query("SELECT r FROM RelacionOperacionPublicacionArtefacto r WHERE r.etiquetaProyecto.id = :id")
	Collection<RelacionOperacionPublicacionArtefacto> operacionesDePublicacionDeUnaEtiqueta(@Param("id") String id);
}
