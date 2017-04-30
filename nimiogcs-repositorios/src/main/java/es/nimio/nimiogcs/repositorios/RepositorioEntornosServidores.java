package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;

public interface RepositorioEntornosServidores 
	extends 
		JpaRepository<RelacionEntornoServidor, String>,
		JpaSpecificationExecutor<RelacionEntornoServidor> {

}
