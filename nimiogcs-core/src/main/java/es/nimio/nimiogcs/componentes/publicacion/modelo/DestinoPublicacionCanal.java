package es.nimio.nimiogcs.componentes.publicacion.modelo;

public class DestinoPublicacionCanal {

	private final String idInterno;
	private final String nombre;
	
	public DestinoPublicacionCanal(String idInterno, String nombre) {
		this.idInterno = idInterno; this.nombre = nombre;
	}

	public String getIdInterno() {
		return idInterno;
	}

	public String getNombre() {
		return nombre;
	}
	
}
