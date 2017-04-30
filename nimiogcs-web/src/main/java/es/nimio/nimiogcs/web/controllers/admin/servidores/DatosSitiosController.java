package es.nimio.nimiogcs.web.controllers.admin.servidores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorAplicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorLibreriaCompartida;
import es.nimio.nimiogcs.jpa.specs.ServidoresArtefactos;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Boton;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrlYActivo;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.Tabs;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelDatosNombreValor;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.controllers.utiles.UtilidadCargarUsuariosPaginaOperaciones;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.BuilderPaginadorOperaciones;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/ciclovida/sitios")
public class DatosSitiosController {

	// ------------------------------------------------
	// Constantes
	// ------------------------------------------------

	private static final int NUMERO_REGISTROS_POR_PAGINA = 10;
	
	private static final String PAGINA_DATOS = "datos";
	private static final String PAGINA_INSTALACION_SITIO = "confsitio";
	private static final String PAGINA_PROCESOS = "procesos";

	// ------------------------------------------------
	// Estado
	// ------------------------------------------------
	
	private IContextoEjecucion ce;

	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public DatosSitiosController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ************************************************
	
	// ------------------------------------------
	// Métodos auxiliares
	// ------------------------------------------
	
	private Localizacion localizacionParaSitio(String actual, Servidor sitio) {
		
		String cola = "";
		if(actual.equalsIgnoreCase(PAGINA_DATOS)) cola = "Datos";
		if(actual.equalsIgnoreCase(PAGINA_PROCESOS)) cola = "Procesos";
		if(actual.equalsIgnoreCase(PAGINA_INSTALACION_SITIO)) cola = "Conf. instalación";
		
		return new Localizacion(
				new ItemBasadoEnUrl("Home", "/"),
				new ItemBasadoEnUrl("Administración", ""),
				new ItemBasadoEnUrl("Sitios", "/admin/ciclovida/sitios"),
				new ItemBasadoEnUrl("Servidor Java"),
				new ItemBasadoEnUrl(sitio.getNombre(), "/admin/ciclovida/sitios/" + sitio.getNombre()),
				new ItemBasadoEnUrl(cola, "")
			);
	}
	
	private Tabs tabsParaSitio(String actual, Servidor sitio, Page<Operacion> operaciones) {
		
		Collection<ItemBasadoEnUrlYActivo> items = new ArrayList<ItemBasadoEnUrlYActivo>();
		items.add(new ItemBasadoEnUrlYActivo("Datos", "/admin/ciclovida/sitios/" + sitio.getId(), actual.equalsIgnoreCase(PAGINA_DATOS)));
		items.add(new ItemBasadoEnUrlYActivo("Configuración instalación", "/admin/ciclovida/sitios/servidor/instalacion/" + sitio.getId(), actual.equalsIgnoreCase(PAGINA_INSTALACION_SITIO)));
		items.add(new ItemBasadoEnUrlYActivo("Procesos", "/admin/ciclovida/sitios/procesos/" + sitio.getId(), actual.equalsIgnoreCase(PAGINA_PROCESOS)));
		
		return new Tabs(componentesTabsSitio(actual, sitio, operaciones), items);
	}
	
	private Collection<IComponente> componentesTabsSitio(String actual, Servidor sitio, Page<Operacion> operaciones) {

		if(actual.equalsIgnoreCase(PAGINA_DATOS)) return componentesTabsPaginaDatos(sitio);
		if(actual.equalsIgnoreCase(PAGINA_INSTALACION_SITIO)) return componentesTabsPaginaInstalacion(sitio);
		if(actual.equalsIgnoreCase(PAGINA_PROCESOS)) return componentesTabsPaginaProcesos(sitio, operaciones);
		
		return new ArrayList<IComponente>();
	}
	
	private Collection<IComponente> componentesTabsPaginaDatos(Servidor sitio) {
		return Arrays.asList( new IComponente[] {
				new PanelDatosNombreValor(
						sitio instanceof ServidorJava ?
							new PanelDatosNombreValor.DatoNombreValor[] {
								new PanelDatosNombreValor.DatoNombreValor("Nombre", sitio.getNombre()),
								new PanelDatosNombreValor.DatoNombreValor("Tipo de servidor", ((ServidorJava)sitio).getTipoServidor().getDescripcion())
							}
							: new PanelDatosNombreValor.DatoNombreValor[] {
									new PanelDatosNombreValor.DatoNombreValor("Nombre", sitio.getNombre())								
							}
					)
				.conTitulo("Datos generales")
					.paraTipoDefecto(),
				new Botonera(
						new BotonEnlace(
								"Editar", 
								"/admin/ciclovida/sitios/editar/" + sitio.getId(),
								Boton.TIPO_NORMAL,
								Boton.TAM_PEQUEÑO),
						new BotonEnlace(
								"Eliminar", 
								"/admin/ciclovida/sitios/eliminar/" + sitio.getId(),
								Boton.TIPO_PELIGRO,
								Boton.TAM_PEQUEÑO)
					).conAlineacionALaDerecha()
			});
	}
	
