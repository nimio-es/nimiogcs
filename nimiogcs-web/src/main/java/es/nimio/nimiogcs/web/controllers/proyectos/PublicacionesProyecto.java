package es.nimio.nimiogcs.web.controllers.proyectos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DestinoPublicacionCanal;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.proyectos.PaginaListadoPublicacionesProyecto;
import es.nimio.nimiogcs.web.dto.p.proyectos.PaginaTablaPublicacionesProyecto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/proyectos/publicaciones")
public class PublicacionesProyecto {

	private IContextoEjecucion ce;
	
	@Autowired
	public PublicacionesProyecto(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModelAndView publicaciones(@PathVariable("id") String id) {
		return impacto(id);
	}
	
	@RequestMapping(path="impacto/{id}", method=RequestMethod.GET)
	public ModelAndView impacto(@PathVariable("id") String id) {

		// tiene que existir el proyecto
		final ElementoBaseProyecto elemento = ce.proyectos().findOne(id);
		if(elemento==null || !(elemento instanceof Proyecto)) 
			throw new ErrorEntidadNoEncontrada();
		
		final Proyecto proyecto = (Proyecto)elemento;
		
		final PaginaTablaPublicacionesProyecto.TablaDatos tablaPublicaciones = tablaPublicaciones(proyecto); 
		
		return ModeloPagina.nuevaPagina(
				new PaginaTablaPublicacionesProyecto(
						proyecto, 
						ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) > 0,
						tablaPublicaciones
				)
		);
	}

