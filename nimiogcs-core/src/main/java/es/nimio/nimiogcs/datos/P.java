package es.nimio.nimiogcs.datos;

import java.util.ArrayList;
import java.util.Collection;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaParametrosDeployer;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccionMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionJenkins;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaReferenciar;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;

public final class P {

	private P(Artefacto artefacto) {
		if(artefacto==null) 
			throw new ErrorInesperadoOperacion("Imposible decorar un artefacto indicado como nulo.");
		decorado = artefacto;
	}
	
	
	final private Artefacto decorado;
	
	public static P of(Artefacto decorado) { return new P(decorado); }
	
	public DirectivaCoordenadasMaven coordenadasMaven()  {
		return directiva(DirectivaCoordenadasMaven.class);
	}

	public DirectivaInventario inventario()  {
		return directiva(DirectivaInventario.class);
	}

	public DirectivaTaxonomia taxonomia() {
		return directiva(DirectivaTaxonomia.class);
	}
	
	public DirectivaAlcances alcances() {
		return directivaEnArtefactoOEnTipo(DirectivaAlcances.class);
	}

	public DirectivaEstrategiaEvolucion evolucion() {
		return directivaEnArtefactoOEnTipo(DirectivaEstrategiaEvolucion.class);
	}
	
	public DirectivaRepositorioCodigo repositorioCodigo() {
		return directiva(DirectivaRepositorioCodigo.class);
	}
	
	public DirectivaRamaCodigo ramaCodigo() {
		return directiva(DirectivaRamaCodigo.class);
	}
	
	public DirectivaReferenciar referenciar() {
		return directivaEnArtefactoOEnTipo(DirectivaReferenciar.class);
	}
	
	public DirectivaProyeccion proyeccion() {
		return directivaEnArtefactoOEnTipo(DirectivaProyeccion.class);
	}
	
	public DirectivaProyeccionMaven proyeccionMaven() {
		return directivaEnArtefactoOEnTipo(DirectivaProyeccionMaven.class);
	}
	
	public DirectivaPublicacionDeployer publicacionDeployer() {
		return directivaEnArtefactoOEnTipo(DirectivaPublicacionDeployer.class);
	}
	
	public DirectivaParametrosDeployer parametrosDeployer() {
		return directiva(DirectivaParametrosDeployer.class);
	}
	
	public DirectivaPublicacionJenkins publicacionJenkins() {
		return directivaEnArtefactoOEnTipo(DirectivaPublicacionJenkins.class);
	}
	
	// ----------------------
	// General
	// ----------------------

	public DirectivaBase buscarDirectiva(String idTipo) {
		for(DirectivaBase db: decorado.getDirectivasArtefacto())
			if(db.getDirectiva().getId().equalsIgnoreCase(idTipo)) return db;
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public <T extends DirectivaBase> T directiva(Class<T> cz) {
		for (DirectivaBase db: decorado.getDirectivasArtefacto())
			if(db.getClass().equals(cz)) return (T)db;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DirectivaBase> T directivaEnArtefactoOEnTipo(Class<T> cz) {
		T posible = directiva(cz);
		if(posible!=null) return posible;
		for(DirectivaBase db: decorado.getTipoArtefacto().getDirectivasTipo()) {
			if(db.getClass().equals(cz)) return (T)db;
		}
		return null;
	}
	
	/**
	 * Busca entre las directiva del artefacto las que sean de tipo diccionario
	 * y que tengan como id el pedido
	 */
	public DirectivaDiccionario diccionario(String idDiccionario) {

		for(DirectivaBase db: decorado.getDirectivasArtefacto()) {
			if(!(db instanceof DirectivaDiccionario)) continue;
			
			DirectivaDiccionario dd = (DirectivaDiccionario)db;
			if(dd.getDiccionario().getId().equalsIgnoreCase(idDiccionario))
				return dd;
		}
		
		return null;
	}
	
	// ---------------
	// carpetas
	// ---------------
	
	public static Collection<P.CarpetaExportacion> carpetasExportar(DirectivaProyeccion proyeccion) {
		final ArrayList<P.CarpetaExportacion> carpetas = new ArrayList<P.CarpetaExportacion>();

		int totalCarpetas = proyeccion.getNumeroCarpetas();
		if(totalCarpetas > 0) {
			carpetas.add(new CarpetaExportacion(proyeccion.getCarpetaOrigen1(), proyeccion.getCarpetaDestino1()));
		}
		if(totalCarpetas > 1) {
			carpetas.add(new CarpetaExportacion(proyeccion.getCarpetaOrigen2(), proyeccion.getCarpetaDestino2()));
		}
		if(totalCarpetas > 2) {
			carpetas.add(new CarpetaExportacion(proyeccion.getCarpetaOrigen3(), proyeccion.getCarpetaDestino3()));
		}
		if(totalCarpetas > 3) {
			carpetas.add(new CarpetaExportacion(proyeccion.getCarpetaOrigen4(), proyeccion.getCarpetaDestino4()));
		}
		if(totalCarpetas > 4) {
			carpetas.add(new CarpetaExportacion(proyeccion.getCarpetaOrigen5(), proyeccion.getCarpetaDestino5()));
		}
		
		return carpetas;
	}
	
	public static final class CarpetaExportacion {
		
		private final String puntoAnclaje;
		private final String rutaExterna;
		
		public CarpetaExportacion(String puntoAnclaje, String rutaExterna) {
			this.puntoAnclaje = puntoAnclaje;
			this.rutaExterna = rutaExterna;
		}

		public String getPuntoAnclaje() {
			return puntoAnclaje;
		}

		public String getRutaExterna() {
			return rutaExterna;
		}
	}	
}
