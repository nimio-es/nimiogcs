package es.nimio.nimiogcs.web.dto.p;

import java.util.ArrayList;
import java.util.Collection;

import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.InclusionFragmento;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.Pagina;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

@Pagina(nombrePlantilla="pagina")
public class EstructuraPagina extends EstructuraAbstractaPagina {

	public EstructuraPagina(String titulo) {
		super(titulo);
	}
	
	// ---
	
	@Override
	public Collection<IComponente> componentes() {

		// tenemos que devolver una colección de elemento separada en 2, 
		// la primera parte será hasta la localización, y el resto 
		// será todo lo demás dentro de dos columnas
		ArrayList<IComponente> before = new ArrayList<IComponente>();
		ArrayList<IComponente> after = new ArrayList<IComponente>();
		
		boolean bAfter = false;
		for(IComponente c: super.componentes()) {
			if(bAfter) after.add(c);
			else {
				before.add(c);
				bAfter = c instanceof Localizacion;
			}
		}
		
		// además, vamos a construir la estructura de la página dentro de dos columnas
		// poniendo a la izquierda el menú
		before.add(
				new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(2)
						.conComponentes(
								new PanelContinente()
								.conTitulo("Opciones")
								.paraTipoInfo()
								.conComponente(
										new ContinenteSinAspecto()
										.conId("sidebar-left")
										.conComponente(
												new InclusionFragmento("_fragmentos", "menu")
										)
								)
						)
				)
				.conColumna(
						new Columnas.Columna()
						.conAncho(10)
						.conComponentes(
								after
						)
				)
		);
		
		return before;
	}

}
