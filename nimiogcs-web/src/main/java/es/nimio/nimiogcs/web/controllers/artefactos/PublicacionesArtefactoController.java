package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.specs.ArtefactosPublicados;
import es.nimio.nimiogcs.jpa.specs.Publicaciones;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.artefactos.PaginaPublicacionesArtefacto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/artefactos/publicaciones")
public class PublicacionesArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public PublicacionesArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModelAndView publicaciones(
			@PathVariable("id") String id,
			@RequestParam(name="pag", required=false, defaultValue="1") Integer pagina) {
		
		// tenemos que confirmar que el artefacto existe
		Artefacto artefacto = ce.artefactos().findOne(id);
		if(artefacto == null) throw new ErrorEntidadNoEncontrada();
		boolean tieneDirectivaPublicacion =
				P.of(artefacto).publicacionDeployer() != null
				|| P.of(artefacto).publicacionJenkins() != null;
		if(!tieneDirectivaPublicacion)throw new ErrorEntidadNoEncontrada();
		
		Page<Publicacion> publicaciones = publicaciones(artefacto, pagina);
		Tuples.T2<Collection<DestinoPublicacion>, Collection<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>>> datosTabla =
				representacionTabla(artefacto, publicaciones);
		
		return ModeloPagina.nuevaPagina(
				new PaginaPublicacionesArtefacto(
						artefacto, 
						ce.operaciones().artefactoConOperacionesEnCurso(artefacto.getId()) != 0,
						publicaciones,
						datosTabla._1,
						datosTabla._2
				)
		);
	}
	
	// --
	
	private Tuples.T2<Collection<DestinoPublicacion>, Collection<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>>> representacionTabla(
			Artefacto artefacto, 
			Page<Publicacion> pagina) {
		
		// creamos un diccionario con los canales y las máquinas
		final Map<String, Collection<DestinoPublicacionCanal>> canales = new HashMap<String, Collection<DestinoPublicacionCanal>>();
		for(ICanalPublicacion cp: ce.contextoAplicacion().getBeansOfType(ICanalPublicacion.class).values()) {
			final DescripcionCanal dc = cp.teoricamentePosiblePublicarArtfacto(artefacto);
			if(dc!=null) canales.put(dc.getNombre(), Arrays.asList(dc.getDestinos()));
		}
		
		// convertimos el mapa anterior en índices conforme posicionamos las columnas
		final ArrayList<String> idsDestinos = new ArrayList<String>();
		final Map<String, Map<String, Integer>> indices = new HashMap<String, Map<String,Integer>>();
		for(Entry<String, Collection<DestinoPublicacionCanal>> e: canales.entrySet()) {
			final String canal = e.getKey();
			indices.put(canal, new HashMap<String, Integer>());
			for(DestinoPublicacionCanal dpc: e.getValue()) {
				final String idDestino = dpc.getIdInterno();
				Integer posicion = idsDestinos.indexOf(idDestino);
				if(posicion<0) {
					idsDestinos.add(idDestino);
					posicion = idsDestinos.size()-1;
				}
				indices.get(canal).put(idDestino, posicion);
			}
		}
		
		// convertimos los ids destinos en las entidades
		ArrayList<DestinoPublicacion> destinos = new ArrayList<DestinoPublicacion>();
		for(String idDestino: idsDestinos) {
			destinos.add(ce.destinosPublicacion().findOne(idDestino));
		}
		
		// -- preparamos la tabla
		ArrayList<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>> datos = 
				new ArrayList<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>>();
		
		for(Publicacion publicacion: pagina.getContent()) {

			// creamos el registro
			final Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]> r = 
					Tuples.tuple(
							publicacion.getCreacion(),
							publicacion.getCanal(),
							publicacion.getEstado(),
							new EtiquetaProyecto[destinos.size()],
							new String[destinos.size()]
					);
			
			// indicamos dónde va la etiqueta
			final Integer posicion = indices.get(publicacion.getCanal()).get(publicacion.getIdDestinoPublicacion());

			// buscamos la etiqueta
			for(PublicacionArtefacto pa: ce.artefactosPublicados().findAll(ArtefactosPublicados.elementosPublicados(publicacion))) {
				if(pa.getArtefacto().getId().equalsIgnoreCase(artefacto.getId())) {
					r._4[posicion] = ce.etiquetas().findOne(pa.getIdEtiqueta());
					r._5[posicion] = pa.getNombreEtiqueta();
				}
			}
			
			datos.add(r);
		}
		
		return 
				Tuples.tuple(
						(Collection<DestinoPublicacion>)destinos, 
						(Collection<Tuples.T5<Date, String, String, EtiquetaProyecto[], String[]>>)datos
				);
	}
	
	private Page<Publicacion> publicaciones(Artefacto artefacto, Integer pagina) {
	
		
		Pageable peticion = new PageRequest(
				pagina - 1, 
				15, 
				Direction.DESC, 
				"creacion");
		
		return 
				ce.publicaciones()
				.findAll(
						Publicaciones.publicacionesDeUnArtefacto(artefacto),
						peticion
				);
	}
	
}
