package es.nimio.nimiogcs.web.dto.f;

import java.io.Serializable;

import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

/**
 * Formulario general para confirmaciones sin mayor información
 */
@GruposDeDatos(
		grupos={
				@GrupoDeDatos(
						id="C", 
						nombre="Confirmación", 
						orden=1, 
						textoDescripcion="Acción/Operación que se quiere confirmar."
				)
		}
)
public final class PeticionConfirmacionGeneral implements Serializable {

	private static final long serialVersionUID = -5561064826662702527L;

	public PeticionConfirmacionGeneral() {}

	public PeticionConfirmacionGeneral(String control, String accion) {
		this.codigoControl = control;
		this.accionAConfirmar = accion;
	}

	
	@Privado
	private String codigoControl;
	
	@GrupoAsociado(grupoContiene="C", ordenEnGrupo=1)
	@EtiquetaFormulario("Acción a confirmar")
	@Estatico
	private String accionAConfirmar;

	public String getCodigoControl() {
		return codigoControl;
	}

	public void setCodigoControl(String codigoControl) {
		this.codigoControl = codigoControl;
	}

	public String getAccionAConfirmar() {
		return accionAConfirmar;
	}

	public void setAccionAConfirmar(String accionAConfirmar) {
		this.accionAConfirmar = accionAConfirmar;
	}
	
	
}
