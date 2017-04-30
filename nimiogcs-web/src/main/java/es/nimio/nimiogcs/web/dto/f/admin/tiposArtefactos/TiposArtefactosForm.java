package es.nimio.nimiogcs.web.dto.f.admin.tiposArtefactos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.NoEditable;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={

		@GrupoDeDatos(
				id="DATOS", 
				nombre="Globales", 
				orden=1, 
				textoDescripcion="Datos Globales.")
       })
public class TiposArtefactosForm {
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Identificador")
	@BloqueDescripcion("Introduzca el identificador del tipo del artefacto")
	@NoEditable
	private String id;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Nombre")
	@BloqueDescripcion("Nombre del tipo del artefacto")
	private String nombre;

	@Privado
	private boolean deUsuario = true;
	
	
	@NotNull(message="Por favor,el identificador de los tipos de artefacto no puede ir vacio")
	@Size(min=3, max=15, message="El tamaño para el valor del identificador debe estar comprendido entre 3 y 15 caracteres")
	public String getId(){
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	

	@NotNull(message="Por favor, dedique unas palabras para indicar el nombre del tipo de artefacto")
	@Size(min=5, max=60, message="El tamaño para el valor del nombre del tipo debe estar comprendido entre 5 y 60 caracteres")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public boolean isDeUsuario() {
		return deUsuario;
	}

	public void setDeUsuario(boolean deUsuario) {
		this.deUsuario = deUsuario;
	}

	public TiposArtefactosForm desde(TipoArtefacto datos) {
		
		this.id = datos.getId();
		this.nombre= datos.getNombre();
		this.deUsuario = datos.getDeUsuario();
		
		return this;
	}	
}
