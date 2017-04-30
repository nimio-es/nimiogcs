package es.nimio.nimiogcs.subtareas.apiweb;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.operaciones.OperacionExternaBase;

public class NotificarResultadoAnalisisCalidad 
	extends 
		OperacionExternaBase<IContextoEjecucionBase, NotificarResultadoAnalisisCalidad.INotificarResultadoCalidad, Boolean> {

	private static final String ETIQUETA_PASE = "etiquetaPase";

	/**
	 * Datos de la notificación
	 */
	public static interface INotificarResultadoCalidad {
		
		String getEtiquetaPase();
		
		boolean getSuperaCalidad();
	}
	
	// --------

	public NotificarResultadoAnalisisCalidad(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}

	public void validar(INotificarResultadoCalidad datos, Errors errores) {
		
		// la única validación que hay que hacer es que el proceso de espera asociado a deployer siga en activo
		// y que se corresponda con el entorno indicado en el valor de finalización
		ProcesoEspera procesoEspera = ce.repos().operaciones().procesoEsperaConTicket(datos.getEtiquetaPase());
		if(procesoEspera == null) {
			errores.rejectValue(ETIQUETA_PASE, "ETQ_DEPLOYER_INVALID", "Identificador de operación Deployer no registrado.");
		} else {
			// ¿Es realmente una operación deployer?
			if (!(procesoEspera instanceof ProcesoEsperaPublicacionDeployer)) 
				errores.rejectValue(ETIQUETA_PASE, "ETQ_DEPLOYER_INVALID", "Identificador no se corresponde con una operación vinculada a Deployer.");
			
			// ¿es para el entorno registrado?
			if(procesoEspera instanceof ProcesoEsperaPublicacionDeployer) {
				ProcesoEsperaPublicacionDeployer procesoDeployer = (ProcesoEsperaPublicacionDeployer)procesoEspera;
				if(!"INTEGRACION".equalsIgnoreCase(procesoDeployer.getEntorno())) 
					errores.rejectValue("entorno", "ENTORNO_NO_COINCIDE", "La calidad solo tiene sentido en pases a integración.");
			}
			
			// confirmamos que el proceso está vinculado a una etiqueta de proyecto
			boolean hayEtiqueta = false;
			for(RelacionOperacion ro: ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(procesoEspera.getId()))
				if(
						(ro instanceof RelacionOperacionElementoProyecto) 
						&& (((RelacionOperacionElementoProyecto)ro).getElementoProyecto() instanceof EtiquetaProyecto)) 
					hayEtiqueta = true;
			
			if(!hayEtiqueta) 
				errores.rejectValue(ETIQUETA_PASE, "SIN_ETIQUETA_PROYECTO", "El proceso no tiene asociado una etiqueta de proyecto");
		}
	}

	@Override
	protected String nombreSistemaNotifica() {
		return "DEPLOYER";
	}

	@Override
	protected String nombreUnicoOperacion(INotificarResultadoCalidad datos, Operacion op) {
		return "NOTIFICACION DEPLOYER RESULTADO DE CALIDAD ETIQUETA PASE '" + datos.getEtiquetaPase() + "'";
	}
	
	@Override
	protected Boolean hazlo(INotificarResultadoCalidad datos, Operacion op) throws ErrorInesperadoOperacion {

		// buscamos el registro del proceso
		ProcesoEspera procesoEspera = ce.repos().operaciones().procesoEsperaConTicket(datos.getEtiquetaPase());
		
		// dentro de las relaciones del proceso con otros elementos, nos quedaremos con la etiqueta de proyecto
		EtiquetaProyecto ep = null;
		for(RelacionOperacion ro: ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(procesoEspera.getId()))
			if(
					(ro instanceof RelacionOperacionElementoProyecto)
					&& (((RelacionOperacionElementoProyecto)ro).getElementoProyecto() instanceof EtiquetaProyecto)) 
					ep = (EtiquetaProyecto)((RelacionOperacionElementoProyecto)ro).getElementoProyecto();

		// si no se ha encontrado, tenemos un problema y no hay que hacer nada
		if(ep==null) return false;
		
		// relacionamos la operación con la etiqueta y con el proyecto y con los artefacots de la etiqueta
		registraRelacionConOperacion(op, ep);
		registraRelacionConOperacion(op, ep.getProyecto());
		for(RelacionElementoProyectoArtefacto epa: ce.repos().elementosProyectos().etiquetas().relaciones().artefactosAfectados(ep))
			registraRelacionConOperacion(op, epa.getArtefacto());
		
		// cambiamos el estado de la etiqueta para indicar si supera o no la calidad
		ep.setCalidadSuperada(datos.getSuperaCalidad());
		ce.repos().elementosProyectos().etiquetas().guardarYVolcar(ep);
		
		return true;
	}

}
