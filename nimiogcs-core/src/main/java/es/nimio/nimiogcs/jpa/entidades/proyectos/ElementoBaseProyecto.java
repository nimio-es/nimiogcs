package es.nimio.nimiogcs.jpa.entidades.proyectos;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_PROYECTOS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_ELEMENTO", discriminatorType = DiscriminatorType.STRING, length = 4)
@DiscriminatorValue(value = "XXXX")
public class ElementoBaseProyecto 
	extends MetaRegistro 
	implements EntidadNombrable {

	// --------------------------------------------------------
	// Constantes
	// --------------------------------------------------------
	
	public static final String NOMBRE_CAMPO_NOMBRE = "nombre";
	
	// --------------------------------------------------------
	// Estado
	// --------------------------------------------------------
	
	@Id
	@Column(name = "ID", nullable=false, length=32)
	private String id;
	
	@Column(name = "NOMBRE", nullable=false, length=30)
	private String nombre;

	@Lob
	@Column(name = "ANOTACIONES", nullable=true)
	private String anotaciones;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="ID_PROYECTO", referencedColumnName="ID")	
	private List<UsoYProyeccionProyecto> usosYProyecciones = new ArrayList<UsoYProyeccionProyecto>();
	
	// --------------------------------------------------------
	// Lectura y escritura del estado
	// --------------------------------------------------------

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getAnotaciones() {
		return anotaciones;
	}

	public void setAnotaciones(String anotaciones) {
		this.anotaciones = anotaciones;
	}

	public List<UsoYProyeccionProyecto> getUsosYProyecciones() {
		return usosYProyecciones;
	}
	
	// --------------------------------------------------------
	// ID
	// --------------------------------------------------------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0,32);
	}
}
