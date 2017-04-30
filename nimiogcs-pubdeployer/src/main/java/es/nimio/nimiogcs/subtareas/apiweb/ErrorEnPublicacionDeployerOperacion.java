package es.nimio.nimiogcs.subtareas.apiweb;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.OperacionBase;
import es.nimio.nimiogcs.web.dto.ErrorPublicacionDeployer;

public class ErrorEnPublicacionDeployerOperacion 
	extends OperacionBase<IContextoEjecucionBase, ErrorPublicacionDeployer, String> {

	public ErrorEnPublicacionDeployerOperacion(IContextoEjecucionBase ce) {
		super(ce);
	}
	
	// ---
	
	public void validar(ErrorPublicacionDeployer datos, Errors errores) {
		
		// alias
		String entorno = datos.getEntorno();
		String etiquetaPase = datos.getEtiquetaPase();

		// cuando estamos en integración este mismo mensaje puede llegar en dos momento.
		// 1. Durante la primera fase, cuando aún no se ha iniciado el proceso de publicación
		//    y Deployer nos devuelve un error con un proceso de espera de respuesta de deployer 
		//    aún activo.
		// 2. Cuando ya se ha iniciado el proceso de publicación en Deployer y ha fallado.
		
		// si es ha indicado el ide de operación, revisamos
		if(etiquetaPase != null && !etiquetaPase.isEmpty()) {

			// empecemos preguntando si estamos en integración
			if(entorno.equalsIgnoreCase("INTEGRACION")) {
				
				// si es así, entonces tenemos que preguntar si tenemos una espera de respuesta
				ProcesoEsperaRespuestaDeployer esperaRespuesta = ce.repos().operaciones().procesoEsperaRespuestaDeployerConEtiquetaPase(etiquetaPase);
				
				// si aún está activa, entonces es que se ha producido en la primera fase de la petición.
				// en ese caso podemos salir directamente
				if(esperaRespuesta != null && !esperaRespuesta.getFinalizado()) return;
			}

			// si no estamos en el entorno de integración o la espera había finalizado, continuamos con 
			// el circuito de control de la publicación.
			// por defecto suponemos que hay una operación de publicación
			ProcesoEspera procesoEspera = ce.repos().operaciones().procesoEsperaConTicket(etiquetaPase);
			
			// pero si es nula es posible que entonces estemos intentando dar por errónea la petición 
			// original (solo para el caso en que nos encontremos en integración)
			if (procesoEspera == null && entorno.equals("INTEGRACION"))
				procesoEspera = ce.repos().operaciones().procesoEsperaRespuestaDeployerConEtiquetaPase(etiquetaPase);
			
			if(procesoEspera == null) {
				errores.rejectValue("etiquetaPase", "ETQ_DEPLOYER_INVALID", "Id de etiqueta Deployer no registrado.");
			} else {
				if(procesoEspera.getFinalizado()) { 
					errores.rejectValue("etiquetaPase", "ETQ_DEPLOYER_FINALIZADA", "Operación Deployer consta como ya finalizada.");
				} else {
					ProcesoEsperaPublicacionDeployer procesoDeployer = (ProcesoEsperaPublicacionDeployer) procesoEspera;
					if(!procesoDeployer.getEntorno().equalsIgnoreCase(entorno)) {
						errores.rejectValue("entorno", "ENTORNO_NO_COINCIDE", "El entorno indicado en la petición no coincide con el registrado");
					}
				}
			}
		}
	}
	
	// ---
	
	@Override
	public String ejecutar(ErrorPublicacionDeployer datos)
			throws ErrorInesperadoOperacion {

		// el id a buscar
		String etiquetaPase = datos.getEtiquetaPase();
		
		// buscamos el registro del proceso, pero hay que tener en cuenta que podemos 
		// estar tratando con la petición de espera original. 
		ProcesoEspera procesoEspera = null;
		
		if(datos.getEntorno().equalsIgnoreCase("INTEGRACION")) {
			
			ProcesoEsperaRespuestaDeployer esperaDeployer = ce.repos().operaciones().procesoEsperaRespuestaDeployerConEtiquetaPase(etiquetaPase);
			if(!esperaDeployer.getFinalizado()) {
				procesoEspera = esperaDeployer;
			}
		}
		
		// si no estamos en integración o la operación de espera ya estaba finalizada, entonces
		// podemos buscar de forma general por la etiqueta de pase
		if(procesoEspera == null)
			procesoEspera = ce.repos().operaciones().procesoEsperaConTicket(etiquetaPase);
		
		// y lo damos por correcto y por finalizado
		procesoEspera.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
		procesoEspera.setMensajeError(datos.getMensaje().length() > 500 ? datos.getMensaje().substring(0, 500) : datos.getMensaje());
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
			publicacion.conError();
			ce.repos().publicaciones().guardarYVolcar(publicacion);
		}
		
		return "";
	}
}
