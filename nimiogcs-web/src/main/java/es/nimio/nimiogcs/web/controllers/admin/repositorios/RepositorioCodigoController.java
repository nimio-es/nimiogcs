package es.nimio.nimiogcs.web.controllers.admin.repositorios;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.f.admin.repositorios.FormularioRepositorioCodigo;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path=Paths.Admin.RepositoriosCodigo.BASE)
public class RepositorioCodigoController {

	private final IContextoEjecucion ce;
	
	@Autowired
	public RepositorioCodigoController(final IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	/**
	 * Información sobre un repositorio concreto
	 */
	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_DATOS, method=RequestMethod.GET)
	public ModelAndView repositorio(@PathVariable String id) {
		
		RepositorioCodigo repositorio = ce.repositorios().findOne(id);
		if(repositorio == null) throw new ErrorEntidadNoEncontrada();
		
		return datos(repositorio);
	}
	
	/**
	 * Formulario para el alta
	 */
	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_NUEVO, method=RequestMethod.GET)
	public ModelAndView registrar_get() {

		return nuevoRegistro()
				.conModelo("datos", new FormularioRepositorioCodigo());
	}

	/**
	 * Método que recoge la petición de alta. 
	 * @throws Throwable 
	 */
	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_NUEVO, method=RequestMethod.POST)
	public ModelAndView registrar_post(
			@ModelAttribute("datos") @Valid final FormularioRepositorioCodigo datosFormulario, 
			Errors errores) {

		datosFormulario.validate(ce, errores);
		
		// comprobar que no los datos son correctos
		if (errores.hasErrors()) {
			return nuevoRegistro()
					.conModelo("datos", datosFormulario);
		}
		
		// almacenarlo
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {

				RepositorioCodigo repositorio = new RepositorioCodigo();
				datosFormulario.fillEntity(repositorio);
				ce.repositorios().saveAndFlush(repositorio);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "REGISTRAR EL REPOSITORIO '" + datosFormulario.getNombre() + "'";
			}
		}.ejecutar();
		
		// salir a la página de repositorios
		return new ModelAndView(Paths.Admin.RepositoriosCodigo.redirect());
	}
	
	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_EDITAR_GET, method=RequestMethod.GET)
	public ModelAndView editar_get(@PathVariable("id") String id) {
		
		RepositorioCodigo repositorio = ce.repositorios().findOne(id);
		if(repositorio == null) throw new ErrorEntidadNoEncontrada();
		
		FormularioRepositorioCodigo formulario = new FormularioRepositorioCodigo();
		formulario.fromEntity(repositorio);
		
		return editarRegistro(repositorio)
				.conModelo("datos", formulario);
	}

	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_EDITAR_POST, method=RequestMethod.POST)
	public ModelAndView editar_post(
			@ModelAttribute("datos") @Valid final FormularioRepositorioCodigo datosFormulario, 
			Errors errores) {
		
		datosFormulario.validate(ce, errores);
		
		if (errores.hasErrors()) {
			return editarRegistro(ce.repositorios().findOne(datosFormulario.getId()))
					.conModelo("datos", datosFormulario);
		}
		
		// almacenarlo
		final RepositorioCodigo repositorio = ce.repositorios().findOne(datosFormulario.getId());
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {

				datosFormulario.fillEntity(repositorio);
				ce.repositorios().saveAndFlush(repositorio);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "MODIFICAR DATOS DEL REPOSITORIO '" + datosFormulario.getNombre() + "'";
			}
		}.ejecutar();
		
		// salir a la página de repositorios
		return new ModelAndView(Paths.Admin.RepositoriosCodigo.redirectDatos(repositorio));
	}

	
	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_ELIMINAR, method=RequestMethod.GET)
	public ModelAndView eliminar_get(@PathVariable("id") String id) {
		
		RepositorioCodigo repositorio = ce.repositorios().findOne(id);
		if(repositorio == null) throw new ErrorEntidadNoEncontrada();

		return eliminar(repositorio)
				.conModelo(
						"datos", 
						new PeticionConfirmacionGeneral(
								"ELIMINAR_REPOSITORIO",
								"Eliminar los datos del repositorio '" + repositorio.getNombre() + "'")
				);
	}

	@RequestMapping(path=Paths.Admin.RepositoriosCodigo.PTRN_REQ_ELIMINAR, method=RequestMethod.POST)
	public ModelAndView eliminar_post(
			@PathVariable("id") String id,
			@ModelAttribute("datos") PeticionConfirmacionGeneral confirmacion) {
		
		final RepositorioCodigo repositorio = ce.repositorios().findOne(id);
		if(repositorio == null) throw new ErrorEntidadNoEncontrada();

		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {

				ce.repositorios().delete(repositorio);
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "ELIMINAR REGISTRO DEL REPOSITORIO '" + repositorio.getNombre() + "'";
			}
		}.ejecutar();
		
		// salir a la página de repositorios
		return new ModelAndView(Paths.Admin.RepositoriosCodigo.redirect());
	}

	
	// --
	// ********************************************
	// --
	
	private Localizacion localizacionBase() {
		return 
				new Localizacion()
				.conItem(Paths.TO_HOME)
				.conItem(Paths.Admin.TO_ADMIN)
				.conItem(Paths.Admin.RepositoriosCodigo.TO_REPOSITORIOS);
	}
	
	private ModeloPagina datos(RepositorioCodigo repositorio) {
		
		return 
				ModeloPagina.nuevaPagina(
						new EstructuraPagina("Datos repositorio")
						.conComponentes(
								localizacionBase()
								.conItem(Paths.Admin.RepositoriosCodigo.toDatos(repositorio)),

								new Columnas()
								.conColumna(
								
										new Columnas.Columna()
										.conAncho(9)
										.conComponentes(
												PanelContinente
												.defectoContraible("Datos principales")
												.conComponentes(
													MetodosDeUtilidad.paresClaveValor(
															"ID:", repositorio.getId(),
															"Nombre:", repositorio.getNombre()
													)
												)
												.conComponentes(	
													MetodosDeUtilidad.parClaveValorUrlFija(
															"Uri raíz:", 
															repositorio.getUriRaizRepositorio(), 
															repositorio.getUriRaizRepositorio()
													),
													
													MetodosDeUtilidad.parClaveValor(
															"Administrador:",
															repositorio.getAdministrador()
													)
												),
												
												PanelContinente
												.defectoContraible("Destino o uso del repositorio")
												.conComponentes(
														MetodosDeUtilidad.paresClaveValor(
																"Para código:", (repositorio.getParaCodigo() ? "Sí" : "No"),
																"Para proyectos:", 	(repositorio.getParaProyectos() ? "Sí" : "No")
														)
												)
										)
										.conComponentesSi(
												repositorio.getParaCodigo(),		
												PanelContinente
												.defectoContraible("Estructura de carpetas del repositorio (para código)")
												.conComponentes(
														MetodosDeUtilidad.paresClaveValor(
																"Raíz taxonomía artefatos:", repositorio.getSubrutaEstables(),
																"Raíz ramificación artefactos:", repositorio.getSubrutaDesarrollo(),
																"Carpeta estables:", repositorio.getCarpetaEstables(),
																"Carpeta etiquetado:", repositorio.getCarpetaEtiquetas(),
																"Carpeta ramas:", repositorio.getCarpetaRamas()
														)
												)
										)
										.conComponentesSi(
												repositorio.getParaProyectos(),
												PanelContinente
												.defectoContraible("Estructura de carpetas del repositorio (para proyectos)")
												.conComponentes(
														MetodosDeUtilidad.paresClaveValor(
																"Subruta de proyección:", repositorio.getSubrutaProyectos(),
																"Subruta para publicación:", repositorio.getSubrutaPublicacion()
														)
												)
										)

								)
								.conColumna(
										new Columnas.Columna()
										.conAncho(3)
										.conComponentes(
												PanelContinente
												.defecto("Acciones")
												.conParrafo("Pulsando sobre el siguiente enlace podrá editar los datos del repositorio de código.")
												.conParrafo("")
												.conComponente(
														new EnlaceSimple("Editar datos", Paths.Admin.RepositoriosCodigo.editar(repositorio))
														.enColumna(12)
												)
												.conParrafo("")
												.conParrafo("También puede eliminar el registro del repositorio pulsando sobre el siguiente enlace.")
												.conComponente(
														new EnlaceSimple("Eliminar registro", Paths.Admin.RepositoriosCodigo.eliminar(repositorio))
														.enColumna(12)
														.peligro()
												)
										)
								)
						)
				);
	}
	
	private Formulario formulario(
			String aceptacion,
			String cancelacion) {
		return new Formulario()
				.urlAceptacion(aceptacion)
				.conComponentes(
						AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
								FormularioRepositorioCodigo.class
						)
				)
				.botoneraEstandar(cancelacion);
	}
	
	private ModeloPagina nuevoRegistro() {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Alta repositorio código/proyecto")
				.conComponentes(
						localizacionBase()
						.conTexto("Nuevo"),
						
						formulario(
								Paths.Admin.RepositoriosCodigo.NUEVO,
								Paths.Admin.RepositoriosCodigo.BASE
						)
				)
		);
	}

	private ModeloPagina editarRegistro(RepositorioCodigo repositorio) {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Editar registro repositorio")
				.conComponentes(
						localizacionBase()
						.conItem(Paths.Admin.RepositoriosCodigo.toDatos(repositorio))
						.conTexto("Editar"),
						
						formulario(
								Paths.Admin.RepositoriosCodigo.EDITAR_POST,
								Paths.Admin.RepositoriosCodigo.datos(repositorio)
						)
				)
		);
	}
	
	private ModeloPagina eliminar(RepositorioCodigo repositorio) {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Eliminar registro repositorio")
				.conComponentes(
						localizacionBase()
						.conItem(Paths.Admin.RepositoriosCodigo.toDatos(repositorio))
						.conTexto("Eliminar"),
						
						new Formulario()
						.urlAceptacion(Paths.Admin.RepositoriosCodigo.eliminar(repositorio))
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
										PeticionConfirmacionGeneral.class
								)
						)
						.botoneraEstandar(Paths.Admin.RepositoriosCodigo.datos(repositorio))
				)
		);
	}
}
