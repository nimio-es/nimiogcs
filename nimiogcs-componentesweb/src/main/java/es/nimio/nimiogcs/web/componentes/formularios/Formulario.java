package es.nimio.nimiogcs.web.componentes.formularios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import es.nimio.nimiogcs.web.componentes.Boton;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;

@Fragmento(grupo="_formularios", id="formulario")
public class Formulario implements IContinente<IComponente> {

	private String idModelo = "datos";
	private String urlAceptacion;
	private String metodo = "POST";
	private Collection<IComponente> componentes = new ArrayList<IComponente>();

	public Formulario() {}
	
	public Formulario(String idModelo, String urlAceptacion, String metodo, String urlCancelacion, IComponente...componentes) {
		this(idModelo, urlAceptacion, metodo, urlCancelacion, Arrays.asList(componentes));
	}
	
	public Formulario(String idModelo, String urlAceptacion, String urlCancelacion, Collection<IComponente> componentes) {
		this(idModelo, urlAceptacion, "POST", urlCancelacion, componentes);
	}
	
	public Formulario(
			String idModelo, 
			String urlAceptacion, 
			String metodo, 
			String urlCancelacion,
			Collection<IComponente> componentes) {
		this.idModelo = idModelo;
		this.urlAceptacion = urlAceptacion;
		this.metodo = metodo;
		this.componentes.addAll(componentes);
		
		// y después de todos los compoenentes, metemos también los botones
		botoneraEstandar(urlCancelacion);
	}
	
	public String idModelo() { return idModelo; }
	public String urlAceptacion() { return urlAceptacion; }
	public String metodo() { return metodo; }
	
	// ---------------------------------------------
	// API FLUIDO
	// ---------------------------------------------
	
	public Formulario idModelo(String id) {
		this.idModelo = id;
		return this;
	}
	
	public Formulario urlAceptacion(String url) {
		this.urlAceptacion = url;
		return this;
	}
	
	public Formulario tipoPost() {
		this.metodo = "POST";
		return this;
	}
	
	public Formulario conComponentes(Collection<IComponente> componentes) {
		this.componentes.addAll(componentes);
		return this;
	}
	public Formulario conComponentes(IComponente... componentes) {
		return conComponentes(Arrays.asList(componentes));
	}
	
	/**
	 * Debe aparecer al final
	 */
	public Formulario botoneraEstandar(String urlCancelacion) {
		this.componentes.add(new Botonera(
				new BotonInput("Aceptar", urlAceptacion, Boton.TIPO_PRINCIPAL, Boton.TAM_NORMAL),
				new BotonEnlace("Cancelar", urlCancelacion)
				).conAlineacionALaDerecha()
			);
		return this;
	}
	
	
	@Override
	public String clasesParaHtml() { return ""; }

	@Override
	public Collection<IComponente> componentes() {
		return componentes;
	}
}
