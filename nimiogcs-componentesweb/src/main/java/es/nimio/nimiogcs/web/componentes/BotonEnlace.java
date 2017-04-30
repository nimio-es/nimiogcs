package es.nimio.nimiogcs.web.componentes;

@Fragmento(id="boton-enlace")
public class BotonEnlace extends Boton {

	public BotonEnlace() {
		super();
	}
	
	public BotonEnlace(String texto, String url) {
		super(texto, url);
	}
	
	public BotonEnlace(String texto, String url, String tipo, String tamaño) {
		super(texto, url, tipo, tamaño);
	}
}
