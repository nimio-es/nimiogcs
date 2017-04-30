package es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.EntornoConServidores;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_ENTORNOS_SERVIDORES")
public class RelacionEntornoServidor extends MetaRegistro {

	public RelacionEntornoServidor() {}
	
	public RelacionEntornoServidor(EntornoConServidores entorno, Servidor servidor) {
		this.entorno = entorno;
		this.servidor = servidor;
	}
	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=36)
	private String id;
	
	@JoinColumn(name="ENTORNO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private EntornoConServidores entorno;
	
	@JoinColumn(name="SERVIDOR", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Servidor servidor;
	
	// ---------------------------------------------
	// Lectura y escritura
	// ---------------------------------------------

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public EntornoConServidores getEntorno() { 
		return entorno;
	}
	
	public void setEntorno(EntornoConServidores entorno) {
		this.entorno = entorno;
	}
	
	public Servidor getServidor() {
		return servidor;
	}
	
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	// ---------------------------------------------
	// ID
	// ---------------------------------------------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 36);
	}
	
}
