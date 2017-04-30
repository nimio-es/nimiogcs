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
import es.nimio.nimiogcs.subtareas.apiweb.ComienzoPublicacionDeployerOperacion;
import es.nimio.nimiogcs.subtareas.apiweb.ErrorEnPublicacionDeployerOperacion;
import es.nimio.nimiogcs.subtareas.apiweb.NotificarResultadoAnalisisCalidad;
import es.nimio.nimiogcs.subtareas.apiweb.PublicacionFinalizadaDeployerOperacion;
import es.nimio.nimiogcs.web.dto.ComienzoPublicacionDeployer;
import es.nimio.nimiogcs.web.dto.ErrorPublicacionDeployer;
import es.nimio.nimiogcs.web.dto.FinalizarPublicacionDeployer;
import es.nimio.nimiogcs.web.dto.NotificarResultadoCalidad;
import es.nimio.nimiogcs.web.dto.RespuestasDeployer;

@Controller
@RequestMapping("/gcs/api/deployer")
public class ApiDeployerController {

	private IContextoEjecucionBase ce;
	
	@Autowired
	public ApiDeployerController(IContextoEjecucionBase ce) {
		this.ce = ce;
	}

	// --

	@RequestMapping(value = "/stayalive", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespuestasDeployer.RespuestaBasePeticionDeployer  stayAlive() {
		return RespuestasDeployer.ack();
	}
	
	/**
	 * Punto de entrada del API para que Deployer indique que se ha comentado una operación de publicación
	 */
	@RequestMapping(value = "/comenzar", method = RequestMethod.POST)
	@ResponseBody
	public RespuestasDeployer.RespuestaBasePeticionDeployer publicacion(@RequestBody @Valid ComienzoPublicacionDeployer datos, Errors errores) {

		// lo primero es que la petición no puede llegar a nulo
		if(datos == null) return RespuestasDeployer.nack("Sin datos de operación");

		// creamos la operación de servicio 
		ComienzoPublicacionDeployerOperacion op = new ComienzoPublicacionDeployerOperacion(ce);
		op.validar(datos, errores);
		
		// si hay errores, ya sabemos que hay que devolverlos
		if(errores.hasErrors()) 
			return conError(errores);
		
		try {
			op.ejecutar(datos);
		} catch (Exception e) {
			return RespuestasDeployer.nack(e.getMessage());
		}
		
		return RespuestasDeployer.ack();
	}
	
	/**
	 * Punto de entrada del API para que Deployer indique que una operación de publicación que se estaba realizando ha concluido.
	 */
	@RequestMapping(value = "/finalizado", method = RequestMethod.POST)
	@ResponseBody
	public RespuestasDeployer.RespuestaBasePeticionDeployer finalizado(@RequestBody @Valid FinalizarPublicacionDeployer datos, Errors errores) {
		
		// lo primero es que la petición no puede llegar a nulo
		if(datos == null) return RespuestasDeployer.nack("Sin datos de operación");

		// instanciamos la operación y validamos la petición
		PublicacionFinalizadaDeployerOperacion op = new PublicacionFinalizadaDeployerOperacion(ce);
		op.validar(datos, errores);

		// si hay errores, ya sabemos que hay que devolverlos
		if(errores.hasErrors()) 
			return conError(errores);
		
		try {
			op.ejecutar(datos);
		} catch (Exception e) {
			return RespuestasDeployer.nack(e.getMessage());
		}
		
		return RespuestasDeployer.ack();
	}
	

	/**
	 * Punto de entrada del API para que Deployer indique que se produce un error con una publicación
	 */
	@RequestMapping(value = "/error", method = RequestMethod.POST)
	@ResponseBody
	public RespuestasDeployer.RespuestaBasePeticionDeployer error(@RequestBody @Valid ErrorPublicacionDeployer datos, Errors errores) {
		
		// lo primero es que la petición no puede llegar a nulo
		if(datos == null) return RespuestasDeployer.nack("Sin datos de operación");

		// instanciamos la operación y validamos la petición
		ErrorEnPublicacionDeployerOperacion op = new ErrorEnPublicacionDeployerOperacion(ce);
		op.validar(datos, errores);
		
		// si hay errores, ya sabemos que hay que devolverlos
		if(errores.hasErrors()) 
			return conError(errores);
		
		try {
			op.ejecutar(datos);
		} catch (Exception e) {
			return RespuestasDeployer.nack(e.getMessage());
		}
		
		return RespuestasDeployer.ack();
	}
	
	@RequestMapping(value = "/calidad", method = RequestMethod.POST)
	@ResponseBody
	public RespuestasDeployer.RespuestaBasePeticionDeployer calidad(@RequestBody @Valid NotificarResultadoCalidad notificacion, Errors errores) {
		
		// lo primero es que la petición no puede llegar a nulo
		if(notificacion == null) return RespuestasDeployer.nack("Sin datos de operación");

		// instanciamos la operación y validamos la petición
		NotificarResultadoAnalisisCalidad op = 
				new NotificarResultadoAnalisisCalidad(ce);
		op.validar(notificacion,  errores);
		
		// si hay errores, ya sabemos que hay que devolverlos
		if(errores.hasErrors()) return conError(errores);
		
		// lanzamos la operación
		try {
			op.setUsuarioAnd("DEPLOYER").ejecutar(notificacion);
		} catch (Exception e) {
			return RespuestasDeployer.nack(e.getMessage());
		}
		
		return RespuestasDeployer.ack();
	}
	
	// ---------------------------------------------------
	// Auxiliares
	// ---------------------------------------------------
	
	private RespuestasDeployer.RespuestaBasePeticionDeployer conError(Errors errores) {
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

			return RespuestasDeployer.nack(
					listaErrores.toArray(new String[listaErrores.size()]));
	}
	
}
