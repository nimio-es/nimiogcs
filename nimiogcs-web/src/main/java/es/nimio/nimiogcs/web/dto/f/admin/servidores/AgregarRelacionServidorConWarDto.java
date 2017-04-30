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
		@GrupoDeDatos(id="GENERAL", nombre="Datos generales", orden=1, textoDescripcion="Datos que se requieren para definir de forma inequívoca la instalación de un WAR en un servidor.")
})
public final class AgregarRelacionServidorConWarDto {

	// -------------------------------------------
	// Utilidades estáticas para rellenar desde 
	// los elementos desde los que comunmente se
	// instanciarán.
	// -------------------------------------------
	
	public static AgregarRelacionServidorConWarDto paraEditar(RelacionServidorAplicacion relacion) {
		
		AgregarRelacionServidorConWarDto edicion = new AgregarRelacionServidorConWarDto();
		ServidorJava servidor = (ServidorJava)relacion.getServidor();
		Artefacto war = relacion.getArtefacto();
		
		edicion.setIdRelacion(relacion.getId());
		edicion.setIdServidorJava(servidor.getId());
		edicion.setIdWar(war.getId());
		edicion.setNombreWar(war.getNombre());
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
	@EtiquetaFormulario("WAR a asociar")
	@BloqueDescripcion("Seleccione el WAR que está relacionado con el servidor.")
	@Seleccion(idColeccion="modwebs")
	@NoEditable(ocultar=true)
	private String idWar;

	// Cuando estemos en modo edición lo que queremos mostrar es el nombre y mantener el id en un campo oculto
	@GrupoAsociado(
			grupoContiene="GENERAL",
			ordenEnGrupo=1)
	@Estatico(enAlta=false)
	@EtiquetaFormulario("WAR asociado")
	private String nombreWar;
	
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

	@NotNull(message="Debe indicar un WAR que se asocia con el servidor.")
	@Size(min=1, message="Debe indicar un WAR que se asocia con el servidor.")
	public String getIdWar() {
		return idWar;
	}

	public void setIdWar(String idWar) {
		this.idWar = idWar;
	}

	public String getNombreWar() {
		return nombreWar;
	}

	public void setNombreWar(String nombreWar) {
		this.nombreWar = nombreWar;
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
