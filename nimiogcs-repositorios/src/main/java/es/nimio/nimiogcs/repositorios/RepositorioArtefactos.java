package es.nimio.nimiogcs.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;

/**
 * Repositorio para opereaciones transversales a todos los artefactos (indistintamente del tipo).
 */
public interface RepositorioArtefactos 
	extends 
		JpaRepository<Artefacto, String>, 
		JpaSpecificationExecutor<Artefacto> {

	// -----------------------------------------------------------------------
	// Artefactos que son de tipo evolutivo o congelación y que son testaferro
	// de un artefacto determinado
	// -----------------------------------------------------------------------
	
	@Query("SELECT t FROM Artefacto t WHERE "
			+ "EXISTS (SELECT e FROM EvolucionArtefacto e WHERE e.id = t.id AND e.artefacto.id = :id) "
			+ "OR EXISTS (SELECT c FROM CongelarEvolucionArtefacto c WHERE c.id = t.id AND c.artefacto.id = :id)")
	List<Artefacto> testaferrosArtefacto(@Param("id") String id);

	
	@Query(
			"SELECT t.id FROM Artefacto t, IN (t.directivas) d "
			+ "WHERE t.tipo.id = :idTipo "
			+ "  AND EXISTS ("
			+ "       SELECT dm"
			+ "         FROM DirectivaCoordenadasMaven dm"
			+ "        WHERE dm.id = d.id "
			+ "          AND dm.idGrupo = :idGrupo"
			+ "          AND dm.idArtefacto = :idArtefacto"
			+ "          AND dm.version = :version"
			+ "          AND dm.empaquetado = :empaquetado"
			+ "  )"
	)
	List<String> artefactoDeTipoYConCoordenadaMaven(
			@Param("idTipo") String idTipo,
			@Param("idGrupo") String idGrupo,
			@Param("idArtefacto") String idArtefacto,
			@Param("version") String version,
			@Param("empaquetado") String empaquetad);
	
	
	// -----------------------------------------------------------------------
	// Búsquedas que facilitan la localización de qué elementos pueden formar 
	// parte de las dependencias de un tipo determinado
	// -----------------------------------------------------------------------

	/**
	 * Devuelve todas las librerías compartidas que están activas actualmente.
	 */
	@Query("SELECT p FROM Artefacto p "
			+ "WHERE p.estadoValidezActivacion = es.nimio.nimiogcs.modelo.enumerados.EnumEstadoValidezYActividad.ValidoActivo "
			+ "  AND p.tipo.id = 'POM' "
			+ "ORDER BY p.nombre")
	List<Artefacto> pomsActivos();
	
	@Query("SELECT e FROM Artefacto e "
			+ "WHERE e.estadoValidezActivacion = es.nimio.nimiogcs.modelo.enumerados.EnumEstadoValidezYActividad.ValidoActivo "
			+ "  AND e.tipo.id = 'EAR' "
			+ "ORDER BY e.nombre")
	List<Artefacto> earsRegistrados(); 
	
	@Query("SELECT mw FROM Artefacto mw "
			+ "WHERE mw.estadoValidezActivacion = es.nimio.nimiogcs.modelo.enumerados.EnumEstadoValidezYActividad.ValidoActivo "
			+ "  AND mw.tipo.id = 'WAR' "
			+ "ORDER BY mw.nombre")
	List<Artefacto> modulosWebActivos();
}
