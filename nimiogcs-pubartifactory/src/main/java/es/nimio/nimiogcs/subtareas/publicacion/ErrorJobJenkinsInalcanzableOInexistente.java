package es.nimio.nimiogcs.subtareas.publicacion;

final class ErrorJobJenkinsInalcanzableOInexistente extends RuntimeException {

	private static final long serialVersionUID = -1420042038271210105L;

	public ErrorJobJenkinsInalcanzableOInexistente() {
		super("Tarea inalcanzable o no inexistente con la configuración actual.");
	}
	
}
