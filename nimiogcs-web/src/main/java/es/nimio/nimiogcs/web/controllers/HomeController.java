package es.nimio.nimiogcs.web.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DestinoPublicacionCanal;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.specs.ArtefactosPublicados;
import es.nimio.nimiogcs.jpa.specs.Publicaciones;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.SaltoDeLinea;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Lazy
@Controller
@RequestMapping({"/", "/homepage"})
@Scope(value="prototype")
public class HomeController {

	private IContextoEjecucion ce;
	
	@Autowired
	public HomeController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ----------------------------------------------------------------
	// Operaciones REST
	// ----------------------------------------------------------------

	@RequestMapping(method=GET)
	public ModelAndView home() {
		
		// la página
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Inicio")
				.conComponentes(
						new Localizacion().conTexto("Home"),
						tabla(),
						panelEntornos()
				)
		);
	}
	
	
	// ------
	
	private IComponente tabla() {

		final ArrayList<List<IComponente>> filas = filas();
		if(filas.size() == 0) return new Parrafo().conTexto("No hay publicaciones en curso.").deTipoPrincipal();
		
		final TablaBasica tabla = new TablaBasica(
				false,
				Arrays.asList(
						new TablaBasica.DefinicionColumna("Fecha", 1),
						new TablaBasica.DefinicionColumna("Canal", 2),
						new TablaBasica.DefinicionColumna("Destino", 2),
						new TablaBasica.DefinicionColumna("Proyecto", 3),
						new TablaBasica.DefinicionColumna("Etiqueta", 3),
						new TablaBasica.DefinicionColumna("Tipo", 1)
				)
		);
		
		for(List<IComponente> fila: filas) tabla.conFila(fila);
		
		return 
				new PanelContinente()
				.conTitulo("Publicaciones en curso")
				.paraTipoPrimario()
				.conComponente(tabla);
	}
	
	
	private ArrayList<List<IComponente>> filas() {
		
		final ArrayList<List<IComponente>> filas = new ArrayList<List<IComponente>>();
		
		for(Publicacion pb: ce.publicaciones().findAll(Publicaciones.publicacionesEnEjecucion())) {
			
			final TextoSimple fecha = new TextoSimple(DateTimeUtils.formaReducida(pb.getCreacion()));
			final TextoSimple canal = new TextoSimple(pb.getCanal());
			final TextoSimple destino = new TextoSimple(ce.destinosPublicacion().findOne(pb.getIdDestinoPublicacion()).getNombre());
			final TextoSimple tipo = 
					new TextoSimple()
					.conTextoSi(pb.esMarchaAtras(), "Marcha atrás")
					.peligroSi(pb.esMarchaAtras())
					.conTextoSi(!pb.esMarchaAtras(), "Normal");
					
			
			// ahora recorremos los artefactos afectados por la publicación para recoger el proyecto y la etiqueta
			final ArrayList<String> etiquetas = new ArrayList<String>();
			for(PublicacionArtefacto pa: ce.artefactosPublicados().findAll(ArtefactosPublicados.elementosPublicados(pb))) {
				
				if(!etiquetas.contains(pa.getIdEtiqueta())) {

					final EnlaceSimple proyecto = new EnlaceSimple()
							.conTexto(pa.getProyecto().getNombre())
							.paraUrl("proyectos/" + pa.getProyecto().getId());
					
					final EnlaceSimple etiqueta = new EnlaceSimple()
							.conTexto(pa.getNombreEtiqueta())
							.paraUrl("proyectos/etiquetas/etiqueta/" + pa.getIdEtiqueta());
					
					filas.add(
							Arrays.asList(
									new IComponente[] {
											fecha, 
											canal,
											destino,
											proyecto,
											etiqueta,
											tipo
									}
							)
					);
					
					// añadimos la etiqueta que ya hemos puesto
					etiquetas.add(pa.getIdEtiqueta());
				}
			}
		}
		
		return filas;
	}
	
	// panel entornos
	
	private PanelContinente panelEntornos() {
		
		final ArrayList<DestinoPublicacion> destinos = new ArrayList<DestinoPublicacion>();
		for(String idDestino: destinosOrdenadosPorCanal())
			destinos.add(ce.destinosPublicacion().findOne(idDestino));
		final int anchoColumna = 12 / destinos.size();
		final ArrayList<TablaBasica.DefinicionColumna> columnas = new ArrayList<TablaBasica.DefinicionColumna>();
		for(final DestinoPublicacion destino: destinos) {
			columnas.add(
					new TablaBasica.DefinicionColumna(
							destino.getNombre(), 
							anchoColumna
					)
			);
		}
		
		final TablaBasica tb = new TablaBasica(true, columnas);
		
		// cargamos las diez últimas publicaciones de cada destino y creamos un array
		// de arrays conteniendo las susodichas publicaciones.
		final Pageable peticion = new PageRequest(0,3,Direction.DESC,"creacion");
		final ArrayList<List<Publicacion>> publicaciones = new ArrayList<List<Publicacion>>();
		int maximo = 0;
		for(final DestinoPublicacion destino: destinos) {
			List<Publicacion> resQuery = 
					ce.publicaciones().findAll(
							Publicaciones.publicacionesDeUnDestino(destino),
							peticion
					)
					.getContent();
			publicaciones.add(resQuery);
			maximo = Math.max(maximo, resQuery.size());
		}
		
		// recorremos fila por fila de cada columna y vamos añadiendo un componente
		// a la fila que se añadirá a la tabla
		for(int fila=0; fila < maximo; fila++) {
			final ArrayList<IComponente> componentesFila = new ArrayList<IComponente>();
			for(int columna=0; columna<destinos.size(); columna++) {
				final List<Publicacion> publicacionesDestino = publicaciones.get(columna); 
				final Publicacion publicacion = publicacionesDestino.size()>fila? 
						publicacionesDestino.get(fila)
						: null;
				if(publicacion==null) componentesFila.add(new TextoSimple(" "));
				else {
					
					// de cada publicación vamos a leer todos los artefactos publicados
					final List<PublicacionArtefacto> artefactosPublicados = 
							ce.artefactosPublicados().findAll(
									ArtefactosPublicados.elementosPublicados(publicacion)
							);
					final HashMap<String, Tuples.T2<EtiquetaProyecto, List<Artefacto>>> mapaEtiquetaArtefactos = 
							new HashMap<String, Tuples.T2<EtiquetaProyecto,List<Artefacto>>>(); 
					for(final PublicacionArtefacto artefactoPublicado: artefactosPublicados) {
						final String nombreEtiqueta = artefactoPublicado.getNombreEtiqueta();
						if(!mapaEtiquetaArtefactos.containsKey(nombreEtiqueta)) {
							mapaEtiquetaArtefactos.put(
									nombreEtiqueta, 
									Tuples.tuple(
											ce.etiquetas().findOne(artefactoPublicado.getIdEtiqueta()),
											(List<Artefacto>)new ArrayList<Artefacto>()
									)
							);
						}
						mapaEtiquetaArtefactos.get(nombreEtiqueta)._2.add(artefactoPublicado.getArtefacto());
					}
					
					final ContinenteSinAspecto componente = 
							new ContinenteSinAspecto();
					
					// añadimos el nombre de la etiqueta y los artefactos
					for(Entry<String, Tuples.T2<EtiquetaProyecto, List<Artefacto>>> e: mapaEtiquetaArtefactos.entrySet()) {
						
						if(e.getValue()._1 == null) {
							componente.conComponente(new TextoSimple(e.getKey()).enNegrita());
						} else {
							componente.conComponente(
									new EnlaceSimple()
									.conTexto(e.getKey())
									.paraUrl("/proyectos/etiquetas/etiqueta/" + e.getValue()._1.getId())
							);
						}

						componente.conComponente(new SaltoDeLinea());
						
						final StringBuilder ars = new StringBuilder();
						for(int pos=0; pos<e.getValue()._2.size(); pos++) {
							ars.append(e.getValue()._2.get(pos).getNombre());
							if(pos<e.getValue()._2.size()) ars.append(", ");
						}
						
						componente.conComponente(
								new TextoSimple()
								.conTexto(ars.toString())
								.conLetraPeq()
						);

						componente.conComponente(new SaltoDeLinea());
					}
					
					// añadimos la fecha y hora
					componente.conComponentes(
							new ContinenteSinAspecto()
							.conComponente(
									new TextoSimple()
									.conTexto("Fecha:")
									.conLetraPeq()
									.enNegrita()
							)
							.conComponente(
									new TextoSimple(DateTimeUtils.formaReducida(publicacion.getCreacion()))
									.conLetraPeq()
							),
							
							new SaltoDeLinea()
					);
					
					// y el estado
					componente.conComponente(
							new TextoSimple()
							.conTexto(publicacion.getEstado())
							.avisoSi(publicacion.getEstado().equalsIgnoreCase(K.X.EJECUCION))
							.exitoSi(publicacion.getEstado().equalsIgnoreCase(K.X.OK))
							.peligroSi(publicacion.getEstado().equalsIgnoreCase(K.X.ERROR))
					);
					
					// ya podemos añadirlo a la fila
					componentesFila.add(componente);
				}
			}
			tb.conFila(componentesFila);
		}
		
		return 
				new PanelContinente()
				.conTitulo("Últimas publicaciones")
				.paraTipoPrimario()
				.conComponente(tb);
	}
	
	private ArrayList<String> destinosOrdenadosPorCanal() {
		
		final ArrayList<String> destinos = new ArrayList<String>();
		
		for(final Tuples.T2<Integer, ICanalPublicacion> t: canalesOrdenadosSegunPrioridad()) {
			for(final DestinoPublicacionCanal dpc: t._2.descripcionCanal().getDestinos()) {
				if(!destinos.contains(dpc.getIdInterno())) {
					destinos.add(dpc.getIdInterno());
				}
			}
		}
		
		return destinos;
	}
	
	private ArrayList<Tuples.T2<Integer, ICanalPublicacion>> canalesOrdenadosSegunPrioridad() {

		// cargamos los publicadores en un array en el que, además, añadiremos la prioridad
		// para ordenarlo de esta forma
		final ArrayList<Tuples.T2<Integer, ICanalPublicacion>> c_canales = new ArrayList<Tuples.T2<Integer,ICanalPublicacion>>();
		for(ICanalPublicacion cp: ce.contextoAplicacion().getBeansOfType(ICanalPublicacion.class).values()) {
			DescripcionCanal dc = cp.descripcionCanal();
			c_canales.add(
					Tuples.tuple(dc.getPrioridad(), cp)
			);
		}
		Collections.sort(
				c_canales,
				new Comparator<Tuples.T2<Integer, ICanalPublicacion>>() {

					@Override
					public int compare(T2<Integer, ICanalPublicacion> o1, T2<Integer, ICanalPublicacion> o2) {
						return o1._1.compareTo(o2._1);
					}
				}
		);
		return c_canales;
	}
}
