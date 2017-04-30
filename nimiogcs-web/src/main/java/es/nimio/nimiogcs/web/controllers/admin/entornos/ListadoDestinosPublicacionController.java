package es.nimio.nimiogcs.web.controllers.admin.entornos;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping("/admin/ciclovida/entornos")
public class ListadoDestinosPublicacionController {


	// ------------------------------------------------
	// Estado
	// ------------------------------------------------
	
	private IContextoEjecucion ce;
	
	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public ListadoDestinosPublicacionController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ************************************************
	
	// ------------------------------------------------
	// Puntos de servicio
	// ------------------------------------------------
	
	@RequestMapping(method=RequestMethod.GET)
	public ModeloPagina list() {
		
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Entornos")
				.conComponentes(
						new Localizacion()
							.conEnlace("Home", "/")
							.conTexto("Entornos"),
						new TablaBasica(
								false,
								Arrays.asList(
										new TablaBasica.DefinicionColumna[] {
												// --- el nombre
												new TablaBasica.DefinicionColumna(
														"Nombre",
														12,
														Collections.list(
																Streams.of(ce.destinosPublicacion().findAll())
																.map(
																		new Function<DestinoPublicacion, IComponente>() {
				
																			@Override
																			public IComponente apply(
																					DestinoPublicacion e) {
																				return new EnlaceSimple(
																						e.getNombre(),
																						"admin/ciclovida/entornos/" + e.getId()
																				);
																			}
																	
																		}
																)
																.getEnumeration()
														)
												)
										}
								)
						)
				)
		);
	}
	
}
