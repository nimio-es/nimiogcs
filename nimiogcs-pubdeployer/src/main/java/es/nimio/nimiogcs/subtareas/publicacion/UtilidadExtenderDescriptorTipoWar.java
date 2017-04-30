package es.nimio.nimiogcs.subtareas.publicacion;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.datos.P;
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
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorAplicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;

final class UtilidadExtenderDescriptorTipoWar {

	private UtilidadExtenderDescriptorTipoWar() {}
	
	public static void extenderDescriptor(StringBuilder sb, IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto, String rutaIntermedia) {
		
		// esto solo aplica para los de tipo WAR
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		DirectivaPublicacionDeployer dph = PT.of(actual.getTipoArtefacto()).publicacionDeployer();
		if(!dph.getComportamiento().equalsIgnoreCase("WAR")) return;

		// la ruta donde encontrar el binario final
		sb.append("BUILD-BINARIO-FINAL=").append(artefacto.getNombre().toLowerCase())
		.append("/target/")
		.append(artefacto.getNombre().toLowerCase())
		.append('.')
		.append(P.of(artefacto).coordenadasMaven().getEmpaquetado())
		.append('\n');
		
		// y la carpeta de publicación, que en este caso es subir los niveles intermedios
		final int niveles = rutaIntermedia.length() - rutaIntermedia.replace("/", "").length();
		String saltos = "";
		for(int i = 0; i < niveles; i++)
			saltos += saltos.length() > 0 ? "/.." : "..";
		sb.append("PUBLICAR-CARPETA=").append(saltos).append("\n");
		
		// --- buscamos los datos del servidor para incluirlos
		
		// -- de entrada vamos a buscar el EAR en la etiqueta
		
		
		String sitio = "";
		String servidor = "?";
		String vh = "?";
		String contextRoot = "?";
		
		// primero buscamos, entre todos los artefactos de la etiqueta del proyecto, el ear al que éste módulo está asociado
		for(RelacionElementoProyectoArtefacto rpa: 
			ce.repos().elementosProyectos().relaciones().artefactosAfectados(elementoProyecto)) {

			Artefacto referido = rpa.getArtefacto();
			Artefacto referidoBase = referido instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)referido).getArtefactoAfectado() : referido;

			if(referidoBase.getTipoArtefacto().getId().equalsIgnoreCase("EAR")) {
				
				boolean encontrado = false;
				
				// hemos encontrado un EAR. Hay que confirmar que tiene entre sus dependencias (evolucionadas o no) una al WAR
				for(Dependencia d: 
					ce.repos().artefactos().dependencias().deArtefacto(referido.getId())) {
					if(d instanceof DependenciaConModuloWeb) 
						if(d.getRequerida().getId().equalsIgnoreCase(actual.getId())) {
							
							// ya tenemos el Ear que andamos buscando, el que hace referencia al módulo web
							// es el que se encuentra, por tanto, en referidoBase
							contextRoot = ((DependenciaConModuloWeb)d).getContextRoot();
							
							RelacionServidorAplicacion rsa = servidorTieneEarInstalado(ce, referidoBase);
							if(rsa!=null) {

								servidor = rsa.getServidor().getNombre();
								sitio = rsa.getIdAplicacion();
								vh = rsa.getVirtualHost();
								
								encontrado = true;
								break;
							}
						}
				}
				
				if(encontrado) break;
			}
		}
		
		// -- las variables de publicación
		sb.append("PUBLICAR-SERVIDOR=").append(servidor).append("\n");
		sb.append("PUBLICAR-APLICACION=").append(sitio).append("\n");
		sb.append("PUBLICAR-MODULO=").append(actual.getNombre().toLowerCase()).append("\n");
		sb.append("PUBLICAR-CONTEXTROOT=").append(contextRoot).append("\n");
		sb.append("PUBLICAR-VIRTUALHOST=").append(vh).append("\n");
		sb.append("PUBLICAR-SITIO=").append(sitio).append("\n");
		
	}
		
	
	/**
	 * Buscamos la relacion de un servidor del entorno de integración que tenga el EAR asociado
	 */
	private static RelacionServidorAplicacion servidorTieneEarInstalado(IContextoEjecucionBase ce, Artefacto ear) {
		
		Artefacto actual = ear instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)ear).getArtefactoAfectado() : ear;
		
		// buscamos entre los EARs de los servidores de integración a ver si es el que estamos buscando
		DestinoPublicacion integracion = ce.repos().destinosPublicacion().buscar("INTEGRACION");
		for(RelacionEntornoServidor relacionEntornoServidor: 
			ce.repos().destinosPublicacion().servidores().deUnDestino(integracion)) {
			
			for(RelacionServidorArtefacto relacionServidorArtefacto:
				ce.repos().servidores().artefactosInstalados().ears(relacionEntornoServidor.getServidor())) {
				if(relacionServidorArtefacto.getArtefacto().getId().equalsIgnoreCase(actual.getId())) 
					return (RelacionServidorAplicacion)relacionServidorArtefacto;  // lo hemos encontrado
			}
		}
		
		return null;
	}
	

}
