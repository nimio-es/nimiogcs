package es.nimio.nimiogcs.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;

public interface RepositorioDependenciasArtefactos extends JpaRepository<Dependencia, String>{
	
	// --------------------------------------------------------
	// Relaciones de artefactos con artefactos
	// --------------------------------------------------------

	/**
	 * El total de relaciones que hay de un artefacto con otros artefactos que lo requieren
	 */
	@Query(value="SELECT COUNT(d) FROM Dependencia d WHERE d.requerido.id = :id")
	long totalRelacionesDependenciaConDestinoElArtefacto(@Param("id") String id);

	@Query(value="SELECT r FROM Dependencia r WHERE r.dependiente.id = :id")
	List<Dependencia> relacionesDependenciaDeUnArtefacto(@Param("id") String id);
	
	@Query(value="SELECT r FROM Dependencia r WHERE r.requerido.id = :id")
	List<Dependencia> relacionesRequierenDeUnArtefacto(@Param("id") String id);

}
