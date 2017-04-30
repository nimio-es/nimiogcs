package es.nimio.nimiogcs.componentes.publicacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DestinoPublicacionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IErrores;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IPeticionPublicacion;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionJenkins;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.subtareas.publicacion.PublicacionJenkins;

@Component
public class CanalPublicacionJenkins implements ICanalPublicacion {

	private IContextoEjecucionBase ce;

	@Autowired
	public CanalPublicacionJenkins(IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// ---
	
	@Override
	public DescripcionCanal descripcionCanal() {
		return 
				new DescripcionCanal(
						10,
						true,
						false,
						"JENKINS", 
						"Se solicitará la publicación comunicando con una tarea Jenkins.",
						new DestinoPublicacionCanal[] {
								new DestinoPublicacionCanal("ARTIFACTORY", "Artifactory")
						}
				);
	}
	
	@Override
	public DescripcionCanal teoricamentePosiblePublicarArtfacto(Artefacto artefacto) {
		// Hay que tener en cuenta cuándo nos encontramos con evolutivos
		Artefacto base = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;

		// preguntamos si tiene la directiva de publicación Jenkins
		if(PT.of(base.getTipoArtefacto()).directiva(DirectivaPublicacionJenkins.class)==null) return null;
		
		//
		return descripcionCanal();
	}
	
	@Override
	public DescripcionCanal posiblePublicarArtefacto(ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {
		return teoricamentePosiblePublicarArtfacto(artefacto);
	}

	@Override
	public void datosPeticion(String idCanal, IPeticionPublicacion peticion) {
		// Sea o no sea Jenkins, no añadiremos ningún parámetro
	}

	@Override
	public void validarPeticion(String idCanal, IPeticionPublicacion peticion, IErrores errores) throws ErrorInesperadoOperacion {
		// como no hay parámetros, no hay nada que validar
	}

	@Override
	public void ejecutarPublicacion(IDatosPeticionPublicacion publicacion) throws ErrorInesperadoOperacion {
		
		if(!publicacion.getCanal().equalsIgnoreCase("JENKINS")) return;
		
		new PublicacionJenkins(ce).ejecutarCon(publicacion);
	}

}
