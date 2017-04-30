package es.nimio.nimiogcs.web.dto.f.directivas;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Checkbox;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;

@EsExtension
public class FormularioDirectivaCaracterizacion extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = 5167880958035781210L;

	private static final String TEXTO_DIRECTIVA = "Caracterización general de un artefacto.";

	public FormularioDirectivaCaracterizacion() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Directivas definen artefacto")
	@Placeholder("Valores separados por coma, p.e. 'COOR_MAVEN,ALCANCES,INVENTARIO,...'")
	@BloqueDescripcion(
			"Lista separada por comas de los identificadores de directivas que son necesarias "
			+ "y requeridas para que el artefacto se comporte como se espera dentro del ciclo "
			+ "de vida. Utilizar '@' para identificar directivas multivariable de un diccionario"
			+ " concreto."
	)
	private String directivasRequeridas;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Directivas opciones artefacto")
	@Placeholder("Valores separados por coma, p.e. 'COOR_MAVEN,ALCANCES,INVENTARIO,...'")
	@BloqueDescripcion(
			"Lista separada por comas de los identificadores de directivas que pueden ser "
			+ "utilizados en un artefacto del tipo para particularizar ciertos comportamientos "
			+ "dentro del ciclo de vida.  Utilizar '@' para identificar directivas multivariable "
			+ "de un diccionario concreto."
	)
	private String directivasOpcionales;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=4)
	@EtiquetaFormulario("Autogenerar coordenadas Maven")
	@Checkbox
	@BloqueDescripcion("Indica cuándo la directiva de coordenadas maven debe ser autogenerada en el proceso de alta.")
	private boolean generarCoordenadasMaven;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=5)
	@EtiquetaFormulario("Empaquetado de la coordenada generada")
	@Placeholder("Valor del empaquetado, como 'JAR', 'WAR', etc.")
	@BloqueDescripcion("El valor que se empleará para rellenar el tipo de empaquetado de la coordenada Maven autogenerada.")
	private String empaquetadoCoordenada;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=6)
	@EtiquetaFormulario("Tratamiento de librería externa")
	@Checkbox
	@BloqueDescripcion("En el alta, el artefacto se tratará como una librería externa, lanzando un proceso de búsqueda y reconocimiento de dependencias adicionales.")
	private boolean esLibreriaExterna;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=7)
	@EtiquetaFormulario("Validación nombre")
	@Placeholder("Expresión regular, p.e. '^[a-z]*$")
	@BloqueDescripcion("Expresión regular con la que se podrá controlar el valor del nombre introducido. Si se mofica debe garantizar que se respeta la validación original además de la deseada.")
	private String patronValidacionNombre;
	
	// -------------------

	public String getDirectivasRequeridas() {
		return directivasRequeridas;
	}

	public void setDirectivasRequeridas(String directivasRequeridas) {
		this.directivasRequeridas = directivasRequeridas;
	}

	public String getDirectivasOpcionales() {
		return directivasOpcionales;
	}

	public void setDirectivasOpcionales(String directivasOpcionales) {
		this.directivasOpcionales = directivasOpcionales;
	}

	public boolean isGenerarCoordenadasMaven() {
		return generarCoordenadasMaven;
	}

	public void setGenerarCoordenadasMaven(boolean generarCoordenadasMaven) {
		this.generarCoordenadasMaven = generarCoordenadasMaven;
	}
	
	public String getEmpaquetadoCoordenada() {
		return empaquetadoCoordenada;
	}

	public void setEmpaquetadoCoordenada(String empaquetadoCoordenada) {
		this.empaquetadoCoordenada = empaquetadoCoordenada;
	}

	public boolean isEsLibreriaExterna() {
		return esLibreriaExterna;
	}

	public void setEsLibreriaExterna(boolean esLibreriaExterna) {
		this.esLibreriaExterna = esLibreriaExterna;
	}

	public String getPatronValidacionNombre() {
		return patronValidacionNombre;
	}

	public void setPatronValidacionNombre(String patronValidacionNombre) {
		this.patronValidacionNombre = patronValidacionNombre;
	}
	

	// -----------------------

	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// todos los identificadores de tipo deben existir
		validarLista(directivasRequeridas, "directivasRequeridas", ce, errores);
		validarLista(directivasOpcionales, "directivasOpcionales", ce, errores);
		
		// si tenemos activo autogenerar la coordenada maven, tendremos que garantizar
		// que se ha definido un empaquetado. O lo contrario: si no está, que no lo 
		// hayamos indicado.
		if(generarCoordenadasMaven && Strings.isNullOrEmpty(empaquetadoCoordenada)) 
			errores.rejectValue("empaquetadoCoordenada", "DEBE-INDICAR-EMPAQUETADO", "Si hay que autogenerar las coordenadas Maven hay que indicar la forma de empaquetado que se usará.");
		if(!generarCoordenadasMaven && !Strings.isNullOrEmpty(empaquetadoCoordenada)) 
			errores.rejectValue("empaquetadoCoordenada", "DEBE-OMITIR-EMPAQUETADO", "No se debe indicar empaquetado de coordenada Maven a usar cuando no se quiere autogenerar dicha coordenada.");

		// si se ha indicado autogenerar coordenada, la directiva de coordenada no debe 
		// estar entre las indicadas (ni las opcionales)
		if(generarCoordenadasMaven) {
			if(Arrays.asList(sanitizedSplit(directivasRequeridas)).contains("COOR_MAVEN")) {
				errores.rejectValue("directivasRequeridas", "SIN-COORDENADA-MAVEN", "No puede indicar la directiva de Coordenadas Maven cuando se ha indicado que se quiere autogenerar como parte del proceso.");
				errores.rejectValue("generarCoordenadasMaven", "SIN-COORDENADA-MAVEN", "No puede indicar la autogeneración de la coordenada Maven si ha indicado la directiva como parte de la lista de directivas.");
			}
			if(Arrays.asList(sanitizedSplit(directivasOpcionales)).contains("COOR_MAVEN")) {
				errores.rejectValue("directivasOpcionales", "SIN-COORDENADA-MAVEN", "No puede indicar la directiva de Coordenadas Maven cuando se ha indicado que se quiere autogenerar como parte del proceso.");
				errores.rejectValue("generarCoordenadasMaven", "SIN-COORDENADA-MAVEN", "No puede indicar la autogeneración de la coordenada Maven si ha indicado la directiva como parte de la lista de directivas.");
			}
			
			// y si queremos autogenerar, tenemos que contar con la taxonomía
			if(!Arrays.asList(sanitizedSplit(directivasRequeridas)).contains("TAXONOMIA")) {
				errores.rejectValue("directivasRequeridas", "REQUIERE-TAXONOMIA", "Es obligatorio contar con la directiva de taxonomía si se desea autogenerar la coordenada Maven.");
				errores.rejectValue("generarCoordenadasMaven", "REQUIERE-TAXONOMIA", "Es obligatorio contar con la directiva de taxonomía si se desea autogenerar la coordenada Maven.");
			}
		}
		
		// si el tratamiento es de librería externa, obligatoriamente se debe haber indicado 
		// la coordenada maven en la lista de directivas a solicitar en el alta
		if(esLibreriaExterna && !Arrays.asList(sanitizedSplit(directivasRequeridas)).contains("COOR_MAVEN")) {
			errores.rejectValue("directivasRequeridas", "CON-COORDENADA-MAVEN", "Únicamente pueden ser tratados como librarías externas los artefactos que hayan definido la directiva de coordenada maven como parte de las directivas requeridas.");
			errores.rejectValue("esLibreriaExterna", "CON-COORDENADA-MAVEN", "Únicamente pueden ser tratados como librarías externas los artefactos que hayan definido la directiva de coordenada maven como parte de las directivas requeridas.");
		}
		
		// tenemos que comprobar que la expresión regular indicada para el nombre no deja cosas raras
		if(Strings.isNotEmpty(patronValidacionNombre)) {
			Pattern p = Pattern.compile(patronValidacionNombre);

			// cosas que no se deberían permitir
			boolean incongruencias = 
					p.matcher("-unico").matches()
					|| p.matcher("pepe_potamo").matches()
					|| p.matcher("@pirulo").matches()
					|| p.matcher("a@b").matches()
					|| p.matcher("a:d").matches();
			
			if(incongruencias)
				errores.rejectValue("patronValidacionNombre", "PATRON-PELIGROSO", "El patrón de validación del nombre tiene pinta de peligroso");
		}
	}
	
	private void validarLista(String lista, String campo, IContextoEjecucion ce, Errors errores) {
		
		if(Strings.isNullOrEmpty(lista)) return;
		
		// cogemos cada elemento de la lista y confirmamos que exista
		for(String id: sanitizedSplit(lista)) {

			// diferenciamos los que empiezan con @ del resto, pues los que empiezan con @
			// serán diccionarios de la directiva general de diccionarios
			if(id.startsWith("@")) {
				if(ce.diccionariosDirectivas().findOne(id.replace("@", ""))==null)
					errores.rejectValue(campo, "DICC-NO-EXISTE", "No hay un diccionario de directiva con identificador '" + id.replace("@", "") + "'");
			} else if(ce.tiposDirectivas().findOne(id)==null)
				errores.rejectValue(campo, "TIPO-NO-EXISTE", "El tipo '" + id + "' no existe");
		}
	}
	
	@Override
	public DirectivaCaracterizacion nueva(IContextoEjecucion ce) {
		DirectivaCaracterizacion nueva = new DirectivaCaracterizacion();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaCaracterizacion o = (DirectivaCaracterizacion)original;
		o.setDirectivasRequeridas(sanitizedSplit(directivasRequeridas));
		o.setDirectivasOpcionales(sanitizedSplit(directivasOpcionales));
		o.setGenerarCoordenadasMaven(generarCoordenadasMaven);
		o.setEmpaquetadoCoordenada(empaquetadoCoordenada);
		o.setLibreriaExterna(esLibreriaExterna);
		o.setPlantillaValidacionNombre(this.patronValidacionNombre);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {

		DirectivaCaracterizacion d = (DirectivaCaracterizacion)directiva;
		setDirectivasRequeridas(join(d.getDirectivasRequeridas()));
		setDirectivasOpcionales(join(d.getDirectivasOpcionales()));
		setGenerarCoordenadasMaven(d.getGenerarCoordenadasMaven());
		setEmpaquetadoCoordenada(d.getEmpaquetadoCoordenada());
		setEsLibreriaExterna(d.getLibreriaExterna());
		setPatronValidacionNombre(d.getPlantillaValidacionNombre());
	}
	
	private String[] sanitizedSplit(String protoLista) {
		return protoLista.replace(" ", "").split(",");
	}
	
	private String join(String[] datos) {
		String r = "";
		for(String d: datos)
			r += r.length() > 0 ? "," + d : d;
		return r;
	}
}
