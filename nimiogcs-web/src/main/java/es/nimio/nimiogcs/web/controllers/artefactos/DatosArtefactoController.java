package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaDatosArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/artefactos")
public class DatosArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public DatosArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ---------------------------------------------------
	// Datos
	// ---------------------------------------------------

	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModeloPagina datos(@PathVariable String id) {
		
		// buscamos la entidad
		Artefacto entidad = ce.artefactos().findOne(id);
		if(entidad==null) throw new ErrorEntidadNoEncontrada();
		
		return ModeloPagina
				.nuevaPagina(
						new PaginaDatosArtefacto(
								entidad,
								ce.operaciones().artefactoConOperacionesEnCurso(entidad.getId()) != 0,
								proyectosEvolucionanUnArtefacto(entidad),
								totalRelacionesDependenciaConDestinoElArtefacto(entidad),
								directivasAdicionales(entidad)
						)
				);
	}

	// ---------------------------------------------------
	// Auxiliares
	// ---------------------------------------------------

	private List<RelacionElementoProyectoArtefacto> proyectosEvolucionanUnArtefacto(Artefacto artefacto) {
		return ce.relacionesProyectos().findAll(
							ArtefactosProyecto.relacionesDeUnArtefactoConProyectos(artefacto)
				);
	}
	
	private long totalRelacionesDependenciaConDestinoElArtefacto(Artefacto artefacto) {
		return ce.dependenciasArtefactos().totalRelacionesDependenciaConDestinoElArtefacto(artefacto.getId());
	}
	
	private ArrayList<Tuples.T2<TipoDirectiva, String>> directivasAdicionales(Artefacto artefacto) {
		
		
		
		ArrayList<Tuples.T2<TipoDirectiva, String>> opcionales = new ArrayList<Tuples.T2<TipoDirectiva, String>>();
		
		DirectivaCaracterizacion dc = PT.of(artefacto.getTipoArtefacto()).caracterizacion();
		if(dc==null) return opcionales;
		
		for(String idDirectiva: dc.getDirectivasOpcionales()) {
			
			// hay que distinguir entre directivas que son de tipo directo de los que son 
			// diccionarios
			if(idDirectiva.startsWith("@")) {
				
				final String idDicc = idDirectiva.replace("@", "");
				
				// confirmamos que el artefacto no tenga ya añadida la directiva con el diccionario
				boolean tiene = false;
				for(DirectivaBase db: artefacto.getDirectivasArtefacto())
					if(db instanceof DirectivaDiccionario)
						tiene = tiene || ((DirectivaDiccionario)db).getDiccionario().getId().equalsIgnoreCase(idDicc);
				if(!tiene) opcionales.add(Tuples.tuple(ce.tiposDirectivas().findOne("DICCIONARIO"), idDicc)); 
				
			} else {
			
				// si el artefacto no tiene ya la directiva, la agregamos a la lista
				if(P.of(artefacto).buscarDirectiva(idDirectiva)==null) 
					opcionales.add(Tuples.tuple(ce.tiposDirectivas().findOne(idDirectiva), (String)null));
			}
		}
		
		return opcionales;
	}

}
