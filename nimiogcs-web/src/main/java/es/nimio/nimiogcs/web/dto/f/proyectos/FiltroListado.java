package es.nimio.nimiogcs.web.dto.f.proyectos;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="FILTRO", 
				nombre = "Parámetros filtrado",
				orden=1, 
				textoDescripcion=""),
})
public class FiltroListado {

	// -----------------------------------------
	// Estado
	// -----------------------------------------

	@GrupoAsociado(
			grupoContiene="FILTRO",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Nombre")
	private String nombre;
	
	// ------------------------------------------
	// Lectura y escritura del estado
	// ------------------------------------------
	
	public String getNombre() { return this.nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	
	// ------------------------------------------
	// API fluido
	// ------------------------------------------

	public FiltroListado conNombreSi(boolean siCierto, String nombre) {
		if(siCierto) setNombre(nombre);
		return this;
	}
}
