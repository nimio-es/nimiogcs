package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;

public interface RepositorioProyectosRelaciones 
	extends 
		JpaRepository<RelacionElementoProyectoArtefacto, String>,
		JpaSpecificationExecutor<RelacionElementoProyectoArtefacto> {

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END "
			+ "FROM Proyecto p, RelacionElementoProyectoArtefacto r "
			+ "WHERE r.artefacto.id = :id "
			+ "AND p.id = r.elementoProyecto.id "
			+ "AND p.estado = es.nimio.nimiogcs.jpa.enumerados.EnumEstadoProyecto.Abierto")
	int existeProyectoAfectaArtefacto(@Param("id") String id);

}
