package es.nimio.nimiogcs.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;

/**
 * Repositorio (de base de datos) de repositorios (de código)
 */
public interface RepositorioRepositoriosCodigo extends JpaRepository<RepositorioCodigo, String>{

	@Query("SELECT rc FROM RepositorioCodigo rc WHERE rc.paraProyectos = 'SI'")
	List<RepositorioCodigo> repositoriosParaProyectos();
}