	private Collection<IComponente> componentesTabsPaginaInstalacion(Servidor sitio) {
		
		return Arrays.asList(
				new IComponente[] {
						new Parrafo("Elementos de software que están instalados en el servidor."),
						new PanelContinente()
						.paraTipoDefecto()
						.conTitulo("Librerías compartidas")
						.conComponentes( 
								new IComponente[] {
									new Parrafo().conTexto("Estas son las librerías compartidas que actualmente se han asociado al servidor.").conLetraPeq(),
									listaLibreriasCompartidasDelServidor(sitio),
									new Botonera(new BotonEnlace("Añadir librería compartida", "/admin/ciclovida/sitios/servidor/lc/nuevo/" + sitio.getId(), BotonEnlace.TIPO_NORMAL, BotonEnlace.TAM_PEQUEÑO))
										.conAlineacionALaDerecha()
								}
						)
						.siendoContraible(),
						
						new PanelContinente()
						.paraTipoDefecto()
						.conTitulo("EAR's")
						.conComponentes(
								new IComponente[] {
									new Parrafo().conTexto("EAR's que están asociados al servidor.").conLetraPeq(),
									listaEarsDelServidor(sitio),
									new Botonera(new BotonEnlace("Añadir EAR", "/admin/ciclovida/sitios/servidor/ear/nuevo/" + sitio.getId(), BotonEnlace.TIPO_NORMAL, BotonEnlace.TAM_PEQUEÑO))
										.conAlineacionALaDerecha()
								})
						.siendoContraible(),
						
						new PanelContinente()
						.paraTipoDefecto()
						.conTitulo("WAR's")
						.conComponentes(
								new IComponente[] {
									new Parrafo().conTexto("Módulos Web (WAR) que están asociados al servidor fuera de un EAR.").conLetraPeq(),
									listaWaresDelServidor(sitio),
									new Botonera(new BotonEnlace("Añadir WAR", "/admin/ciclovida/sitios/servidor/war/nuevo/" + sitio.getId(), BotonEnlace.TIPO_NORMAL, BotonEnlace.TAM_PEQUEÑO))
										.conAlineacionALaDerecha()
								}
						)
						.siendoContraible()
				}
			);
	}
	
	private IComponente listaLibreriasCompartidasDelServidor(Servidor sitio) {
		
		// cargamos las relaciones 
		List<RelacionServidorLibreriaCompartida> libreriasCompartidas = new ArrayList<RelacionServidorLibreriaCompartida>(); 
		for(RelacionServidorArtefacto rel: ce.servidoresArtefactos().findAll(ServidoresArtefactos.pomsEnServidor(sitio))) {
			libreriasCompartidas.add((RelacionServidorLibreriaCompartida)rel);
		}
		
		// si no hay nada, así lo indicamos
		if(libreriasCompartidas.isEmpty()) return 
				new PanelInformativo()
				.conTexto("No hay Librerías compartidas instaladas.")
				.tipoInfo();
		
		// preparamos la tabla
		TablaBasica tabla = new TablaBasica(
				true,
				Arrays.asList(new TablaBasica.DefinicionColumna[] {
						new TablaBasica.DefinicionColumna("Librería compartida", 5),
						new TablaBasica.DefinicionColumna("Ruta servidor", 4),
						new TablaBasica.DefinicionColumna("Acciones", 3)
				})
			);
		for(RelacionServidorLibreriaCompartida relacion: libreriasCompartidas) {
			tabla.columnas().get(0).componentes().add(
					new EnlaceSimple(
							relacion.getArtefacto().getNombre(),
							"/artefactos/" + relacion.getArtefacto().getId()));
			tabla.columnas().get(1).componentes().add(new TextoSimple(relacion.getCarpetaServidor()));
			tabla.columnas().get(2).componentes().add(
					new Botonera()
							.conAlineacionALaDerecha()
							.conBoton(
									new BotonEnlace()
										.conTexto("Editar")
										.paraUrl("/admin/ciclovida/sitios/servidor/lc/editar/" + relacion.getId())
										.conTamañoMuyPequeño()
								)
							.conBoton(
									new BotonEnlace()
										.conTexto("Eliminar")
										.paraUrl("/admin/ciclovida/sitios/servidor/lc/borrar/" + relacion.getId())
										.conTamañoMuyPequeño()
										.deTipoPeligro()
								)
				);
		}
		
		return tabla;
	}
	
