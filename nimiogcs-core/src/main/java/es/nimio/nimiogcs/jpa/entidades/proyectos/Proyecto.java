package es.nimio.nimiogcs.jpa.entidades.proyectos;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoProyecto;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("PROY")
public class Proyecto extends ElementoBaseProyecto {

	// ----------------------------------------------
	// Estado
	// ----------------------------------------------

	@Column(name="ESTADO_PROYECTO", length=20, nullable=false)
	@Enumerated(EnumType.STRING)
	private EnumEstadoProyecto estado;
	
	@JoinColumn(name = "ID_POLIVALENTE_1", nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private RepositorioCodigo enRepositorio;

	
	// ----------------------------------------------
	// Lectura / escritura estado
	// ----------------------------------------------

	public EnumEstadoProyecto getEstado() { return estado; }
	
	public void setEstado(EnumEstadoProyecto estado) { this.estado = estado; }
	
	public RepositorioCodigo getEnRepositorio() { return this.enRepositorio; }
	
	public void setEnRepositorio(RepositorioCodigo repositorio) { this.enRepositorio = repositorio; }
	
	public boolean estaCerrado() { return this.estado == EnumEstadoProyecto.Cerrado; }
	
	// ----------------------------------------------
	// Auxiliares
	// ----------------------------------------------

	public boolean enModoAbierto() { return this.getEstado() == EnumEstadoProyecto.Abierto; }
	public boolean enModoCerrado() { return this.getEstado() == EnumEstadoProyecto.Cerrado; }
	public boolean enModoDesconocido() { return this.getEstado() == EnumEstadoProyecto.Desconocido; }
	
	public String getUrlTrabajoCodigo() {
		return this.getEnRepositorio()!=null ?
					new StringBuilder(this.getEnRepositorio().rutaTotalProyectos())
						.append('/')
						.append(this.getNombre().toLowerCase())
						.toString()
			: null;
	}
}
