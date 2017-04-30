package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Controller
@RequestMapping(path="/artefactos")
public class DirectivaQuitarArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public DirectivaQuitarArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----

	@RequestMapping(path="/{idArtefacto}/directiva/{idDirectiva}/quitar", method=RequestMethod.GET)
	public ModelAndView quitarDirectiva(
			@PathVariable("idArtefacto") String idArtefacto,
			@PathVariable("idDirectiva") String idDirectiva) {
	
		// cargamos el artefacto
		final Artefacto artefacto = ce.repos().artefactos().buscar(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();
		
		// cargamos el tipo de directiva
		final TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne(idDirectiva);
		if(tipoDirectiva==null) throw new ErrorEntidadNoEncontrada();
		
		// primero hay que comprobar que, efectivamente, se puede eliminar la directiva 
		// (que es opcional)
		final DirectivaCaracterizacion caracterizacion = PT.of(artefacto.getTipoArtefacto()).caracterizacion();
		if(caracterizacion==null) throw new ErrorInconsistenciaDatos("El tipo de artefacto no dispone de caracterización");
		final boolean esDirectivaRequerida = enListaDirectivasRequeridas(idDirectiva, caracterizacion.getDirectivasRequeridas());
		if(esDirectivaRequerida) throw new ErrorIntentoOperacionInvalida("No se puede eliminar una directiva que es exigida");
		
		// si se puede eliminar, entonces nos ponemos manos a la obra
		return paginaConfirmar(ce, artefacto, tipoDirectiva);
		
	}
	
	@RequestMapping(path="/{idArtefacto}/directiva/quitar/{idDirectiva}", method=RequestMethod.POST)
	public ModelAndView quitarDirectiva(
			@PathVariable("idArtefacto") final String idArtefacto,
			@PathVariable("idDirectiva") final String idDirectiva,
			@ModelAttribute("datos") final PeticionConfirmacionGeneral datos) {

		// cargamos el artefacto
		final Artefacto artefacto = ce.repos().artefactos().buscar(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();
		
		// cargamos el tipo de directiva
		final TipoDirectiva tipoDirectiva = ce.tiposDirectivas().findOne(idDirectiva);
		if(tipoDirectiva==null) throw new ErrorEntidadNoEncontrada();
		
		// primero hay que comprobar que, efectivamente, se puede eliminar la directiva 
		// (que es opcional)
		final DirectivaCaracterizacion caracterizacion = PT.of(artefacto.getTipoArtefacto()).caracterizacion();
		if(caracterizacion==null) throw new ErrorInconsistenciaDatos("El tipo de artefacto no dispone de caracterización");
		final boolean esDirectivaRequerida = enListaDirectivasRequeridas(idDirectiva, caracterizacion.getDirectivasRequeridas());
		if(esDirectivaRequerida) throw new ErrorIntentoOperacionInvalida("No se puede eliminar una directiva que es exigida");

		// lanzamos la operación de eliminación de la directiva
		new OperacionInternaInlineModulo(this.ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {

				final DirectivaBase directiva = P.of(artefacto).buscarDirectiva(idDirectiva);

				// la quitamos de la lista
				final List<DirectivaBase> directivasArtefacto = artefacto.getDirectivasArtefacto();
				final int posicion = posicionDirectivaEnLista(idDirectiva, directivasArtefacto);
				if(posicion>=0) directivasArtefacto.remove(posicion);
				ce.artefactos().save(artefacto);

				// elimiamos la directiva
				ce.directivas().delete(directiva);
				ce.directivas().flush();
				
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "QUITAR DIRECTIVA DE ARTEFACTO";
			}
			
		}.ejecutar();
		
		return new ModelAndView("redirect:/artefactos/" + idArtefacto);
	}
	
	// ---
	
	private static ModeloPagina paginaConfirmar(
			final IContextoEjecucion ce,
			final Artefacto artefacto,
			final TipoDirectiva tipoDirectiva) {
		
		return 
				ModeloPagina.nuevaPagina(
						new EstructuraPagina("Confirmar eliminación directiva")
						.conComponentes(
								new Localizacion()
								.conEnlace("Home", "/")
								.conEnlace("Artefactos", "/artefactos")
								.conEnlaceYParametros(artefacto.getTipoArtefacto().getNombre(), "/artefactos", "tipo=" + artefacto.getTipoArtefacto().getId())
								.conEnlace(artefacto.getNombre(), "/artefactos/" + artefacto.getId())
								.conTexto("Directivas")
								.conTexto(tipoDirectiva.getNombre())
								.conTexto("Quitar")
						)
						.conComponentes(
								new PanelInformativo()
								.conTexto("Se eliminará la directiva del artefacto")
								.tipoAviso()
						)
						.conComponentes(
								new Formulario()
								.urlAceptacion("/artefactos/" + artefacto.getId() + "/directiva/quitar/" + tipoDirectiva.getId())
								.conComponentes(
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
								).botoneraEstandar("/artefactos/"+ artefacto.getId())
								
						)
				)
				.conModelo(
						"datos", 
						new PeticionConfirmacionGeneral(
								"QUITAR-DIRECTIVA", 
								"Quitar la directiva '" + tipoDirectiva.getNombre() + "' del artefacto '" + artefacto.getNombre() + "'"
						)
				);
	}
	
	// ---
	
	private static boolean enListaDirectivasRequeridas(final String directivaAEliminar, final String[] directivasRequeridas) {
		
		for(final String directivaRequerida: directivasRequeridas) {
			final String directivaRequeridaNormalizada = (
					directivaRequerida.startsWith("@") ?
							directivaRequerida.substring(1)   // cuando se trate de la referncia a un diccionario
							: directivaRequerida
					).trim();
			
			if(directivaRequeridaNormalizada.equalsIgnoreCase(directivaAEliminar)) return true;
		}
			
		return false;
	}
	
	private static int posicionDirectivaEnLista(final String idDirectiva, final List<DirectivaBase> directivas) {
		int pos = 0;
		for(final DirectivaBase db: directivas) {
			if(db.getDirectiva().getId().equalsIgnoreCase(idDirectiva)) return pos;
			pos++;
		}
		return -1;
	}
}
