package es.nimio.nimiogcs.subtareas.apiweb;

import java.util.Collection;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.KSonarEtiqueta;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionSitio;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionTipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.OperacionInternaBase;

public class AnalisisFinalizadoJenkins 
	extends OperacionInternaBase<IContextoEjecucionBase, AnalisisFinalizadoJenkins.IFinalizarAnalisis, Boolean> {

	public static interface IFinalizarAnalisis {

		/**
		 * Identificador del tique asignado a la publicación
		 */
		String getTiqueOperacion();
		
		/**
		 * Numero de violaciones de tipo bloqueante detectadas
		 */
		Integer getNumeroViolacionesBloqueantes();
	}

	// ----
	
	public AnalisisFinalizadoJenkins(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ---
	
	public void validar(IFinalizarAnalisis datos, Errors errores) {
	
		if(!ce.repos().global().buscar(KSonarEtiqueta.PG_ACTIVO).comoValorIgualA("1")) {
			errores.rejectValue(
					"tiqueOperacion", 
					"OPERACION_INACTIVA", 
					"El análisis de calidad en Jenkins está desactivado."
			);
		}
		
		ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		if(pe == null) 
			errores.rejectValue(
					"tiqueOperacion", 
					"TIQUE_NO_EXISTE", 
					"Tique de operación no está registrado."
			);
		else if(pe.getFinalizado()) 
			errores.rejectValue(
					"tiqueOperacion", 
					"TIQUE_NO_EXISTE", 
					"Se trata de una operación ya finalizada."
			);
	}
	
	// ----
	

	@Override
	protected String nombreUnicoOperacion(IFinalizarAnalisis datos, Operacion op) {
		return "CERRAR ANÁLISIS CALIDAD POR 'JENKINS' PARA '" + datos.getTiqueOperacion() + "'";
	};
	
	@Override
	protected void relacionarOperacionConEntidades(IFinalizarAnalisis datos, Operacion op) {
		
		// relacionamos con los elementos que traía la operación original
		ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		Collection<RelacionOperacion> relacionesOperaciones = ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(pe.getId());
		for(RelacionOperacion relacionOperacion: relacionesOperaciones) {
			
			if(relacionOperacion instanceof RelacionOperacionElementoProyecto)
				registraRelacionConOperacion(op, ((RelacionOperacionElementoProyecto)relacionOperacion).getElementoProyecto());
			
			if(relacionOperacion instanceof RelacionOperacionArtefacto)
				registraRelacionConOperacion(op, ((RelacionOperacionArtefacto)relacionOperacion).getEntidad());
			
			if(relacionOperacion instanceof RelacionOperacionDestinoPublicacion)
				registraRelacionConOperacion(op, ((RelacionOperacionDestinoPublicacion)relacionOperacion).getEntorno());
			
			if(relacionOperacion instanceof RelacionOperacionPublicacion)
				registraRelacionConOperacion(op, ((RelacionOperacionPublicacion)relacionOperacion).getPublicacion());
			
			if(relacionOperacion instanceof RelacionOperacionSitio)
				registraRelacionConOperacion(op, ((RelacionOperacionSitio)relacionOperacion).getSitio());
			
			if(relacionOperacion instanceof RelacionOperacionTipoArtefacto)
				registraRelacionConOperacion(op, ((RelacionOperacionTipoArtefacto)relacionOperacion).getTipoArtefacto());
			
			if(relacionOperacion instanceof RelacionOperacionPublicacionArtefacto)
				registraRelacionConOperacion(op, ((RelacionOperacionPublicacionArtefacto)relacionOperacion).getEntidad());
		}
	};
	
	@Override
	protected Boolean hazlo(IFinalizarAnalisis datos, es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion op) throws ErrorInesperadoOperacion {
		
		// buscamos la etiqueta dentro de las relaciones
		EtiquetaProyecto etiqueta = null;
		ProcesoEspera pe = 
				ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		Collection<RelacionOperacion> relacionesOperaciones = 
				ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(pe.getId());
		for(final RelacionOperacion relacionOperacion: relacionesOperaciones) {

			if(relacionOperacion instanceof RelacionOperacionElementoProyecto) {
				RelacionOperacionElementoProyecto relacionElementoProyecto = 
						(RelacionOperacionElementoProyecto)relacionOperacion;
				if(relacionElementoProyecto.getElementoProyecto() instanceof EtiquetaProyecto) {
					etiqueta = (EtiquetaProyecto)relacionElementoProyecto.getElementoProyecto();
					break;
				}
			}
		}
		
		if(etiqueta == null)
			throw new ErrorInconsistenciaDatos(
					"No consta una etiqueta como parte de las relaciones de la operación original."
			);
		
		// damos por cerrada la espera
		pe.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.OK);
		pe.setFinalizado(true);
		ce.repos().operaciones().guardarYVolcar(pe);
		
		// en función del número de violaciones, decidimos si ha pasado o no la calidad
		etiqueta.setCalidadSuperada(datos.getNumeroViolacionesBloqueantes()==0);
		ce.repos().elementosProyectos().etiquetas().guardarYVolcar(etiqueta);
		
		return true;
	}
	
}
