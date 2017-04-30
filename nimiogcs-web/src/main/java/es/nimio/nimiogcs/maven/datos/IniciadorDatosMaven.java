package es.nimio.nimiogcs.maven.datos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Programada;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Service
public class IniciadorDatosMaven {

	private IContextoEjecucion ce;
	
	@Autowired
	public IniciadorDatosMaven(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------
	
	/**
	 * Proceso que se debe ejecutar siempre al comienzo y que se encarga de construir los registros iniciales
	 * @throws Throwable
	 */
	@Scheduled(initialDelay=5*1000, fixedRate=365*24*60*60*1000)
	public void iniciador() {
		new Iniciador(ce).ejecutar();
	}
	
	// ---
	
	static final class Iniciador extends OperacionInternaInlineModulo {
	
	
		public Iniciador(IContextoEjecucion contextoEjecucion) {
			super(contextoEjecucion);
		}
		
		// --

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
			return "REVISION EXISTENCIA DATOS INICIALES MÓDULO 'MAVEN'";
		}
		
		@Override
		protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
			
			escribeMensaje("Revisar los parámetros globales existentes...");
			parametrosGlobales();
			escribeMensaje("");
			

			return true;
		}
		

		private void parametrosGlobales() {
			
			checkAndWriteParametroGlobal(
					"MAVEN.BUSCARDEPENDENCIAS.REPOSITORIOS", 
					"Lista de repositorios en los que ir a buscar las dependencias. En cada línea se identificarán los parámetros del repositorio separados por coma. Debe haber tres parámetros por línea.", 
					"central,default,http://10.0.218.79:8081/artifactory/libs-release\nsnapshots,default,http://10.0.218.79:8081/artifactory/libs-snapshot\n"
			);
			checkAndWriteParametroGlobal(
					"MAVEN.COORDENADAS.EMPAQUETADOS",
					"Lista de tipos de empaquetado que se puede utilizar a la hora de dar un artefacto Maven. Lista de clave y valor, separados por ':' y, cada ítem, separado por coma.",
					"jar:JAR,pom:POM"
			);
			checkAndWriteParametroGlobal(
					"MAVEN.COORDENADAS.GENERAR.GRUPO.RAIZ",
					"Raíz del identificador de grupo en la generación automática de la coordenada Maven. Para el resto se usará la taxonomía elegida.",
					"es.nimio."
			);
			checkAndWriteParametroGlobal(
					"MAVEN.COORDENADAS.GENERAR.VERSION",
					"Valor que se establecerá en el parámetro versión de la coordenada Maven generada automáticamente.",
					"RELEASE"
			);
			checkAndWriteParametroGlobal(
					"PROYECCION.MAVEN.DIRECTIVAS.PROPIEDADES",
					"Lista de qué directivas (tipo) serán recorridas para convertir sus valores en propiedades del archivo pom.xml.",
					"REPOSITORIO-CODIGO,COOR_MAVEN,INVENTARIAR,ALCANCES,ESTRATEGIA-EVOLUCION,RAMA-CODIGO,PROYECTABLE,TAXONOMIA,VERSION-JAVA,DICCIONARIO"
			);
			checkAndWriteParametroGlobal(
					"PROYECCION.MAVEN.DIRECTIVAS.PROPIEDADES.ESPECIALES",
					"Indica que ciertas directivas se traten de forma especial, usando como prefijo el indicado y teniendo en cuenta el orden de primero evolutivo, luego artefacto y finalmente el tipo.",
					"VERSION_JAVA=java\nSERVICIO_EXTERNO=servicio"
			);
			checkAndWriteParametroGlobal(
					"PROYECCION.MAVEN.PLANTILLA.MODULO",
					"Plantilla del archivo pom.xml de un módulo de un proyecto. Hay que respetar las variables de sustitución definidas o el proceso fallará.",
					"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
					+"<project    xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
					+"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
					+"    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
					+"\n"
					+"    <modelVersion>4.0.0</modelVersion>\n"
					+"\n"
					+"    <!-- ============================================================== -->\n"
					+"    <!-- Este archivo pom.xml ha sido autoconstruido como parte del     -->\n"
					+"    <!-- proceso de configuracion del proyecto. Cualquier modificacion  -->\n"
					+"    <!-- realizada manualmente sera ignorada en posteriores recons-     -->\n"
					+"    <!-- trucciones que realicen las herramientas de ciclo de vida.     -->\n"
					+"    <!-- ============================================================== -->\n"
					+"\n"
					+"    <!-- Padre -->\n"
					+"    <parent>\n"
					+"        <groupId>es.nimio.proyectos</groupId>\n"
					+"        <artifactId>${datos.nombre.toLowerCase()}</artifactId>\n"
					+"        <version>${datos.nombre}</version>\n"
					+"    </parent>\n"
					+"\n"
					+"    <!-- Datos del artefacto -->\n"
					+"    #if($coordenadas)\n"
					+"    <groupId>${coordenadas.idGrupo}</groupId>\n"
					+"    <artifactId>${coordenadas.idArtefacto}</artifactId>\n"
					+"    <packaging>${coordenadas.empaquetado}</packaging>\n"
					+"    #else\n"
					+"    <groupId>es.nimio.unknown</groupId>\n"
					+"    <artifactId>${modulo.nombre.toLowerCase()}</artifactId>\n"
					+"    <packaging>pom</packaging>\n"
					+"    #end\n"
					+"    <name>${modulo.nombre}</name>\n"
					+"\n"
					+"    <!-- Propiedades-->\n"
					+"    <properties>\n"
					+"        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n"
					+"        <!-- propiedades de las directivas del artefacto -->\n"
					+"    #foreach($map in $props)\n"
					+"        #foreach($mapEntry in $map.entrySet())\n"
					+"        <$mapEntry.key>$mapEntry.value</$mapEntry.key>\n"
					+"        #end\n"
					+"    #end\n"
					+"\n"
					+"    </properties>\n"
					+"\n"
					+"    <dependencies>\n"
					+"        ${PREDEPS}\n"
					+"        <!-- dependencias -->\n"
					+"        #foreach($dep in $deps_relaciones)\n"
					+"        <dependency>\n"
					+"            <groupId>${dep.coordenadas.idGrupo}</groupId>\n"
					+"            <artifactId>${dep.coordenadas.idArtefacto}</artifactId>\n"
					+"            <type>${dep.coordenadas.empaquetado}</type>\n"
					+"            #if($dep.esModulo)\n"
					+"            <version>${datos.nombre}</version>\n"
					+"            #else \n"
					+"            <version>${dep.coordenadas.version}</version>\n"
					+"            #end\n"
					+"            #if($dep.scope)\n"
					+"            <scope>$dep.scope</scope>\n"
					+"            #end\n"
					+"            #if($dep.opcional)\n"
					+"            <optional>true</optional>\n"
					+"            #end\n"
					+"        </dependency>\n"
					+"        #end\n"
					+"        ${POSTDEPS}\n"
					+"    </dependencies>\n"
					+"\n"
					+"    <build>\n"
					+"        <finalName>${modulo.nombre.toLowerCase()}</finalName>\n"
					+"\n"
					+"        <plugins>\n"
					+"            ${PLUGINS}\n"
					+"        </plugins>\n"
					+"\n"
					+"        ${CARPETAS}\n"
					+"\n"
					+"    </build>\n"
					+"\n"
					+"    <profiles>\n"
					+"        ${PERFILES}\n"
					+"    </profiles>\n"
					+"\n"
					+"    <!-- Version de la plantilla ${VERSIONPLANTILLA} -->\n"
					+"\n"
					+"</project>\n"
			);
			checkAndWriteParametroGlobal(
					"PROYECCION.MAVEN.PLANTILLA.PADRE",
					"Plantilla que define la forma del archivo pom.xml padre de un proyecto. Hay que respetar las variables de sustitución que se encuentran definidas o el proceso fallará.",
					"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
					+"<project    xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
					+"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
					+"    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
					+"\n"
					+"    <modelVersion>4.0.0</modelVersion>\n"
					+"\n"
					+"    <!-- ============================================================== -->\n"
					+"    <!-- Este archivo pom.xml ha sido autoconstruido como parte del     -->\n"
					+"    <!-- proceso de configuracion del proyecto. Cualquier modificacion  -->\n"
					+"    <!-- realizada manualmente sera ignorada en posteriores recons-     -->\n"
					+"    <!-- trucciones que realicen las herramientas de ciclo de vida.     -->\n"
					+"    <!-- ============================================================== -->\n"
					+"\n"
					+"    <!-- Datos del artefacto -->\n"
					+"${IDENTIFICACION}\n"
					+"\n"
					+"    <!-- Propiedades -->\n"
					+"    <properties>\n"
					+"        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n"
					+"${LISTAPROPIEDADES}\n"
					+"    </properties>\n"
					+"\n"
					+"#if($datos.class.simpleName == 'EtiquetaProyecto')\n"
					+"    <distributionManagement>\n"
					+"        <!-- Repositorio Artifactory corporativo -->\n"
					+"        <repository>\n"
					+"            <uniqueVersion>false</uniqueVersion>\n"
					+"            <id>artifactory-repo</id>\n"
					+"            <name>Artifactory</name>\n"
					+"            <url>http://localhost/artifactory</url>\n"
					+"            <layout>default</layout>\n"
					+"        </repository>\n"
					+"    </distributionManagement>\n"
					+"#end\n"
					+"\n"
					+"    <!-- Modulos del proyecto -->\n"
					+"    <modules>\n"
					+"${MODULOS}\n"
					+"    </modules>\n"
					+"    \n"
					+"    <build>\n"
					+"        <pluginManagement>\n"
					+"            <plugins>\n"
					+"${PLUGINSGESTIONADAS}\n"
					+"            </plugins>\n"
					+"        </pluginManagement>\n"
					+"        \n"
					+"        <plugins>\n"
					+"${PLUGINS}\n"
					+"        </plugins>\n"
					+"    </build>\n"
					+"\n"
					+"    <profiles>\n"
					+"\n"
					+"#if($datos.class.simpleName == 'EtiquetaProyecto')\n"
					+"\n"
					+"        <!-- publicacion en Artifactory. Requiere la definicion de usuario/pwd en settings.xml -->\n"
					+"        <!-- SU ACTIVACION EN DESARROLLO LOCAL (EQUIPO PROGRAMADOR) NO FUNCIONA -->\n"
					+"        <profile>\n"
					+"            <id>dply-artifactory</id>\n"
					+"\n"
					+"            <build>\n"
					+"                <plugins>\n"
					+"                    <plugin>\n"
					+"                        <groupId>org.jfrog.buildinfo</groupId>\n"
					+"                        <artifactId>artifactory-maven-plugin</artifactId>\n"
					+"                        <version>2.4.1</version>\n"
					+"                        <inherited>false</inherited>\n"
					+"                        <executions>\n"
					+"                            <execution>\n"
					+"                                <id>build-info</id>\n"
					+"                                <goals>\n"
					+"                                    <goal>publish</goal>\n"
					+"                                </goals>\n"
					+"                                <configuration>\n"
					+"\n"
					+"                                    <deployProperties>\n"
					+"                                        <groupId>${project.groupId}</groupId>\n"
					+"                                        <artifactId>${project.artifactId}</artifactId>\n"
					+"                                        <version>${project.version}</version>\n"
					+"                                    </deployProperties>\n"           
					+"\n"
					+"                                    <publisher>\n"
					+"                                        <contextUrl>http://localhost/artifactory</contextUrl>\n"
					+"                                        <repoKey>libs-release-local</repoKey>\n"
					+"                                        <username>${artifactory.username}</username>\n"
					+"                                        <password>${artifactory.password}</password>\n"
					+"                                    </publisher>\n"
					+"                                    \n"
					+"                                </configuration>\n"
					+"                            </execution>\n"
					+"                        </executions>\n"
					+"                    </plugin>\n"
					+"                </plugins>\n"
					+"            </build>\n"
					+"        </profile>\n"
					+"#end\n"
					+"\n"
					+"    </profiles>\n"
					+"    \n"
					+"    <!-- Version de la plantilla de ${VERSIONPLANTILLA} -->\n"
					+"</project>\n"
			);
			
			ce.repos().global().volcar();
		}
		
		private void checkAndWriteParametroGlobal(String id, String descripcion, String contenido) {
			
			String mensaje = "Revisando existencia parámetro global '" + id + "': ";
			final boolean existe = ce.repos().global().buscar(id) != null;
			mensaje += existe ? "EXISTE" : "NO EXISTE -> DAR DE ALTA...";
			escribeMensaje(mensaje);
			if(!existe) {
				final ParametroGlobal pg = new ParametroGlobal();
				pg.setId(id);;
				pg.setDescripcion(descripcion);
				pg.setContenido(contenido);
				ce.repos().global().guardar(pg);
				escribeMensaje("... registro creado");
			}
		}
	}
	
}
