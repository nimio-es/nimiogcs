package es.nimio.nimiogcs.servicios.externos;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import es.nimio.nimiogcs.errores.ErrorOperacionVersionadoCodigo;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;

public class Subversion {

	// --------------------------------------------------------
	// Inicialización estática
	// --------------------------------------------------------

	static {

		// iniciamos los distintos protocolos de trabajo
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	// --------------------------------------------------------
	// Construcción
	// --------------------------------------------------------

	/**
	 * Constructor en el que se le facilita directamente una estructura de configuración
	 */
	public Subversion(ConfiguracionSubversion configuracion) {
		this.configuracion = configuracion;
	}

	/**
	 * Constructor en el que se le facilita los datos que se requieren para conectar con el repositorio.
	 */
	public Subversion(final String repositorio, final String usuario, final String contrasena) {
		this(new ConfiguracionSubversion._(
			 repositorio,
			 usuario,
			 contrasena));
	}
	
	/**
	 * Constructor en el que se le está facilitando una entidad JPA que representa un repositorio de código
	 * @param repositorio
	 */
	public Subversion(RepositorioCodigo repositorio) {
		this(ConfiguracionSubversion._.desde(repositorio));
	}

	// --------------------------------------------------------
	// Estado
	// --------------------------------------------------------

	private ConfiguracionSubversion configuracion;

	// --------------------------------------------------------
	// Operaciones con el repositorio de código
	// --------------------------------------------------------

	/**
	 * Comprobar que existe un elemento en la ruta remota
	 */
	public boolean existeElementoRemoto(String subruta) {
		
		SVNClientManager svnCl = prepararCliente();
		
		try {
			SVNNodeKind nodeKind = svnCl
				.createRepository(SVNURL.parseURIEncoded(configuracion.getRaiz().toString()) , false)
				.checkPath(subruta, -1);
			
			if(nodeKind == SVNNodeKind.NONE) return false;
			return true;
		
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error comprobando la existencia de la ruta: " + subruta, e);
		} finally {
			svnCl.dispose();
		}
	}
	
	
	public synchronized void crearCarpeta(String subruta)
			throws ErrorOperacionVersionadoCodigo {

		SVNClientManager svnCl = prepararCliente();
		SVNCommitClient svnCmm = svnCl.getCommitClient();

		try {
			String url = configuracion.getRaiz().toString() + "/" + subruta;
			svnCmm.doMkDir(new SVNURL[] { SVNURL.parseURIEncoded(url) },
					"Nueva ruta", null, true);

		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al crear la carpeta: " + subruta, e);
		} finally {

			svnCl.dispose();
		}
	}

	/**
	 * Crea varias carpetas de forma remota en una única operación contra el repositorio.
	 * @param subrutas
	 * @throws ErrorOperacionVersionadoCodigo
	 */
	public synchronized void crearCarpetas(String... subrutas)
			throws ErrorOperacionVersionadoCodigo {

		SVNClientManager svnCl = prepararCliente();
		SVNCommitClient svnCmm = svnCl.getCommitClient();

		try {
			List<SVNURL> rutas = new ArrayList<SVNURL>();
			for (String subruta : subrutas) {
				String url = configuracion.getRaiz().toString() + "/" + subruta;
				rutas.add(SVNURL.parseURIEncoded(url));
			}

			svnCmm.doMkDir(rutas.toArray(new SVNURL[rutas.size()]),
					"Nueva ruta", null, true);

		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al crear las carpetas: " + subrutas, e);
		} finally {

			svnCl.dispose();
		}
	}

	/**
	 * Elimina una carpeta de forma remota.
	 * @param subruta
	 */
	public void eliminarCarpeta(String subruta) {

		SVNClientManager svnCl = prepararCliente();
		SVNCommitClient svnCmm = svnCl.getCommitClient();

		try {
			String url = configuracion.getRaiz().toString() + "/" + subruta;
			svnCmm.doDelete(new SVNURL[] { SVNURL.parseURIEncoded(url) },
					"Eliminar carpeta");

		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al eliminar la carpeta: " + subruta, e);
		} finally {
			svnCl.dispose();
		}
	}

	/**
	 * Copia una carpeta remota
	 * @param carpetasOrigen
	 * @param carpetasDestino
	 * @param mensaje
	 */
	public void copiarCarpetas(
			String carpetasOrigen, 
			String carpetasDestino, 
			String mensaje) {

		SVNClientManager svnCl = prepararCliente();
		SVNCopyClient svnCpy = svnCl.getCopyClient();
		
		try {

			svnCpy.doCopy(
					new SVNCopySource[] {
							new SVNCopySource(
									SVNRevision.HEAD,
									SVNRevision.HEAD, 
									SVNURL.parseURIEncoded(carpetasOrigen))
					}, 
					SVNURL.parseURIEncoded(carpetasDestino), 
					false, 
					true, 
					false, 
					mensaje, 
					null);
				
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al copiar la carpeta: " + carpetasOrigen, e);
		} finally {
			svnCl.dispose();
		}
	}
	
	
	/**
	 * Realiza checkout desde una ruta de repositorio a una carpeta local.
	 * @param rutaRepositorio
	 * @param carpetaLocal
	 * @throws SVNException 
	 */
	public void checkout(String rutaRepositorio, File carpetaLocal) {
		checkout(rutaRepositorio, carpetaLocal, false);
	}
	
	/**
	 * Realiza checkout desde una ruta de repositorio a la carpeta local aplicando la opción indicada con los externos.
	 * 
	 * @param rutaRepositorio
	 * @param carpetaLocal
	 * @param ignorarExternos
	 */
	public void checkout(
			String rutaRepositorio, 
			File carpetaLocal, 
			Boolean ignorarExternos) {

		SVNClientManager svnCl = prepararCliente();
		SVNUpdateClient svnUpd = svnCl.getUpdateClient();
		svnUpd.setIgnoreExternals(ignorarExternos);
		
		try {
			svnUpd.doCheckout(
					SVNURL.parseURIEncoded(rutaRepositorio), 
					carpetaLocal, 
					SVNRevision.UNDEFINED, 
					SVNRevision.HEAD, 
					SVNDepth.INFINITY, 
					false);  
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al hacer checkout desde '" 
							+ rutaRepositorio 
							+ "' a la carpeta '" 
							+ carpetaLocal.toString() 
							+ "'", e);
		} finally {
			svnCl.dispose();
		}
	}

