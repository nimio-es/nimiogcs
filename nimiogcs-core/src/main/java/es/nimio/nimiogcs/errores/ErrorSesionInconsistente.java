package es.nimio.nimiogcs.errores;

public class ErrorSesionInconsistente extends RuntimeException {

	private static final long serialVersionUID = -116579236543803035L;

	public ErrorSesionInconsistente(String mensaje) {
		super(mensaje);
	}
}
