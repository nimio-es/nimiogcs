package es.nimio.nimiogcs.repositorios;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;

@Lazy
public interface RepositorioPublicaciones 
	extends 
		JpaRepository<Publicacion, String>, 
		JpaSpecificationExecutor<Publicacion> {
	
	@Query(value="select p "
			   + "  from Publicacion p, PublicacionArtefacto pa "
			   + " where pa.publicacion = p"
			   + "   and pa.proyecto.id = :idProyecto")
	Page<Publicacion> publicacionesProyecto(@Param("idProyecto") String idProyecto, Pageable pg);
}
