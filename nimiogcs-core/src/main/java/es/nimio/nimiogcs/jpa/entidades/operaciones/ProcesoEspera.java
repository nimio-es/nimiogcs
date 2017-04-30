package es.nimio.nimiogcs.jpa.entidades.operaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("ESPERA")
public class ProcesoEspera extends Operacion {

	// ---------------------------------------
	// Construcción
	// ---------------------------------------

	public ProcesoEspera() {
		// por la propia naturaleza del tipo de proceso, el estado de arranque será "espera"
		this.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ESPERANDO);
		
		// además establecemos que el tiempo máximo de espera serán cinco minutos, por defecto
		setMinutosEspera(5);
	}

	public ProcesoEspera(String ticket, int segundos) {
		this();
		setTicketRelacionado(ticket);
		setSegundosEspera(segundos);
	}

	public ProcesoEspera(String nombre, String ticket, int segundos) {
		this(ticket, segundos);
		setDescripcion(nombre);
	}

	
	// ---------------------------------------
	// Estado
	// ---------------------------------------

	@Column(name="SEGUNDOS_ESPERA", nullable=false)
	private int segundosEspera;

	@Column(name="TICKET_ASOCIADO", nullable=false, length=50)
	private String ticketRelacionado;
	
	// ---------------------------------------
	// Lectura y escritura del estado
	// ---------------------------------------

	public int getSegundosEspera() { return this.segundosEspera; }
	
	public void setSegundosEspera(int segundos) { this.segundosEspera = segundos; }
	
	public int getMinutosEspera() { return this.getSegundosEspera() / 60; } 
	
	public void setMinutosEspera(int minutos) { setSegundosEspera( minutos * 60 ); }

	public int getHorasEspera() { return this.getSegundosEspera() / 60 / 60; }
	
	public void setHorasEspera(int horas) { setSegundosEspera(horas * 60 * 60); }
	
	public String getTicketRelacionado() { return this.ticketRelacionado; }
	
	public void setTicketRelacionado(String ticket) { this.ticketRelacionado = ticket; }
}
