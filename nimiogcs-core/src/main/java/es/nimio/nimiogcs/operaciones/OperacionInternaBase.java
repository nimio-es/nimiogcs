package es.nimio.nimiogcs.operaciones;

import java.io.PrintWriter;
import java.io.StringWriter;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionSitio;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionTipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;

/**
 * Todas las operaciones que tienen un carácter más o menos inmediato y que
 * queremos que deje constancia en la lista de operaciones.
 */
public abstract class OperacionInternaBase<C extends IContextoEjecucionBase, T, R> extends OperacionBase<C, T, R> {

	private String usuarioOperacion = "CAVERNICOLA";
	
	public OperacionInternaBase(C contextoEjecucion) {
		super(contextoEjecucion);
		if(ce.usuario()!=null) usuarioOperacion = 
				ce.usuario().getNombre().toUpperCase();
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

	public void setUsuarioOperacion(String usuario) {
		this.usuarioOperacion = usuario;
	}
	
	public OperacionInternaBase<C, T, R> setUsuarioOperacionAnd(String usuario) {
		setUsuarioOperacion(usuario);
		return this;
	}
	
	/**
	 * Permite sobrecargar el tipo exacto de registro de operación
	 * que deseamos utilizar.
	 */
	protected Operacion nuevaOperacion() {
		return new Operacion();
	}
	
	/**
	 * Permite sobrecargar la obtención del usuario al que asignar la operación.
	 * @return
	 */
	protected String usuarioOperaciones() {
		return usuarioOperacion;
	}
	
	protected String nombreOperacion(T datos, Operacion op) {
		return "OPERACIÓN INTERNA";
	}
	
	/**
	 * Genera un nombre único de la operación. Debe sobrescribirse si se quiere
	 * usar algo más descriptivo.
	 */
	protected String nombreUnicoOperacion(T datos, Operacion op) {
		return new StringBuilder(nombreOperacion(datos, op))
				.append("(cod. '")
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
	protected void registraRelacionConOperacion(Operacion op, Artefacto artefacto) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionArtefacto(op, artefacto)
		);
	}
	
	/**
	 * utilidad para facilitar el registro de relaciones con la operación.
	 */
	protected void registraRelacionConOperacion(Operacion op, TipoArtefacto tipoArtefacto) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionTipoArtefacto(op, tipoArtefacto)
		);
	}
	
	protected void registraRelacionConOperacion(Operacion op, Publicacion publicacion) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionPublicacion(op, publicacion)
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
		Operacion op = nuevaOperacion();
		op.setDescripcion(nombreUnicoOperacion(datos, op));
		op.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.EJECUCION);
		op.setUsuarioEjecuta(usuarioOperaciones());
		op = ce.repos().operaciones().guardarYVolcar(op);
		
		return op;
	}
	
	/**
	 * 2. Relacionamos los elementos que consideramos necesario relacionar
	 * @param op
	 */
	protected abstract void relacionarOperacionConEntidades(T datos, Operacion op);
	
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
		
		this.error("Error durante la operación", th);
	}
}
