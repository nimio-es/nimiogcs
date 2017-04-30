package es.nimio.nimiogcs.web.controllers.proyectos.etiquetas;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosPublicados;
import es.nimio.nimiogcs.operaciones.proyecto.etiquetas.EliminarEtiquetaDeProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.etiquetas.ConfirmarEliminacion;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping(path = "/proyectos/etiquetas/eliminar")
public class EliminarEtiquetaController {

	private IContextoEjecucion ce;

	@Autowired
	public EliminarEtiquetaController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	@RequestMapping(path = "/{idProyecto}/{idEtiqueta}", method = GET)
	public ModeloPagina eliminar(@PathVariable("idProyecto") String idProyecto,
			@PathVariable("idEtiqueta") String idEtiqueta) {

		boolean sePuedeEliminar = leerEntidadYComprobarPosibleEliminacion(idProyecto, idEtiqueta);

		if (!sePuedeEliminar)
			throw new ErrorEntidadNoEncontrada();

		EtiquetaProyecto etiqueta = ce.etiquetas().findOne(idEtiqueta);
		Proyecto proyecto = ce.proyectos().findOne(idProyecto);
		return PaginaEliminarEtiqueta(proyecto, etiqueta).conModelo("confirmacion",
				new ConfirmarEliminacion(proyecto, etiqueta));
	}

	@RequestMapping(method = POST)
	public String eliminar(@ModelAttribute("confirmacion") ConfirmarEliminacion confirmacion) {

		boolean sePuedeEliminar = leerEntidadYComprobarPosibleEliminacion(confirmacion.getIdProyecto(),
				confirmacion.getIdEtiqueta());

		if (!sePuedeEliminar)
			throw new ErrorEntidadNoEncontrada();
		final EtiquetaProyecto etiqueta = ce.etiquetas().findOne(confirmacion.getIdEtiqueta());
		final Proyecto proyecto = ce.proyectos().findOne(confirmacion.getIdProyecto());

		// lanzamos la operación de eliminación de la etiqueta
		new EliminarEtiquetaDeProyecto(ce)
			.ejecutarCon(etiqueta);
		
		return "redirect:/proyectos/etiquetas/" + proyecto.getId();
	}

	// -------------------------------------------------
	// Métodos privados
	// -------------------------------------------------
	private boolean leerEntidadYComprobarPosibleEliminacion(String idProyecto, String idEtiqueta) {

		// -- recogemos la entidad
		EtiquetaProyecto etiqueta = ce.etiquetas().findOne(idEtiqueta);
		Proyecto proyecto = ce.proyectos().findOne(idProyecto);
		if (proyecto == null)
			throw new ErrorEntidadNoEncontrada();
		if (etiqueta == null)
			throw new ErrorEntidadNoEncontrada();

		// -- confirmamos que no haya dependencias que la referencien

		if (tieneOperaciones(proyecto))
			throw new ErrorIntentoOperacionInvalida();

		if (tienePublicacionAsociada(etiqueta))
			throw new ErrorIntentoOperacionInvalida();

		return true;
	}

	private boolean tienePublicacionAsociada(EtiquetaProyecto etiqueta) {
		return !ce.artefactosPublicados().findAll(ArtefactosPublicados.elementosPublicadosEtiqueta(etiqueta)).isEmpty();
	}

	private boolean tieneOperaciones(Proyecto proyecto) {
		return ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) != 0;
	}

	private ModeloPagina PaginaEliminarEtiqueta(Proyecto proyecto, EtiquetaProyecto etiqueta) {

		return ModeloPagina.nuevaPagina(new EstructuraPagina(
				"Eliminar Etiqueta " + etiqueta.getNombre() + "en el proyecto" + proyecto.getNombre())
						.conComponentes(localizacionBase(proyecto.getId()).conTexto("Eliminar"))
						// añadimos un panel informativo indicando que es una
						// operación irreversible
						.conComponentes(new PanelInformativo().tipoPeligro().conTexto(
								"Se dispone a eliminar la siguiente etiqueta del registro. Esta operación es irreversible."))

		.conComponentes(new PanelContinente().conTitulo("Datos generales").paraTipoDefecto().siendoContraible()
				.conComponentes(new Parrafo().conTexto("Datos base del proyecto."),

		MetodosDeUtilidad.parClaveValor("ID:", etiqueta.getId()),
						MetodosDeUtilidad.parClaveValor("Nombre:", etiqueta.getNombre()),
						MetodosDeUtilidad.parClaveValor("Nombre proyecto:", proyecto.getNombre())

		))

		// y el formulario para la confirmación de la eliminación del artefacto
						.conComponentes(new Formulario("confirmacion", "/proyectos/etiquetas/eliminar", "POST",
								"/proyectos/etiquetas/" + proyecto.getId(), AyudanteCalculoEstructuraFormularioDesdePojo
										.altaDesdeDto(ConfirmarEliminacion.class))));

	}

	private Localizacion localizacionBase(String idproyecto) {
		return new Localizacion(new ItemBasadoEnUrl("Home", "/"),
				new ItemBasadoEnUrl("Proyecto", "/proyectos/" + idproyecto),
				new ItemBasadoEnUrl("Etiqueta", "/proyectos/etiquetas/" + idproyecto));

	}
}
