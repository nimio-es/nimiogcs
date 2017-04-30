package es.nimio.nimiogcs.operaciones;

import java.io.PrintWriter;
import java.io.StringWriter;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.NotificacionExterna;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionSitio;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;

/**
 * Todas las operaciones que tienen un carácter más o menos inmediato y que
 * queremos que deje constancia en la lista de operaciones.
 */
public abstract class OperacionExternaBase<C extends IContextoEjecucionBase, T, R> extends OperacionBase<C, T, R> {

	private String usuario;
	
	public OperacionExternaBase(C contextoEjecucion) {
		super(contextoEjecucion);
		if(ce.usuario() != null) this.usuario = ce.usuario().getNombre().toUpperCase(); else this.usuario = "EXTERNO";
	}

	@Override
	public R ejecutar(T d) {

		// 1. creamos el registro del proceso
		Operacion op = crearRegistroOperacion(d);

		try {
			// 2. asociamos a las entidades que corresponda
			//    (cuando esas entidades ya existan)
			relacionarOperacionConEntidades(d, op);

			// 3. hacemos
			R r = hazlo(d, op);

			// y devolvemos
			return r;

		} catch (Exception th) {

			// UPS! CASTAÑA
			seRompio(op, th);
			
			// después de dejar constancia del estrepitoso fracaso, 
			// continuamos la excepción hasta su última consecuencia
			throw new ErrorInesperadoOperacion(th);
			
		} finally {
			
			// 4. finalizamos
			concluyoLaOperacion(op);
		}
	}

	// ------------------------------------------------
	// Las utilidades
	// ------------------------------------------------

	protected abstract String nombreSistemaNotifica();
	

	protected String nombreOperacion(T datos, Operacion op) {
		return "NOTIFICACIÓN EXTERNA DESDE '" + nombreSistemaNotifica() + "'";
	}
	
	/**
	 * Genera un nombre único de la operación. Debe sobrescribirse si se quiere
	 * usar algo más descriptivo.
	 */
	protected String nombreUnicoOperacion(T datos, Operacion op) {
		return new StringBuilder(nombreOperacion(datos, op))
				.append(" (cod. '")
				.append(op.getId())
				.append("')")
				.toString();
	}
	
	protected void registraRelacionConOperacion(Operacion op, DestinoPublicacion entorno) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionDestinoPublicacion(op, entorno)
		);
	}
	
	protected void registraRelacionConOperacion(Operacion op, Servidor sitio) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionSitio(op, sitio)
		);
	}
	
	protected void registraRelacionConOperacion(Operacion op, ElementoBaseProyecto proyecto) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionElementoProyecto(op, proyecto)
		);
	}
	
	/**
	 * utilidad para facilitar el registro de relaciones con la operación.
	 */
	protected void registraRelacionConOperacion(Operacion op, Artefacto entidad) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionArtefacto(op, entidad)
		);
	}
	
	// ------------------------------------------------
	// Las etapas
	// ------------------------------------------------

	/**
	 * 1. Crea el registro de la operación
	 */
	protected Operacion crearRegistroOperacion(T datos) {
		
		// creamos y guardamos
		Operacion op = new NotificacionExterna(nombreSistemaNotifica());
		op.setDescripcion(nombreUnicoOperacion(datos, op));
		op.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.EJECUCION);
		op.setUsuarioEjecuta(this.usuario);
		op = ce.repos().operaciones().guardarYVolcar(op);
		
		return op;
	}
	
	/**
	 * 2. Relacionamos los elementos que consideramos necesario relacionar
	 * @param op
	 */
	protected void relacionarOperacionConEntidades(T datos, Operacion op) {
		// TODO: si se quiere relacionar con algo, que se sobrecargue
	};
	
	/**
	 * 3. Donde tendremos que hacer lo importante. 
	 *    Aquí también cabe añadir relaciones.
	 */
	protected abstract R hazlo(T datos, Operacion op) throws ErrorInesperadoOperacion;
	
	/**
	 * 4. Donde decimos que la operación ha finalizado felizmente.
	 */
	protected void concluyoLaOperacion(Operacion op) {
		op.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.OK);
		op.setFinalizado(true);
		op.setMensajesEjecucion(this.msg.toString());
		ce.repos().operaciones().guardarYVolcar(op);
	}
	
	/**
	 * CASTAÑA. Cuando todo falla y dejamos constancia de ello.
	 */
	protected void seRompio(Operacion op, Throwable th) {
		
		// sacamos la pila de errores a una cadena
		StringWriter error = new StringWriter();
		th.printStackTrace(new PrintWriter(error));
		
		// indicamos que la cosa se ha torcido
		op.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
		op.setFinalizado(true);
		op.setMensajesEjecucion(this.msg.toString());
		op.setMensajeError(error.toString());
		ce.repos().operaciones().guardarYVolcar(op);
		
		// también dejamos constancia del error en el logger correspondiente
		this.error("Error durante la operación", th);
	}
	
	// ------------------------------------------------
	// Adicionales
	// ------------------------------------------------

	public OperacionExternaBase<C, T, R> setUsuarioAnd(String usuario) {
		this.usuario = usuario;
		return this;
	}
}