	/**
	 * Realiza el commit de una carpeta local
	 * @param carpetaLocal
	 */
	public void commit(File carpetaLocal, String mensaje) {
		
		SVNClientManager svnCl = prepararCliente();
		SVNCommitClient svnCmt = svnCl.getCommitClient();
		
		try {
			
			svnCmt.doCommit(
					new File[] { carpetaLocal },
					false,
					mensaje,
					null,
					null,
					false,
					false,
					SVNDepth.INFINITY);
			
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al hacer commit de la carpeta '" + carpetaLocal.toString() + "'", e);
		} finally {
			svnCl.dispose();
		}
	}
	
	// -----------------------------------------------------
	// Relativos a la copia de trabajo local
	// -----------------------------------------------------
	
	/**
	 * Indica que se quiere añadir un archivo/carpeta a una copia de trabajo local. 
	 * @param rutaLocal
	 */
	public void addArchivoLocalACopiaTrabajo(File rutaLocal) {
		
		SVNClientManager svnCl = prepararCliente();
		SVNWCClient svnWC = svnCl.getWCClient();
		
		try {
			svnWC.doAdd(rutaLocal, false, false, true, SVNDepth.INFINITY, false, true);
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al añadir '" + rutaLocal.toString() + "' a la copia de trabajo", e);
		} finally {
			svnCl.dispose();
		}
	}
	
