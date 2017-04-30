package es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "POS")
public class DependenciaPosicional extends Dependencia {

	public DependenciaPosicional() { super(); }

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Column(name="POSICION", nullable=true)
	private Integer posicion;
	

	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------

	public Integer getPosicion() { return posicion; }
	public void setPosicion(int posicion) { this.posicion = posicion; }
	
	// ---------------------------------------------
	// Equivalencia
	// ---------------------------------------------

	@Override
	public boolean equivalente(Dependencia df) {
		
		boolean mismaPosicion = 
				(
						posicion != null
						&& (df instanceof DependenciaPosicional)
						&& (((DependenciaPosicional)df).posicion != null)
						&& posicion.equals(((DependenciaPosicional)df).posicion)
				) || (
						posicion == null
						&& (df instanceof DependenciaPosicional)
						&& (((DependenciaPosicional)df).posicion == null)
				);
		
		return 
				super.equivalente(df)
				&& mismaPosicion; 
	}
	
	// ---------------------------------------------
	// Actualiza
	// ---------------------------------------------

	@Override
	public void actualizaDesde(Dependencia df) throws ErrorInconsistenciaDatos {
		super.actualizaDesde(df);
		this.posicion = ((DependenciaPosicional)df).posicion;
	}

	// ---------------------------------------------
	// Auto-réplica
	// ---------------------------------------------
	
	@Override
	public DependenciaPosicional reproducir() {
		DependenciaPosicional nueva = (DependenciaPosicional)super.reproducir();
		nueva.posicion = posicion;
		return nueva;
	}
	
}
