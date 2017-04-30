package es.nimio.nimiogcs.subtareas.apiweb;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.OperacionInternaBase;
import es.nimio.nimiogcs.operaciones.pospublicacion.PosPublicacion;
import es.nimio.nimiogcs.web.dto.FinalizarPublicacionDeployer;

public class PublicacionFinalizadaDeployerOperacion 
	extends OperacionInternaBase<IContextoEjecucionBase, FinalizarPublicacionDeployer, String> {

	public PublicacionFinalizadaDeployerOperacion(IContextoEjecucionBase ce) {
		super(ce); 
	}
	
	// ---
	
	public void validar(FinalizarPublicacionDeployer datos, Errors errores) {
		
		// la única validación que hay que hacer es que el proceso de espera asociado a deployer siga en activo
		// y que se corresponda con el entorno indicado en el valor de finalización
		ProcesoEspera procesoEspera = ce.repos().operaciones().procesoEsperaConTicket(datos.getEtiquetaPase());
		if(procesoEspera == null) {
			errores.rejectValue("etiquetaPase", "ETQ_DEPLOYER_INVALID", "Identificador de operación Deployer no registrado.");
		} else {
			// ¿Es realmente una operación deployer?
			if (!(procesoEspera instanceof ProcesoEsperaPublicacionDeployer)) 
				errores.rejectValue("etiquetaPase", "ETQ_DEPLOYER_INVALID", "Identificador no se corresponde con una operación vinculada a Deployer.");
			
			// ¿no estará ya finalizada?
			if (procesoEspera.getFinalizado())
				errores.rejectValue("etiquetaPase", "OP_FINALIZADA", "Identificador se corresponde con una operación finalizada.");
			
			// ¿es para el entorno registrado?
			if(procesoEspera instanceof ProcesoEsperaPublicacionDeployer) {
				ProcesoEsperaPublicacionDeployer procesoDeployer = (ProcesoEsperaPublicacionDeployer)procesoEspera;
				if(!procesoDeployer.getEntorno().equals(datos.getEntorno())) 
					errores.rejectValue("entorno", "ENTORNO_NO_COINCIDE", "El entorno indicado en la petición no coincide con el registrado");
			}
		}
	}
	
	// ---
	
	@Override
	protected String nombreUnicoOperacion(FinalizarPublicacionDeployer datos, Operacion op) {
		return "REGISTRAR FINALIZACIÓN DE LA ESPERA POR PUBLICACIÓN EN DEPLOYER '" + datos.getEtiquetaPase() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(FinalizarPublicacionDeployer datos, Operacion op) {
		// lo que se vaya necesitando internamente
	}

	@Override
	protected String hazlo(FinalizarPublicacionDeployer datos, Operacion op) throws ErrorInesperadoOperacion {
		
		// buscamos el registro del proceso
		ProcesoEspera procesoEspera = ce.repos().operaciones().procesoEsperaConTicket(datos.getEtiquetaPase());
		
		// y lo damos por correcto y por finalizado
		procesoEspera.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.OK);
		procesoEspera.setFinalizado(true);
		
		// para, acto seguido, actualizarlo en la base de datos
		ce.repos().operaciones().guardarYVolcar(procesoEspera);
		
		// buscamos entre las relaciones de la operación por si hubiera una publicación asociada
		Publicacion publicacion = null;
		for(RelacionOperacion rop: ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(procesoEspera.getId())) {
			if(rop instanceof RelacionOperacionPublicacion) {
				RelacionOperacionPublicacion ropp = (RelacionOperacionPublicacion)rop;
				publicacion = ropp.getPublicacion();
			}
		}
		
		if(publicacion!=null) {
			publicacion.correcta();
			ce.repos().publicaciones().guardarYVolcar(publicacion);
			
			// cuando se llega a producción es que hemos terminado con este canal
			if(publicacion.getIdDestinoPublicacion().equalsIgnoreCase("PRODUCCION")) {
				new PosPublicacion(ce)
				.setUsuarioOperacionAnd(this.usuarioOperaciones())
				.ejecutar(publicacion.getId());
			}
		}
		
		return "";
	}

}
