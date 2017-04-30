package es.nimio.nimiogcs.jpa.entidades.sistema;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.K;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.modelo.EntidadNombrable;

/**
 * Representa la información de un repositorio de código.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "GCS_REPOSITORIOS_CODIGO")
public class RepositorioCodigo 
	extends MetaRegistro 
	implements EntidadNombrable {

	// -----------------------------------------------
	// Estado
	// -----------------------------------------------

	@Id
	@Column(name = "ID", nullable=false, length=32)
	private String id;
	
	@Column(name = "NOMBRE", nullable=false, length=30)
	private String nombre;

	@Column(name="URI_BASE", nullable=false, length=200)
	private String uriRaizRepositorio;

	@Column(name="PARA_CODIGO", nullable=false, length=2)
	private String paraCodigo = K.L.SI;  // por defecto siempre será para repositar código
	
	@Column(name="SUBRUTA_ESTABLES", nullable=true, length=60)
	private String subrutaEstables;
	
	@Column(name="SUBRUTA_DESARROLLO", nullable=true, length=60)
	private String subrutaDesarrollo;
	
	@Column(name="CARPETA_ESTABLES", nullable=true, length=20)
	private String carpetaEstables;

	@Column(name="CARPETA_ETIQUETAS", nullable=true, length=20)
	private String carpetaEtiquetas;
	
	@Column(name="CARPETA_RAMAS", nullable=true, length=20)
	private String carpetaRamas;
	
	@Column(name="PARA_PROYECTOS", nullable=false, length=2)
	private String paraProyectos = K.L.SI;  // por defecto siempre será para usar en proyectos
	
	@Column(name="SUBRUTA_PROYECTOS", nullable=true, length=60)
	private String subrutaProyectos;
	
	@Column(name="SUBRUTA_PUBLICACION", nullable=true, length=60)
	private String subrutaPublicacion;
	
	@Column(name="USUARIO_ADMINISTRADOR", nullable=false, length=20)
	private String administrador;
	
	@Column(name="PASSWORD_ADMINISTRADOR", nullable=false, length=30)
	private String password;

	// -----------------------------------------------
	// Lectura / Escritura de estado
	// -----------------------------------------------

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@NotNull
	@Size(max=200)
	public String getUriRaizRepositorio() {
		return uriRaizRepositorio;
	}

	public void setUriRaizRepositorio(String uriRaizRepositorio) {
		this.uriRaizRepositorio = uriRaizRepositorio;
	}

	@NotNull
	public Boolean getParaCodigo() { return K.L.desde(this.paraCodigo); }
	
	public void setParaCodigo(Boolean valor) { this.paraCodigo = K.L.para(valor); }
	
	@Size(max=60)
	public String getSubrutaEstables() {
		return subrutaEstables;
	}

	public void setSubrutaEstables(String subrutaEstables) {
		this.subrutaEstables = subrutaEstables;
	}

	@Size(max=60)
	public String getSubrutaDesarrollo() {
		return subrutaDesarrollo;
	}

	public void setSubrutaDesarrollo(String subrutaDesarrollo) {
		this.subrutaDesarrollo = subrutaDesarrollo;
	}

	public String getCarpetaEstables() { return this.carpetaEstables; }
	
	public void setCarpetaEstables(String carpeta) { this.carpetaEstables = carpeta; }
	
	public String getCarpetaEtiquetas() { return this.carpetaEtiquetas; }
	
	public void setCarpetaEtiquetas(String carpeta) { this.carpetaEtiquetas = carpeta; }
	
	public String getCarpetaRamas() { return this.carpetaRamas; }
	
	public void setCarpetaRamas(String carpeta) { this.carpetaRamas = carpeta; }
	
	@NotNull
	public Boolean getParaProyectos() { return K.L.desde(this.paraProyectos); }
	
	public void setParaProyectos(Boolean valor) { this.paraProyectos = K.L.para(valor); }
	
	@Size(max=60)
	public String getSubrutaProyectos() {
		return subrutaProyectos;
	}

	public void setSubrutaProyectos(String subrutaUsos) {
		this.subrutaProyectos = subrutaUsos;
	}
	
	@Size(max=60)
	public String getSubrutaPublicacion() { return this.subrutaPublicacion; }
	
	public void setSubrutaPublicacion(String subruta) { this.subrutaPublicacion = subruta; }

	@NotNull
	@Size(min=3, max=20)
	public String getAdministrador() {
		return administrador;
	}

	public void setAdministrador(String administrador) {
		this.administrador = administrador;
	}

	@NotNull
	@Size(min=6,max=30)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// -----------------------------------------------------------------------
	// ID
	// -----------------------------------------------------------------------
	
	@Override
	protected String generarIdElemento() {
		return super.generarIdElemento().substring(0,32);
	}
	
	// -----------------------------------------------------------------------
	// Mecanismos de utilidad para ubicar un artefacto dentro del repositorio
	// -----------------------------------------------------------------------
	
	// -------- totales fijas (que no dependen del artefacto) -------
	
	/**
	 * Ruta que incluye tanto la raíz como la subruta a los estables.
	 */
	public String rutaTotalEstables() { 
		return 
			new StringBuilder(this.getUriRaizRepositorio())
				.append('/')
				.append(this.getSubrutaEstables())
				.toString(); }

	/**
	 * Ruta que incluye tanto la raíz como la subruta de desarrollo.
	 * @return
	 */
	public String rutaTotalDesarrollo() { 
		return 
				new StringBuilder(this.getUriRaizRepositorio())
					.append('/')
					.append(this.getSubrutaDesarrollo())
					.toString(); 
	}

	/**
	 * Ruta que incluye tanto la raíz como la subruta de proyectos.
	 */
	public String rutaTotalProyectos() { 
		return 
				new StringBuilder(this.getUriRaizRepositorio())
					.append('/')
					.append(this.getSubrutaProyectos())
					.toString(); 
	}
	
	/**
	 * Ruta de uso para las publicaciones
	 */
	public String rutaTotalPublicacion() { 
		return 
				new StringBuilder(this.getUriRaizRepositorio())
					.append('/')
					.append(this.getSubrutaPublicacion())
					.toString(); 
	}
	
	/**
	 * Ruta de uso para las publicaciones de marcas.
	 */
	public String rutaTotalPublicacionMarcas() { 
		return 
				new StringBuilder(rutaTotalPublicacion())
					.append('/')
					.append(getCarpetaEtiquetas())
					.toString();
	}
}
