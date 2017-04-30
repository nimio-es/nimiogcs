package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.GrupoFormularioTexto;
import es.nimio.nimiogcs.web.componentes.formularios.Oculto;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioBaseDirectiva;
import es.nimio.nimiogcs.web.dto.f.directivas.FormularioDirectivaDiccionario;

final class UtilidadGenerarFormularioDirectiva {

	private UtilidadGenerarFormularioDirectiva() {}
	
	public static Collection<IComponente> generarFormulario(IContextoEjecucion ce, FormularioBaseDirectiva fbd) {

		// si es un diccionario hay que construir el formulario a medida
		if(fbd instanceof FormularioDirectivaDiccionario) {
			
			ArrayList<IComponente> componentes = new ArrayList<IComponente>();
			
			// el campo privado, eso siempre, que indica qué diccionario es:
			componentes.add(new Oculto("diccionario"));
			
			// cargamos el diccionario para conocer qué campos hay que usar
			FormularioDirectivaDiccionario fdd = (FormularioDirectivaDiccionario)fbd;
			String idDiccionario = fdd.getDiccionario();
			TipoDirectivaDiccionario tdd = ce.diccionariosDirectivas().findOne(idDiccionario);
			ArrayList<TipoDirectivaDiccionarioDefinicion> definiciones = new ArrayList<TipoDirectivaDiccionarioDefinicion>(tdd.getDefinicionesDiccionario());
			Collections.sort(
					definiciones,
					new Comparator<TipoDirectivaDiccionarioDefinicion>() {
						@Override
						public int compare(TipoDirectivaDiccionarioDefinicion o1,
								TipoDirectivaDiccionarioDefinicion o2) {
							Integer i1 = o1.getPosicion();
							Integer i2 = o2.getPosicion();
							return i1.compareTo(i2);
						}
					}
			);
			
			for(TipoDirectivaDiccionarioDefinicion df: definiciones) {
				componentes.add(
						new GrupoFormularioTexto("mapa[" + df.getClave() + "]", null, df.getEtiqueta(), df.getBloqueDescripcion())
				);
			}
			
			return Arrays.asList(
					new IComponente[] {
							new PanelContinente()
							.conTitulo(tdd.getDescripcion())
							.paraTipoDefecto()
							.conComponentes(componentes)
					}
			);
		} else {
			
			// en caso de no ser un diccionario, podemos basarnos en el esquema autogenerado
			return 
					AyudanteCalculoEstructuraFormularioDesdePojo.convertirDesdeDto(
							AyudanteCalculoEstructuraFormularioDesdePojo.Operacion.EDICION, 
							fbd.getClass(),
							UtilidadDiccionariosSoporteDirectivas.diccionarios(ce)
					);
		}	
	
	}
}
