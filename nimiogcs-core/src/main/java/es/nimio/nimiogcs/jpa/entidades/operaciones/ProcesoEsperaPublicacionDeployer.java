package es.nimio.nimiogcs.jpa.entidades.operaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("DEPLOYER")
public class ProcesoEsperaPublicacionDeployer extends ProcesoEspera {

	// ---------------------------------------
	// Construcción
	// ---------------------------------------

	public ProcesoEsperaPublicacionDeployer() {
		super();
		
		// en el caso de los procesos deployer ponemos 30 minutos de espera
		setMinutosEspera(30);
	}
	
	public ProcesoEsperaPublicacionDeployer(String idOpDeployer, String entorno, int segundos) {
		super(idOpDeployer, segundos);
		setEntorno(entorno);
	}

	public ProcesoEsperaPublicacionDeployer(String nombre, String idDeployer, String entorno, int segundos) {
		super(nombre, idDeployer, segundos);
		setEntorno(entorno);
	}	

	// ---------------------------------------
	// Estado
	// ---------------------------------------
	
	@Column(name="ENTORNO", nullable=false, length=20)
	private String entorno;
	
	// ---------------------------------------
	// Lectura / escritura del estado
	// ---------------------------------------
	
	public String getEntorno() { return this.entorno; }
	public void setEntorno(String entorno) { this.entorno = entorno; }

}
