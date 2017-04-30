package es.nimio.nimiogcs.web.controllers.proyectos;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.proyecto.EliminarProyecto;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.proyectos.ConfirmarEliminacion;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping("/proyectos")
public class EliminarProyectoController {

	private IContextoEjecucion ce;

	@Autowired
	public EliminarProyectoController(IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ------------------------------------------------
	// Eliminar el artefacto
	// ------------------------------------------------

	// 1. presentamos la entidad para confirmar que se quiere eliminar
	@RequestMapping(path = "/eliminar/{id}", method = GET)
	public ModeloPagina eliminar(@PathVariable String id) {

		Proyecto proyecto = leerEntidadYComprobarPosibleEliminacion(id);

		if (proyecto == null)
			throw new ErrorEntidadNoEncontrada();

		return PaginaEliminarProyecto(proyecto).conModelo("confirmacion", new ConfirmarEliminacion(proyecto));
	}

	// 2. eliminamos
	@RequestMapping(path = "/eliminar", method = POST)
	public String eliminar(@ModelAttribute("confirmacion") ConfirmarEliminacion confirmacion) {

		final Proyecto proyecto = leerEntidadYComprobarPosibleEliminacion(confirmacion.getIdProyecto());

		new EliminarProyecto(ce).ejecutarCon(proyecto);
		
		return "redirect:/proyectos";
	}

	// -------------------------------------------------
	// Métodos privados
	// -------------------------------------------------

	private ModeloPagina PaginaEliminarProyecto(Proyecto proyecto) {

		return ModeloPagina
				.nuevaPagina(
						new EstructuraPagina("Eliminar proyecto " + proyecto.getNombre())
								.conComponentes(localizacionBase().conTexto(
										"Eliminar"))
				// añadimos un panel informativo indicando que es una operación
				// irreversible
				.conComponentes(new PanelInformativo().tipoPeligro().conTexto(
						"Se dispone a eliminar el siguiente proyecto del registro. Esta operación es irreversible."))

		.conComponentes(new PanelContinente().conTitulo("Datos generales").paraTipoDefecto().siendoContraible()
				.conComponentes(new Parrafo().conTexto("Datos base del proyecto."),

		MetodosDeUtilidad.parClaveValor("ID:", proyecto.getId()),
						MetodosDeUtilidad.parClaveValor("Nombre:", proyecto.getNombre()),
						MetodosDeUtilidad.parClaveValor("Repositorio:", proyecto.getEnRepositorio().getNombre())))
								.conComponentes(panelArtefactosAfectados(proyecto))
								.conComponentes(panelUsosYProyecciones(proyecto))
								// y el formulario para la confirmación de la
								// eliminación del artefacto
								.conComponentes(new Formulario("confirmacion", "/proyectos/eliminar", "POST",
										"/proyectos/" + proyecto.getId(), AyudanteCalculoEstructuraFormularioDesdePojo
												.altaDesdeDto(ConfirmarEliminacion.class))));

	}

	private Localizacion localizacionBase() {
		return new Localizacion(new ItemBasadoEnUrl("Home", "/"), new ItemBasadoEnUrl("Proyecto", "/proyectos"));

	}

	private PanelContinente panelArtefactosAfectados(Proyecto proyecto) {
		// creamos la tabla

		List<RelacionElementoProyectoArtefacto> artefactosAfectados = ce.relacionesProyectos()
				.findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(proyecto));
		final TablaBasica tablaArtefactos = new TablaBasica(false,
				Arrays.asList(new TablaBasica.DefinicionColumna("Artefacto", 7)));

		// rellenamos las filas
		for (RelacionElementoProyectoArtefacto r : artefactosAfectados) {

			// lo que nos interesa es el artefacto
			final Artefacto artefacto = r.getArtefacto();

			tablaArtefactos.conFila(new TextoSimple().conTexto(artefacto.getNombre()));
		}

		// queda devolver el panel
		return new PanelContinente().conTitulo("Artefactos afectados").paraTipoDefecto().siendoContraible()
				.conComponentes(new Parrafo().conTexto("Esta es la relación de artefactos afectados por el proyecto."),
						tablaArtefactos);
	}

	private PanelContinente panelUsosYProyecciones(Proyecto proyecto) {

		// por cada uso incluiremos un subpanel
		List<IComponente> subpaneles = new ArrayList<IComponente>();
		if (proyecto.getUsosYProyecciones() != null) {
			for (UsoYProyeccionProyecto uso : proyecto.getUsosYProyecciones()) {

				if (uso instanceof ProyeccionMavenDeProyecto) {

					subpaneles.add(new PanelContinente().conTitulo("Proyección utilizando Maven").paraTipoDefecto()
							.conComponentes(new Parrafo().conTexto(
									"La proyección Maven permite que el proyecto se contruya utilizando Maven desde línea de comando."),

					MetodosDeUtilidad.parClaveValor("Url proyecto:",
							((ProyeccionMavenDeProyecto) uso).getUrlRepositorio())));
				}
			}
		}

		// panel conteniendo los subpaneles
		return new PanelContinente().paraTipoDefecto().conTitulo("Usos y proyecciones").siendoContraible()
				.conComponentesSi(proyecto.getUsosYProyecciones() != null && proyecto.getUsosYProyecciones().size() > 0,
						subpaneles)
				.conComponenteSi(proyecto.getUsosYProyecciones() == null || proyecto.getUsosYProyecciones().size() == 0,
						new Parrafo().conTexto("El proyecto no dispone de usos asociados a él.").deTipoAviso());
	}

	private Proyecto leerEntidadYComprobarPosibleEliminacion(String id) {

		// -- recogemos la entidad
		Proyecto proyecto = ce.proyectos().findOne(id);
		if (proyecto == null)
			throw new ErrorEntidadNoEncontrada();

		// -- confirmamos que no haya dependencias que la referencien
		if (tieneEtiquetas(proyecto))
			throw new ErrorIntentoOperacionInvalida();

		if (tieneOperaciones(proyecto))
			throw new ErrorIntentoOperacionInvalida();

		return proyecto;
	}

	private boolean tieneEtiquetas(final Proyecto proyecto) {
		return ce.etiquetas().findAll(new Specification<EtiquetaProyecto>() {

			@Override
			public Predicate toPredicate(Root<EtiquetaProyecto> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

				return cb.equal(root.get("proyecto").get("id"), proyecto.getId());
			}

		}).size() > 0;
	}

	/**
	 * Recupera el resto de operacines que se pueden realizar sobre un proyecto
	 */

	private boolean tieneOperaciones(Proyecto proyecto) {
		return ce.operaciones().artefactoConOperacionesEnCurso(proyecto.getId()) != 0;
	}
}
