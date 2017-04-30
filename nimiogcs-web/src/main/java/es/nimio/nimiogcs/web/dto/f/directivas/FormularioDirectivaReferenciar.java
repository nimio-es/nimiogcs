package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.Pattern;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaReferenciar;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;

@EsExtension
public class FormularioDirectivaReferenciar extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = 2522388862035447295L;

	private static final String TEXTO_DIRECTIVA = "Lista y forma en que se pueden referenciar otros tipos de artefactos por éste.";

	public FormularioDirectivaReferenciar() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Posibles con posición")
	@Placeholder("Valores separados por coma, p.e. 'LIBJAVA,WAR,POM,...'")
	@BloqueDescripcion("Lista separada por comas de los identificadores de tipo de aquellos tipos de artefactos que se podrán relacionar con éste usando un tipo de dependencia donde el único valor a tener en cuenta es la posición.")
	private String posiblesPosicional;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Posibles con alcance")
	@Placeholder("Valores separados por coma, p.e. 'LIBJAVA,WAR,POM,...'")
	@BloqueDescripcion("Lista separada por comas de los identificadores de tipo de aquellos tipos de artefactos que se podrán relacionar con éste usando un tipo de dependencia donde se solicitará el alcance, además de tener en cuenta la posición.")
	private String posiblesAlcance;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=4)
	@EtiquetaFormulario("Posibles tipo módulo web")
	@Placeholder("Valores separados por coma, p.e. 'LIBJAVA,WAR,POM,...'")
	@BloqueDescripcion("Lista separada por comas de los identificadores de tipo de aquellos tipos de artefactos que se podrán relacionar con éste usando un tipo de dependencia donde se solicitará el contexto raíz, además de tener en cuenta la posición.")
	private String posiblesWeb;

	
	// -------------------

	@Pattern(regexp="^[A-Z\\,\\-]*$", message="El formato debe ser de una lista de identificadores separados por comas")
	public String getPosiblesPosicional() {
		return posiblesPosicional;
	}

	public void setPosiblesPosicional(String posiblesPosicional) {
		this.posiblesPosicional = posiblesPosicional;
	}

	@Pattern(regexp="^[A-Z\\,\\-]*$", message="El formato debe ser de una lista de identificadores separados por comas")
	public String getPosiblesAlcance() {
		return posiblesAlcance;
	}

	public void setPosiblesAlcance(String posiblesAlcance) {
		this.posiblesAlcance = posiblesAlcance;
	}

	@Pattern(regexp="^[A-Z\\,\\-]*$", message="El formato debe ser de una lista de identificadores separados por comas")
	public String getPosiblesWeb() {
		return posiblesWeb;
	}

	public void setPosiblesWeb(String posiblesWeb) {
		this.posiblesWeb = posiblesWeb;
	}


	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// todos los identificadores de tipo deben existir
		validarLista(posiblesPosicional, "posiblesPosicional", ce, errores);
		validarLista(posiblesAlcance, "posiblesAlcance", ce, errores);
		validarLista(posiblesWeb, "posiblesWeb", ce, errores);
	}
	
	private void validarLista(String lista, String campo, IContextoEjecucion ce, Errors errores) {
		// cogemos cada elemento de la lista y confirmamos que exista
		for(String id: lista.split(",")) {
			if(Strings.isNullOrEmpty(id)) continue;
			if(ce.tipos().findOne(id)==null)
				errores.rejectValue(campo, "TIPO-NO-EXISTE", "El tipo '" + id + "' no existe");
		}
	}
	
	@Override
	public DirectivaReferenciar nueva(IContextoEjecucion ce) {
		DirectivaReferenciar nueva = new DirectivaReferenciar();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaReferenciar o = (DirectivaReferenciar)original;
		o.setPosiblesPosicional(posiblesPosicional.split(","));
		o.setPosiblesAlcance(posiblesAlcance.split(","));
		o.setPosiblesWeb(posiblesWeb.split(","));
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {

		DirectivaReferenciar d = (DirectivaReferenciar)directiva;
		setPosiblesPosicional(join(d.getPosiblesPosicional()));
		setPosiblesAlcance(join(d.getPosiblesAlcance()));
		setPosiblesWeb(join(d.getPosiblesWeb()));
	}
	
	private String join(String[] datos) {
		String r = "";
		for(String d: datos)
			r += r.length() > 0 ? "," + d : d;
		return r;
	}
}
