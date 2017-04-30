package es.nimio.nimiogcs.web.controllers.artefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaDependenciasEnCursoArtefacto;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaDependenciasEstaticasArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/artefactos/dependencias")
public class DependenciasArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public DependenciasArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Listado
	// ------------------------------------------------

	@RequestMapping(path="{id}", method=GET)
	public ModeloPagina dependencias(
			@PathVariable String id) {
		
		return estaticas(id);
	}
	
	@RequestMapping(path="/estaticas/{id}", method=GET)
	public ModeloPagina estaticas(@PathVariable String id) {

		// primero es confirmar que existe una entidad con el id 
		Artefacto entidad = ce.artefactos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(
				new PaginaDependenciasEstaticasArtefacto(
						entidad,
						ce.operaciones().artefactoConOperacionesEnCurso(entidad.getId()) > 0,
						cargaArtefactosRequeridos(entidad),
						cargaArtefactosQueDependen(entidad)
				)
		);
	}
	
	@RequestMapping(path="/encurso/{id}", method=GET)
	public ModeloPagina encurso(@PathVariable String id) {
		// primero es confirmar que existe una entidad con el id 
		Artefacto entidad = ce.artefactos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		// y devolvemos la paginación
		return ModeloPagina.nuevaPagina(
				new PaginaDependenciasEnCursoArtefacto(
						entidad,
						ce.operaciones().artefactoConOperacionesEnCurso(entidad.getId()) > 0
				)
		);
	}
	
	
	// --------------------------------------------------
	// utiles
	// --------------------------------------------------
	
	/**
	 * Devuelve los artefactos requeridos
	 * @param artefacto
	 * @return
	 */
	private List<Dependencia> cargaArtefactosRequeridos(Artefacto artefacto) {
		
		List<Dependencia> relacionesConsulta = new ArrayList<Dependencia>( 
				ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(artefacto.getId())
		);
	
		Collections.sort(
				relacionesConsulta,
				new Comparator<Dependencia>() {

					@Override
					public int compare(Dependencia o1, Dependencia o2) {

						// si ninguna es de tipo posicional, lo dejamos igual
						if(!(o1 instanceof DependenciaPosicional) && !(o2 instanceof DependenciaPosicional)) return 0;
						
						// si la primera es de tipo posicional y la segunda no, se posiciona primero la segunda
						if(o1 instanceof DependenciaPosicional && !(o2 instanceof DependenciaPosicional)) return 1;
						
						// si la segunda es de tipo posicional y la primera no, se posiciona primero la primera
						if(!(o1 instanceof DependenciaPosicional) && o2 instanceof DependenciaPosicional) return -1;
						
						// en cualquier otro caso, comapramos las posiciones
						DependenciaPosicional p1 = (DependenciaPosicional)o1;
						DependenciaPosicional p2 = (DependenciaPosicional)o2;
						
						Integer pos1 = p1.getPosicion() != null ? p1.getPosicion() : 0;
						Integer pos2 = p2.getPosicion() != null ? p2.getPosicion() : 0;
						
						return pos1.compareTo(pos2);
					}
				}
		);
		
		return relacionesConsulta;
	}

	/**
	 * Devuelve los artefactos que dependen de éste
	 * @param id
	 * @return
	 */
	private List<Dependencia> cargaArtefactosQueDependen(Artefacto artefacto) {

		List<Dependencia> relacionesConsulta = ce.dependenciasArtefactos().relacionesRequierenDeUnArtefacto(artefacto.getId());
		
		// quitamos los evolutivos 
		List<Dependencia> resultado = new ArrayList<Dependencia>();
		for(Dependencia r: relacionesConsulta) {
			if(!(r.getDependiente() instanceof EvolucionArtefacto)) resultado.add(r);
		}
		
		// lo ordenamos con el nombre
		Collections.sort(
				resultado,
				new Comparator<Dependencia>() {

					@Override
					public int compare(Dependencia o1, Dependencia o2) {
						return o1.getDependiente().getNombre().compareTo(o2.getDependiente().getNombre());
					}
				}
		);
		
		return resultado;
	}
}
