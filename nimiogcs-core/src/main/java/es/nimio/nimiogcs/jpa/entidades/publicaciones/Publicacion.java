package es.nimio.nimiogcs.jpa.entidades.publicaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;

@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_PUBLICACIONES")
public class Publicacion extends MetaRegistro {

	@Id
	@Column(name="ID", nullable=false, length=36)
	private String id;

	@Column(name="ID_DESTINO_PUBLICACION", nullable=false, length=32)
	private String idDestinoPublicacion;

	@Column(name="ESTADO", nullable=false, length=20)
	private String estado;
	
	@Column(name="CANAL", nullable=false, length=30)
	private String canal;
	
	@Column(name="MARCHA_ATRAS", nullable=false, length=2)
	private String marchaAtras = "NO";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdDestinoPublicacion() {
		return idDestinoPublicacion;
	}

	public void setIdDestinoPublicacion(String idDestinoPublicacion) {
		this.idDestinoPublicacion = idDestinoPublicacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public void enEjecucion() {
		this.setEstado(K.X.EJECUCION);
	}
	
	public void conError() {
		this.setEstado(K.X.ERROR);
	}
	
	public void correcta() {
		this.setEstado(K.X.OK);
	}

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}
	
	public boolean esMarchaAtras() {
		return K.L.desde(marchaAtras);
	}
	
	public void setMarchaAtras(boolean marchaAtras) {
		this.marchaAtras = K.L.para(marchaAtras);
	}
	
	// -------
	// ID
	// -------

	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0,36);
	}
	
}
