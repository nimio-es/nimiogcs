package es.nimio.nimiogcs.web.controllers.operaciones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoBatch;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionSitio;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.utils.DateTimeUtils;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.AreaPre;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/operaciones")
public class DatosOperacionesController {

	private IContextoEjecucion ce;

	@Autowired
	public DatosOperacionesController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	// --

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ModeloPagina datos(@PathVariable String id) {

		Operacion entidad = ce.operaciones().findOne(id);

		if (entidad == null)
			throw new ErrorEntidadNoEncontrada();

		// la parte común de la página
		EstructuraAbstractaPagina pagina = new EstructuraPagina("Operaciones").conComponentes(

		// La localización
				new Localizacion().conEnlace("Home", "/").conEnlace("Operaciones", "/operaciones")
						.conTexto(entidad.getId())

		).conComponentesSi(entidad.getEstadoEjecucionProceso() == EnumEstadoEjecucionProceso.ERROR,
				new PanelInformativo().tipoPeligro().conTexto("Esta operación concluye con errores."));

		// dependiendo de si estamos con una operación en curso/espera o si
		// estamos con una finalizada
		// mostraremos la información en una o dos columnas.
		if (!entidad.getFinalizado()) {
			pagina.conComponentes(new Columnas()
					.conColumna(new Columnas.Columna().conAncho(10).conComponentes(fichaOperacion(entidad)))
					.conColumna(new Columnas.Columna().conAncho(2)
							.conComponentes(new PanelContinente().conComponentes(
									new Parrafo("Utilice el siguiente enlace para terminar el proceso como error"),
									new Columnas.Columna().conAncho(12)
											.conComponentes(new Botonera().conAlineacionALaDerecha()
													.conBoton(new BotonEnlace().conTamañoMuyPequeño()
															.conTexto("Terminar").deTipoAviso()
															.paraUrl("/operaciones/terminar/" + entidad.getId())

			)).sinFilas()).conTitulo("Finalizar proceso").conLetraPeq().paraTipoDefecto())));
		} else {
			pagina.conComponentes(fichaOperacion(entidad));
		}

		// devolvemos la estructura final
		return ModeloPagina.nuevaPagina(pagina);

	}

	private List<IComponente> fichaOperacion(Operacion entidad) {

		ArrayList<IComponente> cps = new ArrayList<IComponente>();

		// panel datos generales
		cps.add(new PanelContinente().conTitulo("Datos generales").paraTipoDefecto().siendoContraible()
				.conComponentes(new Parrafo().conTexto("Datos comunes a todos los tipos de operaciones"),

		MetodosDeUtilidad.parClaveValor("ID:", entidad.getId()),
						MetodosDeUtilidad.parClaveValor("Descripcion:", entidad.getDescripcion()),
						MetodosDeUtilidad.parClaveValor("Tipo operacion:", entidad.getTipoOperacion()),
						MetodosDeUtilidad.parClaveValor("Usuario", entidad.getUsuarioEjecuta()),
						MetodosDeUtilidad.parClaveValor("Estado:", entidad.getEstadoEjecucionProceso().name()),
						MetodosDeUtilidad.parClaveValor("Fecha inicio:",
								DateTimeUtils.formaReducida(entidad.getCreacion())),
						MetodosDeUtilidad.parClaveValor("Fecha Finalizacion:",
								DateTimeUtils.formaReducida(entidad.getModificacion())),
						MetodosDeUtilidad.parClaveValor("Duración:", entidad.diferenciaTiempo())

		));

		if (entidad instanceof ProcesoBatch) {
			cps.add(new PanelContinente().conTitulo("Batch").paraTipoDefecto().siendoContraible()
					.conComponentes(new Parrafo().conTexto("Se trata de una operación de tipo BATCH"),

			MetodosDeUtilidad.parClaveValor("ID ejecución (BATCH):", (entidad instanceof ProcesoBatch)
					? Long.toString(((ProcesoBatch) entidad).getIdBatchJobExecution()) : "")));
		}

		if (entidad instanceof ProcesoEspera) {
			cps.add(new PanelContinente().conTitulo("Proceso de espera").paraTipoDefecto().siendoContraible()
					.conComponenteSi(entidad instanceof ProcesoEspera,
							new PanelContinente().conComponentes(
									new Parrafo().conTexto("Se trata de una operacion de tipo Proceso de Espera"),
									MetodosDeUtilidad.parClaveValor("Tique",
											(entidad instanceof ProcesoEspera)
													? ((ProcesoEspera) entidad).getTicketRelacionado() : "")))
					.conComponentesSi(entidad instanceof ProcesoEsperaPublicacionDeployer,
							new PanelContinente().conTitulo("Proceso espera Publicaciones Deployer").paraTipoDefecto()
									.conComponentes(
											new Parrafo().conTexto(
													"Se trata de una operacion de tipo Proceso Espera de publicaciones Deployer"),
									MetodosDeUtilidad.parClaveValor("Entorno",
											(entidad instanceof ProcesoEsperaPublicacionDeployer)
													? ((ProcesoEsperaPublicacionDeployer) entidad).getEntorno() : "")

			)).conComponentesSi(entidad instanceof ProcesoEsperaRespuestaDeployer,
					new PanelContinente().paraTipoDefecto().conTitulo("Proceso Espera de Respuesta Deployer")
							.conComponentes(
									new Parrafo().conTexto(
											"Se trata de una operacion de tipo Proceso de Respuesta de publicaciones Deployer"),
							MetodosDeUtilidad.parClaveValor("Etiqueta:",
									(entidad instanceof ProcesoEsperaRespuestaDeployer)
											? ((ProcesoEsperaRespuestaDeployer) entidad).getEtiquetaPase() : ""))));
		}

		// las relaciones de la operación con otros elementos
		cps.add(panelDependenciasOperacion(entidad.getId()));

		// mensajes de la ejecución de la operación
		cps.add(new PanelContinente().conTitulo("Mensajes ejecución de la operación").paraTipoDefecto()
				.siendoContraible().empiezaContraidoSi(Strings.isNullOrEmpty(entidad.getMensajesEjecucion()))
				.conComponente(new AreaPre().conTexto(entidad.getMensajesEjecucion()).deTipoInfo()));

		// mensaje de error
		cps.add(new PanelContinente().conTitulo("Mensaje de error")
				.paraTipoDefectoSi(Strings.isNullOrEmpty(entidad.getMensajeError()))
				.paraTipoPeligroSi(Strings.isNotEmpty(entidad.getMensajeError())).siendoContraible()
				.empiezaContraidoSi(Strings.isNullOrEmpty(entidad.getMensajeError()))
				.conComponente(new AreaPre().conTexto(entidad.getMensajeError()).deTipoInfo()));

		return cps;
	}

