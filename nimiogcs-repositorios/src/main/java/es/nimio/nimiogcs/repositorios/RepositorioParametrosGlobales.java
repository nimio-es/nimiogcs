package es.nimio.nimiogcs.repositorios;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;

@Lazy
public interface RepositorioParametrosGlobales extends JpaRepository<ParametroGlobal, String> , JpaSpecificationExecutor<ParametroGlobal> {
	
}
