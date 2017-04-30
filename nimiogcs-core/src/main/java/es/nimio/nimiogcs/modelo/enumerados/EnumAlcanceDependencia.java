package es.nimio.nimiogcs.modelo.enumerados;

import java.util.Arrays;

/**
 * Posibilidades de alcance admitidas para la dependencia.
 */
public enum EnumAlcanceDependencia {

	// los tipos
	CompilarYEmpaquetar("compile", false, "Compilar y empaquetar"), 
	CompilarSinEmpaquetar("compile", true, "Compilar (sin empaquetar)"), 
	Provisto("provided", false, "Provisto"), 
	Testing("test", false, "Pruebas");

	private static final EnumAlcanceDependencia[] ALL = new EnumAlcanceDependencia[] { 
		CompilarYEmpaquetar, 
		CompilarSinEmpaquetar, 
		Provisto, 
		Testing 
	};
	
	// los datos asociados a cada tipo
	private String scopeMaven;
	private Boolean optionalMaven;
	private String textoDescripcion;
	
	public static EnumAlcanceDependencia[] getAll() {
		return Arrays.copyOf(ALL, ALL.length);
	}
	
	public String scopeMaven() { return this.scopeMaven; }
	public Boolean esOpcional() { return this.optionalMaven; }
	
	/**
	 * Texto que facilita presentar el valor al usuario.
	 */
	public String getTextoDescripcion() { return this.textoDescripcion; }
	
	private EnumAlcanceDependencia(String scopeMaven, boolean esOpcional, String textoDescripcion) {
		this.scopeMaven = scopeMaven;
		this.optionalMaven = esOpcional;
		this.textoDescripcion = textoDescripcion;
	}
	
	/**
	 * Localiza el tipo de enumerado a partir del texto de descripción
	 */
	public static EnumAlcanceDependencia desdeTextoDescipcion(String texto) {
		
		for (EnumAlcanceDependencia alc : EnumAlcanceDependencia.ALL)
			if (alc.getTextoDescripcion().equalsIgnoreCase(texto))
				return alc;

		// llegados aquí, entonces es que no hay un alcances que se pueda
		// convertir
		return null;
	}
}