package es.nimio.nimiogcs.subtareas.apiweb;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.OperacionInternaBase;
import es.nimio.nimiogcs.operaciones.pospublicacion.PosPublicacion;

public class PublicacionFinalizadaJenkins 
	extends OperacionInternaBase<IContextoEjecucionBase, PublicacionFinalizadaJenkins.IFinalizarPublicacion, Boolean> {

	public static interface IFinalizarPublicacion {

		/**
		 * Identificador del tique asignado a la publicación
		 */
		String getTiqueOperacion();
	}

	// ----
	
	public PublicacionFinalizadaJenkins(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ---
	
	public void validar(IFinalizarPublicacion datos, Errors errores) {
		
		ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		if(pe == null) errores.rejectValue("tiqueOperacion", "TIQUE_NO_EXISTE", "Tique de operación no está registrado.");
		else if(pe.getFinalizado()) errores.rejectValue("tiqueOperacion", "TIQUE_NO_EXISTE", "Se trata de una operación ya finalizada.");
	}
	
	// ----
	

	@Override
	protected String nombreUnicoOperacion(IFinalizarPublicacion datos, Operacion op) {
		return "CERRAR PUBLICACIÓN CANAL 'JENKINS' PARA '" + datos.getTiqueOperacion() + "'";
	};
	
	@Override
	protected void relacionarOperacionConEntidades(IFinalizarPublicacion datos, Operacion op) {
		// se irá relacionando con los elementos a medida que los vayamos procesando
	};
	
	@Override
	protected Boolean hazlo(IFinalizarPublicacion datos, es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion op) throws ErrorInesperadoOperacion {
		
		// cargamos la operación de espera que esté asociada al ticket indicado
		ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(
				datos.getTiqueOperacion()
		);
		
		// mantenemos en una variable el valor del id de publicación para pasarlo
		// a la tarea de pos-proceso
		String idPublicacion = null;
		
		// si no hay errores podemos dar por finalizada la publicación
		for(RelacionOperacion rop: ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(pe.getId())) {

			// relacionamos con el elemento de la espera
			if(rop instanceof RelacionOperacionArtefacto) 
				registraRelacionConOperacion(op, ((RelacionOperacionArtefacto)rop).getEntidad());
			if(rop instanceof RelacionOperacionPublicacion)
				registraRelacionConOperacion(op, ((RelacionOperacionPublicacion)rop).getPublicacion());
			if(rop instanceof RelacionOperacionElementoProyecto) 
				registraRelacionConOperacion(op, ((RelacionOperacionElementoProyecto)rop).getElementoProyecto());
			if(rop instanceof RelacionOperacionDestinoPublicacion)
				registraRelacionConOperacion(op, ((RelacionOperacionDestinoPublicacion)rop).getEntorno());
			
			// si no es de tipo publicación, continuamos buscando
			if(!(rop instanceof RelacionOperacionPublicacion)) continue;
			
			RelacionOperacionPublicacion ropp = (RelacionOperacionPublicacion)rop;
			Publicacion pb = ropp.getPublicacion();
			pb.setEstado(K.X.OK);
			ce.repos().publicaciones().guardarYVolcar(pb);
			
			idPublicacion = pb.getId();
		}
		
		// finalizamos la operación de espera
		pe.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.OK);
		pe.setFinalizado(true);
		ce.repos().operaciones().guardarYVolcar(pe);
		
		// debemos llamar a la tarea de pos publicación
		if(idPublicacion!=null)
			new PosPublicacion(ce)
			.setUsuarioOperacionAnd(this.usuarioOperaciones())
			.ejecutar(idPublicacion);
		
		return true;
	}
	
}
