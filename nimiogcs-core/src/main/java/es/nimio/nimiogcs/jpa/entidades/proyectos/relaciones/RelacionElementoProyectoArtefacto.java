package es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;

/**
 * Relación de un proyecto con un artefacto v2
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_PROYECTOS_RELACIONES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_RELACION", discriminatorType = DiscriminatorType.STRING, length = 4)
@DiscriminatorValue(value = "ARTF")
public class RelacionElementoProyectoArtefacto extends MetaRegistro {

	public RelacionElementoProyectoArtefacto() { super(); }

	public RelacionElementoProyectoArtefacto(ElementoBaseProyecto elementoProyecto, Artefacto artefacto) { 
		this.elementoProyecto = elementoProyecto;
		this.artefacto = artefacto;
	}
	
	// ---------------------------------------------
	// Estado
	// ---------------------------------------------
	
	@Id
	@Column(name="ID", nullable=false, length=36)
	private String id;
	
	@JoinColumn(name="ORIGEN", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private ElementoBaseProyecto elementoProyecto;
	
	@JoinColumn(name="DESTINO", nullable=false)
	@OneToOne(fetch=FetchType.EAGER)
	private Artefacto artefacto;

	
	// ---------------------------------------------
	// Lectura y escritura estado
	// ---------------------------------------------
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public ElementoBaseProyecto getElementoProyecto() {
		return elementoProyecto;
	}

	public void setElementoProyecto(ElementoBaseProyecto proyecto) {
		this.elementoProyecto = proyecto;
	}

	public Artefacto getArtefacto() {
		return artefacto;
	}

	public void setArtefacto(Artefacto artefacto) {
		this.artefacto = artefacto;
	}

	
	// ---------------------------------------------
	// ID
	// ---------------------------------------------
	
	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 36);
	}

}
