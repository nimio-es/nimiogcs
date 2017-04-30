package es.nimio.nimiogcs.jpa.entidades.artefactos;

public interface ITestaferroArtefacto {

	Artefacto getArtefactoAfectado();
	
	/**
	 * Indica cuándo una representación evolutiva de un 
	 * artefacto está sincronizado con la rama estable.
	 */
	boolean getSincronizadoEstable();
}
