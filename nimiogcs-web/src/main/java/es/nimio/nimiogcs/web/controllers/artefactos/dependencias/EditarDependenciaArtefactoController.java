package es.nimio.nimiogcs.web.controllers.artefactos.dependencias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConAlcance;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConModuloWeb;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.artefactos.dependencias.EditarDependenciaAlcance;
import es.nimio.nimiogcs.web.dto.f.artefactos.dependencias.EditarDependenciaWeb;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/artefactos/dependencias/estaticas/editar")
public class EditarDependenciaArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public EditarDependenciaArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ---
	
	@RequestMapping(path="/{artefacto}/{requerido}", method=RequestMethod.GET)
	public ModelAndView inicio(
			@PathVariable("artefacto") final String idArtefacto,
			@PathVariable("requerido") final String idRequerido) {
		
		return crearFormularioEntrada(idArtefacto, idRequerido);
	}

	@RequestMapping(path="/alcance", method=RequestMethod.POST)
	public ModelAndView post_alcance(
			@ModelAttribute("datos") @Valid EditarDependenciaAlcance datos,
			Errors errores
			) {
		return procesarPost(datos, errores);
	}

	@RequestMapping(path="/web", method=RequestMethod.POST)
	public ModelAndView post_web(
			@ModelAttribute("datos") @Valid EditarDependenciaWeb datos,
			Errors errores
			) {
		return procesarPost(datos, errores);		
	}
	
	// ----------------------------------------------------------
	// utilidad
	// ----------------------------------------------------------

	private ModelAndView crearFormularioEntrada(final String idArtefacto, final String idRequerido) {
		// comprobaciones
		// 1. que existen las entidades
		final Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		final Artefacto requerido = ce.artefactos().findOne(idRequerido);
		if(artefacto==null || requerido==null) throw new ErrorEntidadNoEncontrada();
		
		// 2. que hay una relación de dependencia entre ellas
		Dependencia dependencia = null;
		for(Dependencia d: ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(idArtefacto)) {
			if(d.getDependiente().getId().equalsIgnoreCase(idArtefacto) 
					&& d.getRequerida().getId().equalsIgnoreCase(idRequerido)) dependencia = d;
		}
		if(dependencia==null) throw new ErrorEntidadNoEncontrada();
		
		// creamos el formulario
		Object formulario = dependencia instanceof DependenciaConAlcance ? 
				new EditarDependenciaAlcance(dependencia.getId(), artefacto.getNombre(), requerido.getNombre())
				: new EditarDependenciaWeb(dependencia.getId(), artefacto.getNombre(), requerido.getNombre());
				
		// construmos la vista
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Editar relación de dependencia")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Artefactos", "/artefactos")
						.conTexto(artefacto.getTipoArtefacto().getNombre())
						.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
						.conEnlace("Dependencias", "/artefactos/dependencias/" + artefacto.getId())
						.conEnlace("Con " + requerido.getNombre(), "/artefactos/dependencias/" + requerido.getId())
						.conTexto("Editar"),
						
						new PanelInformativo()
						.conTexto("Se dispone a editar la configuración de una relación de dependencia entre dos artefactos.")
						.tipoInfo(),
						
						new Formulario()
						.urlAceptacion(
								"/artefactos/dependencias/estaticas/editar/" 
								+  (dependencia instanceof DependenciaConAlcance ? "alcance" : "web")
						)
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
										formulario.getClass(), 
										diccionarios(requerido)
								)
						)
						.botoneraEstandar("/artefactos/dependencias/estaticas/" + idArtefacto)
				)
		)
		.conModelo("datos", formulario);
	}

	private ModelAndView procesarPost(final Object formulario, final Errors errores) {

		final String idRelacion = formulario instanceof EditarDependenciaAlcance ? 
				((EditarDependenciaAlcance)formulario).getIdRelacion()
				: ((EditarDependenciaWeb)formulario).getIdRelacion();
		
		// cargamos la relación de dependencia
		final Dependencia relacion = (Dependencia)ce.dependenciasArtefactos().findOne(idRelacion);
		if(relacion == null) throw new ErrorEntidadNoEncontrada();
		
		// si hay errores volvemos a mostrar el formulario
		if(errores.hasErrors()) 
			return crearFormularioEntrada(
					relacion.getDependiente().getId(), 
					relacion.getRequerida().getId()
			);
		
		// en caso contrario tratamos la actualización
		new OperacionInternaModulo<Boolean, Boolean>(ce) {

			@Override
			protected String nombreUnicoOperacion(Boolean datos, Operacion op) {
				final String artefacto = relacion.getDependiente().getNombre();
				final String requerido = relacion.getRequerida().getNombre();
				
				return "CAMBIAR DATOS DE LA RELACIÓN ENTRE '" + artefacto + "' Y '" + requerido + "'";
			}
			
			@Override
			protected void relacionarOperacionConEntidades(Boolean datos, Operacion op) {
				registraRelacionConOperacion(op, relacion.getDependiente());
				registraRelacionConOperacion(op, relacion.getRequerida());
			}

			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				// en función del tipo de la relación modificamos un campo u otro
				if(relacion instanceof DependenciaConAlcance) {
					
					// de alcance
					DependenciaConAlcance rAlcance = (DependenciaConAlcance)relacion;
					EditarDependenciaAlcance f = (EditarDependenciaAlcance)formulario;
					rAlcance.setAlcanceElegido(EnumAlcanceDependencia.valueOf(f.getAlcance()));
				} else if(relacion instanceof DependenciaConModuloWeb) {
					
					// con módulo web
					DependenciaConModuloWeb rModWeb = (DependenciaConModuloWeb)relacion;
					EditarDependenciaWeb f = (EditarDependenciaWeb)formulario;
					rModWeb.setContextRoot(f.getRutaContexto());
				} else if(relacion instanceof DependenciaPosicional) {
					
					// TODO: De momento no tengo claro qué hacer
					
				}
				
				// guardamos
				ce.dependenciasArtefactos().saveAndFlush(relacion);
				
				return true;
			}
		}
		.ejecutar(true);

		// y salimos redirigiendo a la pantalla de dependencias
		return new ModelAndView("redirect:/artefactos/dependencias/estaticas/" + relacion.getDependiente().getId());
	}
	
	private Map<String, Collection<NombreDescripcion>> diccionarios(Artefacto requerido) {
		
		final HashMap<String, Collection<NombreDescripcion>> dics = new HashMap<String, Collection<NombreDescripcion>>();
		
		final DirectivaAlcances d = P.of(requerido).alcances();
		if(d==null) return dics;
		
		final ArrayList<NombreDescripcion> datos = new ArrayList<NombreDescripcion>();
		for(EnumAlcanceDependencia alcance: d.getAlcances()) 
			datos.add(new NombreDescripcion(alcance.toString(), alcance.getTextoDescripcion()));
			
		dics.put("alcances", datos);
		return dics;
		
	}
}
