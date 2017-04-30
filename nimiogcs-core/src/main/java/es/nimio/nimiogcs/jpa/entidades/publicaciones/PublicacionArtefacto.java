package es.nimio.nimiogcs.jpa.entidades.publicaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.RegistroConIdCalculado;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_PUBLICACIONES_ARTEFACTOS")
public class PublicacionArtefacto extends RegistroConIdCalculado {

	@Id
	@Column(name="ID", nullable=false, length=36)
	private String id;
	
	@JoinColumn(name="ID_PUBLICACION", nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private Publicacion publicacion;
	
	@JoinColumn(name="ID_ARTEFACTO", nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private Artefacto artefacto;
	
	@Column(name="DIRECTO", nullable=false, length=2)
	private String directo;

	@JoinColumn(name="ID_ARTEFACTO_CONTIENE", nullable=true)
	@ManyToOne(fetch=FetchType.EAGER)
	private Artefacto artefactoContiene;

	@Column(name="ID_ETIQUETA", nullable=true, length=32)
	private String idEtiqueta;

	@Column(name="NOMBRE_ETIQUETA", nullable=false, length=40)
	private String nombreEtiqueta;

	@JoinColumn(name="ID_PROYECTO", nullable=true)
	@ManyToOne(fetch=FetchType.EAGER)
	private Proyecto proyecto;

	// ----
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Publicacion getPublicacion() {
		return publicacion;
	}

	public void setPublicacion(Publicacion publicacion) {
		this.publicacion = publicacion;
	}

	public Artefacto getArtefacto() {
		return artefacto;
	}

	public void setArtefacto(Artefacto artefacto) {
		this.artefacto = artefacto;
	}
	
	public boolean getDirecto() {
		return K.L.desde(directo);
	}

	public void setDirecto(boolean directo) {
		this.directo = K.L.para(directo);
	}
	
	public Artefacto getArtefactoContiene() {
		return artefactoContiene;
	}

	public void setArtefactoContiene(Artefacto artefactoContiene) {
		this.artefactoContiene = artefactoContiene;
	}

	public String getIdEtiqueta() {
		return idEtiqueta;
	}

	public void setIdEtiqueta(String idEtiqueta) {
		this.idEtiqueta = idEtiqueta;
	}

	public String getNombreEtiqueta() {
		return nombreEtiqueta;
	}

	public void setNombreEtiqueta(String nombreEtiqueta) {
		this.nombreEtiqueta = nombreEtiqueta;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}
	
	
	// ----------------
	// ID
	// ----------------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0, 36);
	}
}
