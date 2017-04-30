package es.nimio.nimiogcs.jpa.entidades.proyectos;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.K;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("ETQ")
public class EtiquetaProyecto extends ElementoBaseProyecto {

	// ----------------------------------------------
	// Estado
	// ----------------------------------------------

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ID_POLIVALENTE_1", nullable=false)
	private Proyecto proyecto;
	
	@Column(name="CALIDAD_SUPERADA", nullable=true, length=2)
	private String calidadSuperada;
	
	// ----------------------------------------------
	// Lectura / Escritura estado
	// ----------------------------------------------

	@Override
	@Size(min=5, max=60, message="El nombre de una etiqueta debe tener al menos una longitud de 5 caracteres y no superar nunca los 60 caracteres.")
	public String getNombre() {
		return super.getNombre();
	}
	
	public Proyecto getProyecto() {
		return proyecto;
	}
	
	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}
	
	public Boolean getCalidadSuperada() {
		return calidadSuperada != null ? K.L.desde(calidadSuperada) : null;
	}
	
	public void setCalidadSuperada(boolean calidad) {
		calidadSuperada = K.L.para(calidad);
	}
	
	public boolean calidadSuperada() {
		return K.L.SI.equalsIgnoreCase(calidadSuperada);
	}
	
	public boolean calidadPendiente() {
		return !calidadSuperada() 
				&& ! K.L.NO.equalsIgnoreCase(calidadSuperada);
	}
}
