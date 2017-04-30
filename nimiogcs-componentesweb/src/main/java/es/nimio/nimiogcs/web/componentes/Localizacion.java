package es.nimio.nimiogcs.web.componentes;


/**
 * Representa el BREADCRUMB de la página
 */
@Fragmento(id="breadcrumb")
public final class Localizacion extends ListaItemsBasadosEnUrl<ItemBasadoEnUrl> {

	public Localizacion() {
		super(new ItemBasadoEnUrl[] {});
	}
	
	public Localizacion(ItemBasadoEnUrl... items) {
		
		// un poco rebuscado, pero para la localización necesitamos
		// que la lista esté ordenada de forma inversa
		super(invertir(items));
	}
	
	public Localizacion conItem(ItemBasadoEnUrl item) {
		this.items.add(0, item);
		return this;
	}
	
	public Localizacion conTexto(String texto) {
		return conItem(new ItemBasadoEnUrl(texto, "", ""));
	}
	
	public Localizacion conEnlace(String texto, String url) {
		return conItem(new ItemBasadoEnUrl(texto, url, ""));
	}

	public Localizacion conEnlaceYParametros(String texto, String url, String parametros) {
		return conItem(new ItemBasadoEnUrl(texto, url, parametros));
	}

	@Override
	public String clasesParaHtml() { return ""; }
}
