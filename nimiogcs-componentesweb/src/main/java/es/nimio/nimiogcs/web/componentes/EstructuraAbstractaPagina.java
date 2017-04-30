package es.nimio.nimiogcs.web.componentes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Representa la página que vamos a pintar.
 */
@Pagina
public abstract class EstructuraAbstractaPagina implements IContinente<IComponente> {

	private String titulo;
	private final ArrayList<IComponente> componentes = new ArrayList<IComponente>();
	
	public EstructuraAbstractaPagina(String titulo) {
		this.titulo = titulo;
	}
	
	public String titulo() { return titulo; }
	public Collection<IComponente> componentes() { return componentes; }
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
	
	// -----------------------------------------
	// API fluido
	// -----------------------------------------

	public EstructuraAbstractaPagina conComponentes(Collection<IComponente> componentes) {
		this.componentes.addAll(componentes);
		return this;
	}
	
	public EstructuraAbstractaPagina conComponentes(IComponente... componentes) {
		return conComponentes(Arrays.asList(componentes));
	}
	
	public EstructuraAbstractaPagina conComponentesSi(boolean condicion, IComponente... componentes) {
		if(condicion) return conComponentes(componentes);
		return this;
	}
}
