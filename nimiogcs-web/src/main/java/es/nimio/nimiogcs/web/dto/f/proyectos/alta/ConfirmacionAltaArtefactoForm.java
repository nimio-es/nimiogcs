package es.nimio.nimiogcs.web.dto.f.proyectos.alta;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="CONFIRMACION", 
				nombre = "Datos a confirmar",
				orden=1, 
				textoDescripcion="Confirmación de los datos para el alta del proyecto.")
})
public class ConfirmacionAltaArtefactoForm {

	// ------------------------------
	// Estado
	// ------------------------------
	
	@GrupoAsociado(
			grupoContiene="CONFIRMACION",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Nombre")
	@Estatico
	private String nombre;
	
	@GrupoAsociado(
			grupoContiene="CONFIRMACION",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Tipo uso inicial")
	@Estatico
	private String proyeccion;

	@Privado
	private String idRepositorio;
	
	@GrupoAsociado(
			grupoContiene="CONFIRMACION",
			ordenEnGrupo=3)
	@EtiquetaFormulario("Repositorio de proyecto")
	@Estatico
	private String nombreRepositorio;
	
	@Privado
	private String idArtefacto;
	
	@GrupoAsociado(
			grupoContiene="CONFIRMACION",
			ordenEnGrupo=4)
	@EtiquetaFormulario("Primer artefacto")
	@Estatico
	private String nombreArtefacto;

	// ------------------------------
	// Lectura y escritura del estado
	// ------------------------------
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getProyeccion() {
		return proyeccion;
	}

	public void setProyeccion(String proyeccion) {
		this.proyeccion = proyeccion;
	}

	public String getIdRepositorio() {
		return idRepositorio;
	}

	public void setIdRepositorio(String idRepositorio) {
		this.idRepositorio = idRepositorio;
	}

	public String getNombreRepositorio() {
		return nombreRepositorio;
	}

	public void setNombreRepositorio(String nombreRepositorio) {
		this.nombreRepositorio = nombreRepositorio;
	}

	public String getIdArtefacto() {
		return idArtefacto;
	}

	public void setIdArtefacto(String idArtefacto) {
		this.idArtefacto = idArtefacto;
	}

	public String getNombreArtefacto() {
		return nombreArtefacto;
	}

	public void setNombreArtefacto(String nombreArtefacto) {
		this.nombreArtefacto = nombreArtefacto;
	}
	
	
	// ------------------------------
	// Construcción
	// ------------------------------
	
	public ConfirmacionAltaArtefactoForm() {}
	
	public ConfirmacionAltaArtefactoForm(
			String nombre, 
			String proyeccion, 
			String idRepositorio,
			String nombreRepositorio,
			String idArtefacto, 
			String nombreArtefacto) {
		
		setNombre(nombre);
		setProyeccion(proyeccion);
		setIdRepositorio(idRepositorio);
		setNombreRepositorio(nombreRepositorio);
		setIdArtefacto(idArtefacto);
		setNombreArtefacto(nombreArtefacto);
	}
	
}
