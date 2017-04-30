package es.nimio.nimiogcs.jpa.entidades;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;

/**
 * Conjunto de datos mínimos que deben tener todos los registros que se almacenen
 *
 */
@MappedSuperclass
public abstract class MetaRegistro extends RegistroConIdCalculado {

	// ===============================================
	// Estado
	// ===============================================
	
	@Column(name = "FECHA_CREA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creacion;

	@Column(name = "FECHA_MODIFICA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modificacion;

	@Version
	@Column(name = "SYS_CNT_MODIF", nullable = false)
	private Long contadorModificaciones;
	
	// -----------------------------------------------
	// Estado: consulta y modificación
	// -----------------------------------------------

	public Date getCreacion() {
		return creacion;
	}

	public void setCreacion(Date creacion) {
		this.creacion = creacion;
	}

	public Date getModificacion() {
		return modificacion;
	}

	public void setModificacion(Date modificacion) {
		this.modificacion = modificacion;
	}
	
	// -----------------------------------------------
	// PrePersist and PreModify callback
	// -----------------------------------------------

	@PreUpdate
	@PrePersist
	public void actualizarFechaModificacion() {
		Date instante = new Date();
		modificacion = instante;
		if (creacion == null) creacion = instante;
	}
	
	// -----------------------------------------------
	// Construcción
	// -----------------------------------------------

	public MetaRegistro() {
		super();
		
		// -- fijamos el id de esta instancia con lo que nos devuelva la utilidad pasándole el prefijo
		this.setId(generarIdElemento());
	}
	
	
	// ----------------------------------------------
	// Reproducción
	// ----------------------------------------------

	/**
	 * Método que debe ser sobrecargado / redefinido en cada clase
	 * (siempre que el mecanismo de reflexión no funcione) 
	 * que herede de esta para asegurarnos que obtenemos una instancia
	 * adecuada a lo que queremos reproducir.
	 */
	protected MetaRegistro nuevaInstancia() {
		MetaRegistro nueva = null;
		try {
			nueva = (MetaRegistro) this.getClass().getConstructor((Class<?> [])null).newInstance((Object[])null);
		} catch (InstantiationException e) {
			throw new ErrorInesperadoOperacion(e);
		} catch (IllegalAccessException e) {
			throw new ErrorInesperadoOperacion(e);
		} catch (IllegalArgumentException e) {
			throw new ErrorInesperadoOperacion(e);
		} catch (InvocationTargetException e) {
			throw new ErrorInesperadoOperacion(e);
		} catch (NoSuchMethodException e) {
			throw new ErrorInesperadoOperacion(e);
		} catch (SecurityException e) {
			throw new ErrorInesperadoOperacion(e);
		}
		
		return nueva;
	}
	
}
