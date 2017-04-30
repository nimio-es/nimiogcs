package es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "PROY")
public class RelacionOperacionElementoProyecto extends RelacionOperacion {

	public RelacionOperacionElementoProyecto() { super(); }

	public RelacionOperacionElementoProyecto(Operacion operacion, ElementoBaseProyecto elementoProyecto) { 
		super(operacion);
		this.elementoProyecto = elementoProyecto;
	}

	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private ElementoBaseProyecto elementoProyecto;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public ElementoBaseProyecto getElementoProyecto() {
		return elementoProyecto;
	}

	public void setElementoProyecto(ElementoBaseProyecto elementoProyecto) {
		this.elementoProyecto = elementoProyecto;
	}
	
}
