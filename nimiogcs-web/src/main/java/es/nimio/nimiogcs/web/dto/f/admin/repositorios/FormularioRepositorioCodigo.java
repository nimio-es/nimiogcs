package es.nimio.nimiogcs.web.dto.f.admin.repositorios;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Checkbox;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Secreto;

@GruposDeDatos(
		grupos = {
				@GrupoDeDatos(
					id="DATOS", 
					nombre="Datos principales", 
					orden=1, 
					textoDescripcion=""
				),
				@GrupoDeDatos(
						id="USOS", 
						nombre="Destino o uso del repositorio", 
						orden=2, 
						textoDescripcion="Un repositorio puede emplearse para código y/o para mapeado de proyectos."
				),
				@GrupoDeDatos(
						id="RUTASCODIGO", 
						nombre="Estructura de carpetas del repositorio (para código)", 
						orden=3, 
						textoDescripcion="Carpetas y la estructura donde se distribuirán los elementos según el uso de código fuente de los artefactos."
				),
				@GrupoDeDatos(
						id="RUTASPROY", 
						nombre="Estructura de carpetas del repositorio (para proyectos)", 
						orden=4, 
						textoDescripcion="Carpetas y la estructura donde se distribuirán los elementos según el uso de proyección de los proyectos."
				),
				@GrupoDeDatos(
						id="SEC", 
						nombre="Seguridad", 
						orden=5, 
						textoDescripcion="Control de acceso. Usuario que podrá crear/eliminar cualquier carpeta de la estructura."
				)
		}
)
public class FormularioRepositorioCodigo {

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@Privado
	private String id;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Identificador")
	@BloqueDescripcion("Nombre o identificación con la que distinguir el repositorio.")
	private String nombre;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Url de acceso")
	@BloqueDescripcion("Url base del repositorio.")
	private String uriRaizRepositorio;

	@GrupoAsociado(grupoContiene="USOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Para código")
	@Checkbox
	@BloqueDescripcion("El repositorio se empleará para almacenar el código fuente de los artefactos que se configuran con uso de repositorio.")
	private boolean paraCodigo;

	@GrupoAsociado(grupoContiene="USOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Para proyectos")
	@Checkbox
	@BloqueDescripcion("El repositorio se empleará para la proyección de las estructura de proyecto.")
	private boolean paraProyectos;
	
	@GrupoAsociado(grupoContiene="RUTASCODIGO", ordenEnGrupo=1)
	@EtiquetaFormulario("Raíz taxonomía artefatos")
	@Placeholder("p.e. artefactos")
	@BloqueDescripcion("Raíz (partiendo de la url del repositorio) donde se registrará la estructura de taxonomías de los artefactos. "
			+ "Aplica cuando el repositorio da soporte al código fuente.")
	private String subrutaEstables;
	
	@GrupoAsociado(grupoContiene="RUTASCODIGO", ordenEnGrupo=2)
	@EtiquetaFormulario("Raíz ramificación artefactos")
	@Placeholder("p.e. desarrollo")
	@BloqueDescripcion("Raíz (partiendo de la url del repositorio) donde se crearán las ramificación de los artefactos. "
			+ "Aplica cuando el repositorio da soporte al código fuente.")
	private String subrutaDesarrollo;
	
	@GrupoAsociado(grupoContiene="RUTASCODIGO", ordenEnGrupo=3)
	@EtiquetaFormulario("Carpeta estables")
	@Placeholder("p.e. trunk o estable")
	@BloqueDescripcion("Nombre de la carpeta donde se registrará y mantendrá el código estable del artefacto.")
	private String carpetaEstables;

	@GrupoAsociado(grupoContiene="RUTASCODIGO", ordenEnGrupo=5)
	@EtiquetaFormulario("Carpeta ramas")
	@Placeholder("p.e. branches, trabajo o ramas")
	@BloqueDescripcion("Nombre de la carpeta donde se crearán las ramas de trabajo. A tener en cuenta que se concatenará a la subruta de ramificación definida antes.")
	private String carpetaRamas;
	
	@GrupoAsociado(grupoContiene="RUTASCODIGO", ordenEnGrupo=4)
	@EtiquetaFormulario("Carpeta etiquetado")
	@Placeholder("p.e. tags, etiquetas o marcas")
	@BloqueDescripcion("Nombre de la carpeta donde se registrarán y mantendrán las etiquetas de los proyectos.")
	private String carpetaEtiquetas;
	
	@GrupoAsociado(grupoContiene="RUTASPROY", ordenEnGrupo=6)
	@EtiquetaFormulario("Subruta de proyección")
	@Placeholder("p.e. usos/dsr/proy")
	@BloqueDescripcion("Subruta, partiendo de la raíz del repositorio, donde se crearán las estructuras de proyección de trabajo. Solo si el repositorio es de uso para proyectos.")
	private String subrutaProyectos;
	
	@GrupoAsociado(grupoContiene="RUTASPROY", ordenEnGrupo=7)
	@EtiquetaFormulario("Subruta para publicación")
	@Placeholder("p.e. usos/pblc")
	@BloqueDescripcion("Subruta, partiendo de la raíz del repositorio, donde se crearán las estructuras de soporte a las etiquetas que se utilizarán para la publicación.")
	private String subrutaPublicacion;

