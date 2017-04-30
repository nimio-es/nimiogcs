package es.nimio.nimiogcs.web.dto.p;

import java.util.ArrayList;
import java.util.Collection;

import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceExterno;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

public final class MetodosDeUtilidad {

	public static Collection<IComponente> paresClaveValor(String... firstKeyAndSecondValue) {
		
		final ArrayList<IComponente> columnas = new ArrayList<IComponente>();
		
		for(int i = 0; i < firstKeyAndSecondValue.length; i += 2) {
			columnas.add(
					parClaveValor(
							firstKeyAndSecondValue[i],
							firstKeyAndSecondValue[i+1]
					)
			);
		}
		
		return columnas;
	}
	
	public static Columnas parClaveValor(String clave, String valor) {
		return parClaveValor(clave, valor, 4);
	}
	
	public static Columnas parClaveValor(String clave, String valor, int anchoTitulo) {
		return new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(anchoTitulo)
						.conComponentes(
								new TextoSimple()
								.conTexto(clave)
								.enNegrita()
						)
						.sinFilas()
				)
				.conColumna(
						new Columnas.Columna()
						.conAncho(12-anchoTitulo)
						.conComponentes(
								new TextoSimple(valor)
						)
						.sinFilas()
				);
	}

	public static Columnas parClaveValorUrl(String clave, String valor) {
		return parClaveValorUrl(clave, valor, valor);
	}

	public static Columnas parClaveValorUrl(String clave, String valor, String url) {
		return new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(4)
						.conComponentes(
								new TextoSimple()
								.conTexto(clave)
								.enNegrita()
						)
						.sinFilas()
				)
				.conColumna(
						new Columnas.Columna()
						.conAncho(8)
						.conComponentes(
								new EnlaceSimple(valor, url)
						)
						.sinFilas()
				);
	}
	
	public static Columnas parClaveValorUrlFija(String clave, String valor, String url) {
		return new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(4)
						.conComponentes(
								new TextoSimple()
								.conTexto(clave)
								.enNegrita()
						)
						.sinFilas()
				)
				.conColumna(
						new Columnas.Columna()
						.conAncho(8)
						.conComponentes(
								new EnlaceExterno()
								.conTexto(valor)
								.paraUrl(url)
						)
						.sinFilas()
				);
	}	
}
