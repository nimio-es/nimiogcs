package es.nimio.nimiogcs.jpa.entidades;

import java.util.UUID;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class RegistroConIdCalculado {

	// -----------------------------------------------
	// Estado: consulta y modificación
	// -----------------------------------------------

	public abstract String getId();
	public abstract void setId(String id);

	// -----------------------------------------------
	// Construcción
	// -----------------------------------------------

	public RegistroConIdCalculado() {
		super();
		
		// -- fijamos el id de esta instancia con lo que nos devuelva la utilidad pasándole el prefijo
		this.setId(generarIdElemento());
	}
	
	// -----------------------------------------------
	// Utilidades
	// -----------------------------------------------

	/**
	 * Método auxiliar que genera un Id de elemento único.
	 * 
	 */
	protected String generarIdElemento() {

		UUID r = UUID.randomUUID();
		UUID r2 = UUID.randomUUID();
		String finalId = new StringBuilder(40) 
				.append(Long.toString(Math.abs(System.currentTimeMillis()), Character.MAX_RADIX))
				.append('-') 
				.append(
						Long.toString(
								Math.abs(r.getMostSignificantBits()), Character.MAX_RADIX
						)
				)
				.append(
						Long.toString(
								Math.abs(r.getLeastSignificantBits()), Character.MAX_RADIX
						)
				)
				.append(
						Long.toString(
								Math.abs(r2.getMostSignificantBits()), Character.MAX_RADIX
						)
				)
				.toString()
				.toUpperCase();

		// y lo devolvemos
		return finalId;
	}
	
}
