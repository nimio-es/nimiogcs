package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "PUBLC")
public class RelacionOperacionPublicacionArtefacto extends RelacionOperacionArtefacto {

	public RelacionOperacionPublicacionArtefacto() { super(); }

	public RelacionOperacionPublicacionArtefacto(Operacion operacion, Artefacto entidad, String ticket, EtiquetaProyecto etiquetaProyecto) { 
		super(operacion, entidad);
		this.ticketOriginal = ticket;
		this.etiquetaProyecto = etiquetaProyecto;
	}

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Column(name="TICKET_PETICION", nullable=false, length=50)
	private String ticketOriginal;
	
	@JoinColumn(name="ID_ETIQUETA_PROYECTO", nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private EtiquetaProyecto etiquetaProyecto;
	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	@NotNull(message="El valor de identificador del tique original no puede ser nulo.")
	@Size(max=50, message="El identificador del tique original no puede superar los 50 caracteres.")
	public String getTicketOriginal() { 
		return this.ticketOriginal;
	}
	
	public void setTicketOriginal(String ticketOriginal) {
		this.ticketOriginal = ticketOriginal;
	}
	
	@NotNull
	public EtiquetaProyecto getEtiquetaProyecto() {
		return this.etiquetaProyecto;
	}
	
	public void setEtiquetaProyecto(EtiquetaProyecto etiquetaProyecto) {
		this.etiquetaProyecto = etiquetaProyecto;
	}
}
