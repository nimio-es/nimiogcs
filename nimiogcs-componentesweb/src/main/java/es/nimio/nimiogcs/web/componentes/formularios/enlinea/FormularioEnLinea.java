package es.nimio.nimiogcs.web.componentes.formularios.enlinea;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;

@Fragmento(grupo="_formularios", id="inline_texto")
public class FormularioEnLinea implements IComponente {

	private String etiqueta = "";
	private String idEntrada = "valor";
	private String textoEntrada = "";
	private String textoBoton = "Go!";
	private String urlSubmit = "";
	
	public FormularioEnLinea() {}

	// ---------------------------------
	// Lectura estado
	// ---------------------------------
	
	public boolean tieneEtiqueta() {
		return !Strings.isNullOrEmpty(etiqueta);
	}
	
	public String etiqueta() {
		return tieneEtiqueta() ? etiqueta + ":" : "";
	}
	
	public String idEntrada() {
		return idEntrada;
	}
	
	public String textoEntrada() {
		return textoEntrada;
	}
	
	public String boton() {
		return textoBoton;
	}
	
	public String urlEnvio() {
		return urlSubmit;
	}
	
	// ---------------------------------
	// Api fluido
	// ---------------------------------
	
	public FormularioEnLinea conEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
		return this;
	}
	
	public FormularioEnLinea conIdentificadorEntrada(String id) {
		idEntrada = id;
		return this;
	}
	
	public FormularioEnLinea conTextoEntrada(String texto) {
		textoEntrada = texto;
		return this;
	}
	
	public FormularioEnLinea conTextoBoton(String texto) {
		textoBoton = texto;
		return this;
	}
	
	public FormularioEnLinea paraUrlEnvio(String url) {
		urlSubmit = url;
		return this;
	}
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
