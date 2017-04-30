package es.nimio.nimiogcs.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;

public interface RepositorioDirectivas extends JpaRepository<DirectivaBase, String> {

	@Query("select dm from DirectivaCoordenadasMaven dm "
			+ "where dm.idGrupo = :idGrupo and dm.idArtefacto = :idArtefacto "
			+ "and dm.version = :version and empaquetado = :empaquetado ")
	List<DirectivaCoordenadasMaven> directivasMaven(
			@Param("idGrupo") String idGrupo, 
			@Param("idArtefacto") String idArtefacto,
			@Param("version") String version,
			@Param("empaquetado") String empaquetado);
	
	@Query("select COUNT(dd) from DirectivaDiccionario dd where dd.diccionario.id = :idDiccionario")
	long contarDirectivasDiccionario(@Param("idDiccionario") String idDiccionario);
	
}
