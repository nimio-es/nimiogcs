package es.nimio.nimiogcs.errores;

public class ErrorInesperadoOperacion extends RuntimeException {

	private static final long serialVersionUID = -8769447455886380910L;

	public ErrorInesperadoOperacion() {
		super();
	}
	
	public ErrorInesperadoOperacion(String message) {
		super(message);
	}
	
	public ErrorInesperadoOperacion(Throwable th) {
		super(th);
	}
	
	public ErrorInesperadoOperacion(String message, Throwable th) {
		super(message, th);
	}
}
