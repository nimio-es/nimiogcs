package es.nimio.nimiogcs.jpa.entidades.operaciones;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.utils.DateTimeUtils;

/**
 * Sucedáneo de registro de operación que se crea para dejar constancia 
 * de errores generales 
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("TORTA")
public class ErrorGeneral extends Operacion {

	public ErrorGeneral() {
		super();
		setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
		setFinalizado(true);
		setDescripcion("TORTA " + DateTimeUtils.convertirAFormaYYMMDDHHMMSS(new Date()));
	}
	
	public ErrorGeneral(String descripcion) {
		this();
		setDescripcion(descripcion);
	}

}