	@RequestMapping(path="lista/{id}", method=RequestMethod.GET)
	public ModelAndView listado(
			@PathVariable("id") String id,
			@RequestParam(name="pag", required=false, defaultValue="1") Integer pag) {

		// tiene que existir el proyecto
		final ElementoBaseProyecto elemento = ce.proyectos().findOne(id);
		if(elemento==null || !(elemento instanceof Proyecto)) 
			throw new ErrorEntidadNoEncontrada();
		
		final Proyecto proyecto = (Proyecto)elemento;
		
		final PaginaListadoPublicacionesProyecto.ListaDatos listaPublicaciones = 
				listaPublicaciones(proyecto, pag-1);
		
		return ModeloPagina.nuevaPagina(
				new PaginaListadoPublicacionesProyecto(
						proyecto, 
						ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) > 0, 
						listaPublicaciones
				)
		);
	}
	
	// ----

	private PaginaTablaPublicacionesProyecto.TablaDatos tablaPublicaciones(Proyecto proyecto) {
	
		// cargamos los elementos del proyecto
		final Collection<PaginaTablaPublicacionesProyecto.ArtefactoProyecto> artefactosProyecto = artefactosProyecto(proyecto);

		// evaluamos con qué canales nos quedamos 
		final Collection<ICanalPublicacion> canalesPublicacion = canalesAUsar(artefactosProyecto);
		
		// lista y diccionario de los destinos de publicación
		final ArrayList<DestinoPublicacion> destinos = new ArrayList<DestinoPublicacion>();
		final Map<String, Integer> idxDestinos = new HashMap<String, Integer>();
		for(ICanalPublicacion cp: canalesPublicacion) {
			DescripcionCanal dc = cp.descripcionCanal();
			for(DestinoPublicacionCanal dpc: dc.getDestinos()) {
				if(idxDestinos.containsKey(dpc.getIdInterno())) continue;
				destinos.add(ce.destinosPublicacion().findOne(dpc.getIdInterno()));
				idxDestinos.put(dpc.getIdInterno(), destinos.size()-1);
			}
		}
		
		// queda ir fila por fila buscando la última publicación 
		// en cada uno de los canales posibles
		PaginaTablaPublicacionesProyecto.DatoCelda[][] datos = 
				new PaginaTablaPublicacionesProyecto.DatoCelda[artefactosProyecto.size()][destinos.size()];
		int fila = 0;
		for(PaginaTablaPublicacionesProyecto.ArtefactoProyecto pap: artefactosProyecto) {

			// para cada artefacto mantenemos un mapa de los posibles para no confundir
			// con no publicaciones
			final HashMap<String, Boolean> posibles = new HashMap<String, Boolean>();
			for(String id: idxDestinos.keySet()) posibles.put(id, false);
			for(ICanalPublicacion cp: canalesPublicacion) {
				final boolean posible = cp.teoricamentePosiblePublicarArtfacto(pap.base())!= null;  
				if(posible) {
					DescripcionCanal dc = cp.descripcionCanal();
					for(DestinoPublicacionCanal dpc: dc.getDestinos())
						posibles.put(dpc.getIdInterno(), true);
				}
			}
			
			// recorremos los casos posibles y pedimos la última publicación
			for(Entry<String, Boolean> p: posibles.entrySet()) {
				final int columna = idxDestinos.get(p.getKey()); 
				if(p.getValue()) {
					
					// todas las consultas tendrán el mismo tamaño
					Pageable peticion = new PageRequest(
							0, 
							1, 
							Direction.DESC, 
							"id");
					
					// toca buscar la etiqueta
					Page<PublicacionArtefacto> pb = 
							ce.artefactosPublicados()
							.deProyectoYArtefactoYDestino(
									proyecto.getId(), 
									pap.base().getId(),
									p.getKey(),
									peticion
							);
					
					if(pb.getContent().size() == 0) {

						datos[fila][columna] = new PaginaTablaPublicacionesProyecto.DatoCeldaSinPublicacion();
						
					} else {
					
						// y añadir la celda
						datos[fila][columna] = 
								new PaginaTablaPublicacionesProyecto.DatoCeldaPublicacion(
										pb.getContent().get(0),
										ce.etiquetas().findOne(pb.getContent().get(0).getIdEtiqueta())
								);
					}
					
				} else {
					
					// un dato de no aplica
					datos[fila][columna] = new PaginaTablaPublicacionesProyecto.DatoCeldaNoAplica();
				}
			}
			
			// siguiente fila
			fila++;
		}
		
		return new PaginaTablaPublicacionesProyecto.TablaDatos(destinos,artefactosProyecto,datos);
	}
	
	private Collection<PaginaTablaPublicacionesProyecto.ArtefactoProyecto> artefactosProyecto(Proyecto proyecto) {

		ArrayList<PaginaTablaPublicacionesProyecto.ArtefactoProyecto> artefactos = new ArrayList<PaginaTablaPublicacionesProyecto.ArtefactoProyecto>();
		
		// recorremos toda la lista de artefactos del proyecto 
		// y los vamos metiendo en la lista a devolver. 
		for(RelacionElementoProyectoArtefacto repa: 
			ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto))) {

			final Artefacto evolutivo = repa.getArtefacto();
			final Artefacto base = evolutivo instanceof ITestaferroArtefacto ? 
					((ITestaferroArtefacto)evolutivo).getArtefactoAfectado() 
					: evolutivo;
					
			artefactos.add(new PaginaTablaPublicacionesProyecto.ArtefactoProyecto(base, evolutivo));
		}
		
		return artefactos;
	}
	
	private Collection<ICanalPublicacion> canalesAUsar(Collection<PaginaTablaPublicacionesProyecto.ArtefactoProyecto> artefactosProyecto) {

		final ArrayList<ICanalPublicacion> canalesPublicacion = new ArrayList<ICanalPublicacion>(); 

		// cargamos los canales de publicación
		final Collection<ICanalPublicacion> canalesPublicacionCandidatos = ce.contextoAplicacion().getBeansOfType(ICanalPublicacion.class).values();
			
		for(ICanalPublicacion cp: canalesPublicacionCandidatos) {
			boolean algunoPuede = false;
			for(PaginaTablaPublicacionesProyecto.ArtefactoProyecto pap: artefactosProyecto) {
				algunoPuede = (cp.teoricamentePosiblePublicarArtfacto(pap.base()) != null);
				if(algunoPuede) break;
			}
			
			if(algunoPuede) canalesPublicacion.add(cp);
		}
		return canalesPublicacion;
	}
	
	// ----

	private PaginaListadoPublicacionesProyecto.ListaDatos listaPublicaciones(Proyecto proyecto, Integer pagina) {
		
		// las peticiones
		Pageable peticion = new PageRequest(
				pagina, 
				10, 
				Direction.DESC, 
				"creacion");
		final Page<Publicacion> publicaciones = ce.publicaciones().publicacionesProyecto(proyecto.getId(), peticion);
		
		// vamos a leer todos los canales
		final Map<String, DestinoPublicacion> destinos = new HashMap<String, DestinoPublicacion>();
		for(Publicacion p: publicaciones.getContent()) {
			final String id = p.getIdDestinoPublicacion();
			if(destinos.containsKey(id)) continue;
			destinos.put(id, ce.destinosPublicacion().findOne(id));
		}
		
		return new PaginaListadoPublicacionesProyecto.ListaDatos(
				destinos.values(), 
				publicaciones
		);
	}
}
