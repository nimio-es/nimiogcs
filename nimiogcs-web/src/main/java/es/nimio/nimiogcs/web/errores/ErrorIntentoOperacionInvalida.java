package es.nimio.nimiogcs.web.errores;

public class ErrorIntentoOperacionInvalida extends RuntimeException {

	private static final long serialVersionUID = -2691534745579866927L;
	
	public ErrorIntentoOperacionInvalida() {
		super();
	}
	
	public ErrorIntentoOperacionInvalida(String message) {
		super(message);
	}

}
