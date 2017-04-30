package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;

public abstract class Artefactos 
	extends RepositorioBase<Artefacto> {

	public abstract Artefactos.Dependencias dependencias();
	
	// ---
	
	public static abstract class Dependencias extends RepositorioBase<Dependencia> {
		
		/**
		 * Dependencias que tiene un artefacto;
		 * @param idArtefacto
		 * @return colección con todas las dependencias que tiene un artefacto.
		 */
		public abstract Collection<Dependencia> deArtefacto(String idArtefacto);
		
	}
	
}
