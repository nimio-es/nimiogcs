package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorAplicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorLibreriaCompartida;

public abstract class Servidores 
	extends RepositorioBase<Servidor> {

	public abstract Servidores.RelacionesConArtefactos artefactosInstalados();
	
	// ---
	
	public static abstract class RelacionesConArtefactos extends RepositorioBase<RelacionServidorArtefacto> {
		
		public abstract Collection<RelacionServidorLibreriaCompartida> poms(Servidor servidor);
		
		public abstract Collection<RelacionServidorAplicacion> ears(Servidor servidor);
		
		public abstract Collection<RelacionServidorAplicacion> aplicaciones(Servidor servidor);
	}
}
