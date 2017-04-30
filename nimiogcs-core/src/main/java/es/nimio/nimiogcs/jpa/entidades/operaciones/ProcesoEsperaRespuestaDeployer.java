package es.nimio.nimiogcs.jpa.entidades.operaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Representa el proceso de espera a que Deployer comience la primera publicación
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("ESPERA_DEPLOYER")
public class ProcesoEsperaRespuestaDeployer extends ProcesoEspera {

	// ---------------------------------------
	// Construcción
	// ---------------------------------------

	public ProcesoEsperaRespuestaDeployer() {
		super();
		
		// esperamos 10 minutos a que Deployer nos confirme que ha comenzado con la operación 
		setMinutosEspera(10);
	}
	
	public ProcesoEsperaRespuestaDeployer(String descripcion, String ticketPeticion, String etiquetaPase) {
		super(descripcion, ticketPeticion, 10 * 60);
		setEtiquetaPase(etiquetaPase);
	}

	// ---------------------------------------
	// Estado
	// ---------------------------------------
	
	@Column(name="ETIQUETA_PASE", nullable=false, length=30)
	private String etiquetaPase;
	
	
	// ---------------------------------------
	// Lectura / escritura del estado
	// ---------------------------------------
	
	public String getEtiquetaPase() { return this.etiquetaPase; }
	public void setEtiquetaPase(String etiquetaPase) { this.etiquetaPase= etiquetaPase; }

}
