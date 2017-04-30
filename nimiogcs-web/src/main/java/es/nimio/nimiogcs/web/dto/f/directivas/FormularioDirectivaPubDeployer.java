package es.nimio.nimiogcs.web.dto.f.directivas;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

@EsExtension
public class FormularioDirectivaPubDeployer extends FormularioBaseDirectiva  {

	private static final long serialVersionUID = 7836239837135293455L;

	private static final String TEXTO_DIRECTIVA = "Parámetros del tipo para la publicación usando el canal de publicaciones Deployer.";

	public FormularioDirectivaPubDeployer() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=2)
	@EtiquetaFormulario("Comportamiento")
	@BloqueDescripcion("Comportamiento o familia de artefactos para la publicación por DEPLOYER.")
	@Seleccion(idColeccion="comportamientos-deployer")
	private String comportamiento;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Código elemento Deployer")
	@Placeholder("Código numérico, por ejemplo '0307'")
	@BloqueDescripcion("Código numérico que representa el elemento Deployer. Necesario para la correcta comunicación con el servicio.")
	private String codigoElemento;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=4)
	@EtiquetaFormulario("Carpeta descriptor")
	@Placeholder("Nombre de la carpeta, por ejemplo 'librería'")
	@BloqueDescripcion("Nombre de la carpeta donde se creará y clasificará el descriptor como parte de la petición Deployer.")
	private String carpetaElemento;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=5)
	@EtiquetaFormulario("Subruta en target")
	@BloqueDescripcion("Dentro de la carpeta target de la compilación, qué elemento toca publicar. Si se necesita el nombre del artefacto usar '@artefacto@' para que se sustituya el valor correspondiente.")
	private String elementoTarget;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=6)
	@EtiquetaFormulario("Directivas a proyectar")
	@BloqueDescripcion("Lista separada por coma de directivas que se trasladarán al descriptor. Si se quiere referenciar un diccionario anteponer '@' al identificador.")
	private String directivas;

	// -------------------

	public String getComportamiento() {
		return comportamiento;
	}
	
	public void setComportamiento(String comportamiento) {
		this.comportamiento = comportamiento;
	}
	
	@NotNull(message="Debe indicar un valor comprendido entre 1 y  99999")
	@Size(min=1, max=5, message="Debe indicar un valor comprendido entre 1 y  99999")
	@Pattern(regexp="^[0-9]{1,5}$", message="Debe indicar un valor comprendido entre 1 y  99999")
	public String getCodigoElemento() {
		return codigoElemento;
	}

	public void setCodigoElemento(String codigoElemento) {
		this.codigoElemento = codigoElemento;
	}

	@Size(min=3, max=15, message="La longitud del nombre de la carpeta debe estar comprendido entre 3 y 15 caracteres")
	public String getCarpetaElemento() {
		return carpetaElemento;
	}

	public void setCarpetaElemento(String carpetaElemento) {
		this.carpetaElemento = carpetaElemento;
	}
	
	public String getElementoTarget() {
		return elementoTarget;
	}

	public void setElementoTarget(String elementoTarget) {
		this.elementoTarget = elementoTarget;
	}

	public String getDirectivas() {
		return directivas;
	}

	public void setDirectivas(String directivas) {
		this.directivas = directivas;
	}

	
	// -----------------------

	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {

		if(Strings.isNullOrEmpty(directivas)) return;
		
		// cogemos cada elemento de la lista y confirmamos que exista
		for(String id: sanitizedSplit(directivas)) {

			// diferenciamos los que empiezan con @ del resto, pues los que empiezan con @
			// serán diccionarios de la directiva general de diccionarios
			if(id.startsWith("@")) {
				if(ce.diccionariosDirectivas().findOne(id.replace("@", ""))==null)
					errores.rejectValue("directivas", "DICC-NO-EXISTE", "No hay un diccionario de directiva con identificador '" + id.replace("@", "") + "'");
			} else if(ce.tiposDirectivas().findOne(id)==null)
				errores.rejectValue("directivas", "TIPO-NO-EXISTE", "El tipo '" + id + "' no existe");
		}
	}
	
	@Override
	public DirectivaPublicacionDeployer nueva(IContextoEjecucion ce) {
		DirectivaPublicacionDeployer nueva = new DirectivaPublicacionDeployer();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaPublicacionDeployer o =(DirectivaPublicacionDeployer)original;
		o.setComportamiento(comportamiento);
		o.setCodigoElemento(codigoElemento);
		o.setCarpetaElemento(carpetaElemento);
		o.setElementoTarget(elementoTarget);
		o.setDirectivas(sanitizedSplit(directivas));
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		DirectivaPublicacionDeployer d = (DirectivaPublicacionDeployer)directiva;
		setComportamiento(d.getComportamiento());
		setCodigoElemento(d.getCodigoElemento());
		setCarpetaElemento(d.getCarpetaElemento());
		setElementoTarget(d.getElementoTarget());
		setDirectiva(join(d.getDirectivas()));
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
