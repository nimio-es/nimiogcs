package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;

public interface RepositorioDiccionariosDirectivasDefiniciones extends JpaRepository<TipoDirectivaDiccionarioDefinicion, String>{

}
