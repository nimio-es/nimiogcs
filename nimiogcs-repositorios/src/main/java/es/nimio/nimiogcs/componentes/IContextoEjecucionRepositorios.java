package es.nimio.nimiogcs.componentes;

import es.nimio.nimiogcs.repositorios.RepositorioAplicaciones;
import es.nimio.nimiogcs.repositorios.RepositorioArtefactos;
import es.nimio.nimiogcs.repositorios.RepositorioDependenciasArtefactos;
import es.nimio.nimiogcs.repositorios.RepositorioDestinosPublicacion;
import es.nimio.nimiogcs.repositorios.RepositorioDiccionariosDirectivas;
import es.nimio.nimiogcs.repositorios.RepositorioDiccionariosDirectivasDefiniciones;
import es.nimio.nimiogcs.repositorios.RepositorioDirectivas;
import es.nimio.nimiogcs.repositorios.RepositorioEntornosServidores;
import es.nimio.nimiogcs.repositorios.RepositorioEtiquetasProyectos;
import es.nimio.nimiogcs.repositorios.RepositorioOperaciones;
import es.nimio.nimiogcs.repositorios.RepositorioParametrosGlobales;
import es.nimio.nimiogcs.repositorios.RepositorioProyectos;
import es.nimio.nimiogcs.repositorios.RepositorioProyectosRelaciones;
import es.nimio.nimiogcs.repositorios.RepositorioPublicaciones;
import es.nimio.nimiogcs.repositorios.RepositorioPublicacionesArtefactos;
import es.nimio.nimiogcs.repositorios.RepositorioRelacionesOperaciones;
import es.nimio.nimiogcs.repositorios.RepositorioRepositoriosCodigo;
import es.nimio.nimiogcs.repositorios.RepositorioServidores;
import es.nimio.nimiogcs.repositorios.RepositorioServidoresArtefactos;
import es.nimio.nimiogcs.repositorios.RepositorioTiposArtefactos;
import es.nimio.nimiogcs.repositorios.RepositorioTiposDirectivas;
import es.nimio.nimiogcs.repositorios.RepositorioUsosYProyeccionesProyecto;
import es.nimio.nimiogcs.repositorios.RepositorioUsuarios;

public interface IContextoEjecucionRepositorios {

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Repositorios
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	RepositorioParametrosGlobales global();
	
	RepositorioTiposDirectivas tiposDirectivas();
	
	RepositorioTiposArtefactos tipos();
	
	/**
	 * Repositorio de acceso a las aplicaciones empresariales
	 */
	RepositorioAplicaciones aplicaciones();
	
	/**
	 * Repositorio de acceso a los artefactos (clase base)
	 */
	RepositorioArtefactos artefactos();
	
	/**
	 * Repositorio con acceso a las directivas, tanto de artefacto como de tipo de artefacto
	 */
	RepositorioDirectivas directivas();
	
	/**
	 * Los diccionarios que se podrán emplear para las directivas de tipo diccionario
	 */
	RepositorioDiccionariosDirectivas diccionariosDirectivas();
	
	RepositorioDiccionariosDirectivasDefiniciones definicionesDiccionarios();
	
	/**
	 * Las dependencias de los artefactos
	 */
	RepositorioDependenciasArtefactos dependenciasArtefactos();
	
	/**
	 * Repositorio de acceso a los proyectos
	 */
	RepositorioProyectos proyectos();
	
	RepositorioEtiquetasProyectos etiquetas();
	
	RepositorioProyectosRelaciones relacionesProyectos();
	
	RepositorioUsosYProyeccionesProyecto usosProyecto();
	
	/**
	 * Repositorio de acceso a las operaciones
	 */
	RepositorioOperaciones operaciones();
	
	/**
	 * Las relaciones de elementos con operaciones
	 */
	RepositorioRelacionesOperaciones relacionesOperaciones();

	/**
	 * Repositorio de acceso a los sitios.
	 * @return
	 */
	RepositorioServidores servidores();
	
	/**
	 * Artefactos instalados o vinculados a los servidores
	 */
	RepositorioServidoresArtefactos servidoresArtefactos();
	
	/**
	 * Repositorio con los entornos
	 */
	RepositorioDestinosPublicacion destinosPublicacion();
	
	/**
	 * Repositorio con acceso a las publicaciones
	 */
	RepositorioPublicaciones publicaciones();
	
	/**
	 * Acceso a los artefactos publicados
	 */
	RepositorioPublicacionesArtefactos artefactosPublicados();
	
	/**
	 * Relaciones de los entornos con los servidores
	 */
	RepositorioEntornosServidores entornosServidores();
	
	/**
	 * Repositorio con los codigos
	 */
	RepositorioRepositoriosCodigo repositorios();
	
	/**
	 * Repositorio de los usuarios registrados a nivel de aplicación
	 */
	RepositorioUsuarios usuarios();
	
}
