package es.nimio.nimiogcs.datos;

import java.util.Collection;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Programada;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInline;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public abstract class IniciadorBase extends OperacionInternaInline<IContextoEjecucion> {

	public IniciadorBase(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ----
	
	private IContextoEjecucion ice() { return (IContextoEjecucion)ce; }
	
	@Override
	protected Operacion nuevaOperacion() {
		return new Programada();
	}

	@Override
	protected String usuarioOperaciones() {
		return "CAVERNICOLA";
	}
	
	
	@Override
	protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
		
		tiposDeDirectivas();
		tiposDeArtefactos();
		parametrosGlobales();
		destinosPublicacion();

		return true;
	}
	
	private void tiposDeDirectivas() {
		escribeMensaje("Revisar los tipos de directivas existentes...");
		for(Tuples.T5<String, String, Boolean, Boolean, Boolean> t: listaTiposDeDirectivas()) {
			checkAndWriteTipoDirectiva(t._1, t._2, t._3, t._4, t._5);
		}

		ice().tiposDirectivas().flush();
		escribeMensaje("");
	}
	
	private void tiposDeArtefactos() {
		escribeMensaje("Revisar los tipos de artefactos existentes...");
		for(Tuples.T3<String, String, Boolean> t: listaTiposDeArtefactos()) {
			checkAndWriteTipoArtefacto(t._1, t._2, t._3);
		}

		ice().tipos().flush();
		escribeMensaje("");
	}

	private void parametrosGlobales() {
		escribeMensaje("Revisar los parámetros globales existentes...");
		for(Tuples.T3<String, String, String> t: listaParametrosGlobales()) {
			checkAndWriteParametroGlobal(t._1, t._2, t._3);
		}

		ce.repos().global().volcar();
		escribeMensaje("");
	}
	
	private void destinosPublicacion() {
		escribeMensaje("Revisar los destinos de publicación existentes...");
		for(Tuples.T2<String, String> t: listaDestinosPublicacion()) {
			checkAndWriteDestinoPublicacion(t._1, t._2);
		}
		ice().destinosPublicacion().flush();
		escribeMensaje("");
	}
	
	protected abstract Collection<Tuples.T5<String, String, Boolean, Boolean, Boolean>> listaTiposDeDirectivas();
	protected abstract Collection<Tuples.T3<String, String, Boolean>> listaTiposDeArtefactos();
	protected abstract Collection<Tuples.T3<String, String, String>> listaParametrosGlobales();
	protected abstract Collection<Tuples.T2<String, String>> listaDestinosPublicacion();
	
	private void checkAndWriteTipoDirectiva(String id, String nombre, boolean paraTipo, boolean paraArtefacto, boolean conDiccionario) {

		String mensaje = "Revisando existencia tipo directiva '" + id + "' ('" + nombre + "): ";
		final boolean existe = ice().tiposDirectivas().findOne(id) != null;
		mensaje += existe ? "EXISTE" : "NO EXISTE -> DAR DE ALTA...";
		escribeMensaje(mensaje);
		if(!existe) {
			final TipoDirectiva td = new TipoDirectiva();
			td.setId(id);
			td.setNombre(nombre);
			td.setParaTipoArtefacto(paraTipo);
			td.setParaArtefacto(paraArtefacto);
			td.setConDiccionario(conDiccionario);
			ice().tiposDirectivas().save(td);
			escribeMensaje("... registro creado");
		}
	}
	
	private void checkAndWriteTipoArtefacto(String id, String nombre, boolean deUsuario) {
		
		String mensaje = "Revisando existencia tipo artefacto '" + id + "' ('" + nombre + "): ";
		final boolean existe = ice().tipos().findOne(id) != null;
		mensaje += existe ? "EXISTE" : "NO EXISTE -> DAR DE ALTA...";
		escribeMensaje(mensaje);
		if(!existe) {
			final TipoArtefacto ta = new TipoArtefacto();
			ta.setId(id);
			ta.setNombre(nombre);
			ta.setDeUsuario(deUsuario);
			ice().tipos().save(ta);
			escribeMensaje("... registro creado");
		}
	}
	
	private void checkAndWriteParametroGlobal(String id, String descripcion, String contenido) {
		
		String mensaje = "Revisando existencia parámetro global '" + id + "': ";
		final boolean existe = ce.repos().global().buscar(id) != null;
		mensaje += existe ? "EXISTE" : "NO EXISTE -> DAR DE ALTA...";
		escribeMensaje(mensaje);
		if(!existe) {
			final ParametroGlobal pg = new ParametroGlobal();
			pg.setId(id);;
			pg.setDescripcion(descripcion);
			pg.setContenido(contenido);
			ce.repos().global().guardar(pg);
			escribeMensaje("... registro creado");
		}
	}
	
	private void checkAndWriteDestinoPublicacion(String id, String nombre) {
		
		String mensaje = "Revisando existencia destino publicación '" + id + "': ";
		final boolean existe = ice().destinosPublicacion().findOne(id) != null;
		mensaje += existe ? "EXISTE" : "NO EXISTE -> DAR DE ALTA...";
		escribeMensaje(mensaje);
		if(!existe) {
			final DestinoPublicacion dp = new DestinoPublicacion();
			dp.setId(id);
			dp.setNombre(nombre);
			ice().destinosPublicacion().save(dp);
			escribeMensaje("... registro creado");
		}
	}
}
