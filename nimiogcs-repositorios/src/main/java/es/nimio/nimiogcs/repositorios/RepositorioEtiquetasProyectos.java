package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;

public interface RepositorioEtiquetasProyectos 
	extends 
		JpaRepository<EtiquetaProyecto, String>, 
		JpaSpecificationExecutor<EtiquetaProyecto> {
}
