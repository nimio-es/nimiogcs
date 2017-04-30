package es.nimio.nimiogcs.web.api;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.subtareas.apiweb.AnalisisFinalizadoJenkins;
import es.nimio.nimiogcs.web.dto.FinalizarAnalisisJenkins;
import es.nimio.nimiogcs.web.dto.RespuestasJenkins;

@RestController
@RequestMapping(
		path="/api/qa/jenkinsjob/finalizado", 
		produces="application/json", 
		consumes="application/json"
)
public class ApiJenkinsSonarController {

	private final IContextoEjecucionBase ce;
	
	@Autowired
	public ApiJenkinsSonarController(final IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// ---
	
	@RequestMapping(method=RequestMethod.POST)
	public RespuestasJenkins.RespuestaBasePeticionJenkins finalizado(
			@RequestBody @Valid FinalizarAnalisisJenkins  datos, 
			Errors errores) {
		
		// lo primero es que la petición no puede llegar a nulo
		if(datos == null) return RespuestasJenkins.nack("Sin datos de operación");

		AnalisisFinalizadoJenkins op = new AnalisisFinalizadoJenkins(ce);
		op.validar(datos, errores);
		if(errores.hasErrors()) return conError(errores);	
		
		// queremos que la tarea de finalización use el mismo usuario 
		// que se ha registrado en la petición
		ProcesoEspera pe = ce.repos().operaciones().procesoEsperaConTicket(datos.getTiqueOperacion());
		op.setUsuarioOperacionAnd(pe.getUsuarioEjecuta())
		.ejecutar(datos);
		
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
