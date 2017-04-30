package es.nimio.nimiogcs.web.api;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.subtareas.apiweb.PublicacionFinalizadaJenkins;
import es.nimio.nimiogcs.web.dto.jenkins.ErrorPublicacionJenkins;
import es.nimio.nimiogcs.web.dto.jenkins.FinalizarPublicacionJenkins;
import es.nimio.nimiogcs.web.dto.jenkins.RespuestasJenkins;

@Controller
@RequestMapping("/api/jenkins")
public class ApiJenkinsPublicacionController {

	private final IContextoEjecucionBase ce;
	
	@Autowired
	public ApiJenkinsPublicacionController(IContextoEjecucionBase ce) {
		this.ce = ce;
	}

	// --

	/**
	 * Punto de entrada del API para que Deployer indique que una operación de publicación que se estaba realizando ha concluido.
	 * @throws Throwable 
	 */
	@RequestMapping(value = "/finalizado", method = RequestMethod.POST)
	@ResponseBody
	public RespuestasJenkins.RespuestaBasePeticionJenkins finalizado(
			@RequestBody @Valid FinalizarPublicacionJenkins datos, 
			Errors errores) {
		
		// lo primero es que la petición no puede llegar a nulo
		if(datos == null) return RespuestasJenkins.nack("Sin datos de operación");

		PublicacionFinalizadaJenkins pfj = new PublicacionFinalizadaJenkins(ce);
		pfj.validar(datos, errores);
		if(errores.hasErrors()) return conError(errores);

		// queremos que la tarea de finalización use el mismo usuario 
		// que se ha registrado en la petición
		ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		pfj
		.setUsuarioOperacionAnd(pe.getUsuarioEjecuta())
		.ejecutar(datos);
		
		return RespuestasJenkins.ack();
	}
	
	
	@RequestMapping(value = "/error", method = RequestMethod.POST)
	@ResponseBody
	public RespuestasJenkins.RespuestaBasePeticionJenkins error(
			@RequestBody @Valid ErrorPublicacionJenkins datos, 
			Errors errores) {

		// lo primero es que la petición no puede llegar a nulo
		if(datos == null) return RespuestasJenkins.nack("Sin datos de operación");

		// cargamos la operación de espera que esté asociada al ticket indicado
		final ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		if(pe == null) 
			errores.rejectValue("tiqueOperacion", "TIQUE_NO_EXISTE", "Tique de operación no está registrado.");
		else 
			if(pe.getFinalizado()) 
				errores.rejectValue("tiqueOperacion", "TIQUE_NO_EXISTE", "Se trata de una operación ya finalizada.");

		if(pe == null || errores.hasErrors()) return conError(errores);
		
		// si no hay errores podemos dar por finalizada la publicación
		for(RelacionOperacion rop: ce.repos().operaciones().relaciones().relacionesDeUnaOperacion(pe.getId())) {

			if(!(rop instanceof RelacionOperacionPublicacion)) continue;
			
			RelacionOperacionPublicacion ropp = (RelacionOperacionPublicacion)rop;
			Publicacion pb = ropp.getPublicacion();
			pb.setEstado(K.X.ERROR);
			ce.repos().publicaciones().guardarYVolcar(pb);
		}
		
		// finalizamos la operación de espera
		pe.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
		pe.setMensajeError(datos.getMensaje());
		pe.setFinalizado(true);
		ce.repos().operaciones().guardarYVolcar(pe);
		
		return RespuestasJenkins.ack();
	}

	
	// ---------------------------------------------------
	// Auxiliares
	// ---------------------------------------------------
	
	private RespuestasJenkins.RespuestaBasePeticionJenkins conError(Errors errores) {
		List<String> listaErrores =
				Collections.list(
					Streams.of(errores.getAllErrors())
					.map(new Function<ObjectError, String>() {
						@Override
						public String apply(ObjectError v) {
							return v.getDefaultMessage();
						}
					})
					.getEnumeration()
				);

			return RespuestasJenkins.nack(
					listaErrores.toArray(new String[listaErrores.size()]));
	}
	
	
}