	public void eliminarArchivoEnCopiaDeTrabajo(File rutaLocal) {
		
		SVNClientManager svnCl = prepararCliente();
		SVNWCClient svnWC = svnCl.getWCClient();
		
		try {
			svnWC.doDelete(rutaLocal, true, true, false);
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al añadir '" + rutaLocal.toString() + "' a la copia de trabajo", e);
		} finally {
			svnCl.dispose();
		}
	}
	
	
	/**
	 * Fija el valor de una propiedad determinada
	 * @param rutaLocal
	 * @param propiedad
	 * @param valor
	 */
	public void establecePropiedadAArchivoEnCopiaLocal(File rutaLocal, String propiedad, String valor) {
		
		SVNClientManager svnCl = prepararCliente();
		SVNWCClient svnWC = svnCl.getWCClient();
		
		try {
			svnWC.doSetProperty(rutaLocal, propiedad, SVNPropertyValue.create(valor), false, SVNDepth.IMMEDIATES, null, null);
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al añadir la propiedad '" + propiedad + "', con valor (" + valor + ") a la ruta " + rutaLocal.toString() + "' en la copia de trabajo", e);
		} finally {
			svnCl.dispose();
		}
	}
	
	
	// --------------------------------------------------------
	// Reintegración y chequeos de reintegrabilidad
	// --------------------------------------------------------
	
	public void reintegrarEnLocal(File rutaLocal, String urlRepoRama) {
		
		SVNClientManager svnCl = prepararCliente();
		try {
		svnCl.getDiffClient().doMergeReIntegrate(
				SVNURL.parseURIEncoded(urlRepoRama), 
				SVNRevision.HEAD, 
				rutaLocal, 
				false);
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error al intentar reintegrar el código.",
					e
			);
		} finally {
			svnCl.dispose();
		}
	}
	
	public void mezclado(File rutaLocal, String urlRepoRama, boolean marcado) {
		
		SVNClientManager svnCl = prepararCliente();
		try {
			svnCl.getDiffClient().doMerge(
					SVNURL.parseURIEncoded(urlRepoRama), 
					SVNRevision.HEAD,
					Collections.singleton(new SVNRevisionRange(SVNRevision.create(1), SVNRevision.HEAD)),
					rutaLocal,
					SVNDepth.INFINITY,
					true,
					false,
					false,
					marcado
			);
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error durante el merge con la copia local.",
					e
			);
		} finally {
			svnCl.dispose();
		}
	}
	
	public List<String> infoElegiblesMezclado(String urlOrigen, String urlDestino) {
		
		final ArrayList<String> revisiones = new ArrayList<String>();
		
		SVNClientManager svnCl = prepararCliente();
		try {

			svnCl.getDiffClient()
			.doGetLogEligibleMergeInfo(
					SVNURL.parseURIEncoded(urlOrigen), 
					SVNRevision.HEAD, 
					SVNURL.parseURIEncoded(urlDestino), 
					SVNRevision.HEAD, 
					true, 
					new String [] {}, 
					new ISVNLogEntryHandler() {
						@Override
						public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
							revisiones.add(Long.toString(logEntry.getRevision()));
						}
					}
			);
		} catch (SVNException e) {
			throw new ErrorOperacionVersionadoCodigo(
					"Error durante la obtención de la información elegible de mezclado.",
					e
			);
		} finally {
			svnCl.dispose();
		}
		
		return revisiones;
	}
	
	// --------------------------------------------------------
	// Métodos privados
	// --------------------------------------------------------

	@SuppressWarnings("deprecation")
	private SVNClientManager prepararCliente() {

		// la configuración
		ISVNOptions opciones = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(configuracion.getUsuario(), configuracion.getContrasena());

		// instanciamos el cliente
		return SVNClientManager.newInstance(opciones, authManager);
	}
}
