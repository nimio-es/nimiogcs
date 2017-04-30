package es.nimio.nimiogcs.web.dto.f.admin.servidores;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorLibreriaCompartida;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.NoEditable;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;

/**
 * DTO para mantener el formulario de registro de los datos que requiere el alta de un EAR 
 * dentro de un servidor Java. 
 */
@GruposDeDatos(grupos={
		@GrupoDeDatos(id="GENERAL", nombre="Datos generales", orden=1, textoDescripcion="Datos que se requieren para definir de forma inequívoca la instalación de una Librería compartida en un servidor.")
})
public final class AgregarRelacionServidorConLibreriaCompartidaDto {

	// -------------------------------------------
	// Utilidades estáticas para rellenar desde 
	// los elementos desde los que comunmente se
	// instanciarán.
	// -------------------------------------------
	
	public static AgregarRelacionServidorConLibreriaCompartidaDto paraEditar(RelacionServidorLibreriaCompartida relacion) {
		
		AgregarRelacionServidorConLibreriaCompartidaDto edicion = new AgregarRelacionServidorConLibreriaCompartidaDto();
		ServidorJava servidor = (ServidorJava)relacion.getServidor();
		Artefacto libreriaCompartida = relacion.getArtefacto();
		
		edicion.setIdRelacion(relacion.getId());
		edicion.setIdServidorJava(servidor.getId());
		edicion.setIdLibreriaCompartida(libreriaCompartida.getId());
		edicion.setNombreLibreriaCompartida(libreriaCompartida.getNombre());
		edicion.setCarpetaServidor(relacion.getCarpetaServidor());
		
		return edicion;
	}
	
	// -------------------------------------------
	// Estado
	// -------------------------------------------

	// -- Solamente tiene sentido cuando nos encontremos en edición
	@Privado
	private String idRelacion;
	
	@Privado
	private String idServidorJava;
	
	@GrupoAsociado(
			grupoContiene="GENERAL", 
			ordenEnGrupo=1)
	@EtiquetaFormulario("Librería compartida a asociar")
	@BloqueDescripcion("Seleccione la librería compartida que está relacionado con el servidor.")
	@Seleccion(idColeccion="lcs")
	@NoEditable(ocultar=true)
	private String idLibreriaCompartida;

	// Cuando estemos en modo edición lo que queremos mostrar es el nombre y mantener el id en un campo oculto
	@GrupoAsociado(
			grupoContiene="GENERAL",
			ordenEnGrupo=1)
	@Estatico(enAlta=false)
	@EtiquetaFormulario("Librería compartida asociada")
	private String nombreLibreriaCompartida;
	
	@GrupoAsociado(
			grupoContiene="GENERAL", 
			ordenEnGrupo=2)
	@EtiquetaFormulario("Carpeta de servidor")
	@BloqueDescripcion("Indique la ruta de la carpeta de servidor donde se instala esta librería compartida.")
	@Placeholder("Ruta instalación de la librería compartida")
	private String carpetaServidor;
	
	// -------------------------------------------
	// Leer y escribir estado
	// -------------------------------------------
	
	public String getIdRelacion() {
		return idRelacion;
	}

	public void setIdRelacion(String idRelacion) {
		this.idRelacion = idRelacion;
	}

	@NotNull(message="Sin el identificador del servidor no se puede procesar la petición.")
	public String getIdServidorJava() {
		return idServidorJava;
	}

	public void setIdServidorJava(String idServidorJava) {
		this.idServidorJava = idServidorJava;
	}

	@NotNull(message="Debe indicar la Librería compartida que se asocia con el servidor.")
	@Size(min=1, message="Debe indicar la Librería compartida que se asocia con el servidor.")
	public String getIdLibreriaCompartida() {
		return idLibreriaCompartida;
	}

	public void setIdLibreriaCompartida(String idLibreriaCompartida) {
		this.idLibreriaCompartida = idLibreriaCompartida;
	}

	public String getNombreLibreriaCompartida() {
		return nombreLibreriaCompartida;
	}

	public void setNombreLibreriaCompartida(String nombreLibreriaCompartida) {
		this.nombreLibreriaCompartida = nombreLibreriaCompartida;
	}

	@NotNull(message="La ruta de la carpeta de instalación no puede quedar vacía.")
	@Size(min=2, message="La ruta de instalación de la librería compartida en el servidor no puede tener una longitur menor a 2 caracteres.")
	public String getCarpetaServidor() {
		return carpetaServidor;
	}

	public void setCarpetaServidor(String idAplicacion) {
		this.carpetaServidor = idAplicacion;
	}
	
}
