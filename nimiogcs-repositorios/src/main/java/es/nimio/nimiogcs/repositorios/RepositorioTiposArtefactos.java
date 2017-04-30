package es.nimio.nimiogcs.repositorios;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;

@Lazy
public interface RepositorioTiposArtefactos extends JpaRepository<TipoArtefacto, String> {

}
