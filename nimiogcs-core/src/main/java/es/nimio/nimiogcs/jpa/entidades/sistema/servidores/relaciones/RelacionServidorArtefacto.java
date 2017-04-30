package es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;

/**
 * Relación base de los servidores java con los artefactos java
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_SERVIDORES_ARTEFACTOS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO", discriminatorType = DiscriminatorType.STRING, length = 4)
@DiscriminatorValue(value = "BASE")
public class RelacionServidorArtefacto extends MetaRegistro {

	public RelacionServidorArtefacto() {}

	public RelacionServidorArtefacto(Servidor servidor, Artefacto artefacto) {
		setServidor(servidor);
		setArtefacto(artefacto);
	}

	// -----------------------------------------
	// Estado
	// -----------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=36)
	private String id;
	
	@JoinColumn(name="SERVIDOR", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Servidor servidor;
	
	@JoinColumn(name="ARTEFACTO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Artefacto artefacto;

	
	// -----------------------------------------
	// Lecutra / Escritura del estado
	// -----------------------------------------
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Servidor getServidor() {
		return this.servidor;
	}
	
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
	
	public Artefacto getArtefacto() {
		return this.artefacto;
	}
	
	public void setArtefacto(Artefacto artefacto) {
		this.artefacto = artefacto;
	}
	
	
	// -----------------------------------------
	// ID
	// -----------------------------------------
	
	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 36);
	}
	
}
