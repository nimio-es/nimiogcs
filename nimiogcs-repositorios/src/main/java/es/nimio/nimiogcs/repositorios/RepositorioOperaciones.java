package es.nimio.nimiogcs.repositorios;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoBatch;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;

public interface RepositorioOperaciones extends JpaRepository<Operacion, String>, JpaSpecificationExecutor<Operacion> {

	@Query(value="SELECT o FROM Operacion o "
			+ "WHERE o.finalizado = 'NO' "
			+ "   OR (o.estadoEjecucionProceso = es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso.ERROR "
			+ "  AND o.modificacion > :fechaCorte) " 
			+ "ORDER BY o.modificacion DESC",
			countQuery="SELECT count(o) FROM Operacion o "
					+ "WHERE o.finalizado = 'NO' "
					+ "   OR (o.estadoEjecucionProceso = es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso.ERROR "
					+ "  AND o.modificacion > :fechaCorte)")
	Page<Operacion> operacionesConInteresPaginaInicial(Pageable pg, @Param("fechaCorte") Date fechaCorte);
	
	@Query(value="SELECT pb FROM ProcesoBatch pb WHERE pb.idBatchJobExecution = :idBatchJobExecution")
	ProcesoBatch procesoBatchConIdDeEjecucion(@Param("idBatchJobExecution") long idBatchJobExecution);
	
	@Query(value="SELECT o FROM Operacion o, RelacionOperacionArtefacto r "
			+ "WHERE r.operacion = o "
			+ "AND r.entidad.id = :id "
			+ "ORDER BY o.creacion DESC",
			countQuery="SELECT count(o) FROM Operacion o, RelacionOperacionArtefacto r "
			+ "WHERE r.operacion = o "
			+ "AND r.entidad.id = :id")
	Page<Operacion> operacionesDeArtefacto(Pageable pg, @Param("id") String id);
	
	/**
	 * Devuelve un valor numérico si el artefacto tiene operaciones en curso.
	 */
	@Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END "
			+ "FROM Operacion o, RelacionOperacionArtefacto r "
			+ "WHERE r.operacion = o "
			+ "AND r.entidad.id = :id "
			+ "AND o.finalizado = 'NO'")
	int artefactoConOperacionesEnCurso(@Param("id") String id);

	/**
	 * Devuelve un proceso de espera asociado a un ticket
	 */
	@Query("SELECT pe FROM ProcesoEspera pe WHERE pe.ticketRelacionado = :ticket")
	ProcesoEspera procesoEsperaConTicket(@Param("ticket") String ticket);

	/**
	 * Devuelve, si lo encuentra, el proceso de espera asociado a una respuesta desde Deployer.
	 */
	@Query("SELECT pr FROM ProcesoEsperaRespuestaDeployer pr WHERE pr.etiquetaPase = :etiquetaPase")
	ProcesoEsperaRespuestaDeployer procesoEsperaRespuestaDeployerConEtiquetaPase(@Param("etiquetaPase") String etiquetaPase);
	
}
