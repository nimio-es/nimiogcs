package es.nimio.nimiogcs.web.controllers.admin.tiposArtefactos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paginacion.Indexador;
import es.nimio.nimiogcs.web.componentes.paginacion.Paginador;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping("/admin/tipos")
public class ListadoTiposArtefactosController {
	private IContextoEjecucion ce;

	@Autowired
	public ListadoTiposArtefactosController (IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private final static int NUMERO_REGISTROS_POR_PAGINA = 10;
	
	// ------------------------------------------------
	// Listado
	// ------------------------------------------------		
	
	@RequestMapping(method=GET)
	public ModelAndView index(
			@RequestParam(required=false, value="pag" , defaultValue="1") Integer pag) {
	
		int paginaActual = pag != null ? pag : 1;
		Pageable pageable = new PageRequest(
				paginaActual - 1, 
				NUMERO_REGISTROS_POR_PAGINA, 
				Direction.ASC, "nombre");
		
		 Page<TipoArtefacto> pagina = ce.tipos().findAll(pageable); 
		// calculamos la ruta de redirección
			StringBuffer sb = new StringBuffer("pag=%d");
			// y devolvemos la paginación
		
			return ModeloPagina.nuevaPagina(
					new EstructuraPagina("Tipos")
					.conComponentes(
							new Localizacion()
							.conEnlace("Home", "/")
							.conTexto("Administración")
							.conEnlace("Tipos de artefactos", "/admin/tipos")
					)
					.conComponentes(
							new IComponente[] {
									new Columnas()
									.conColumna(
											new Columnas.Columna()
											.conAncho(10)
									        .conComponentes(
									        		new PanelContinente()
													.conTitulo("Tipos de Artefactos registrados")
													.paraTipoPrimario()
													.conComponentes(
										        		fabricaPaginador(
														pagina, 
														"admin/tipos", 
														sb.toString())
									            )
			                           )
									)
			                           .conColumna(
			                        		   new Columnas.Columna()
			   								   .conAncho(2)
			   								   .conComponentes(
																new PanelContinente()
									                            .conComponentes(
											                    new Parrafo("Pulsando en el botón podrá añadir un nuevo tipo a la lista."),
											new Columnas.Columna()
											.conAncho(14)
											.conComponentes(
													new Botonera()
													.conAlineacionALaIzquierda()
													.conBoton(
															new BotonEnlace()
															.conTamañoMuyPequeño()
															.conTexto("Crear nuevo tipo")
															.deTipoAviso()
															.paraUrl("/admin/tipos/nuevo" )
															
													)

											)
											.sinFilas()
									)
									.conTitulo("Acciones")
									.conLetraPeq()
									.paraTipoDefecto()
											
									)
			   								 )
					
							}	
				)	
					
					
		);
					
					
}

	public static Paginador fabricaPaginador(
			Page<TipoArtefacto> tipoArtefacto, String urlBase, String tipos) {
		
		return new Paginador()
		.conIndexador(
				new Indexador()
				.conTotalPaginas(tipoArtefacto.getTotalPages())
				.enPagina(tipoArtefacto.getNumber() + 1)
				.usarPlantillaRedireccion(urlBase)
				.usarPlantillaDeParametros(tipos)
		)
		.conContenido(
				new TablaBasica(
						true,
						Arrays.asList(
								new TablaBasica.DefinicionColumna[] {

										// --- Identificador
										new TablaBasica.DefinicionColumna(
												"Identificador",
												2,
												Collections.list(
														Streams.of(tipoArtefacto.getContent())
														.map(
																new Function<TipoArtefacto, IComponente>() {

																	@Override
																	public IComponente apply(
																			TipoArtefacto e) {
																		return new EnlaceSimple(
																				e.getId(), 
																				"admin/tipos/" +e.getId())
																		
																		;
																	}
															
																}
														)
														.getEnumeration()
												)
										),
										
										// --- Nombre
										new TablaBasica.DefinicionColumna(
												"Descripción",
												8,
												Collections.list(
														Streams.of(tipoArtefacto.getContent())
														.map(
																new Function<TipoArtefacto, IComponente>() {

																	@Override
																	public IComponente apply(
																			TipoArtefacto e) {
																    		return new TextoSimple()
																			.conTexto(e.getNombre());
																	}
															
																}
														)
														.getEnumeration()
												)
										),
										
										new TablaBasica.DefinicionColumna(
												"Uso",
												2,
												Collections.list(
														Streams.of(tipoArtefacto.getContent())
														.map(
																new Function<TipoArtefacto, IComponente>() {

																	@Override
																	public IComponente apply(
																			TipoArtefacto e) {
																    		return new TextoSimple()
																			.conTexto(
																					e.getDeUsuario() ? "USUARIO" : "INTERNO"
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
				);
		}
				
}