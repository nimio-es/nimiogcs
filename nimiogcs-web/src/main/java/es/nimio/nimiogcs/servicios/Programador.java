package es.nimio.nimiogcs.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Programada;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.AplicacionEmpresa;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.jpa.specs.Operaciones;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.FactoriaServicioWebDeployer;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.Aplicacion;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.GetAplicacionesResponse;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAI;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAIPortType;

/**
 * Lanza taras programadas de seguimiento
 */
@Service
public class Programador {

	private IContextoEjecucion ce;
	
	@Autowired
	public Programador(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	/**
	 * Procede a actualizar todas las aplicaciones empresariales.
	 * Se preguntará cada 12 horas, pero la primera ejecución se realizará tras 
	 * veinte segundos de espera.
	 * @throws Throwable 
	 */
	@Scheduled(fixedRate=12*60*60*1000, initialDelay=20*1000)
	public void actualizarAplicaciones() {

		new OperacionInternaInlineModulo(ce) {

			private static final String NOMBRE_OPERACION = "REVISIÓN LISTADO APLICACIONES";

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
				return NOMBRE_OPERACION;
			}
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				boolean cambios = false;
				
				ServicePAI factoriaServicioDeployer = ce.contextoAplicacion().getBean(FactoriaServicioWebDeployer.class).creaServicio();
				ServicePAIPortType deployer = factoriaServicioDeployer.getServicePAIHttpSoap12Endpoint(); 
				
				escribeMensaje("Iniciado proxy con DEPLOYER: " + factoriaServicioDeployer.getWSDLDocumentLocation().toString());
				debug(NOMBRE_OPERACION + ": Solicitando lista de aplicaciones.");
				
				// solicitamos los datos al servicio
				GetAplicacionesResponse respuestaAplicaciones = deployer.getAplicaciones();

				escribeMensaje("Lista de aplicaciones solicitada.");
				
				// generamos una colección con las aplicaciones
				for(Aplicacion app: respuestaAplicaciones.getReturn()) {
					
					String codigo = app.getAppCode().getValue();
					String nombre = app.getAppDescripcion().getValue();

					escribeMensaje("Aplicación '" + codigo + ": " + nombre + "'");
					
					// comprobamos si ya existe
					if(ce.aplicaciones().findOne(codigo)==null) {
						
						// no existe
						escribeMensaje("Descubierta una aplicación que no está registrada: " + codigo + " : " + nombre);
						
						ce.aplicaciones().save(new AplicacionEmpresa(codigo, nombre));
						
						cambios=true;
					}
				}
				
				escribeMensaje("Actualización de la lista de aplicaciones terminada");
				
				// flushing
				if(cambios) ce.aplicaciones().flush();
				
				return true;
			}
		}.ejecutar();
		
	}
	
	
	/**
	 * Busca en la lista de tareas pendiente de terminar aquellas cuyo tiempo de espera haya sido superado.
	 * @throws Throwable 
	 */
	@Scheduled(fixedRate=5*60*1000, initialDelay=30*1000)
	public void cancelarEsperasSuperadoTiempo() {
		
		// para no llenar de "caca" la lista de operaciones vamos a
		// preguntar primero si, realmente, hay tareas no finalizadas
		final List<Operacion> operacionesNoFinalizadas = ce.operaciones().findAll(Operaciones.noFinalizadas());
		final List<ProcesoEspera> procesosEnEspera = new ArrayList<ProcesoEspera>();
		for(Operacion op: operacionesNoFinalizadas) {
			if(op instanceof ProcesoEspera) procesosEnEspera.add((ProcesoEspera)op);
		}
		
		
		if(procesosEnEspera.size() > 0) {
			new OperacionInternaInlineModulo(ce) {
				
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
					return "CANCELAR ESPERAS CON TIEMPO SUPERADO";
				}
				
				@Override
				protected Boolean hazlo(Boolean datos, Operacion op) {
					
					// marca para saber si hemos hecho cambios
					boolean cambios = false;
					
					// pedimos todas las tareas de espera que estén, valga la redundancia, esperando
					for(ProcesoEspera pe: procesosEnEspera) {
	
						escribeMensaje("Encontrada operación: " + pe.getNombre());
						
						// calculamos la diferencia entre el instante actual y el de creación
						long dif = new Date().getTime() - pe.getCreacion().getTime();
						
						// comparamos con el tiempo máximo de espera
						if(TimeUnit.MILLISECONDS.toSeconds(dif) > pe.getSegundosEspera()) {
							
							// dejamos constancia en la propia tarea
							escribeMensaje("Debería estar ya cerrada.");
							
							//  forzamos a la tarea por finalizada y, además, con error
							pe.setMensajeError(
									(Strings.isNotEmpty(pe.getMensajeError()) ? pe.getMensajeError() : "")  
									+ "\n\nFINALIZADA DE FORMA FORZOSA TRAS SUPERAR EL TIEMPO DE ESPERA ASIGNADO\n\n"
							);
							pe.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
							pe.setFinalizado(true);
							ce.operaciones().save(pe);
							
							// además, como caso particular, si el proceso en espera tiene
							// alguna publicación asociada, tendremos que pasarla también a errónera
							RelacionOperacionPublicacion ropp = null;
							for(RelacionOperacion rop: ce.relacionesOperaciones().relacionesDeUnaOperacion(pe.getId())) {
								if(!(rop instanceof RelacionOperacionPublicacion)) continue;
								ropp = (RelacionOperacionPublicacion)rop;
								break;
							}
							if(ropp!=null) {
								Publicacion pb = ropp.getPublicacion();
								pb.setEstado(K.X.ERROR);
								ce.publicaciones().save(pb);
							}
							
							cambios = true;
						}
					}
					
					// flushing
					if(cambios) {
						ce.operaciones().flush();
						ce.publicaciones().flush();
					}
					
					return true;
				}
			}.ejecutar();
		}
	}
	
}
