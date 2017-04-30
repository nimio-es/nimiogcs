package es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;

/**
 * Relación entre un servidor y un EAR para indicar qué EAR están instalados
 * en qué servidores.
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "APP")
public class RelacionServidorAplicacion extends RelacionServidorArtefacto {

	public RelacionServidorAplicacion() { super(); }

	public RelacionServidorAplicacion(ServidorJava servidor, Artefacto app, String idAplicacion, String virtualHost) {
		super(servidor, app);
		this.idAplicacion = idAplicacion;
		this.virtualHost = virtualHost;
	}

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Column(name="APP_NOMBRE_APLICACION", nullable=false, length=30)
	private String idAplicacion;
	
	@Column(name="APP_VIRTUAL_HOST", nullable=false, length=40)
	private String virtualHost;
	

	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public String getIdAplicacion() { return this.idAplicacion; }
	public void setIdAplicacion(String idAplicacion) { this.idAplicacion = idAplicacion; }
	
	public String getVirtualHost() { return this.virtualHost; }
	public void setVirtualHost(String virtualHost) { this.virtualHost = virtualHost; }
}
