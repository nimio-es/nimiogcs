package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;

public abstract class DestinosPublicacion extends RepositorioBase<DestinoPublicacion> {

	/**
	 * Mecanismo para recoger las relaciones con los servidores
	 */
	public abstract DestinosPublicacion.RelacionesConServidores servidores();
	
	// ---
	
	/**
	 * Relaciones que tiene un destino de publicación con distintos servidores.
	 */
	public static abstract class RelacionesConServidores 
		extends RepositorioBase<RelacionEntornoServidor> {
		
		/**
		 * Todos los servidores con los que está relacionado un entorno
		 */
		public abstract Collection<RelacionEntornoServidor> deUnDestino(DestinoPublicacion destino);
		
	}
}
