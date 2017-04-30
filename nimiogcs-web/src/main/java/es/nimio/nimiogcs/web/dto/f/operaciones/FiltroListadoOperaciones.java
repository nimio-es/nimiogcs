package es.nimio.nimiogcs.web.dto.f.operaciones;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;


@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="FILTRO", 
				nombre = "",
				orden=1, 
				textoDescripcion=""),
})
public class FiltroListadoOperaciones {

	// -----------------------------------------
		// Estado
		// -----------------------------------------

		@GrupoAsociado(
				grupoContiene="FILTRO",
				ordenEnGrupo=2)
		@EtiquetaFormulario("Descripcion")
		private String descripcion;
		
		@GrupoAsociado(
				grupoContiene="FILTRO",
				ordenEnGrupo=3)
		@EtiquetaFormulario("Tipo")
		private String tipo;
		
		@GrupoAsociado(
				grupoContiene="FILTRO",
				ordenEnGrupo=4)
		@EtiquetaFormulario("Estado")
		@Seleccion(idColeccion="estados")
		private String estado;
		
		@GrupoAsociado(
				grupoContiene="FILTRO",
				ordenEnGrupo=4)
		@EtiquetaFormulario("Usuario")
		private String usuario;
		
		// ------------------------------------------
		// Lectura y escritura del estado
		// ------------------------------------------
		
		public String getDescripcion() { return this.descripcion; }
		public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
		
		public String getTipo() { return this.tipo; }
		public void setTipo(String tipo) { this.tipo = tipo; }
		
		public String getEstado() { return this.estado; }
		public void setEstado(String estado) { this.estado = estado; }
		
		public String getUsuario() { return this.usuario; }
		public void setUsuario(String usuario) { this.usuario = usuario; }
		
		// ------------------------------------------
		// API fluido
		// ------------------------------------------

		public FiltroListadoOperaciones conDescripcionSi(boolean siCierto, String descripcion) {
			if(siCierto) setDescripcion(descripcion);
			return this;
		}
		public FiltroListadoOperaciones conTipoSi(boolean siCierto, String tipo) {
			if(siCierto) setTipo(tipo);
			return this;
		}
		public FiltroListadoOperaciones conEstadoSi(boolean siCierto, String estado) {
			if(siCierto) setEstado(estado);
			return this;
		}
		public FiltroListadoOperaciones conUsuarioSi(boolean siCierto, String usuario) {
			if(siCierto) setUsuario(usuario);
			return this;
		}
}
