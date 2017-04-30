package es.nimio.nimiogcs.jpa.repositorios;

import java.util.List;

import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;

public abstract class ArtefactosPublicados 
	extends RepositorioBase<PublicacionArtefacto>{

	public abstract List<PublicacionArtefacto> elementosPublicados(Publicacion publicacion);
}
