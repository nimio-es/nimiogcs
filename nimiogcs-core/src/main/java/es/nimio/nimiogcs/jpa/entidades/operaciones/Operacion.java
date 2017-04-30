package es.nimio.nimiogcs.jpa.entidades.operaciones;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_OPERACIONES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_OP", discriminatorType = DiscriminatorType.STRING, length = 20)
@DiscriminatorValue(value = "OPERACION")
public class Operacion 
	extends MetaRegistro 
	implements EntidadNombrable {

	// -------------------------------------------------------
	// Estado
	// -------------------------------------------------------
	
	@Column(name = "TIPO_OP", insertable = false, updatable = false)
	private String tipoOperacion;

	@Id
	@Column(name = "ID", nullable = false, length = 32)
	private String id;
	
	@Column(name = "DESCRIPCION", nullable = false, length = 200)
	private String descripcion;
	
	@Column(name="ESTADO_EJECUCION", nullable=false, length=20)
	@Enumerated(EnumType.STRING)
	private EnumEstadoEjecucionProceso estadoEjecucionProceso;
	
	@Column(name="FINALIZADO", nullable=false, length=2)
	private String finalizado = K.L.NO;

	@Lob
	@Column(name = "MENSAJES_EJECUCION", nullable = true)
	private String mensajesEjecucion;

	@Lob
	@Column(name = "MENSAJE_ERROR", nullable = true)
	private String mensajeError;

	@Column(name = "USUARIO_EJECUTA", nullable = true)
	private String usuarioEjecuta;

	@Column(name = "TIEMPO_INICIO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date tiempoInicio;

	@Column(name = "TIEMPO_TERMINA", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date tiempoTermina;

	// -------------------------------------------------------
	// Leer / Escribir estado
	// -------------------------------------------------------

	public String getTipoOperacion() { return tipoOperacion; }
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getDescripcion() { return this.descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	
	public String getNombre() { return getDescripcion(); }
	
	public EnumEstadoEjecucionProceso getEstadoEjecucionProceso() { return this.estadoEjecucionProceso; }
	public void setEstadoEjecucionProceso(EnumEstadoEjecucionProceso estado) { this.estadoEjecucionProceso = estado; }
	
	public boolean getFinalizado() { return K.L.desde(this.finalizado); }
	public void setFinalizado(boolean valor) { 
		this.finalizado = K.L.para(valor);
		
		// si la damos por terminada, además fijamos la fecha de finalización
		if(valor) this.tiempoTermina = new Date();
	}
	
	public String getMensajesEjecucion() { return mensajesEjecucion; }
	public void setMensajesEjecucion(String datos) { this.mensajesEjecucion = datos; }
	
	public String getMensajeError() { return this.mensajeError; }
	public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }
	
	public String getUsuarioEjecuta() { return usuarioEjecuta; }
	public void setUsuarioEjecuta(String usuario) { this.usuarioEjecuta = usuario; }
	
	public Date getTiempoInicio() {return tiempoInicio;}
	public void setTiempoInicio(Date tiempoInicio) {this.tiempoInicio = tiempoInicio;}

	public Date getTiempoTermina() {return tiempoTermina;}
	public void setTiempoTermina(Date tiempoTermina) {this.tiempoTermina = tiempoTermina;}

	/**
	 * Diferencia de tiempo entre el momento que se crea y, si ha terminado, el instante que cambia de estado. 
	 * Y, si no ha terminado, entre el instante actual.
	 */
	public String diferenciaTiempo() {
		// cálculo de la diferencia
		long current = getFinalizado() ? getTiempoTermina().getTime() : new Date().getTime();
		long diff = current - getTiempoInicio().getTime();
		
		// para presentarlo
		long mili = diff, secs = 0, mins = 0, horas = 0, dias = 0;
		if (mili >= 1000) { secs = mili / 1000; mili = mili % 1000; }
		if (mili >= 500) { secs = secs + 1; }
		if (secs >= 60) { mins = secs / 60; secs = secs % 60; }
		if (mins >= 60) { horas = mins / 60; mins = mins % 60; }
		if (horas >= 24) { dias = horas / 24; horas = horas % 24; }
		
		// empezamos de izquierda a derecha
		StringBuilder representacion = new StringBuilder();
		if(dias>0) representacion.append(dias).append("d ");
		if(horas > 0 || dias > 0) representacion.append(horas).append("h ");
		if(mins > 0 || horas > 0 || dias > 0) representacion.append(mins).append("m ");
		representacion.append(secs).append("s");

		return representacion.toString();
	}
	
	// -----------------------------------------------
	// PrePersist and PreModify callback
	// -----------------------------------------------

	@Override
	public void actualizarFechaModificacion() {
		super.actualizarFechaModificacion();
		if(tiempoInicio==null) tiempoInicio = new Date();
	};
	
	// --------------------------------------------------
	// Generación del ID del elemento
	// --------------------------------------------------
	
	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 32);
	}
	
}
