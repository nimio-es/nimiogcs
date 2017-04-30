package es.nimio.nimiogcs.web.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.operaciones.ErrorGeneral;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@ControllerAdvice
public class AsesorController {

	private static final Logger logger = LoggerFactory.getLogger(AsesorController.class); 
	
	private final IContextoEjecucion ce;

	@Autowired
	public AsesorController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	@ExceptionHandler(ErrorIntentoOperacionInvalida.class)
	public String intentoOperacionInvalida() {
		return "errores/violaciondeseguridad";
	}
	
	@ExceptionHandler(ErrorEntidadNoEncontrada.class)
	public String noEncontrado() {
		return "errores/noencontrado";
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView errorGeneral(final Exception exception) {

		// siempre lo sacamos por el logger
		logger.error("Error recibido: ", exception);
		
		// además, lo vamos a pasar como parte del modelo para pintarlo en pantalla
		StringBuilder megaTexto = new StringBuilder();
		Throwable actual = exception;
		while(actual != null) {
			
			StringWriter w = new StringWriter();
			actual.printStackTrace(new PrintWriter(w));
			
			megaTexto.append("Excepción ===>\n")
			.append(w.toString())
			.append("\n----------------------------------------------------\n\n");
			
			actual = actual.getCause();
		}
		
		// antes de sacar al monstruo, vamos a registrar la operación desastrosa
		// pero tenemos que hacerlo como una operación fuera del hilo para que
		// no se retroceda como parte del error actual
		String batacazoName = 
				"TORTA:: " 
				+ (exception != null ?
						exception.getMessage().toUpperCase()
						: "");
		if(batacazoName.length() > 200) batacazoName = batacazoName.substring(0, 200);
		final String f_batacazoName = batacazoName;
		final String f_batacazoText = megaTexto.toString();
		final String f_usuario = ce.usuario().getNombre().toUpperCase();
		ce.executor().execute(
				new Runnable() {
					@Override
					public void run() {
						ErrorGeneral batacazo = new ErrorGeneral(f_batacazoName);
						batacazo.setMensajeError(f_batacazoText);
						batacazo.setUsuarioEjecuta(f_usuario);
						ce.operaciones().saveAndFlush(batacazo);
					}
				}
		);
			
		ModelAndView mv = new ModelAndView("errores/inespecifico");
		mv.addObject("errorcito", megaTexto.toString());
		return mv;
	}
}
