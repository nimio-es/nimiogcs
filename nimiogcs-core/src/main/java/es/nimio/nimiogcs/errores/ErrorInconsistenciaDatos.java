package es.nimio.nimiogcs.errores;

public class ErrorInconsistenciaDatos extends RuntimeException {

	private static final long serialVersionUID = -5037427575138157888L;

	public ErrorInconsistenciaDatos(String mensaje) {
		super(mensaje);
	}
}
