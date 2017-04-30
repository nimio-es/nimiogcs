package es.nimio.nimiogcs.servicios;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.nimio.nimiogcs.componentes.IVariablesEntorno;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.repositorios.IRegistroRepositorios;
import es.nimio.nimiogcs.jpa.repositorios.RegistroRepositorios;
import es.nimio.nimiogcs.modelo.IUsuario;
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

/**
 * Estamos pasando en cientos de sitios los mismos repositorios, las mismas operaciones, etc., etc.
 * Cada vez que queremos otra instancia de algo añadimos otro campo y/o modificamos el con constructor.
 * Esta clase/servicio nace con la sana intención de que se inyecten todos los repositorios y todos
 * los servicios/componentes que se vayan requiriendo de forma general y sea solamente ésta la que se
 * pase en los constructores que se necesiten. 
 */
@Service
public class ContextoEjecucion implements IContextoEjecucion {


	// ================================================
	// Variables locales
	// ================================================
	private final Environment environment;
	private final ApplicationContext ac;

	@Autowired
	public ContextoEjecucion(
			final ApplicationContext ac, 
			final Environment environment) {
		this.environment = environment;
		this.ac = ac;
	}

	// -----
	

	// ================================================
	// Acceso a los servicios
	// ================================================

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Entorno
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * El entorno de la aplicación
	 */
	@Override
	public Environment environment() { return this.environment; }
	
	@Override
	public IVariablesEntorno variablesEntorno() {
		return new IVariablesEntorno() {
			
			@Override
			public String propiedadRequerida(String propiedad) {
				return environment().getRequiredProperty(propiedad);
			}
		};
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Contexto de aplicación
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Contexto de aplicación Spring
	 */
	@Override
	public ApplicationContext contextoAplicacion() { return ac; }
	
	/**
	 * Ejecutor de operaciones asíncronas
	 */
	@Override
	public Executor executor() { return (TaskExecutor)ac.getBean("executor"); }
	
	/**
	 * Scheduler disponible 
	 */
	@Override
	public TaskScheduler scheduler() { return (TaskScheduler)ac.getBean("scheduler"); }
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Petición entrante del Servlet
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Override
	public HttpServletRequest servletRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		if(requestAttributes==null) return null;
		return requestAttributes.getRequest(); 
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Usuario
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	@Override
	public IUsuario usuario() {
		SecurityContext contexto = SecurityContextHolder.getContext();
		if(contexto==null) return null;
		final Authentication authentication = contexto.getAuthentication();
		if(authentication == null) return null;
		if(authentication instanceof AnonymousAuthenticationToken) return null;
		return 
				new IUsuario() {
					@Override
					public String getNombre() {
						return ((User)authentication.getPrincipal()).getUsername();
					}
					@Override
					public Collection<String> getAutorizaciones() {
						return 
								Collections.list(
										Streams.of(((User)authentication.getPrincipal()).getAuthorities())
										.map(
												new Function<GrantedAuthority, String>() {
													@Override
													public String apply(GrantedAuthority grant) {
														return grant.getAuthority();
													}
												}
										)
										.getEnumeration()
								);
					}
				};
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Repositorios
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	

	@Override
	public IRegistroRepositorios repos() {
		return new RegistroRepositorios(this);
	}	
	
	@Override
	public RepositorioParametrosGlobales global() {
		return ac.getBean(RepositorioParametrosGlobales.class);
	}
	
	@Override
	public RepositorioTiposDirectivas tiposDirectivas() {
		return ac.getBean(RepositorioTiposDirectivas.class);
	}
	
	@Override
	public RepositorioTiposArtefactos tipos() {
		return ac.getBean(RepositorioTiposArtefactos.class);
	}
	
	@Override
	public RepositorioAplicaciones aplicaciones() {
		return ac.getBean(RepositorioAplicaciones.class);
	}
	
	/**
	 * Repositorio de acceso a los artefactos (clase base)
	 */
	@Override
	public RepositorioArtefactos artefactos() { 
		return ac.getBean(RepositorioArtefactos.class); 
	}

	@Override
	public RepositorioDirectivas directivas() {
		return ac.getBean(RepositorioDirectivas.class);
	}

	@Override
	public RepositorioDiccionariosDirectivas diccionariosDirectivas() {
		return ac.getBean(RepositorioDiccionariosDirectivas.class);
	}
	
	@Override
	public RepositorioDiccionariosDirectivasDefiniciones definicionesDiccionarios() {
		return ac.getBean(RepositorioDiccionariosDirectivasDefiniciones.class);
	}
	
	/**
	 * Las dependencias de los artefactos
	 */
	@Override
	public RepositorioDependenciasArtefactos dependenciasArtefactos() {
		return ac.getBean(RepositorioDependenciasArtefactos.class);
	}
		
	/**
	 * Repositorio de acceso a los proyectos
	 */
	@Override
	public RepositorioProyectos proyectos() { 
		return ac.getBean(RepositorioProyectos.class); 
	}
	
	@Override
	public RepositorioEtiquetasProyectos etiquetas() {
		return ac.getBean(RepositorioEtiquetasProyectos.class);
	}
	
	@Override
	public RepositorioProyectosRelaciones relacionesProyectos() {
		return ac.getBean(RepositorioProyectosRelaciones.class);
	}
	
	@Override
	public RepositorioUsosYProyeccionesProyecto usosProyecto() {
		return ac.getBean(RepositorioUsosYProyeccionesProyecto.class);
	}
	
	/**
	 * Repositorio de acceso a las operaciones
	 */
	@Override
	public RepositorioOperaciones operaciones() { 
		return ac.getBean(RepositorioOperaciones.class); 
	}
	
	/**
	 * Las relaciones de elementos con operaciones
	 */
	@Override
	public RepositorioRelacionesOperaciones relacionesOperaciones() {
		return ac.getBean(RepositorioRelacionesOperaciones.class);
	}

	/**
	 * Repositorio de acceso a los sitios.
	 * @return
	 */
	@Override
	public RepositorioServidores servidores() {
		return ac.getBean(RepositorioServidores.class);
	}
	
	/**
	 * Artefactos instalados o vinculados a los servidores
	 */
	@Override
	public RepositorioServidoresArtefactos servidoresArtefactos() {
		return ac.getBean(RepositorioServidoresArtefactos.class);
	}
	
	/**
	 * Repositorio con los entornos
	 */
	@Override
	public RepositorioDestinosPublicacion destinosPublicacion() { 
		return ac.getBean(RepositorioDestinosPublicacion.class); 
	}
	
	@Override
	public RepositorioPublicaciones publicaciones() {
		return ac.getBean(RepositorioPublicaciones.class); 
	}
	
	@Override
	public RepositorioPublicacionesArtefactos artefactosPublicados() {
		return ac.getBean(RepositorioPublicacionesArtefactos.class); 
	}
	
	/**
	 * Relaciones de los entornos con los servidores
	 */
	@Override
	public RepositorioEntornosServidores entornosServidores() {
		return ac.getBean(RepositorioEntornosServidores.class);
	}
	
	/**
	 * Repositorio con los codigos
	 */
	@Override
	public RepositorioRepositoriosCodigo repositorios(){
		return ac.getBean(RepositorioRepositoriosCodigo.class);
	}
	
	@Override
	public RepositorioUsuarios usuarios() {
		return ac.getBean(RepositorioUsuarios.class);
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// IContextoEjecucionBase
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Override
	public <T> T componente(Class<T> cz) {
		return ac.getBean(cz);
	}
	
	@Override
	public <T> Collection<T> componentes(Class<T> cz) {
		return ac.getBeansOfType(cz).values();
	}
}
