package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;

public interface RepositorioDiccionariosDirectivas extends JpaRepository<TipoDirectivaDiccionario, String>, JpaSpecificationExecutor<TipoDirectivaDiccionario> {

}
