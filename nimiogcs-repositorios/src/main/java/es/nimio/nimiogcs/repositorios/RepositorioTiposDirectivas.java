package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;

public interface RepositorioTiposDirectivas extends JpaRepository<TipoDirectiva, String>{

}
