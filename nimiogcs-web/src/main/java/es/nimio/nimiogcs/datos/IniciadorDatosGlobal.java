package es.nimio.nimiogcs.datos;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.functional.Tuples.T3;
import es.nimio.nimiogcs.functional.Tuples.T5;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Service
public class IniciadorDatosGlobal {

	private IContextoEjecucion ce;
	
	@Autowired
	public IniciadorDatosGlobal(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------
	
	/**
	 * Proceso que se debe ejecutar siempre al comienzo y que se encarga de construir los registros iniciales
	 * @throws Throwable
	 */
	@Scheduled(initialDelay=2*1000, fixedRate=365*24*60*60*1000)
	public void iniciador() {
		new Iniciador(ce).ejecutar();
	}
	
	// ---
	
	static final class Iniciador extends IniciadorBase {
	
	
		public Iniciador(IContextoEjecucion contextoEjecucion) {
			super(contextoEjecucion);
		}
		
		// --

		@Override
		protected String generaNombreUnico() {
			return "REVISION EXISTENCIA DATOS INICIALES";
		}

		@Override
		protected Collection<T5<String, String, Boolean, Boolean, Boolean>> listaTiposDeDirectivas() {
			ArrayList<T5<String, String, Boolean, Boolean, Boolean>> registros = new ArrayList<Tuples.T5<String,String,Boolean,Boolean,Boolean>>();
			registros.add(Tuples.tuple("DICCIONARIO", "Directiva basada en diccionario de parámetros", false, true, true));
			registros.add(Tuples.tuple("CARACTERIZACION", "Caracterización de la familia de artefactos", true, false, false));
			registros.add(Tuples.tuple("REFERENCIAR", "Tipos que puede referenciar", true, false, false));
			registros.add(Tuples.tuple("INVENTARIO", "Inventariar", false, true, false));
			registros.add(Tuples.tuple("TAXONOMIA", "Taxonomía", false, true, false));
			registros.add(Tuples.tuple("REPO-CODIGO", "Repositorio de código", false, true, false));
			registros.add(Tuples.tuple("VERSION_JAVA", "Versión Java", true, true, false));
			registros.add(Tuples.tuple("ESTRUCT_CODIGO", "Carpetas de código fuente", true, false, false));
			registros.add(Tuples.tuple("COOR_MAVEN", "Coordenadas Maven", false, true, false));
			registros.add(Tuples.tuple("ALCANCES", "Alcances autorizados", false, true, false));
			registros.add(Tuples.tuple("PUBLICACION_DEPLOYER", "Configuración de publicación Deployer", true, false, false));
			registros.add(Tuples.tuple("PARAMETROS_DEPLOYER", "Parámetros de artefacto para la publicación Deployer", false, true, false));
			registros.add(Tuples.tuple("EVOLUCION", "Estrategia de evolución", true, true, false));
			registros.add(Tuples.tuple("RAMA_CODIGO", "Rama de trabajo en repositorio de código", false, true, false));
			registros.add(Tuples.tuple("PROYECCION", "Proyectable", true, false, false));
			registros.add(Tuples.tuple("PROYECCION_MAVEN", "Parámetros para la proyección Maven", true, true, false));
			registros.add(Tuples.tuple("PUBLICACION_JENKINS", "Configuración de publicación invocando una tarea Jenkins", true, false, false));
			return registros;
		}

		@Override
		protected Collection<T3<String, String, Boolean>> listaTiposDeArtefactos() {
			ArrayList<Tuples.T3<String, String, Boolean>> registros = new ArrayList<Tuples.T3<String,String,Boolean>>();
			registros.add(Tuples.tuple("EVOLUTIVO", "Evolutivo de un artefacto", false));
			registros.add(Tuples.tuple("FREEZE", "Congelación de evolución", false));
			return registros;
		}
		
		@Override
		protected Collection<Tuples.T3<String, String, String>> listaParametrosGlobales() {
			ArrayList<Tuples.T3<String, String, String>> registros = new ArrayList<Tuples.T3<String,String,String>>();
			registros.add(
					Tuples.tuple(
							"ARTEFACTOS.DIRECTIVAS.EVOLUCIONABLES", 
							"Directivas que son evolucionables como parte de la evolución de un artefacto.", 
							"VERSION_JAVA"
					)
			);
			registros.add(
					Tuples.tuple(
							"CANAL.PUBLICACION.OFRECERTODOS",
							"Establece la política de ignora (1) o no (0) la prioridad de los canales y ofrecer los artefactos publicables en cada uno de ellos. En entornos de producción conveniente dejar en valor 0",
							"0"
					)
			);
			
			return registros;
		}
		
		@Override
		protected Collection<T2<String, String>> listaDestinosPublicacion() {
			return new ArrayList<Tuples.T2<String,String>>();
		}
	}
	
}
