package es.nimio.nimiogcs.web.controllers.artefactos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/artefactos/dependencias/estaticas/mover")
public class MoverDependenciasArtefactoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public MoverDependenciasArtefactoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -------------------------------------------------------
	// Mover dependencia
	// -------------------------------------------------------
	
	@RequestMapping(path="/{artefacto}/{requerido}", method=RequestMethod.GET)
	public String mover(
			@PathVariable("artefacto") final String idArtefacto,
			@PathVariable("requerido") final String idRequerido,
			@RequestParam(name="valor", required=true) final Integer nuevaPosicion) {
		
		// leemos el artefacto
		final Artefacto artefacto = ce.artefactos().findOne(idArtefacto);
		if(artefacto==null) throw new ErrorEntidadNoEncontrada();
		
		// leemos el artefacto final
		final Artefacto artefactoRequerido = ce.artefactos().findOne(idRequerido);
		if(artefactoRequerido==null) throw new ErrorEntidadNoEncontrada();

		new OperacionInternaModulo<Boolean, Boolean>(ce) {

			@Override
			protected String nombreUnicoOperacion(Boolean datos, Operacion op) {
				return "CAMBIAR DE POSICIÓN '" 
						+ artefactoRequerido.getNombre() 
						+ "' EN LAS DEPENDENCIAS DE '" 
						+ artefacto.getNombre() + "'";
			}
			
			@Override
			protected void relacionarOperacionConEntidades(Boolean datos, Operacion op) {
				// de momento relacionamos con los dos artefactos
				registraRelacionConOperacion(op, artefacto);
				registraRelacionConOperacion(op, artefactoRequerido);
			}

			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {

				// ¿cuál es la posición final en la que tendremos que dejar el objeto?
				int posicionFinal = nuevaPosicion;

				// leemos las relaciones de dependencia
				List<Dependencia> dependencias = ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(idArtefacto);
				
				// buscamos la dependencia a la que estamos haciendo referencia
				Dependencia aMover = null;
				for(Dependencia dependencia: dependencias) {
					if(dependencia.getRequerida().getId().equalsIgnoreCase(idRequerido)) {
						aMover = dependencia; 
						break;
					}
				}
				
				// ¿la hemos encontrado?
				if(aMover==null) throw new ErrorEntidadNoEncontrada();
				
				// solo movemos si tenemos una relación posicional
				if(aMover instanceof DependenciaPosicional) {

					final DependenciaPosicional relPos = (DependenciaPosicional)aMover;

					// ¿cuál es la posición actual?
					final int posActual = relPos.getPosicion();
					
					// ¿cuál es el límite?
					int maximo = dependencias.size();
					if(posicionFinal < 1) posicionFinal = 1;
					if(posicionFinal > maximo) posicionFinal = maximo; 
					
					// ya tenemos la relación que hay que trasladar
					// ¿para arriba o para abajo? 
					// En cualquier caso, nos movemos en un rango
					int iniRango = Math.min(posActual, posicionFinal);
					int finRango = Math.max(posActual, posicionFinal);
					int sentido = Integer.signum(posActual - posicionFinal);
						
					// hacia arriba en la lista, pues
					for(Dependencia dependencia: dependencias) {
						
						// casos en lo que ignoramos la iteración
						if(!(dependencia instanceof DependenciaPosicional)) continue;
						if(dependencia.getId().equalsIgnoreCase(relPos.getId())) continue;
						
						DependenciaPosicional aCambiar = (DependenciaPosicional)dependencia;
						
						// si está fura del rango a mover, lo ignoramos
						if(aCambiar.getPosicion() < iniRango || aCambiar.getPosicion() > finRango) continue;
						
						// aplicamos la diferencia del sentido
						aCambiar.setPosicion( aCambiar.getPosicion() + sentido );
					}
					
					// actualizamos la de la propia relación a 
					relPos.setPosicion(posicionFinal);
				}

				// lo guardamos todo
				for(Dependencia dependencia: dependencias) {
					ce.dependenciasArtefactos().save(dependencia);
				}
				ce.dependenciasArtefactos().flush();

				return true;
			}
		}.ejecutar(true);
		
		// volvemos a la página con las relaciones
		return "redirect:/artefactos/dependencias/estaticas/" + idArtefacto;
	}
}
