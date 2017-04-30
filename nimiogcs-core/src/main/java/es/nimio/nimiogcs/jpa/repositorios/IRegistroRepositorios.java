package es.nimio.nimiogcs.jpa.repositorios;

public interface IRegistroRepositorios {

	ParametrosGlobales global();
	
	Operaciones operaciones();

	Usuarios usuarios();
	
	Artefactos artefactos();

	ElementosProyecto elementosProyectos();
	
	DestinosPublicacion destinosPublicacion();
	
	Servidores servidores();
	
	Publicaciones publicaciones();
	
	ArtefactosPublicados artefactosPublicados();
}
