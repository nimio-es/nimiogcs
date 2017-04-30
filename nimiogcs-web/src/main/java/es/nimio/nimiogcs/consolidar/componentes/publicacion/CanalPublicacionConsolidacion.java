package es.nimio.nimiogcs.consolidar.componentes.publicacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DestinoPublicacionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IErrores;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IPeticionPublicacion;
import es.nimio.nimiogcs.consolidar.K;
import es.nimio.nimiogcs.consolidar.subtareas.publicacion.ConsolidarArtefacto;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.errores.ErrorIntentoOperacionInvalida;

@Component
public class CanalPublicacionConsolidacion implements ICanalPublicacion {

	private IContextoEjecucion ce;

	@Autowired
	public CanalPublicacionConsolidacion(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ----
	
	@Override
	public DescripcionCanal descripcionCanal() {

		ParametroGlobal pg = ce.global().findOne(K.GLOBAL_CANAL_PUBLICACION_CONSOLIDACION_USOMANUAL);
		boolean elegir = 
				pg != null
				&& Strings.isNotEmpty(pg.getContenido())
				&& pg.getContenido().trim().equals("1");
		
		return 
				new DescripcionCanal(
						1000,  		// siempre al final 
						elegir,  	// no podrá ser elegido por el usuario
						false,
						"CONSOLIDACION", 
						"Consolidación de configuración y código", 
						new DestinoPublicacionCanal[] {
								new DestinoPublicacionCanal("CONSOLIDACION", "Consolidación")
						}
				);
	}

	@Override
	public DescripcionCanal teoricamentePosiblePublicarArtfacto(Artefacto artefacto) {

		Artefacto base = (artefacto instanceof ITestaferroArtefacto) ?
				((ITestaferroArtefacto)artefacto).getArtefactoAfectado()
				: artefacto;
		
		// todo lo que tenga una estrategia de evolución definida podrá ser consolidado
		return (P.of(base).evolucion() != null)? descripcionCanal() : null;
	}

	@Override
	public DescripcionCanal posiblePublicarArtefacto(ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {

		Artefacto base = (artefacto instanceof ITestaferroArtefacto) ?
				((ITestaferroArtefacto)artefacto).getArtefactoAfectado()
				: artefacto;
		
		// todo lo que tenga una estrategia de evolución definida podrá ser consolidado
		return (P.of(base).evolucion() != null)? descripcionCanal() : null;
	}

	@Override
	public void datosPeticion(String idCanal, IPeticionPublicacion peticion) {

		// nada que añadir
	}

	@Override
	public void validarPeticion(String idCanal, IPeticionPublicacion peticion, IErrores errores) throws ErrorInesperadoOperacion {

		if(idCanal.equalsIgnoreCase("CONSOLIDACION")) return;
		
		// ¿hemos establecido la posibilidad de elegirlo?
		ParametroGlobal pg = ce.global().findOne(K.GLOBAL_CANAL_PUBLICACION_CONSOLIDACION_USOMANUAL);
		boolean elegir = 
				pg != null
				&& Strings.isNotEmpty(pg.getContenido())
				&& pg.getContenido().trim().equals("1");
		
		if(!elegir)
			throw new ErrorIntentoOperacionInvalida("La publicación de consolidación no puede ser lanzada manualmente");
	}

	@Override
	public void ejecutarPublicacion(IDatosPeticionPublicacion publicacion) throws ErrorInesperadoOperacion {

		if(!publicacion.getCanal().equalsIgnoreCase(K.ID_CONSOLIDAR)) return;
		
		new ConsolidarArtefacto(ce)
			.ejecutarCon(publicacion);
	}

}
