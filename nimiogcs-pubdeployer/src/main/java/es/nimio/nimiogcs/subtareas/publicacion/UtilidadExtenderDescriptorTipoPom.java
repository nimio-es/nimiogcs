package es.nimio.nimiogcs.subtareas.publicacion;

import java.util.ArrayList;
import java.util.Collection;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorLibreriaCompartida;

final class UtilidadExtenderDescriptorTipoPom {

	private UtilidadExtenderDescriptorTipoPom() {}
	
	public static void extenderDescriptor(StringBuilder sb, IContextoEjecucionBase ce, ElementoBaseProyecto elementoProyecto, Artefacto artefacto, String rutaIntermedia) {
		
		// esto solo aplica para los de tipo POM
		Artefacto actual = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		DirectivaPublicacionDeployer dph = PT.of(actual.getTipoArtefacto()).publicacionDeployer();
		if(!dph.getComportamiento().equalsIgnoreCase("CONJUNTO-LIBS")) return;

		// la ruta donde encontrar el binario final
		sb.append("BUILD-BINARIO-FINAL=").append(artefacto.getNombre().toLowerCase())
		.append("/target/dependency")
		.append('\n');
		
		// llegados aquí, buscamos todos los servidores en los que el pom está instalado
		Collection<RelacionServidorLibreriaCompartida> slcs = buscarRelacionesServidorLibreriaCompartida(ce, actual);
		
		// indicamos el total de servidores
		sb.append("PUBLICACION-TOTAL-SERVIDORES=").append(slcs.size()).append("\n");
		
		// calculamos los saltos a retroceder para la ruta relativa
		// y la carpeta de publicación, que en este caso es subir los niveles intermedios
		final int niveles = rutaIntermedia.length() - rutaIntermedia.replace("/", "").length();
		String saltos = "";
		for(int i = 0; i < niveles; i++)
			saltos += saltos.length() > 0 ? "/.." : "..";
		
		// ahora vamos recorriendo las relaciones y añadimos el servidor y la ruta o carpeta en la que está instalada
		int numero = 1;
		for(RelacionServidorLibreriaCompartida slc: slcs) {
			
			// por cada relación, introducimos el nombre del servidor y la ruta de copia
			sb.append("PUBLICAR-SERVIDOR.").append(numero).append("=").append(slc.getServidor().getNombre()).append("\n");
			sb.append("PUBLICAR-CARPETA.").append(numero).append("=")
				.append(saltos)
				.append(slc.getCarpetaServidor()).append("\n");
			
			// terminamos incrementando la posición
			numero++;
		}
	}

	
	private static Collection<RelacionServidorLibreriaCompartida> buscarRelacionesServidorLibreriaCompartida(IContextoEjecucionBase ce, Artefacto lc) {
		
		final ArrayList<RelacionServidorLibreriaCompartida> lista = new ArrayList<RelacionServidorLibreriaCompartida>();
		
		// buscamos en el entorno de integración todos los servidores donde haya una relación que esté asociada a la LC (POM)
		DestinoPublicacion integracion = ce.repos().destinosPublicacion().buscar("INTEGRACION");
		for(RelacionEntornoServidor relacionEntornoServidor: 
			ce.repos().destinosPublicacion().servidores().deUnDestino(integracion)) {
			
			for(RelacionServidorArtefacto relacionServidorArtefacto:
				ce.repos().servidores().artefactosInstalados().poms(relacionEntornoServidor.getServidor())) {
				if(relacionServidorArtefacto.getArtefacto().getId().equalsIgnoreCase(lc.getId())) 
					lista.add((RelacionServidorLibreriaCompartida)relacionServidorArtefacto);
			}
		}
		
		return lista;
	}
}
