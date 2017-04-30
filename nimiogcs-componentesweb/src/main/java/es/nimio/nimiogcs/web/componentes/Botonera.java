package es.nimio.nimiogcs.web.componentes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Lista de botones
 */
@Fragmento(id="botonera")
public class Botonera implements IContinente<Boton> {

	public static final String ALINEA_DERECHA = "derecha";
	public static final String ALINEA_IZQUIERDA = "izquierda";
	

	private boolean incrustadoEnLinea = false;
	private String alineacion = ALINEA_IZQUIERDA;
	private final Collection<Boton> botones = new ArrayList<Boton>();
	
	public Botonera() {}
	
	public Botonera(Boton... botones) {
		this(ALINEA_IZQUIERDA, botones);
	}
	
	public Botonera(String alineacion, Boton... botones) {
		this(alineacion, Arrays.asList(botones));
	}
	
	public Botonera(String alineacion, Collection<Boton> botones) {
		this.alineacion = alineacion;
		this.botones.addAll(botones);
	}
	
	public boolean tieneAlineacionDerecha() { return this.alineacion.equalsIgnoreCase(ALINEA_DERECHA); }
	public boolean estaIncrustado() { return this.incrustadoEnLinea; }

	// --------------------------------------
	// API fluido
	// --------------------------------------

	public Botonera conAlineacionALaDerecha() { this.alineacion = ALINEA_DERECHA; return this; }
	public Botonera conAlineacionALaIzquierda() { this.alineacion = ALINEA_IZQUIERDA; return this; }
	public Botonera incrustarEnLinea() { this.incrustadoEnLinea = true; return this; }
	public Botonera conBoton(Boton boton) { this.componentes().add(boton); return this; }
	public Botonera conBotonSi(boolean condicion, Boton boton) { if(condicion) return conBoton(boton); return this; }
	
	// --------------------------------------
	// IComponente e IContenedor
	// --------------------------------------
	
	@Override
	public String clasesParaHtml() {
		return tieneAlineacionDerecha() ? "text-right" : "";
	}

	@Override
	public Collection<Boton> componentes() {
		return botones;
	}
}
