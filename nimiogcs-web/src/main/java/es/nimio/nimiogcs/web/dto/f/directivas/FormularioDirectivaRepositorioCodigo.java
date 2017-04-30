package es.nimio.nimiogcs.web.dto.f.directivas;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@EsExtension
public class FormularioDirectivaRepositorioCodigo extends FormularioBaseDirectiva {

	private static final long serialVersionUID = -1287362410337325363L;

	private static final String TEXTO_DIRECTIVA = "Repositorio de versionado de código";

	public FormularioDirectivaRepositorioCodigo() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}

	// ---------------------------------

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Repositorio de código")
	@Seleccion(idColeccion="repositorios")
	@BloqueDescripcion("Repositorio de código donde se gestionará el código fuente asociado al artefacto")
	private String repositorioCodigo;
	
	// ---------------------------------
	
	public String getRepositorioCodigo() {
		return repositorioCodigo;
	}
	
	public void setRepositorioCodigo(String aplicacion) {
		this.repositorioCodigo = aplicacion;
	}
	
	// ---------------------------------

	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// NADA QUE HACER
	}

	@Override
	public DirectivaRepositorioCodigo nueva(IContextoEjecucion ce) {
		DirectivaRepositorioCodigo nueva = new DirectivaRepositorioCodigo();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaRepositorioCodigo o = (DirectivaRepositorioCodigo)original;
		o.setRepositorio(ce.repositorios().findOne(repositorioCodigo));
	}

	@Override
	public void datosDesde(DirectivaBase directiva) {
		this.repositorioCodigo = ((DirectivaRepositorioCodigo)directiva).getRepositorio().getId();
	}
	
}