	private IComponente listaEarsDelServidor(Servidor sitio) {
		
		// cargamos las relaciones 
		List<RelacionServidorAplicacion> ears = new ArrayList<RelacionServidorAplicacion>();
		for(RelacionServidorArtefacto rel: ce.servidoresArtefactos().findAll(ServidoresArtefactos.earsEnServidor(sitio))) 
			ears.add((RelacionServidorAplicacion)rel);
		
		// si no hay nada, así lo indicamos
		if(ears.isEmpty()) return 
				new PanelInformativo()
				.tipoInfo()
				.conTexto("No hay EAR's instalados.");
		
		// preparamos la tabla
		TablaBasica tabla = new TablaBasica(
				true,
				Arrays.asList(new TablaBasica.DefinicionColumna[] {
						new TablaBasica.DefinicionColumna("Ear", 3),
						new TablaBasica.DefinicionColumna("Id. Aplicación", 3),
						new TablaBasica.DefinicionColumna("Virtual Host", 3),
						new TablaBasica.DefinicionColumna("Acciones", 3)
				})
			);
		for(RelacionServidorAplicacion relacion: ears) {
			tabla.columnas().get(0).componentes().add(
					new EnlaceSimple(
							relacion.getArtefacto().getNombre(),
							"artefactos/" + relacion.getArtefacto().getId()));
			tabla.columnas().get(1).componentes().add(new TextoSimple(relacion.getIdAplicacion()));
			tabla.columnas().get(2).componentes().add(new TextoSimple(relacion.getVirtualHost()));
			tabla.columnas().get(3).componentes().add(
					new Botonera()
							.conAlineacionALaDerecha()
							.conBoton(
									new BotonEnlace()
										.conTexto("Editar")
										.paraUrl("/admin/ciclovida/sitios/servidor/ear/editar/" + relacion.getId())
										.conTamañoMuyPequeño()
								)
							.conBoton(
									new BotonEnlace()
										.conTexto("Eliminar")
										.paraUrl("/admin/ciclovida/sitios/servidor/ear/borrar/" + relacion.getId())
										.conTamañoMuyPequeño()
										.deTipoPeligro()
								)
				);
		}
		
		return tabla;
	}
	
	private IComponente listaWaresDelServidor(Servidor sitio) {
		
		// cargamos las relaciones 
		List<RelacionServidorAplicacion> wares = new ArrayList<RelacionServidorAplicacion>();
		for(RelacionServidorArtefacto rel: ce.servidoresArtefactos().findAll(ServidoresArtefactos.warsEnServidor(sitio))) {
			wares.add((RelacionServidorAplicacion)rel);
		}

		// si no hay nada, así lo indicamos
		if(wares.isEmpty()) return 
				new PanelInformativo()
				.tipoInfo()
				.conTexto("No hay WARes instalados.");
		
		// preparamos la tabla
		TablaBasica tabla = new TablaBasica(
				true,
				Arrays.asList(new TablaBasica.DefinicionColumna[] {
						new TablaBasica.DefinicionColumna("Ear", 3),
						new TablaBasica.DefinicionColumna("Id. Aplicación", 3),
						new TablaBasica.DefinicionColumna("Virtual Host", 3),
						new TablaBasica.DefinicionColumna("Acciones", 3)
				})
			);
		for(RelacionServidorAplicacion relacion: wares) {
			tabla.columnas().get(0).componentes().add(
					new EnlaceSimple(
							relacion.getArtefacto().getNombre(),
							"/artefactos/" + relacion.getArtefacto().getId()));
			tabla.columnas().get(1).componentes().add(new TextoSimple(relacion.getIdAplicacion()));
			tabla.columnas().get(2).componentes().add(new TextoSimple(relacion.getVirtualHost()));
			tabla.columnas().get(3).componentes().add(
					new Botonera()
							.conAlineacionALaDerecha()
							.conBoton(
									new BotonEnlace()
										.conTexto("Editar")
										.paraUrl("/admin/ciclovida/sitios/servidor/war/editar/" + relacion.getId())
										.conTamañoMuyPequeño()
								)
							.conBoton(
									new BotonEnlace()
										.conTexto("Eliminar")
										.paraUrl("/admin/ciclovida/sitios/servidor/war/borrar/" + relacion.getId())
										.conTamañoMuyPequeño()
										.deTipoPeligro()
								)
				);
		}
		
		return tabla;
	}
	
