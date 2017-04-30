package es.nimio.nimiogcs.componentes.publicacion;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;

final class UtilidadCalcularPublicabilidadTipoPom {
	
	private UtilidadCalcularPublicabilidadTipoPom() {}

	public static boolean esPublicable(IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {
		
		// esto solo aplica para los de tipo POM
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		DirectivaPublicacionDeployer dph = PT.of(actual.getTipoArtefacto()).publicacionDeployer();
		if(!dph.getComportamiento().equalsIgnoreCase("CONJUNTO-LIBS")) return false;
		
		// para poder publicar el archivo POM, éste tiene que estar asociado a un servidor
		Servidor servidor = servidorTieneLibreriaCompartidaInstalada(ce, artefacto);
		if(servidor==null) return false;
		
		return true;
	}
	
	/**
	 * Buscamos la existencia de un servidor del entorno de integración que tenga el POM asociado
	 */
	private static Servidor servidorTieneLibreriaCompartidaInstalada(IContextoEjecucionBase ce, Artefacto pom) {
		
		Artefacto actual = pom instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)pom).getArtefactoAfectado() : pom;
		
		// buscamos entre los EARs de los servidores de integración a ver si es el que estamos buscando
		DestinoPublicacion integracion = ce.repos().destinosPublicacion().buscar("INTEGRACION");
		for(RelacionEntornoServidor relacionEntornoServidor:
			ce.repos().destinosPublicacion().servidores().deUnDestino(integracion)) {
			
			for(RelacionServidorArtefacto relacionServidorArtefacto:
				ce.repos().servidores().artefactosInstalados().poms(relacionEntornoServidor.getServidor())) {
				if(relacionServidorArtefacto.getArtefacto().getId().equalsIgnoreCase(actual.getId())) 
					return relacionServidorArtefacto.getServidor();  // lo hemos encontrado
			}
		}
		
		return null;
	}

}
