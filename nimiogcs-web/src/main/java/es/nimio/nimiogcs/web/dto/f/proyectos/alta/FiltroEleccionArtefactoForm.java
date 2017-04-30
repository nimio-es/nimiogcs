package es.nimio.nimiogcs.web.dto.f.proyectos.alta;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="FILTRO", 
				nombre = "",
				orden=1, 
				textoDescripcion="")
})
public class FiltroEleccionArtefactoForm {

	public FiltroEleccionArtefactoForm() {}

	public FiltroEleccionArtefactoForm(
			String nombre, 
			String tipo, 
			String proyectable, 
			String filtroNombre) {
		
		setNombre(nombre);
		setTipo(tipo);
		setProyectable(proyectable);
		setFiltroNombre(filtroNombre);
	}

	
	// -----------------------------------------
	// Estado
	// -----------------------------------------
	
	@Privado
	private String nombre;

	@Privado
	private String tipo;

	@Privado
	private String proyectable;
	
	@GrupoAsociado(
			grupoContiene="FILTRO",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Nombre artefacto")
	private String filtroNombre;

	// -----------------------------------------
	// Lectura y escritura del estado
	// -----------------------------------------
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getProyectable() {
		return proyectable;
	}

	public void setProyectable(String proyectable) {
		this.proyectable = proyectable;
	}

	public String getFiltroNombre() {
		return filtroNombre;
	}

	public void setFiltroNombre(String filtroNombre) {
		this.filtroNombre = filtroNombre;
	}
	
}
