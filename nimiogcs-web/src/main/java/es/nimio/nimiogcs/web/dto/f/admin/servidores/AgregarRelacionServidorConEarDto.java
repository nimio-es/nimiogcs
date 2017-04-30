package es.nimio.nimiogcs.web.dto.f.admin.servidores;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorAplicacion;
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
		@GrupoDeDatos(id="GENERAL", nombre="Datos generales", orden=1, textoDescripcion="Datos que se requieren para definir de forma inequívoca la instalación de un EAR en un servidor.")
})
public final class AgregarRelacionServidorConEarDto {

	// -------------------------------------------
	// Utilidades estáticas para rellenar desde 
	// los elementos desde los que comunmente se
	// instanciarán.
	// -------------------------------------------
	
	public static AgregarRelacionServidorConEarDto paraEditar(RelacionServidorAplicacion relacion) {
		
		AgregarRelacionServidorConEarDto edicion = new AgregarRelacionServidorConEarDto();
		ServidorJava servidor = (ServidorJava)relacion.getServidor();
		Artefacto ear = relacion.getArtefacto();
		
		edicion.setIdRelacion(relacion.getId());
		edicion.setIdServidorJava(servidor.getId());
		edicion.setIdEar(ear.getId());
		edicion.setNombreEar(ear.getNombre());
		edicion.setIdAplicacion(relacion.getIdAplicacion());
		edicion.setVirtualHost(relacion.getVirtualHost());
		
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
	@EtiquetaFormulario("EAR a asociar")
	@BloqueDescripcion("Seleccione el EAR que está relacionado con el servidor.")
	@Seleccion(idColeccion="ears")
	@NoEditable(ocultar=true)
	private String idEar;

	// Cuando estemos en modo edición lo que queremos mostrar es el nombre y mantener el id en un campo oculto
	@GrupoAsociado(
			grupoContiene="GENERAL",
			ordenEnGrupo=1)
	@Estatico(enAlta=false)
	@EtiquetaFormulario("EAR a asociar")
	private String nombreEar;
	
	@GrupoAsociado(
			grupoContiene="GENERAL", 
			ordenEnGrupo=2)
	@EtiquetaFormulario("Nombre de aplicación")
	@BloqueDescripcion("Indique el identificador o nombre de aplicación con el que quedará instalado en el servidor.")
	@Placeholder("Nombre/Id. instalación de aplicación")
	private String idAplicacion;
	
	@GrupoAsociado(
			grupoContiene="GENERAL", 
			ordenEnGrupo=3)
	@EtiquetaFormulario("Host Virtual")
	@BloqueDescripcion("El Virtual Host que se asocia y por el que es atendida esta aplicación.")
	@Placeholder("Identificador del virtual host")
	private String virtualHost;

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

	@NotNull(message="Debe indicar un EAR que se asocia con el servidor.")
	@Size(min=1, message="Debe indicar un EAR que se asocia con el servidor.")
	public String getIdEar() {
		return idEar;
	}

	public void setIdEar(String idEar) {
		this.idEar = idEar;
	}

	public String getNombreEar() {
		return nombreEar;
	}

	public void setNombreEar(String nombreEar) {
		this.nombreEar = nombreEar;
	}

	@NotNull(message="El identificador de aplicación no puede quedar vacío.")
	@Size(min=3, message="El nombre interno del servidor para la aplicación no puede tener un tamaño inferior a 3 caracteres.")
	public String getIdAplicacion() {
		return idAplicacion;
	}

	public void setIdAplicacion(String idAplicacion) {
		this.idAplicacion = idAplicacion;
	}

	@NotNull(message="El virtual host de la aplicación no puede quedar vacío.")
	@Size(min=6, message="El tamaño de virtual host no debe ser inferior a seis caracteres.")
	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}
	
	
	
}
