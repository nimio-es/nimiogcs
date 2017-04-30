package es.nimio.nimiogcs.componentes.publicacion;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConModuloWeb;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;

final class UtilidadCalcularPublicabilidadTipoWar {

	private UtilidadCalcularPublicabilidadTipoWar() {}
	
	public static boolean esPublicable(IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {
		
		// esto solo aplica para los de tipo WAR
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		DirectivaPublicacionDeployer dph = PT.of(actual.getTipoArtefacto()).publicacionDeployer();
		if(!dph.getComportamiento().equalsIgnoreCase("WAR")) return false;
		
		// Un tipo WAR requiere que el módulo forme parte del EAR de un proyecto y que dicho EAR esté asociado a un servidor
		
		// Empezamos preguntando si en la etiqueta hay un EAR (o una evolución de EAR) que esté relacionado con el WAR 
		Artefacto ear = earEnEtiqueta(ce, elementoProyecto, artefacto);
		if(ear == null) return false;
		
		// Pero que haya un EAR (o una evolución de EAR) que lo contenga, no significa que esté en un servidor.
		Servidor servidor = servidorTieneEarInstalado(ce, ear);
		if(servidor == null) return false;
		
		return true;
	}
	
	/**
	 * Buscamos la existencia de un EAR dentro de la etiqueta actual que apunte al artefacto que queremos desplegar
	 */
	private static Artefacto earEnEtiqueta(IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {

		// por si es evolutivo
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;

		// estamos buscando un EAR
		for(RelacionElementoProyectoArtefacto rpa: ce.repos().elementosProyectos().relaciones().artefactosAfectados(elementoProyecto)) {
			Artefacto referido = rpa.getArtefacto();
			Artefacto referidoBase = referido instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)referido).getArtefactoAfectado() : referido;
			
			if(referidoBase.getTipoArtefacto().getId().equalsIgnoreCase("EAR")) {
				
				// hemos encontrado un EAR. Hay que confirmar que tiene entre sus dependencias (evolucionadas o no) una al WAR
				for(Dependencia d: ce.repos().artefactos().dependencias().deArtefacto(referido.getId())) {
					if(d instanceof DependenciaConModuloWeb) 
						if(d.getRequerida().getId().equalsIgnoreCase(actual.getId())) return referido; // lo hemos encontrado (al menos uno)
				}
			}
		}
		
		return null;
	}

	/**
	 * Buscamos la existencia de un servidor del entorno de integración que tenga el EAR asociado
	 */
	private static Servidor servidorTieneEarInstalado(IContextoEjecucionBase ce, Artefacto ear) {
		
		Artefacto actual = ear instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)ear).getArtefactoAfectado() : ear;
		
		// buscamos entre los EARs de los servidores de integración a ver si es el que estamos buscando
		DestinoPublicacion integracion = ce.repos().destinosPublicacion().buscar("INTEGRACION");
		for(RelacionEntornoServidor relacionEntornoServidor: 
			ce.repos().destinosPublicacion().servidores().deUnDestino(integracion)) {
			
			for(RelacionServidorArtefacto relacionServidorArtefacto:
				ce.repos().servidores().artefactosInstalados().ears(relacionEntornoServidor.getServidor())) {
				if(relacionServidorArtefacto.getArtefacto().getId().equalsIgnoreCase(actual.getId())) 
					return relacionServidorArtefacto.getServidor();  // lo hemos encontrado
			}
		}
		
		return null;
	}
	
}
