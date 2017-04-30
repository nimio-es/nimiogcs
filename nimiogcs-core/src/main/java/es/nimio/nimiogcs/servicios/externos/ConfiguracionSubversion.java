package es.nimio.nimiogcs.servicios.externos;

import java.net.URI;

import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;

/**
 * Datos de configuración del repositorio
 */
public interface ConfiguracionSubversion {

	/**
	 * Raíz del repositorio.
	 */
	URI getRaiz();
	
	String getUsuario();
	
	String getContrasena();
	
	// --------------------------------------------------------
	// Implementación con los valores pasados en el constructor
	// --------------------------------------------------------
	
	public static final class _ implements ConfiguracionSubversion {

		private URI raiz;
		private String usuario;
		private String contrasena;
		
		public _(String raiz, String usuario, String contrasena) {
			this.raiz = URI.create(raiz);
			this.usuario = usuario;
			this.contrasena = contrasena;
		}
		
		@Override
		public URI getRaiz() {
			return this.raiz;
		}

		@Override
		public String getUsuario() {
			return this.usuario;
		}

		@Override
		public String getContrasena() {
			return this.contrasena;
		}
		
		// -----------------------------------------------------------
		// Rutina auxiliar que crea una configuración a partir de 
		// un objeto repositorio.
		// -----------------------------------------------------------

		public static ConfiguracionSubversion desde(RepositorioCodigo repositorio) {
			return new _(
					repositorio.getUriRaizRepositorio(),
					repositorio.getAdministrador(),
					repositorio.getPassword()
					);
		}
	}
}