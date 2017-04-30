package es.nimio.nimiogcs.errores;

public class ErrorOperacionVersionadoCodigo extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7233266026221317993L;

	public ErrorOperacionVersionadoCodigo(String causa, Throwable innerException) {
		super(causa, innerException);
	}
	
}
