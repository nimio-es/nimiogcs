package es.nimio.nimiogcs.componentes.publicacion;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;

final class UtilidadCalcularPublicabilidadTipoEstaticos {

	private UtilidadCalcularPublicabilidadTipoEstaticos() {}
	
	public static boolean esPublicable(IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {
		
		// esto solo aplica para los de tipo WAR
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		DirectivaPublicacionDeployer dph = PT.of(actual.getTipoArtefacto()).publicacionDeployer();
		if(!dph.getComportamiento().equalsIgnoreCase("ESTATICOS")) return false;

		// los estáticos no dependen de otras configuraciones
		return true;
	}
}
