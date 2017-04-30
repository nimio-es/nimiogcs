package es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;

/**
 * Relación de un servidor con un archivo pom que hará las veces 
 * de mecanismo de obtención de las partes de una librería compartida.
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "LCJV")
public class RelacionServidorLibreriaCompartida extends RelacionServidorArtefacto {

	public RelacionServidorLibreriaCompartida() { super(); }

	public RelacionServidorLibreriaCompartida(ServidorJava servidor, Artefacto pom, String carpetaServidor) {
		super(servidor, pom);
		this.carpetaServidor = carpetaServidor;
	}

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Column(name="LCJV_CARPETA", nullable=false, length=30)
	private String carpetaServidor;
	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public String getCarpetaServidor() { return this.carpetaServidor; }
	
	public void setCarpetaServidor(String idAplicacion) { this.carpetaServidor = idAplicacion; }
	
}
