package es.nimio.nimiogcs.componentes.publicacion.modelo;

/**
 * Define la información básica del canal de publicación.
 */
public final class DescripcionCanal {
	
	private final int prioridad;
	private final boolean elegible;
	private final boolean gestionaCalidad;
	private final String nombre;
	private final String descripcion;
	private final DestinoPublicacionCanal[] destinos;
	
	public DescripcionCanal(
			final int prioridad, 
			final boolean elegible, 
			final boolean gestionaCalidad,
			final String nombre, 
			final String descripcion, 
			DestinoPublicacionCanal[] destinos) {
		this.prioridad = prioridad;
		this.elegible = elegible;
		this.gestionaCalidad = gestionaCalidad;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.destinos = destinos;
	}

	public int getPrioridad() {
		return prioridad;
	}
	
	/**
	 * Señala cuándo un canal es elegible o, por cotra,
	 * debe ser lanzado solamente de forma automática. 
	 */
	public boolean isElegible() {
		return elegible;
	}
	
	/**
	 * Señala cuándo un canal gestiona la calidad o depende
	 * de que la etiqueta haya pasado ya la calidad previamente.
	 */
	public boolean getGestionaCalidad() {
		return gestionaCalidad;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public DestinoPublicacionCanal[] getDestinos() {
		return destinos;
	}
}