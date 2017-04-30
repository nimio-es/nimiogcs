package es.nimio.nimiogcs.web.componentes.paneles;

import java.util.Arrays;
import java.util.Collection;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;

/**
 * Panel donde se muestra información en forma lista clave-valor
 */
public class PanelDatosNombreValor extends PanelContinente {

	// -----------------------------------------
	// Tipo interno
	// -----------------------------------------

	@Fragmento(id="nombrevalor")
	public static final class DatoNombreValor extends Tuples.T2<String, ItemBasadoEnUrl> implements IComponente {

		private static final long serialVersionUID = -4136734121693734825L;
		
		public DatoNombreValor(String nombre, String valor) {
			super(nombre, new ItemBasadoEnUrl(valor, "", ""));
		}
		
		public DatoNombreValor(String nombre, String valor, String url) {
			super(nombre, new ItemBasadoEnUrl(valor, url.trim(), ""));
		}

		public String nombre() { return this._1; }
		public ItemBasadoEnUrl valor() { return this._2; }
		
		@Override
		public String clasesParaHtml() {
			return "";
		}
	}

	// -----------------------------------------
	// Construcción
	// -----------------------------------------

	@SuppressWarnings("unchecked")
	public PanelDatosNombreValor(Collection<DatoNombreValor> datos) {
		super((Collection<IComponente>)(Collection<?>)datos); // doble cast para evitar error de compilador
	}

	public PanelDatosNombreValor(DatoNombreValor... datos) {
		this(Arrays.asList(datos));
	}

}
