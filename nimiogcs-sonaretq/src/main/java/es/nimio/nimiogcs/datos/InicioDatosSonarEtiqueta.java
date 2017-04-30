package es.nimio.nimiogcs.datos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.KSonarEtiqueta;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.operaciones.OperacionInternaInline;

@Service
public class InicioDatosSonarEtiqueta {

	private final IContextoEjecucionBase ce;
	
	@Autowired
	public InicioDatosSonarEtiqueta(final IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// --
	
	@Scheduled(initialDelay=5*1000, fixedRate=365*24*60*60*1000)
	public void iniciador() {
		
		new OperacionInternaInline<IContextoEjecucionBase>(ce) {

			@Override
			protected String generaNombreUnico() {
				return "INICIAR DATOS EVALLUACION CALIDAD SONAR ETIQUETA";
			}

			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
				
				checkParametroGlobal(
						KSonarEtiqueta.PG_ACTIVO, 
						"Indicador de activación o no del proceso de análisis automático delegando en una tarea SONAR cuando se etiqueta un proyecto", 
						"0"
				);
				
				checkParametroGlobal(
						KSonarEtiqueta.PG_URL_BASE_JENKINS, 
						"Url raíz del servidor Jenkins que ejecutará el proceso de análisis de calidad de una etiqueta determinada", 
						"http://localhost"
				);
				
				checkParametroGlobal(
						KSonarEtiqueta.PG_USER_JENKINS,
						"Usuario con permisos para ejecutar la tarea Jenkins con el análisis de calidad",
						"usuario"
				);
				
				checkParametroGlobal(
						KSonarEtiqueta.PG_PWD_JENKINS, 
						"Contraseña del usuario con permisos para ejecutar la tarea de análisis de calidad", 
						"password");
				
				checkParametroGlobal(
						KSonarEtiqueta.PG_JOB_JENKINS, 
						"Nombre de la tarea que se ejecutará en Jenkins y donde se ha configurado el script encargado de lanzar el análisis de calidad", 
						"qa_etiqueta_nimio"
				);
				
				return true;
			}
			
			private void checkParametroGlobal(
					final String idParametro,
					final String descripcion,
					final String valorInicial) {
				
				escribeMensaje("Comprobar existencia parámetro '" + idParametro + "'");
				if(ce.repos().global().buscar(idParametro)==null) {
					escribeMensaje("... no existe: ");
					ParametroGlobal pg = new ParametroGlobal();
					pg.setId(idParametro);
					pg.setDescripcion(descripcion);
					pg.setContenido(valorInicial);
					ce.repos().global().guardarYVolcar(pg);
					escribeMensaje("... creado.");
					
				} else {
					escribeMensaje("... existe");
				}
				
				escribeMensaje("");
			}
			
		}.ejecutar();		
	}
}
