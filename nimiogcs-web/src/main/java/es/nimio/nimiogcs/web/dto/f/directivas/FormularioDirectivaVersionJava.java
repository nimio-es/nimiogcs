package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaVersionJava;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;

@EsExtension
public class FormularioDirectivaVersionJava extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = -8911653494697434513L;

	private static final String TEXTO_DIRECTIVA = "Versiones de la máquina virtual Java que se emplearán para compilar y como máquina destino.";

	public FormularioDirectivaVersionJava() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Versión compilación")
	@Placeholder("Versión de compilación de la JVM, p.e. 1.6")
	@BloqueDescripcion("Versión de compilación de la máquina virtual java que se usará en la construcción. Los valores actualmnente admitidos son '1.5', '1.6', '1.7' y '1.8'")
	private String versionCompila;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Versión destino")
	@Placeholder("Versión destino de la JVM, p.e. 1.6")
	@BloqueDescripcion("Versión destino o de tiempo de ejecución de la máquina virtual java que se usará en la construcción. Los valores actualmnente admitidos son '1.5', '1.6', '1.7' y '1.8'")
	private String versionDestino;


	// -------------------
	
	@Size(min=3, max=3, message="Los valores permitidos para la versión de compilación son '1.5', '1.6', '1.7', '1.8'")
	@Pattern(regexp="1.5|1.6|1.7|1.8", message="Los valores permitidos para la versión de compilación son '1.5', '1.6', '1.7', '1.8'")
	public String getVersionCompila() {
		return versionCompila;
	}

	public void setVersionCompila(String versionCompila) {
		this.versionCompila = versionCompila;
	}

	@Size(min=3, max=3, message="Los valores permitidos para la versión destino son '1.5', '1.6', '1.7', '1.8'")
	@Pattern(regexp="1.5|1.6|1.7|1.8", message="Los valores permitidos para la versión destino son '1.5', '1.6', '1.7', '1.8'")
	public String getVersionDestino() {
		return versionDestino;
	}

	public void setVersionDestino(String versionDestino) {
		this.versionDestino = versionDestino;
	}

	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		int c = Integer.parseInt(versionCompila.replace(".", ""));
		int d = Integer.parseInt(versionDestino.replace(".", ""));
		if(c>d) 
			errores.rejectValue("versionDestino", "DEBE-SER-MAYOR", "La versión destino debe ser mayor o igual a la de compilación.");
	}

	@Override
	public DirectivaVersionJava nueva(IContextoEjecucion ce) {
		DirectivaVersionJava nueva = new DirectivaVersionJava();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaVersionJava o = (DirectivaVersionJava)original;
		o.setVersionCompila(versionCompila);
		o.setVersionDestino(versionDestino);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		
		DirectivaVersionJava d = (DirectivaVersionJava)directiva;
		setVersionCompila(d.getVersionCompila());
		setVersionDestino(d.getVersionDestino());
	}
}
