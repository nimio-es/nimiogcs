package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;

public interface RepositorioServidoresArtefactos 
	extends 
		JpaRepository<RelacionServidorArtefacto, String>,
		JpaSpecificationExecutor<RelacionServidorArtefacto> {

}
