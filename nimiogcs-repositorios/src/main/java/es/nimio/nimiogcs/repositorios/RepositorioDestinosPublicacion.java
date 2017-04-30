package es.nimio.nimiogcs.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;

public interface RepositorioDestinosPublicacion extends JpaRepository<DestinoPublicacion, String> {

	@Query("SELECT p FROM Operacion p "
			+ "WHERE p.finalizado = 'NO' "
			+ "AND EXISTS(SELECT r FROM RelacionOperacionDestinoPublicacion r, DestinoPublicacion e "
			+ "   WHERE e.nombre = :nombreEntorno "
			+ "     AND r.operacion.id = p.id"
			+ "     AND r.entorno.id = e.id) "
			+ "ORDER BY p.creacion DESC")
	List<Operacion> operacionesEnEjecucionDeUnEntorno(@Param("nombreEntorno") String nombreEntorno);
}
