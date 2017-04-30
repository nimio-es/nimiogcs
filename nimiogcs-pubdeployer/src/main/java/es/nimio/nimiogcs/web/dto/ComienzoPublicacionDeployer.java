package es.nimio.nimiogcs.web.dto;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.subtareas.apiweb.ComienzoPublicacionDeployerOperacion.IComienzoPublicacionDeployer;

/**
 * Mensaje que se envía desde Deployer para notificar el comienzo de la
 * publicación
 */
public class ComienzoPublicacionDeployer implements IComienzoPublicacionDeployer {

	public static class DatosArtefacto implements IComienzoPublicacionDeployer.IDatosArtefacto {
		private String id;
		private String nombre;
		private String ticket;

		public String getId() { return this.id;}
		public void setId(String id) { this.id = id; }
		
		public String getNombre() { return this.nombre; }
		public void setNombre(String nombre) { this.nombre = nombre; }

		public String getTicket() { return this.ticket; }
		public void setTicket(String ticket) { this.ticket = ticket; }
	}

	private String entorno;
	private String etiquetaPase;
	private String usuario;
	private String marchaAtras;
	
	private List<DatosArtefacto> artefactos;

	// ----------------------

	@NotNull(message="Entorno no indicado")
	@Pattern(regexp="INTEGRACION|PREPRODUCCION|PRODUCCION", message="Entorno debe tener como valor 'INTEGRACION', 'PREPRODUCCION' o 'PRODUCCION'")
	public String getEntorno() {
		return entorno;
	}

	public void setEntorno(String entorno) {
		this.entorno = entorno;
	}

	@NotNull(message="No hay identificador de etiqueta de pase para la operación Deployer asociado")
	@Size(min=10, message="El identificador de la etiqueta de pase no tiene el tamaño mínimo requerido")
	public String getEtiquetaPase() {
		return etiquetaPase;
	}

	public void setEtiquetaPase(String ticket) {
		this.etiquetaPase = ticket;
	}

	@NotNull(message="No hay usuario asociado a la operación")
	@Size(min=3, message="El identificador de usuario de operación no tiene el tamaño mínimo requerido")
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Size(min=1, message="No hay artefactos asociados a la petición")
	public List<DatosArtefacto> getArtefactos() { 
		return this.artefactos;
	}
	
	public void setArtefactos(List<DatosArtefacto> artefactos) {
		this.artefactos = artefactos;
	}
	
	public String getMarchaAtras() {
		return this.marchaAtras;
	}
	
	public void setMarchaAtras(String marchaAtras) {
		this.marchaAtras = marchaAtras;
	}
	
	@Override
	public boolean getEsMarchaAtras() {
		return Strings.isNotEmpty(marchaAtras);
	}
	
	// ---------------------------------------
	// Exclusivos para la interfaz
	// ---------------------------------------

	public List<IComienzoPublicacionDeployer.IDatosArtefacto> getListaArtefactos() {
		return 
				Collections.list(
					Streams.of(artefactos)
					.map(new Function<DatosArtefacto, IComienzoPublicacionDeployer.IDatosArtefacto>() {
	
						@Override
						public IDatosArtefacto apply(DatosArtefacto v) {
							return v;
						}
					})
					.getEnumeration()
				);
	}

}
