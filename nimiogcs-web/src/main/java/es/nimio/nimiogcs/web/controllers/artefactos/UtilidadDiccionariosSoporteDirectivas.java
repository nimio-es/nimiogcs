package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;

import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.jpa.entidades.sistema.AplicacionEmpresa;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

final class UtilidadDiccionariosSoporteDirectivas {

	private UtilidadDiccionariosSoporteDirectivas() {}
	
	/**
	 * Devuelve todos los posibles diccionarios utilizables en la modificación/alta de las directivas existentes
	 * @return
	 */
	public static Map<String, Collection<NombreDescripcion>> diccionarios(IContextoEjecucion ce) {
		
		final HashMap<String, Collection<NombreDescripcion>> seleccionables = new HashMap<String, Collection<NombreDescripcion>>();
		
		// las posibles estrategias de evolución
		Collection<NombreDescripcion> estrategias = new ArrayList<NombreDescripcion>();
		estrategias.add(new NombreDescripcion(DirectivaEstrategiaEvolucion.VALOR_PROYECTO, DirectivaEstrategiaEvolucion.EXPLICACION_PROYECTO));
		estrategias.add(new NombreDescripcion(DirectivaEstrategiaEvolucion.VALOR_UNICA, DirectivaEstrategiaEvolucion.EXPLICACION_UNICA));
		seleccionables.put("estrategias", estrategias);
				
		// los repositorios
		Collection<NombreDescripcion> repositorios = new ArrayList<NombreDescripcion>();
		for(RepositorioCodigo repo: ce.repositorios().findAll(new Sort("nombre")))
			repositorios.add(new NombreDescripcion(repo.getId(), repo.getNombre()));
		seleccionables.put("repositorios", repositorios);

		// las aplicaciones
		Collection<NombreDescripcion> aplicaciones = new ArrayList<NombreDescripcion>();
		for(AplicacionEmpresa app: ce.aplicaciones().findAll(new Sort("id")))
			aplicaciones.add(new NombreDescripcion(app.getId(), app.getId() + " - " + app.getNombre()));
		seleccionables.put("aplicaciones", aplicaciones);
		
		// los empaquetados
		Collection<NombreDescripcion> empaquetados = new ArrayList<NombreDescripcion>();
		ParametroGlobal pg = ce.global().findOne("MAVEN.COORDENADAS.EMPAQUETADOS");
		if(pg!=null) {
			String contenido = pg.getContenido().replace("\n","").replace("\r","");
			String[] items = contenido.split(",");
			for(String item: items) {
				String[] cv = item.split(":");
				empaquetados.add(new NombreDescripcion(cv[0], cv[1]));		
			}
		}
		seleccionables.put("empaquetados", empaquetados);
		
		return seleccionables;
	}
	
	
}
