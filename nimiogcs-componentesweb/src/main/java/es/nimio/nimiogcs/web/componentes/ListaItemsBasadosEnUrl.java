package es.nimio.nimiogcs.web.componentes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ListaItemsBasadosEnUrl<T extends IItemBasadoEnUrl> implements IComponente {

	protected List<T> items = new ArrayList<T>();

	public ListaItemsBasadosEnUrl() {}
	
	public ListaItemsBasadosEnUrl(T... items) {
		for(T item: items) this.items.add(item);
	}
	
	public IItemBasadoEnUrl[] items() { 
		return this.items.toArray(new IItemBasadoEnUrl[this.items.size()]);
	}

	public static ItemBasadoEnUrl[] invertir(ItemBasadoEnUrl... items) {
		List<ItemBasadoEnUrl> itemsInvertidos = Arrays.asList(items);
		Collections.reverse(itemsInvertidos);
		return itemsInvertidos.toArray(new ItemBasadoEnUrl[itemsInvertidos.size()]);
	}
	
	// -------------------------------------------------------------------
	// Api fluido
	// -------------------------------------------------------------------

	public ListaItemsBasadosEnUrl<T> conItem(T item) { items.add(item); return this; }
	
}