package es.nimio.nimiogcs.jpa.entidades.operaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Representación almacenada de un proceso BATCH
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("BATCH")
public class ProcesoBatch extends Operacion {

	// -------------------------------------------------------
	// Estado
	// -------------------------------------------------------

	@Column(name="JOB_EXECUTION_ID", nullable=false)
	private long idBatchJobExecution;
	
	// -------------------------------------------------------
	// Leer / Escribir estado
	// -------------------------------------------------------

	public long getIdBatchJobExecution() { return this.idBatchJobExecution; }
	public void setIdBatchJobExecution(long id) { this.idBatchJobExecution = id; }
	
}
