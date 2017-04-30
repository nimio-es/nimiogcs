package es.nimio.nimiogcs.repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;

public interface RepositorioProyectos extends JpaRepository<Proyecto, String>, JpaSpecificationExecutor<Proyecto> {
	
	Proyecto findByNombre(String nombre);
	
	Proyecto findByIdOrNombre(String id, String nombre);

	// -------------------------------------------
	// Soporte al listado
	// -------------------------------------------

	/**
	 * Pagina todos los proyectos ordenados por el nombre.
	 */
	@Query(
			value = "SELECT p FROM Proyecto p ORDER BY p.nombre",
			countQuery = "SELECT count(p) FROM Proyecto p"
			)
	Page<Proyecto> proyectos(Pageable pg);
	
	/**
	 * Pagina todos los proyectos donde el nombre empieza con el valor indicado.
	 */
	@Query(
			value = "SELECT p FROM Proyecto p WHERE p.nombre like :nombre ORDER BY p.nombre",
			countQuery = "SELECT count(p) FROM Proyecto p WHERE p.nombre like :nombre"
			)
	Page<Proyecto> proyectosCuyoNombreEmpiezaCon(Pageable pg, @Param("nombre") String nombre);
	
	// -------------------------------------------
	
	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END "
			+ "FROM EtiquetaProyecto e "
			+ "WHERE e.proyecto.id = :id "
			+ "AND e.nombre = :etiqueta")
	int existeEtiquetaDeProyectoConNombre(@Param("id") String id, @Param("etiqueta") String etiqueta);
	
}
