package es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "MWEB")
public class DependenciaConModuloWeb extends DependenciaPosicional {

	public DependenciaConModuloWeb() { super(); }

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Column(name="CTX_ROOT_MODULO", nullable=false, length=30)
	private String contextRoot;

	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------

	public String getContextRoot() { return this.contextRoot; }
	
	public void setContextRoot(String contextRoot) { this.contextRoot = contextRoot; }
	
	// ---------------------------------------------
	// Equivalencia
	// ---------------------------------------------

	@Override
	public boolean equivalente(Dependencia df) {
		return 
				super.equivalente(df)
				&& this.contextRoot.equalsIgnoreCase(((DependenciaConModuloWeb)df).contextRoot);
	}
	
	// ---------------------------------------------
	// Actualiza
	// ---------------------------------------------

	@Override
	public void actualizaDesde(Dependencia df) throws ErrorInconsistenciaDatos {
		super.actualizaDesde(df);
		this.contextRoot = ((DependenciaConModuloWeb)df).contextRoot;
	}
	
	// ---------------------------------------------
	// Auto-réplica
	// ---------------------------------------------
	
	@Override
	public DependenciaConModuloWeb reproducir() {
		DependenciaConModuloWeb nueva = (DependenciaConModuloWeb) super.reproducir();
		nueva.contextRoot = contextRoot;
		return nueva;
	}
	
}
