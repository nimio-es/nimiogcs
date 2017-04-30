package es.nimio.nimiogcs.jpa.entidades.sistema.servidores;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.enumerados.EnumTipoServidorJava;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("SRVJ")
public class ServidorJava extends Servidor {

	// ----------------------------------------------------
	// Estado
	// ----------------------------------------------------
	
	@Column(name="STR_POLIVALENTE_1", nullable=false)
	@Enumerated(EnumType.STRING)
	private EnumTipoServidorJava tipoServidor;
	
	// ----------------------------------------------------
	// Leer / Escribir estado
	// ----------------------------------------------------
	
	@Override
	@NotNull
	@Size(min=3, max=20, message="Los tamaños mínimo y máximo para el nombre de un servidor deben ser 3 y 20, respectivamente.")
	@Pattern(regexp = "[A-Za-z0-9]+", message="El nombre del servidor solamente acepta caracteres alfanuméricos.")
	public String getNombre() {
		return super.getNombre();
	}
	
	public EnumTipoServidorJava getTipoServidor() { 
		return this.tipoServidor; 
	}
	
	public void setTipoServidor(EnumTipoServidorJava tipoServidor) { 
		this.tipoServidor = tipoServidor; 
	}
	
}
