package es.nimio.nimiogcs.datos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Programada;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.EntornoConServidores;
import es.nimio.nimiogcs.operaciones.OperacionInternaInline;

@Service
public class IniciadorDatosDeployer {

	private IContextoEjecucionBase ce;
	
	@Autowired
	public IniciadorDatosDeployer(IContextoEjecucionBase ce) {
		this.ce = ce;
	}

	@Scheduled(initialDelay=5*1000, fixedRate=365*24*60*60*1000)
	public void iniciador() {
		
		// -------------------------------------------------
		// Garantizamos que exista un destino de instalación
		// que se corresponda con Artifactory
		// -------------------------------------------------
		
		boolean necesitaInicio = 
				ce.repos().destinosPublicacion().buscar("INTEGRACION") == null
				|| ce.repos().destinosPublicacion().buscar("PREPRODUCCION") == null
				|| ce.repos().destinosPublicacion().buscar("PRODUCCION") == null
				|| ce.repos().global().buscar("PUBLICACION.DEPLOYER.COMPORTAMIENTOS") == null
				|| ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.URL") == null
				|| ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.USUARIO") == null
				|| ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.PASSWORD") == null
				|| ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.GRUPOELEMENTOS") == null
				|| ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.URL") == null;
		
		if(necesitaInicio) {
		
			new OperacionInternaInline<IContextoEjecucionBase>(ce) {
				
				@Override
				protected Operacion nuevaOperacion() {
					return new Programada();
				}

				@Override
				protected String usuarioOperaciones() {
					return "CAVERNICOLA";
				}
				
				@Override
				protected String generaNombreUnico() {
					return "AUTOREGISTRAR DATOS NECESARIOS PARA PUBLICACIÓN USANDO EL CANAL 'DEPLOYER'";
				}
				
				@Override
				protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
					
					if(ce.repos().destinosPublicacion().buscar("INTEGRACION") == null) {
			
						EntornoConServidores i = new EntornoConServidores();
						i.setId("INTEGRACION");
						i.setNombre("Integración");
						ce.repos().destinosPublicacion().guardarYVolcar(i);
					}
					
					if(ce.repos().destinosPublicacion().buscar("PREPRODUCCION") == null) {
			
						EntornoConServidores p = new EntornoConServidores();
						p.setId("PREPRODUCCION");
						p.setNombre("Preproducción");
						ce.repos().destinosPublicacion().guardarYVolcar(p);
					}
					
					if(ce.repos().destinosPublicacion().buscar("PRODUCCION") == null) {
			
						EntornoConServidores p = new EntornoConServidores();
						p.setId("PRODUCCION");
						p.setNombre("Producción");
						ce.repos().destinosPublicacion().guardarYVolcar(p);
					}

					if(ce.repos().global().buscar("PUBLICACION.DEPLOYER.COMPORTAMIENTOS") == null) {
						
						ParametroGlobal pg = new ParametroGlobal();
						pg.setId("PUBLICACION.DEPLOYER.COMPORTAMIENTOS");
						pg.setContenido("GENERAL=Configuración general que no aplica valores específicos al descriptor\n"
								+ "LIBRERIA=Librerías y artefactos que son utilizados por otros artefactos\n"
								+ "WAR=Archivos de aplicación (como WAR) o módulos web que forman parte de aplicaciones empresariales\n"
								+ "CONJUNTO-LIBS=Conjunto de librerías a desplegar agrupadamente\n"
								+ "ESTATICOS=Conjunto de recursos estáticos como imágenes, estilos y páginas web a usar en las aplicaciones\n"
						);
						pg.setDescripcion(
								"Lista de los comportamientos soportados por el módulo de publicaciones DEPLOYER. "
								+ "Cada línea contiene un ítem y cada ítem tendrá un registro clave-valor separados por el signo igual."
						);
						ce.repos().global().guardarYVolcar(pg);
					}
					
					if(ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.URL")==null) {
						
						ParametroGlobal pg = new ParametroGlobal();
						pg.setId("SERVICIOS.EXTERNOS.DEPLOYER.URL");
						pg.setContenido("http://localhost:8160/services/ServicePAI?wsdl");
						pg.setDescripcion("Url que se empleará para instanciar el Servicio Web Deployer PAI.");
						ce.repos().global().guardarYVolcar(pg);
					}
					
					if(ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.GRUPOELEMENTOS")==null) {
						
						ParametroGlobal pg = new ParametroGlobal();
						pg.setId("SERVICIOS.EXTERNOS.DEPLOYER.GRUPOELEMENTOS");
						pg.setContenido("30");
						pg.setDescripcion("Agrupación de los elementos Deployer en la que se encontrarán los descriptores de los tipos de elemento publicables usando NIMIO.");
						ce.repos().global().guardarYVolcar(pg);
					}
					
					if(ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.URL")==null) {
						
						ParametroGlobal pg = new ParametroGlobal();
						pg.setId("PUBLICACION.DEPLOYER.BUSSVN.URL");
						pg.setContenido("http://localhost/svn/Pruebas/deployer_bus");
						pg.setDescripcion("Url del repositorio Subversion que se utilizará para almacenar los descriptores de la petición de publicación.");
						ce.repos().global().guardarYVolcar(pg);
					}

					if(ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.USUARIO")==null) {
						
						ParametroGlobal pg = new ParametroGlobal();
						pg.setId("PUBLICACION.DEPLOYER.BUSSVN.USUARIO");
						pg.setContenido("");
						pg.setDescripcion("Usuario con permiso para escribir en la carpetea Subversion donde se depositarán los descriptores de publicación.");
						ce.repos().global().guardarYVolcar(pg);
					}

					if(ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.PASSWORD")==null) {
						
						ParametroGlobal pg = new ParametroGlobal();
						pg.setId("PUBLICACION.DEPLOYER.BUSSVN.PASSWORD");
						pg.setContenido("");
						pg.setDescripcion("Contraseña del usuario con permisos para escribir en el canal Subversion donde se dejarán los descriptores de la petición.");
						ce.repos().global().guardarYVolcar(pg);
					}

					return true;
				}
				
			}.ejecutar();
		}
	}
}
