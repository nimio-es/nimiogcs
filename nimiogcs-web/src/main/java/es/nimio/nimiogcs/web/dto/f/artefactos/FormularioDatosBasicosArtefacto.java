package es.nimio.nimiogcs.web.dto.f.artefactos;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.specs.Artefactos;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;

@GruposDeDatos(grupos={
		@GrupoDeDatos(
				id="DATOS", 
				nombre="Datos básicos", 
				orden=1, 
				textoDescripcion="")
})
public class FormularioDatosBasicosArtefacto implements Serializable {

	private static final long serialVersionUID = 187348652844930328L;

	public FormularioDatosBasicosArtefacto() {}
	
	public FormularioDatosBasicosArtefacto(String idTipo) { this.idTipo = idTipo; }
	
	// --------------------------
	
	@Privado
	private String idTipo;
	
	@GrupoAsociado(grupoContiene="DATOS", ordenEnGrupo=1)
	@EtiquetaFormulario("Nombre artefacto")
	@BloqueDescripcion("Nombre único que se le asignará al artefacto")
	private String nombre;

	// --------------------------

	public String getIdTipo() {
		return idTipo;
	}
	
	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}
	
	@NotNull(message="Debe indicar el nombre del artefacto.")
	@Size(min=5, message="El tamaño mínimo para el nombre del artefacto debe tener cinco caracteres.")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// --------------------------

	/**
	 * Amplía la validación de los datos recogidos más allá de los atributos que se puedean fijar
	 */
	public void validar(IContextoEjecucion ce, Errors errores) {

		// no pueden existir dos artefactos con el mismo nombre
		if(ce.artefactos().findOne(Artefactos.artefactoConNombre(nombre))!=null) {
			errores.rejectValue("nombre", "ARTEFACTO-DUPLICADO", "Ya existe un artefacto con el nombre señalad");
		}
		
		// además debe garantizarse que cumplimos la caracterización del nombre del tipo
		DirectivaCaracterizacion dc = PT.of(ce.tipos().findOne(idTipo)).caracterizacion();
		
		// siempre debe garantizarse el patron base
		if(!Pattern.matches("^[a-zA-Z0-9.-]*$", this.nombre))
			errores.rejectValue(
					"nombre",
					"ERROR-PATRON-BASE",
					"El nombre no cumple las reglas de construcción: solo caracteres alfanuméricos y el guión están permitidos."
			);

		// si hay definida plantilla de validación adicional, la aplicaremos
		if(Strings.isNotEmpty(dc.getPlantillaValidacionNombre())) { 
			if(!Pattern.matches(dc.getPlantillaValidacionNombre(), this.nombre))
				errores.rejectValue(
						"nombre", 
						"ERROR-PATRON-CARACTERIZACION",
						"No se supera la restricciones de nombre de la caracterización. "
						+ "Debe cumplir la expresión regular: '" + dc.getPlantillaValidacionNombre() + "'"
				);
		}
	}
	
	/**
	 * Crea el tipo de entidad a partir de los datos del formulario
	 */
	public Artefacto nueva(IContextoEjecucion ce) {
		Artefacto nueva = new Artefacto();
		actualiza(ce, nueva);
		nueva.setTipoArtefacto(ce.tipos().findOne(this.idTipo));
		return nueva;
	}
	
	/**
	 * Modifica los datos de una directiva a partir de los datos del formulario
	 */
	public void actualiza(IContextoEjecucion ce, Artefacto original) {
		original.setNombre(nombre.toUpperCase());
	}
	
	/**
	 * Rellena los dataos del formulario a partir de los datos de la directiva
	 */
	public void datosDesde(Artefacto artefacto) {
		this.nombre = artefacto.getNombre();
	}
}
