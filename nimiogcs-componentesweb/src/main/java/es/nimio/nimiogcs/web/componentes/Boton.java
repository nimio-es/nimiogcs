package es.nimio.nimiogcs.web.componentes;

import es.nimio.nimiogcs.Strings;

public class Boton implements IComponente {

	public static final String TAM_NORMAL = "";
	public static final String TAM_PEQUEÑO = "pequeño";
	public static final String TAM_MUY_PEQUEÑO = "muypequeño";
	
	public static final String TIPO_NORMAL = "";
	public static final String TIPO_PRINCIPAL = "principal";
	public static final String TIPO_AVISO = "aviso";
	public static final String TIPO_PELIGRO = "peligro";

	private String tamaño = TAM_NORMAL;
	private String tipo = TIPO_NORMAL;
	private String texto = "";
	private String url = "";
	private String parametros = "";
	
	public Boton() {}
	
	public Boton(String texto, String url) {
		this(texto, url, TIPO_NORMAL, TAM_NORMAL);
	}
	
	public Boton(String texto, String url, String tipo, String tamaño) {
		this.texto = texto;
		this.url = url;
		this.tipo = tipo;
		this.tamaño = tamaño;
	}
	
	public String texto() { return this.texto; }
	public String url() { return this.url; }
	public String parametros() { return Strings.isNotEmpty(parametros) ? "?" + parametros : ""; }
	
	public boolean esTamNormal() { return this.tamaño.equalsIgnoreCase(TAM_NORMAL); }
	public boolean esTamPequeño() { return this.tamaño.equalsIgnoreCase(TAM_PEQUEÑO); }
	public boolean esTamMuyPequeño() { return this.tamaño.equalsIgnoreCase(TAM_MUY_PEQUEÑO); }
	
	public boolean esTipoNormal() { return this.tipo.equalsIgnoreCase(TIPO_NORMAL); }
	public boolean esTipoPrincipal() { return this.tipo.equalsIgnoreCase(TIPO_PRINCIPAL); }
	public boolean esTipoAviso() { return this.tipo.equalsIgnoreCase(TIPO_AVISO); }
	public boolean esTipoPeligro() { return this.tipo.equalsIgnoreCase(TIPO_PELIGRO); }
	
	// --------------------------------------
	// API fluido
	// --------------------------------------

	public Boton conTexto(String texto) { this.texto = texto; return this; }
	
	public Boton paraUrl(String url) { this.url = url; return this; }
	
	public Boton conParametros(String parametros) { this.parametros = parametros; return this; }

	public Boton conTamañoPequeño() { this.tamaño = TAM_PEQUEÑO; return this; }

	public Boton conTamañoMuyPequeño() { this.tamaño = TAM_MUY_PEQUEÑO; return this; }
	
	public Boton deTipoPeligro() { this.tipo = TIPO_PELIGRO; return this; }
	
	public Boton deTipoAviso() { this.tipo = TIPO_AVISO; return this; }
	
	// --------------------------------------
	// IComponente
	// --------------------------------------
	
	@Override
	public String clasesParaHtml() {
		String clase = "btn";
		
		if(esTamPequeño()) clase += " btn-sm";
		if(esTamMuyPequeño()) clase += " btn-xs";
		
		if(esTipoNormal()) clase += " btn-default";
		if(esTipoPrincipal()) clase += " btn-primary";
		if(esTipoAviso()) clase += " btn-warning";
		if(esTipoPeligro()) clase += " btn-danger";
		
		return clase;
	}
}
