package es.nimio.nimiogcs.repositorios;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;

public interface RepositorioServidores extends JpaRepository<Servidor, String> {

	@Query("SELECT s FROM Servidor s ORDER BY s.nombre")
	List<Servidor> findAll();
	
	@Query("SELECT s FROM Servidor s WHERE s.id = :idNombre OR s.nombre = :idNombre")
	Servidor findByIdOrNombre(@Param("idNombre") String idNombre);
	
	
	// -------------------------------
	// Relacionadas con entornos
	// -------------------------------
	
	@Query("SELECT s FROM Servidor s WHERE NOT EXISTS(SELECT r FROM RelacionEntornoServidor r WHERE r.servidor = s) ORDER BY s.nombre")
	Collection<Servidor> servidoresAunNoRelacionadosConEntornos();
}