	private PanelContinente panelDependenciasOperacion(String id) {

		// calculamos las relaciones
		Collection<RelacionOperacion> relaciones = ce.relacionesOperaciones().relacionesDeUnaOperacion(id);

		// si no hay relaciones, lo indicamos
		if (relaciones.size() == 0)
			return new PanelContinente().conTitulo("Dependencias").paraTipoDefecto()
					.siendoContraible().conComponentes(new Parrafo()
							.conTexto("La operación no está relacionada con ningún elemento.").deTipoPrincipal())
					.empiezaContraido();

		// ---
		// en caso contrario, tenemos que caluclar la tabla para poder dibujarla
		// creamos la tabla

		final TablaBasica tablaRelaciones = new TablaBasica(false, Arrays.asList(

		new TablaBasica.DefinicionColumna("Tipo", 3), new TablaBasica.DefinicionColumna("Nombre", 3)));

		// rellenamos las filas
		for (RelacionOperacion relacion : relaciones) {

			if (relacion instanceof RelacionOperacionElementoProyecto) {
				final RelacionOperacionElementoProyecto roep = (RelacionOperacionElementoProyecto)relacion;
				final String url =
						roep.getElementoProyecto() != null ?
								roep.getElementoProyecto() instanceof Proyecto ? 
										"proyectos/" 
										: "proyectos/etiquetas/etiqueta/"
								: "";

				tablaRelaciones.conFila(
						new TextoSimple().conTexto(
								(roep.getElementoProyecto() instanceof Proyecto)
									? "Proyecto" : "Etiqueta"
						),

						roep.getElementoProyecto() != null ?
								new EnlaceSimple()
								.conTexto(roep.getElementoProyecto().getNombre())
								.paraUrl(url + roep.getElementoProyecto().getId())
								: new TextoSimple("Incoherencia!").aviso()
		
				);
			}
			if (relacion instanceof RelacionOperacionArtefacto) {

				tablaRelaciones.conFila(new TextoSimple()
						.conTexto(((RelacionOperacionArtefacto) relacion).getEntidad().getTipoArtefacto().getNombre()),

				new EnlaceSimple().conTexto(((RelacionOperacionArtefacto) relacion).getEntidad().getNombre())
						.paraUrl("artefactos/" + ((RelacionOperacionArtefacto) relacion).getEntidad().getId()));

			}
			if (relacion instanceof RelacionOperacionDestinoPublicacion) {
				tablaRelaciones.conFila(new TextoSimple().conTexto("Entorno"),

				new EnlaceSimple().conTexto(((RelacionOperacionDestinoPublicacion) relacion).getEntorno().getNombre())
						.paraUrl("admin/ciclovida/entornos/"
								+ ((RelacionOperacionDestinoPublicacion) relacion).getEntorno().getId()));

			}
			if (relacion instanceof RelacionOperacionPublicacionArtefacto) {

				tablaRelaciones.conFila(new TextoSimple().conTexto("Tique"),

				new EnlaceSimple().conTexto(((RelacionOperacionPublicacionArtefacto) relacion).getTicketOriginal())

				);
			}
			if (relacion instanceof RelacionOperacionSitio) {

				tablaRelaciones.conFila(new TextoSimple().conTexto("Servidor"),

				new EnlaceSimple().conTexto(((RelacionOperacionSitio) relacion).getSitio().getNombre())
						.paraUrl("admin/ciclovida/sitios/" + ((RelacionOperacionSitio) relacion).getSitio().getId()));

			}
		}

		// queda devolver el panel
		return new PanelContinente().conTitulo("Dependencias").paraTipoDefecto().siendoContraible()
				.conComponentes(new Parrafo().conTexto("Estas son las dependencias que tiene la operación."),

		tablaRelaciones);
	}

}