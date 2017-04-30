package es.nimio.nimiogcs.operaciones;

import java.io.PrintWriter;
import java.io.StringWriter;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionSitio;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.modelo.IUsuario;

/**
 * Definición de lo que es una operación.
 *
 * @param <T>
 * @param <R>
 */
public abstract class ProcesoAsincronoBase<C extends IContextoEjecucionBase, T> 
	extends OperacionBase<C, ProcesoAsincronoBase.Peticion<T>, Boolean> 
	implements Runnable {

	// ------------------------------------------------
	// La clase que encapsula la petición y la política
	// de continuación según el caso
	// ------------------------------------------------
	
	public static final class Peticion<R> {
		
		private final R datos;
		private final Function<? super R,?> cuandoExito;
		private final Function<? super R,?> cuandoFallo;
		
		public Peticion(R datos) {
			this(datos, null, null);
		}
		public Peticion(R datos, Function<R,?> cuandoExito) {
			this(datos, cuandoExito, null);
		}
		public Peticion(R datos, Function<R,?> cuandoExito, Function<R,?> cuandoFallo) {
			this.datos = datos;
			this.cuandoExito = cuandoExito;
			this.cuandoFallo = cuandoFallo;
		}
		
		public R getDatos() {
			return datos;
		}
		
		public Function<? super R,?> getCuandoExito() {
			return cuandoExito;
		}
		
		public Function<? super R,?> getCuandoFallo() {
			return cuandoFallo;
		}
	}
	
	// -------------------------------------------------
	
	protected ProcesoAsincronoBase.Peticion<T> datos;
	protected IUsuario usuario;
	
	/**
	 * Constructor en el que se le suministra el contexto de ejecución de la aplicación
	 */
	public ProcesoAsincronoBase(C contextoEjecucion) {
		super(contextoEjecucion);
	} 
	
	/**
	 * La operación de ejecución en este caso se basa en lanzar la versión asíncrona
	 */
	@Override
	public Boolean ejecutar(ProcesoAsincronoBase.Peticion<T> d) {
		
		// los dejamos en el ámbito local
		datos  = d;
		
		// asignamos el usuario desde el contexto
		// no exisitirá si la tarea se lanza
		// desde un thread asíncrono
		if(usuario==null) usuario = ce.usuario();
		
		// construimos una petición al ejecutor
		ce.executor().execute(this);
		
		return true;
	}
	
	/**
	 * Versión simple que rellena el resto de datos
	 */
	public Boolean ejecutarCon(T subdatos) {
		return ejecutar(new Peticion<T>(subdatos));
	}
	
	public Boolean ejecutarCon(T subdatos, Function<T,?> conExito) {
		return ejecutar(new Peticion<T>(subdatos, conExito));
	}

	public Boolean ejecutarCon(T subdatos, Function<T,?> conExito, Function<T,?> conFallo) {
		return ejecutar(new Peticion<T>(subdatos, conExito, conFallo));
	}

	// ------------------------------------
	// permitimos que desde fuera se pueda
	// definir el usuario para que tareas
	// asíncronas puedan darle el suyo
	// ------------------------------------

	public IUsuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(IUsuario usuario) {
		this.usuario = usuario;
	}
	
	/**
	 * Versión fluida del método que permite asignar el usuario
	 */
	public ProcesoAsincronoBase<C, T> setUsuarioAnd(IUsuario usuario) {
		setUsuario(usuario);
		return this;
	}

	// ------------------------------------
	// la parte runnable
	// ------------------------------------
	
	@Override
	public void run() {

		// 1. creamos el registro del proceso
		ProcesoAsincrono op = crearRegistroOperacion();

		try {
			// 2. asociamos a las entidades que corresponda
			//    (cuando esas entidades ya existan)
			relacionarOperacionConEntidades(datos.getDatos(), op);

			// 3. hacemos
			hazlo(datos.getDatos(), op);

			// 4. finalizamos
			concluyoLaOperacion(op);
			
			// 5. Terminamos lanzando la función que define qué hacer en caso de éxito
			if(datos.getCuandoExito()!=null) datos.getCuandoExito().apply(this.datos.getDatos());
			
		} catch (Exception th) {

			// UPS! CASTAÑA
			seRompio(op, th);
			
			// pues lanzamos la función que define qué hacer en caso de fallo
			if(datos.getCuandoFallo()!=null) datos.getCuandoFallo().apply(this.datos.getDatos());
		}
	}
	
	// ------------------------------------------------
	// Las utilidades
	// ------------------------------------------------

	/**
	 * Al introducirlo en un método permitimos que se pueda sobrecargar 
	 * para aquellos casos que la tarea que llame sepa cómo mejor 
	 * obtener el usuario
	 */
	protected String usuario() {
		return usuario == null ? "UNK" : usuario.getNombre();
	}
	
	protected String nombreOperacion(T datos, ProcesoAsincrono op) {
		return "OPERACIÓN INTERNA ASÍNCRONA";
	}
	
	/**
	 * Genera un nombre único de la operación. Debe sobrescribirse si se quiere
	 * usar algo más descriptivo.
	 */
	protected String nombreUnicoOperacion(T datos, ProcesoAsincrono op) {
		return new StringBuilder(nombreOperacion(datos, op))
				.append(" (cod. '")
				.append(op.getId())
				.append("')")
				.toString();
	}

	
	protected void registraRelacionConOperacion(Operacion op, ElementoBaseProyecto proyecto) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionElementoProyecto(op, proyecto)
		);
	}
	
	protected void registraRelacionConOperacion(ProcesoAsincrono op, DestinoPublicacion entorno) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionDestinoPublicacion(op, entorno)
		);
	}
	
	protected void registraRelacionConOperacion(ProcesoAsincrono op, Servidor sitio) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionSitio(op, sitio)
		);
	}
	
	/**
	 * utilidad para facilitar el registro de relaciones con la operación.
	 */
	protected void registraRelacionConOperacion(ProcesoAsincrono op, Artefacto entidad) {
		ce.repos().operaciones().relaciones().guardarYVolcar(
				new RelacionOperacionArtefacto(op, entidad)
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
	protected ProcesoAsincrono crearRegistroOperacion() {
		
		// creamos y guardamos
		ProcesoAsincrono op = new ProcesoAsincrono();
		op.setDescripcion(nombreUnicoOperacion(datos.getDatos(), op));
		op.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.EJECUCION);
		op.setUsuarioEjecuta(usuario().toUpperCase());
		op = ce.repos().operaciones().guardarYVolcar(op);
		
		return op;
	}
	
	/**
	 * 2. Relacionamos los elementos que consideramos necesario relacionar
	 * @param op
	 */
	protected abstract void relacionarOperacionConEntidades(T datos, ProcesoAsincrono op);
	
	/**
	 * 3. Donde tendremos que hacer lo importante. 
	 *    Aquí también cabe añadir relaciones.
	 * @throws Throwable 
	 */
	protected abstract void hazlo(T datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion;
	
	/**
	 * 4. Donde decimos que la operación ha finalizado felizmente.
	 */
	protected void concluyoLaOperacion(ProcesoAsincrono op) {
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
		final StringWriter error = new StringWriter();
		th.printStackTrace(new PrintWriter(error));
		
		// por si ya se ha ido escribiendo en el canal de error
		final String mensajeError = op.getMensajeError() != null ? op.getMensajeError() : "";
		
		// indicamos que la cosa se ha torcido
		op.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
		op.setFinalizado(true);
		op.setMensajesEjecucion(this.msg.toString());
		op.setMensajeError(mensajeError + error.toString());
		ce.repos().operaciones().guardarYVolcar(op);
		
		// también dejamos constancia del error en el logger correspondiente
		this.error(mensajeError, th);
	}
}
