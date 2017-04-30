package es.nimio.nimiogcs.datos;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccionMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;

public final class PT {

	private PT(TipoArtefacto artefacto) {
		decorado = artefacto;
	}
	
	
	final private TipoArtefacto decorado;
	
	public static PT of(TipoArtefacto decorado) { return new PT(decorado); }
	
	public DirectivaCaracterizacion caracterizacion() {
		return directiva(DirectivaCaracterizacion.class);
	}
	
	public DirectivaCoordenadasMaven coordenadasMaven()  {
		return directiva(DirectivaCoordenadasMaven.class);
	}

	public DirectivaInventario directivaInventario()  {
		return directiva(DirectivaInventario.class);
	}

	public DirectivaRamaCodigo ramaCodigo() {
		return directiva(DirectivaRamaCodigo.class);
	}
	
	public DirectivaProyeccion proyeccion() {
		return directiva(DirectivaProyeccion.class);
	}
	
	public DirectivaProyeccionMaven proyeccionMaven() {
		return directiva(DirectivaProyeccionMaven.class);
	}
	
	public DirectivaPublicacionDeployer publicacionDeployer() {
		return directiva(DirectivaPublicacionDeployer.class);
	}
	
	// ----------------------
	// General
	// ----------------------
	
	public DirectivaBase buscarDirectiva(String idTipo) {
		for(DirectivaBase db: decorado.getDirectivasTipo())
			if(db.getDirectiva().getId().equalsIgnoreCase(idTipo)) return db;
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends DirectivaBase> T directiva(Class<T> cz) {
		for (DirectivaBase db: decorado.getDirectivasTipo())
			if(db.getClass().equals(cz)) return (T)db;
		return null;
	}
	
	/**
	 * Busca entre las directiva del artefacto las que sean de tipo diccionario
	 * y que tengan como id el pedido
	 */
	public DirectivaDiccionario diccionario(String idDiccionario) {

		for(DirectivaBase db: decorado.getDirectivasTipo()) {
			if(!(db instanceof DirectivaDiccionario)) continue;
			
			DirectivaDiccionario dd = (DirectivaDiccionario)db;
			if(dd.getDiccionario().getId().equalsIgnoreCase(idDiccionario))
				return dd;
		}
		
		return null;
	}
}