	private Collection<IComponente> componentesTabsPaginaProcesos(Servidor sitio, Page<Operacion> operaciones) {
		return Arrays.asList(
				new IComponente[] {
						BuilderPaginadorOperaciones.fabricaPaginador(
								operaciones,
								UtilidadCargarUsuariosPaginaOperaciones.cargarUsuariosPagina(ce, operaciones),
								"/admin/ciclovida/sitios/operaciones/" + sitio.getId(), 
								"pag=%d"
						)
				}
			);
	}
	
	/**
 	 * Los componentes de la página global.
	 */ 
	private Collection<IComponente> componentesPaginaSitio(String actual, Servidor sitio, Page<Operacion> pagina) {

		Collection<IComponente> componentes = comienzaConPanelAvisoSiProcesosEnCurso(
				new ArrayList<IComponente>(),
				sitio);
		
		componentes.add(tabsParaSitio(actual, sitio, pagina));

		return componentes;
	}

	// TODO: esto podría pasarse a una clase de utilidad común para que sea usado por varios
	private Collection<IComponente> comienzaConPanelAvisoSiProcesosEnCurso(Collection<IComponente> componentes, Servidor sitio) {
		if(ce.operaciones().artefactoConOperacionesEnCurso(sitio.getId()) != 0) 
			componentes.add(
					new PanelInformativo()
					.tipoAviso()
					.conTexto(
							"Hay una o varias operaciones actualmente en curso. "
							+ "Las nuevas operaciones que se podrán realizar estarán condicionadas "
							+ "a la finalización de las operaciones que ya se encuentran en curso."
					)
			);
		
		return componentes;
	}
	
	// ------------------------------------------
	// Métodos REST
	// ------------------------------------------

	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModeloPagina read(@PathVariable String id) {

		final String paginaActual = PAGINA_DATOS;
		
		// buscamos la entidad
		Servidor sitio = ce.servidores().findByIdOrNombre(id);
		if(sitio == null) throw new ErrorEntidadNoEncontrada();

		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Datos del sitio")
					.conComponentes(localizacionParaSitio(paginaActual, sitio))
					.conComponentes(componentesPaginaSitio(paginaActual, sitio, null))
			);
	}
	
	@RequestMapping(path="/servidor/instalacion/{id}", method=RequestMethod.GET)
	public ModeloPagina confsitio(@PathVariable String id) {

		final String paginaActual = PAGINA_INSTALACION_SITIO;
		
		// buscamos la entidad
		Servidor sitio = ce.servidores().findByIdOrNombre(id);
		if(sitio == null) throw new ErrorEntidadNoEncontrada();

		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Elementos instalados en el servidor")
					.conComponentes(localizacionParaSitio(paginaActual, sitio))
					.conComponentes(componentesPaginaSitio(paginaActual, sitio, null))
			);
	}
	
	/**
	 * Lista los procesos asociados al sitio.
	 */
	@RequestMapping(path="/operaciones/{id}", method=RequestMethod.GET)
	public ModelAndView procesos(
			@PathVariable String id,
			@RequestParam(required=false, value="pag", defaultValue="1") Integer pagina) {

		final String paginaActual = PAGINA_PROCESOS;
		
		// buscamos la entidad
		Servidor sitio = ce.servidores().findByIdOrNombre(id);
		if(sitio == null) throw new ErrorEntidadNoEncontrada();

		// estructura de paginación
		Pageable pageable = new PageRequest(pagina - 1, NUMERO_REGISTROS_POR_PAGINA);

		// cargamos las operaciones asociadas
		Page<Operacion> operacionesServidor = ce.operaciones().operacionesDeArtefacto(pageable, id);

		// vamos preparando la vista y el model
		// ModelAndView mv = new ModelAndView("/admin/ciclovida/sitios/datos_procesos");
		ModelAndView mv = ModeloPagina.nuevaPagina(
				new EstructuraPagina("Procesos asociados al sitio")
					.conComponentes(localizacionParaSitio(paginaActual, sitio))
					.conComponentes(componentesPaginaSitio(paginaActual, sitio, operacionesServidor))
			);
		
		return mv;
	}

}
