package es.nimio.nimiogcs.web.dto.f.directivas;

import java.io.Serializable;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={

		@GrupoDeDatos(
				id="DATOS", 
				nombre="Parámetros de la directiva", 
				orden=1, 
				textoDescripcion="Parámetros o datos que definen la directiva")

       })
public abstract class FormularioBaseDirectiva implements Serializable {

	private static final long serialVersionUID = 341735769727577348L;

	public FormularioBaseDirectiva() {}

	// -----------------------
	
	@Privado
	private String idEntidad;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Directiva")
	@Estatico
	private String directiva;
	
	@Privado
	private boolean esAlta = true;
	
	// -----------------------
	
	public String getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(String idTipo) {
		this.idEntidad = idTipo;
	}

	public String getDirectiva() {
		return directiva;
	}

	public void setDirectiva(String directiva) {
		this.directiva = directiva;
	}
	
	public boolean getEsAlta() {
		return esAlta;
	}
	
	public void setEsAlta(boolean esAlta) {
		this.esAlta = esAlta;
	}

	// -----------------------
	
	/**
	 * Amplía la validación de los datos recogidos más allá de los atributos que se puedean fijar
	 */
	public abstract void validar(IContextoEjecucion ce, Errors errores);
	
	/**
	 * Crea el tipo de entidad a partir de los datos del formulario
	 */
	public abstract DirectivaBase nueva(IContextoEjecucion ce);
	
	/**
	 * Modifica los datos de una directiva a partir de los datos del formulario
	 */
	public abstract void actualiza(IContextoEjecucion ce, DirectivaBase original);
	
	/**
	 * Rellena los dataos del formulario a partir de los datos de la directiva
	 */
	public abstract void datosDesde(DirectivaBase directiva);
}
