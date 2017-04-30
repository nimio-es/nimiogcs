package es.nimio.nimiogcs.web.componentes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Representa las pestañas que se muestran en las páginas
 */
@Fragmento(id="tabs")
public final class Tabs extends ListaItemsBasadosEnUrl<ItemBasadoEnUrlYActivo> implements IContinente<IComponente> {

	private Collection<IComponente> componentes = new ArrayList<IComponente>();
	
	public Tabs() {
		super();
	}
	
	public Tabs(Collection<IComponente> contenido, ItemBasadoEnUrlYActivo... items) {
		super(items);
		this.componentes.addAll(contenido);
	}
	
	public Tabs(Collection<IComponente> contenido, Collection<ItemBasadoEnUrlYActivo> items) {
		this(contenido, items.toArray(new ItemBasadoEnUrlYActivo[items.size()]));
	}

	// -------------------------------------------------------
	// API fluido
	// -------------------------------------------------------
	
	public Tabs conTab(ItemBasadoEnUrlYActivo tab) {
		return (Tabs)super.conItem(tab);
	}
	
	public Tabs conTab(String nombre, String url, boolean activa) {
		return this.conTab(new ItemBasadoEnUrlYActivo(nombre, url, "", activa));
	}
	
	public Tabs conTabSi(boolean condicion, String nombre, String url, boolean activa) {
		if(condicion) return conTab(nombre, url, activa);
		return this; // si no se cumple la condición simplemente devolvemos la entidad sin cambios
	}
	
	public Tabs conComponenteSi(boolean condicion, IComponente componente) {
		if(condicion) return conComponente(componente);
		return this;
	}
	
	public Tabs conComponente(IComponente componente) {
		this.componentes.add(componente);
		return this;
	}
	
	public Tabs conComponentes(IComponente...componentes) {
		return this.conComponentes(Arrays.asList(componentes));
	}
	
	public Tabs conComponentes(Collection<IComponente> componentes) {
		this.componentes.addAll(componentes);
		return this;
	}
	
	// -------------------------------------------------------
	// IComponente & IContinente
	// -------------------------------------------------------
	
	@Override
	public Collection<IComponente> componentes() {
		return componentes;
	}
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
}
