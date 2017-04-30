package es.nimio.nimiogcs.repositorios;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;

@Lazy
public interface RepositorioPublicacionesArtefactos 
	extends 
		JpaRepository<PublicacionArtefacto, String>,
		JpaSpecificationExecutor<PublicacionArtefacto> {

	@Query(value="select pa "
			+ "from Publicacion p, PublicacionArtefacto pa "
			+ "where p.idDestinoPublicacion = :idDestino"
			+ "  and pa.publicacion = p"
			+ "  and pa.artefacto.id = :idArtefacto "
			+ "  and pa.proyecto.id = :idProyecto")
	Page<PublicacionArtefacto> deProyectoYArtefactoYDestino(
			@Param("idProyecto") String idProyecto, 
			@Param("idArtefacto") String idArtefacto,
			@Param("idDestino") String idDestino,
			Pageable pg);

}
