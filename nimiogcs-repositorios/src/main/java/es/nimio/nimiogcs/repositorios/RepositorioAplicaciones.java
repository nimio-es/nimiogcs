package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import es.nimio.nimiogcs.jpa.entidades.sistema.AplicacionEmpresa;

public interface RepositorioAplicaciones extends JpaRepository<AplicacionEmpresa, String> {

}
