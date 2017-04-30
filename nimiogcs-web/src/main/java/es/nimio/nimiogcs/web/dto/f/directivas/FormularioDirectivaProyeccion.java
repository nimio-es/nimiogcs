package es.nimio.nimiogcs.web.dto.f.directivas;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;

@EsExtension
public class FormularioDirectivaProyeccion extends FormularioBaseDirectiva {

	private static final long serialVersionUID = -5232821553999510907L;

	private static final String TEXTO_DIRECTIVA = "Estrategia de proyección general para todos los artefactos del tipo.";

	public FormularioDirectivaProyeccion() {
		super();
		setDirectiva(TEXTO_DIRECTIVA);
	}
	
	// -------------------
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=3)
	@EtiquetaFormulario("Carpeta origen (1)")
	@BloqueDescripcion("Primera carpeta del repositorio de código relativa a la raíz del artefacto que se proyectará")
	private String carpetaOrigen1;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=4)
	@EtiquetaFormulario("Carpeta destino (1)")
	@BloqueDescripcion("Punto de anclaje en la proyección de la primera carpeta del repositorio de código")
	private String carpetaDestino1;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=5)
	@EtiquetaFormulario("Carpeta origen (2)")
	@BloqueDescripcion("Segunda carpeta del repositorio de código relativa a la raíz del artefacto que se proyectará")
	private String carpetaOrigen2;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=6)
	@EtiquetaFormulario("Carpeta destino (2)")
	@BloqueDescripcion("Punto de anclaje en la proyección de la segunda carpeta del repositorio de código")
	private String carpetaDestino2;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=7)
	@EtiquetaFormulario("Carpeta origen (3)")
	@BloqueDescripcion("Tercera carpeta del repositorio de código relativa a la raíz del artefacto que se proyectará")
	private String carpetaOrigen3;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=8)
	@EtiquetaFormulario("Carpeta destino (3)")
	@BloqueDescripcion("Punto de anclaje en la proyección de la tercera carpeta del repositorio de código")
	private String carpetaDestino3;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=9)
	@EtiquetaFormulario("Carpeta origen (4)")
	@BloqueDescripcion("Cuarta carpeta del repositorio de código relativa a la raíz del artefacto que se proyectará")
	private String carpetaOrigen4;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=10)
	@EtiquetaFormulario("Carpeta destino (4)")
	@BloqueDescripcion("Punto de anclaje en la proyección de la cuarta carpeta del repositorio de código")
	private String carpetaDestino4;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=11)
	@EtiquetaFormulario("Carpeta origen (5)")
	@BloqueDescripcion("Quinta carpeta del repositorio de código relativa a la raíz del artefacto que se proyectará")
	private String carpetaOrigen5;

	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=12)
	@EtiquetaFormulario("Carpeta destino (5)")
	@BloqueDescripcion("Punto de anclaje en la proyección de la quinta carpeta del repositorio de código")
	private String carpetaDestino5;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=14)
	@EtiquetaFormulario("Elementos a ignorar")
	@BloqueDescripcion("Indique qué elementos deben ser ignorados en las operaciones de 'commit' contra el repositorio de código. Uno por línea.")
	@AreaTexto
	private String svnIgnores;

	// ----------------
	
	public String getCarpetaOrigen1() {
		return carpetaOrigen1;
	}

	public void setCarpetaOrigen1(String carpetaOrigen1) {
		this.carpetaOrigen1 = carpetaOrigen1;
	}

	public String getCarpetaDestino1() {
		return carpetaDestino1;
	}

	public void setCarpetaDestino1(String carpetaDestino1) {
		this.carpetaDestino1 = carpetaDestino1;
	}

	public String getCarpetaOrigen2() {
		return carpetaOrigen2;
	}

	public void setCarpetaOrigen2(String carpetaOrigen2) {
		this.carpetaOrigen2 = carpetaOrigen2;
	}

	public String getCarpetaDestino2() {
		return carpetaDestino2;
	}

	public void setCarpetaDestino2(String carpetaDestino2) {
		this.carpetaDestino2 = carpetaDestino2;
	}

	public String getCarpetaOrigen3() {
		return carpetaOrigen3;
	}

	public void setCarpetaOrigen3(String carpetaOrigen3) {
		this.carpetaOrigen3 = carpetaOrigen3;
	}

	public String getCarpetaDestino3() {
		return carpetaDestino3;
	}

	public void setCarpetaDestino3(String carpetaDestino3) {
		this.carpetaDestino3 = carpetaDestino3;
	}

	public String getCarpetaOrigen4() {
		return carpetaOrigen4;
	}

	public void setCarpetaOrigen4(String carpetaOrigen4) {
		this.carpetaOrigen4 = carpetaOrigen4;
	}

	public String getCarpetaDestino4() {
		return carpetaDestino4;
	}

	public void setCarpetaDestino4(String carpetaDestino4) {
		this.carpetaDestino4 = carpetaDestino4;
	}

	public String getCarpetaOrigen5() {
		return carpetaOrigen5;
	}

	public void setCarpetaOrigen5(String carpetaOrigen5) {
		this.carpetaOrigen5 = carpetaOrigen5;
	}

	public String getCarpetaDestino5() {
		return carpetaDestino5;
	}

	public void setCarpetaDestino5(String carpetaDestino5) {
		this.carpetaDestino5 = carpetaDestino5;
	}

	public String getSvnIgnores() {
		return svnIgnores;
	}

	public void setSvnIgnores(String svnIgnores) {
		this.svnIgnores = svnIgnores;
	}


	// -----------------------
	
	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		// Hay que confirmar que emparejamos las carpetas
		// carpetas 1
		if(Strings.isNotEmpty(carpetaOrigen1) && Strings.isNullOrEmpty(carpetaDestino1))
			errores.rejectValue("carpetaDestino1", "ORIGEN-SIN-DESTINO", "Si se define una carpeta origen, se debe definir la carpeta destino de la proyección");
		if(Strings.isNullOrEmpty(carpetaOrigen1) && Strings.isNotEmpty(carpetaDestino1))
			errores.rejectValue("carpetaOrigen1", "DESTINO-SIN-ORIGEN", "Si se define una carpeta destino, se debe definir la carpeta origen de la proyección");

		// carpetas 2
		if(Strings.isNotEmpty(carpetaOrigen2) && Strings.isNullOrEmpty(carpetaDestino2))
			errores.rejectValue("carpetaDestino2", "ORIGEN-SIN-DESTINO", "Si se define una carpeta origen, se debe definir la carpeta destino de la proyección");
		if(Strings.isNullOrEmpty(carpetaOrigen2) && Strings.isNotEmpty(carpetaDestino2))
			errores.rejectValue("carpetaOrigen2", "DESTINO-SIN-ORIGEN", "Si se define una carpeta destino, se debe definir la carpeta origen de la proyección");

		// carpetas 3
		if(Strings.isNotEmpty(carpetaOrigen3) && Strings.isNullOrEmpty(carpetaDestino3))
			errores.rejectValue("carpetaDestino3", "ORIGEN-SIN-DESTINO", "Si se define una carpeta origen, se debe definir la carpeta destino de la proyección");
		if(Strings.isNullOrEmpty(carpetaOrigen3) && Strings.isNotEmpty(carpetaDestino3))
			errores.rejectValue("carpetaOrigen3", "DESTINO-SIN-ORIGEN", "Si se define una carpeta destino, se debe definir la carpeta origen de la proyección");

		// carpetas 4
		if(Strings.isNotEmpty(carpetaOrigen4) && Strings.isNullOrEmpty(carpetaDestino4))
			errores.rejectValue("carpetaDestino4", "ORIGEN-SIN-DESTINO", "Si se define una carpeta origen, se debe definir la carpeta destino de la proyección");
		if(Strings.isNullOrEmpty(carpetaOrigen4) && Strings.isNotEmpty(carpetaDestino4))
			errores.rejectValue("carpetaOrigen4", "DESTINO-SIN-ORIGEN", "Si se define una carpeta destino, se debe definir la carpeta origen de la proyección");

		// carpetas 5
		if(Strings.isNotEmpty(carpetaOrigen5) && Strings.isNullOrEmpty(carpetaDestino5))
			errores.rejectValue("carpetaDestino5", "ORIGEN-SIN-DESTINO", "Si se define una carpeta origen, se debe definir la carpeta destino de la proyección");
		if(Strings.isNullOrEmpty(carpetaOrigen5) && Strings.isNotEmpty(carpetaDestino5))
			errores.rejectValue("carpetaOrigen5", "DESTINO-SIN-ORIGEN", "Si se define una carpeta destino, se debe definir la carpeta origen de la proyección");
	}

	@Override
	public DirectivaProyeccion nueva(IContextoEjecucion ce) {
		DirectivaProyeccion nueva = new DirectivaProyeccion();
		actualiza(ce, nueva);
		return nueva;
	}
	
	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaProyeccion o= (DirectivaProyeccion)original;		
		o.setCarpetaOrigen1(carpetaOrigen1);
		o.setCarpetaDestino1(carpetaDestino1);
		o.setCarpetaOrigen2(carpetaOrigen2);
		o.setCarpetaDestino2(carpetaDestino2);
		o.setCarpetaOrigen3(carpetaOrigen3);
		o.setCarpetaDestino3(carpetaDestino3);
		o.setCarpetaOrigen4(carpetaOrigen4);
		o.setCarpetaDestino4(carpetaDestino4);
		o.setCarpetaOrigen5(carpetaOrigen5);
		o.setCarpetaDestino5(carpetaDestino5);
		o.setSvnIgnores(svnIgnores);
		int total = 0;
		total += Strings.isNotEmpty(carpetaOrigen1) ? 1 : 0;
		total += Strings.isNotEmpty(carpetaOrigen2) ? 1 : 0;
		total += Strings.isNotEmpty(carpetaOrigen3) ? 1 : 0;
		total += Strings.isNotEmpty(carpetaOrigen4) ? 1 : 0;
		total += Strings.isNotEmpty(carpetaOrigen5) ? 1 : 0;
		o.setNumeroCarpetas(total);
	}
	
	@Override
	public void datosDesde(DirectivaBase directiva) {
		
		DirectivaProyeccion d = (DirectivaProyeccion)directiva;
		setCarpetaOrigen1(d.getCarpetaOrigen1());
		setCarpetaDestino1(d.getCarpetaDestino1());
		setCarpetaOrigen2(d.getCarpetaOrigen2());
		setCarpetaDestino2(d.getCarpetaDestino2());
		setCarpetaOrigen3(d.getCarpetaOrigen3());
		setCarpetaDestino3(d.getCarpetaDestino3());
		setCarpetaOrigen4(d.getCarpetaOrigen4());
		setCarpetaDestino4(d.getCarpetaDestino4());
		setCarpetaOrigen5(d.getCarpetaOrigen5());
		setCarpetaDestino5(d.getCarpetaDestino5());
		setSvnIgnores(d.getSvnIgnores());
	}
}
