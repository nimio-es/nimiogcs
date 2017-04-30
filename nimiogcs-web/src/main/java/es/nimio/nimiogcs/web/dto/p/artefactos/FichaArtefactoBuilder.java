package es.nimio.nimiogcs.web.dto.p.artefactos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.dto.p.directivas.FactoriaPanelesDirectivas;

/**
 * Clase interna que se utilizará para deconstruir un artefacto en las piezas relevantes
 * que se mostrarán al usuario.
 */
class FichaArtefactoBuilder {

	/**
	 * Realiza la pertinente deconstrucción
	 */
	public static Collection<IComponente> deconstruye(Artefacto artefacto) {

		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		// datos generales
		componentes.add(
				new PanelContinente()
				.conTitulo("Datos generales")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponente(MetodosDeUtilidad.parClaveValor("Identificador:", artefacto.getId()))
				.conComponente(MetodosDeUtilidad.parClaveValor("Nombre:", artefacto.getNombre()))
		);
		
		componentes.addAll(deconstruyeDirectivas(artefacto));
		
		return componentes;
	}
	
	public static Collection<IComponente> deconstruye(TipoArtefacto tipo) {
		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		componentes.add(
				new PanelContinente()
				.conTitulo("Datos del tipo")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponente(MetodosDeUtilidad.parClaveValor("Tipo:", tipo.getNombre()))
		);
		
		componentes.addAll(deconstruyeDirectivas(tipo));
		
		return componentes;
	}
	
	private static Collection<IComponente> deconstruyeDirectivas(Artefacto artefacto) {
		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();

		for(DirectivaBase directiva: artefacto.getDirectivasArtefacto()) {
			
			// calcular la ruta de edición teniendo en cuenta la particularidad de los diccionarios
			final StringBuilder pathToEdit = 
					new StringBuilder("artefactos/")
					.append(artefacto.getId())
					.append("/directiva/")
					.append(directiva.getDirectiva().getId())
					.append('/');
			if(directiva instanceof DirectivaDiccionario)
				pathToEdit
				.append(((DirectivaDiccionario)directiva).getDiccionario().getId())
				.append('/');
			pathToEdit.append("editar");
			
			componentes.add(
					FactoriaPanelesDirectivas.fabricaPanel(
							directiva, 
							puedeEditar(artefacto, directiva) ? 
									pathToEdit.toString()
									: null, 
							puedeEliminar(artefacto, directiva) ? 
									"artefactos/" + artefacto.getId() + "/directiva/" + directiva.getDirectiva().getId() + "/quitar" 
									: null
					)
			);
		}
		
		return componentes;
	}

	private static Collection<IComponente> deconstruyeDirectivas(TipoArtefacto tipo) {
		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();

		for(DirectivaBase directiva: tipo.getDirectivasTipo()) {
			
			componentes.add(
					FactoriaPanelesDirectivas.fabricaPanel(directiva)
			);
		}
		
		return componentes;
	}
	
	private static boolean puedeEditar(Artefacto artefacto, DirectivaBase directiva) {
		
		// si la directiva es de tipo rama de código NUNCA se podrá modificar
		if(directiva instanceof DirectivaRamaCodigo) return false;
		
		DirectivaCaracterizacion dc = PT.of(artefacto.getTipoArtefacto()).caracterizacion(); 
		
		// casos particulares: que la directiva sea la de coordenadas maven y que sea autogenerada
		if(directiva instanceof DirectivaCoordenadasMaven)
		{
			if(dc==null) return true;
			return !dc.getGenerarCoordenadasMaven();
		}
		
		// Para todo lo demás, de momento, siempre se puede editar
		return true;
	}

	private static boolean puedeEliminar(Artefacto artefacto, DirectivaBase directiva) {
		
		// si no podemos editar, menos quitar
		if(!puedeEditar(artefacto, directiva)) return false;
		
		// se podrá eliminar siempre que no sea una directiva de caracterización obligatoria
		DirectivaCaracterizacion dc = PT.of(artefacto.getTipoArtefacto()).caracterizacion(); 
		if(dc==null) return false;
		return !Arrays.asList(dc.getDirectivasRequeridas()).contains(directiva.getDirectiva().getId());
	}
	
}