	@GrupoAsociado(grupoContiene="SEC", ordenEnGrupo=1)
	@EtiquetaFormulario("Nombre de usuario")
	@BloqueDescripcion("El usuario elegido debe tener permisos de administración (crear, modificar y eliminar) en las rutas que se hayan indicado anteriormente.")
	private String administrador;
	
	@GrupoAsociado(grupoContiene="SEC", ordenEnGrupo=2)
	@EtiquetaFormulario("Contraseña")
	@Secreto
	private String password;

	// -----
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@NotNull(message="Debe indicar un nombre")
	@Size(min=1, max=30, message="Debe indicara un nombre y no debe superar los treinta caracteres.")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@NotNull(message="Debe indicar la URL de acceso al repositorio.")
	@Size(min=1, max=200, message="Debe indicara la URL de acceso al repositorio. Máximo 200 caracteres.")
	public String getUriRaizRepositorio() {
		return uriRaizRepositorio;
	}

	public void setUriRaizRepositorio(String uriRaizRepositorio) {
		this.uriRaizRepositorio = uriRaizRepositorio;
	}

	public boolean isParaCodigo() {
		return paraCodigo;
	}

	public void setParaCodigo(boolean paraCodigo) {
		this.paraCodigo = paraCodigo;
	}

	public boolean isParaProyectos() {
		return paraProyectos;
	}

	public void setParaProyectos(boolean paraProyectos) {
		this.paraProyectos = paraProyectos;
	}

	public String getSubrutaEstables() {
		return subrutaEstables;
	}

	public void setSubrutaEstables(String subrutaEstables) {
		this.subrutaEstables = subrutaEstables;
	}

	public String getSubrutaDesarrollo() {
		return subrutaDesarrollo;
	}

	public void setSubrutaDesarrollo(String subrutaDesarrollo) {
		this.subrutaDesarrollo = subrutaDesarrollo;
	}

	public String getCarpetaEstables() {
		return carpetaEstables;
	}

	public void setCarpetaEstables(String carpetaEstables) {
		this.carpetaEstables = carpetaEstables;
	}

	public String getCarpetaEtiquetas() {
		return carpetaEtiquetas;
	}

	public void setCarpetaEtiquetas(String carpetaEtiquetas) {
		this.carpetaEtiquetas = carpetaEtiquetas;
	}

	public String getCarpetaRamas() {
		return carpetaRamas;
	}

	public void setCarpetaRamas(String carpetaRamas) {
		this.carpetaRamas = carpetaRamas;
	}

	public String getSubrutaProyectos() {
		return subrutaProyectos;
	}

	public void setSubrutaProyectos(String subrutaProyectos) {
		this.subrutaProyectos = subrutaProyectos;
	}

	public String getSubrutaPublicacion() {
		return subrutaPublicacion;
	}

	public void setSubrutaPublicacion(String subrutaPublicacion) {
		this.subrutaPublicacion = subrutaPublicacion;
	}

	@NotNull(message="Debe indicar el usuario con permisos de administración.")
	@Size(min=1, message="Debe indicar el usuario con permisos de administración.")
	public String getAdministrador() {
		return administrador;
	}

	public void setAdministrador(String administrador) {
		this.administrador = administrador;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	// -----

	public void validate(IContextoEjecucion ce, Errors errores)  {
		
		if(!isParaCodigo() && !isParaProyectos()) {
			errores.rejectValue("paraCodigo", "UN_USO", "Debe establecer al menos un uso");
			errores.rejectValue("paraProyectos", "UN_USO", "Debe establecer al menos un uso");
		}
		
	}
	
	public void fillEntity(RepositorioCodigo repositorio) {
		repositorio.setNombre(nombre);
		repositorio.setUriRaizRepositorio(uriRaizRepositorio);
		repositorio.setParaCodigo(paraCodigo);
		repositorio.setParaProyectos(paraProyectos);
		repositorio.setSubrutaEstables(subrutaEstables);
		repositorio.setSubrutaDesarrollo(subrutaDesarrollo);
		repositorio.setCarpetaEstables(carpetaEstables);
		repositorio.setCarpetaEtiquetas(carpetaEtiquetas);
		repositorio.setCarpetaRamas(carpetaRamas);
		repositorio.setSubrutaProyectos(subrutaProyectos);
		repositorio.setSubrutaPublicacion(subrutaPublicacion);
		repositorio.setAdministrador(administrador);
		repositorio.setPassword(password);
	}
	
	public void fromEntity(RepositorioCodigo repositorio) {
		id = repositorio.getId();
		nombre = repositorio.getNombre();
		uriRaizRepositorio = repositorio.getUriRaizRepositorio();
		paraCodigo = repositorio.getParaCodigo();
		paraProyectos = repositorio.getParaProyectos();
		subrutaEstables = repositorio.getSubrutaEstables();
		subrutaDesarrollo = repositorio.getSubrutaDesarrollo();
		carpetaEstables = repositorio.getCarpetaEstables();
		carpetaEtiquetas = repositorio.getCarpetaEtiquetas();
		carpetaRamas = repositorio.getCarpetaRamas();
		subrutaProyectos = repositorio.getSubrutaProyectos();
		subrutaPublicacion = repositorio.getSubrutaPublicacion();
		administrador = repositorio.getAdministrador();
		password = repositorio.getPassword();
	}
	
}
