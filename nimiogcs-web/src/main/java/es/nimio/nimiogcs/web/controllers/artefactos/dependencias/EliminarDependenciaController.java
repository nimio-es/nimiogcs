package es.nimio.nimiogcs.web.controllers.artefactos.dependencias;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConAlcance;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConModuloWeb;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.artefactos.dependencias.ConfirmarEliminarDependencia;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/artefactos/dependencias/estaticas/quitar")
public class EliminarDependenciaController {

	private IContextoEjecucion ce;
	
	@Autowired
	public EliminarDependenciaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ---
	
	@RequestMapping(path="/{artefacto}/{requerido}", method=RequestMethod.GET)
	public ModelAndView inicio(
			@PathVariable("artefacto") final String idArtefacto,
			@PathVariable("requerido") final String idRequerido) {
		
		return crearFormularioEntrada(idArtefacto, idRequerido);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String finalizar(
			@ModelAttribute("datos") final ConfirmarEliminarDependencia confirmacion) {
		
		final Dependencia dependencia = (Dependencia)ce.dependenciasArtefactos().findOne(confirmacion.getIdRelacion());
		if(dependencia == null) throw new ErrorEntidadNoEncontrada();
		
		// lanzamos/ registramos la operación
		new OperacionInternaModulo<Boolean, Boolean>(ce) {

			@Override
			protected String nombreUnicoOperacion(Boolean datos, Operacion op) {
				final String artefacto = dependencia.getDependiente().getNombre();
				final String requerido = dependencia.getRequerida().getNombre();
				return "ELIMINAR RELACIÓN ENTRE '" 
						+ artefacto 
						+ "' Y '"
						+ requerido
						+ "'";
			}
			
			@Override
			protected void relacionarOperacionConEntidades(Boolean datos, Operacion op) {
				registraRelacionConOperacion(op, dependencia.getDependiente());
				registraRelacionConOperacion(op, dependencia.getRequerida());
			}

			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				// si es una relación de tipo posicional (y es raro que no lo sea)
				// hay que mover todas las relaciones existentes para relenar ese puesto
				if(dependencia instanceof DependenciaPosicional) {
					
					final int posicion = ((DependenciaPosicional)dependencia).getPosicion();
					
					for(Dependencia relacion: ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(dependencia.getDependiente().getId())) {
						if(!(relacion instanceof DependenciaPosicional)) continue;

						DependenciaPosicional posicional = (DependenciaPosicional)relacion;
						if(posicional.getPosicion() <= posicion) continue;
						
						posicional.setPosicion(posicional.getPosicion() - 1);
						ce.dependenciasArtefactos().save(relacion);
					}
				}
				
				// terminamos por eliminar la dependencia
				ce.dependenciasArtefactos().delete(dependencia);
				ce.dependenciasArtefactos().flush();
				
				return null;
			}

			
			
		}.ejecutar(true);
		
		// volvemos a la página con las relaciones
		return "redirect://artefactos/dependencias/estaticas/" + dependencia.getDependiente().getId();
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
		
		// construmos la vista
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Eliminar relación de dependencia")
				.conComponentes(
						new Localizacion()
						.conEnlace("Home", "/")
						.conEnlace("Artefactos", "/artefactos")
						.conTexto(artefacto.getTipoArtefacto().getNombre())
						.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
						.conEnlace("Dependencias", "/artefactos/dependencias/" + artefacto.getId())
						.conEnlace("Con " + requerido.getNombre(), "/artefactos/dependencias/" + requerido.getId())
						.conTexto("Quitar"),
						
						new PanelInformativo()
						.conTexto("Se dispone a quitar una relación de dependencia entre dos artefactos. Esta operación es irreversible.")
						.tipoAviso(),
						
						new Formulario()
						.urlAceptacion(
								"/artefactos/dependencias/estaticas/quitar" 
						)
						.conComponentes(
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
										ConfirmarEliminarDependencia.class
								)
						)
						.botoneraEstandar("/artefactos/dependencias/estaticas/" + idArtefacto)
				)
		)
		.conModelo(
				"datos", 
				new ConfirmarEliminarDependencia(
						dependencia.getId(),
						artefacto.getNombre(),
						requerido.getNombre(),
						dependencia instanceof DependenciaConAlcance ? "Alcance" : (dependencia instanceof DependenciaConModuloWeb ?"Web" : "Posicional"),
						dependencia instanceof DependenciaConAlcance ? 
								((DependenciaConAlcance)dependencia).getAlcanceElegido().getTextoDescripcion()
								: (
										dependencia instanceof DependenciaConModuloWeb ? 
												((DependenciaConModuloWeb)dependencia).getContextRoot() 
												: Integer.toString(((DependenciaPosicional)dependencia).getPosicion())
								)
				)
		);
	}
}

