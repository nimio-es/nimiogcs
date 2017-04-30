package es.nimio.nimiogcs.subtareas.publicacion;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;

final class UtilidadExtenderDescriptorTipoGeneral {

	private UtilidadExtenderDescriptorTipoGeneral() {}
	
	public static void extenderDescriptor(StringBuilder sb, IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto, String rutaIntermedia) {

		// 
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		DirectivaPublicacionDeployer dph = PT.of(actual.getTipoArtefacto()).publicacionDeployer();
		if(!dph.getComportamiento().equalsIgnoreCase("GENERAL")) return;
		
		// la carpeta de publicación, que en este caso es subir los niveles intermedios
		final int niveles = rutaIntermedia.length() - rutaIntermedia.replace("/", "").length();
		String saltos = "";
		for(int i = 0; i < niveles; i++)
			saltos += saltos.length() > 0 ? "/.." : "..";
		sb.append("PUBLICAR-CARPETA=").append(saltos).append("\n");
	}

}
