package es.nimio.nimiogcs.jpa.repositorios;

import es.nimio.nimiogcs.componentes.IContextoEjecucionRepositorios;
import es.nimio.nimiogcs.jpa.repositorios.Artefactos;
import es.nimio.nimiogcs.jpa.repositorios.ArtefactosPublicados;
import es.nimio.nimiogcs.jpa.repositorios.DestinosPublicacion;
import es.nimio.nimiogcs.jpa.repositorios.ElementosProyecto;
import es.nimio.nimiogcs.jpa.repositorios.IRegistroRepositorios;
import es.nimio.nimiogcs.jpa.repositorios.Operaciones;
import es.nimio.nimiogcs.jpa.repositorios.ParametrosGlobales;
import es.nimio.nimiogcs.jpa.repositorios.Publicaciones;
import es.nimio.nimiogcs.jpa.repositorios.Servidores;
import es.nimio.nimiogcs.jpa.repositorios.Usuarios;

public class RegistroRepositorios implements IRegistroRepositorios {

	private final IContextoEjecucionRepositorios interno;
	
	public RegistroRepositorios(IContextoEjecucionRepositorios interno) {
		this.interno = interno;
	}

	// ---
	
	@Override
	public ParametrosGlobales global() {
		return new ParametrosGlobalesImpl(interno);
	}
	
	@Override
	public Operaciones operaciones() {
		return new OperacionesImpl(interno);
	}

	@Override
	public Usuarios usuarios() {
		return new UsuariosImpl(interno);
	}

	@Override
	public Artefactos artefactos() {
		return new ArtefactosImpl(interno);
	}
	
	@Override
	public ElementosProyecto elementosProyectos() {
		return new ElementosProyectosImpl(interno);
	}
	
	@Override
	public DestinosPublicacion destinosPublicacion() {
		return new DestinosPublicacionImpl(interno);
	}
	
	@Override
	public Servidores servidores() {
		return new ServidoresImpl(interno);
	}
	
	@Override
	public Publicaciones publicaciones() {
		return new PublicacionesImpl(interno);
	}
	
	@Override
	public ArtefactosPublicados artefactosPublicados() {
		return new ArtefactosPublicadosImpl(interno);
	}
}
