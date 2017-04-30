package es.nimio.nimiogcs.jpa.entidades.artefactos.directivas;

import java.util.HashMap;
import java.util.Map;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_DIRECTIVAS")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DIRECTIVA", discriminatorType = DiscriminatorType.STRING, length = 20)
@DiscriminatorValue(value = "DIRECTIVA")
public class DirectivaBase extends MetaRegistro {

	// --------------------------------
	// Estado
	// --------------------------------

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DIRECTIVA", insertable=false, updatable=false)
	private TipoDirectiva directiva;
	
	@Id
	@Column(name="ID", nullable=false, length=36)
	private String id;
	
	// --------------------------------
	// Lectura y escritura del estado
	// --------------------------------

	public TipoDirectiva getDirectiva() {
		return directiva;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	// -------------------------------------
	// Seudoclonación
	// -------------------------------------

	public DirectivaBase replicar() {
		DirectivaBase replica = (DirectivaBase)nuevaInstancia();
		return replica;
	}
	
	// -------------------------------------
	// Método de utilidad para generar
	// un diccionario con todos los valores
	// -------------------------------------

	public Map<String, String> getMapaValores() {
		return new HashMap<String, String>();
	}
	
	// --------------------------------
	// ID
	// --------------------------------

	@Override
	protected String generarIdElemento() {
		// TODO Auto-generated method stub
		return super.generarIdElemento().substring(0,36);
	}
}
