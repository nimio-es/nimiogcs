package es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "ALC")
public class DependenciaConAlcance extends DependenciaPosicional {

	public DependenciaConAlcance() { super(); }

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Column(name="ALCANCE_ELEGIDO", length=30, nullable=false)
	@Enumerated(EnumType.STRING)
	private EnumAlcanceDependencia alcanceElegido;

	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------

	public EnumAlcanceDependencia getAlcanceElegido() {
		return alcanceElegido;
	}

	public void setAlcanceElegido(EnumAlcanceDependencia alcanceElegido) {
		this.alcanceElegido = alcanceElegido;
	}

	// ---------------------------------------------
	// Equivalencia
	// ---------------------------------------------

	@Override
	public boolean equivalente(Dependencia df) {
		return 
				super.equivalente(df)
				&& this.alcanceElegido == ((DependenciaConAlcance)df).alcanceElegido;
	}

	// ---------------------------------------------
	// Actualiza
	// ---------------------------------------------

	@Override
	public void actualizaDesde(Dependencia df) throws ErrorInconsistenciaDatos {
		super.actualizaDesde(df);
		this.alcanceElegido = ((DependenciaConAlcance)df).alcanceElegido;
	}
	
	// ---------------------------------------------
	// Auto-réplica
	// ---------------------------------------------
	
	@Override
	public DependenciaConAlcance reproducir() {
		DependenciaConAlcance nueva = (DependenciaConAlcance) super.reproducir();
		nueva.alcanceElegido = alcanceElegido;
		return nueva;
	}
	
}
