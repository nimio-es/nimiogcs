package es.nimio.nimiogcs.web.dto.f;

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
public class FiltroListadoParametrosGlobales {
	// -----------------------------------------
	// Estado
	// -----------------------------------------

	@GrupoAsociado(
			grupoContiene="FILTRO",
			ordenEnGrupo=1)
	@EtiquetaFormulario("Identificador")
	private String identificador;
	
	
	@GrupoAsociado(
			grupoContiene="FILTRO",
			ordenEnGrupo=2)
	@EtiquetaFormulario("Descripcion")
	private String descripcion;
			
			
			

	// ------------------------------------------
	// Lectura y escritura del estado
	// ------------------------------------------
			
			
	public String getIdentificador() { return this.identificador; }
	public void setIdentificador(String identificador) { this.identificador = identificador; }
	
	public String getDescripcion() { return this.descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

			

	// ------------------------------------------
	// API fluido
	// ------------------------------------------

	public FiltroListadoParametrosGlobales conIdentificadorSi(boolean siCierto, String identificador) {
		if(siCierto) setIdentificador(identificador);
		return this;
	}		
	
	public FiltroListadoParametrosGlobales conDescripcionSi(boolean siCierto, String descripcion) {
				if(siCierto) setDescripcion(descripcion);
				return this;
			}
}
