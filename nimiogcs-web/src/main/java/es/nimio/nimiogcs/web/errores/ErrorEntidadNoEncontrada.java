package es.nimio.nimiogcs.web.errores;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Entidad no encontrada")
public class ErrorEntidadNoEncontrada extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1759161016522747877L;

}
