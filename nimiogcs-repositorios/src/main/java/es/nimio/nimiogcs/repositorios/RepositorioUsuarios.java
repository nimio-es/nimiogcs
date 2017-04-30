package es.nimio.nimiogcs.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;

public interface RepositorioUsuarios extends JpaRepository<Usuario, String> {

	boolean exists(String userName);

}
