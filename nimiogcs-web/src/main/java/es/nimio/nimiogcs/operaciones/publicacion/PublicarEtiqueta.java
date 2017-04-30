package es.nimio.nimiogcs.operaciones.publicacion;

import java.util.Collection;
import java.util.Map;

import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public class PublicarEtiqueta extends OperacionInternaModulo<PublicarEtiqueta.PeticionPublicacion, Boolean> {
	
	/**
	 * Clase que recoge la petición relativa a la publicación
	 */
	public static final class PeticionPublicacion 
		implements IDatosPeticionPublicacion {
		
		private final String canal;
		private final Map<String, String> parametrosCanal;  
		private final EtiquetaProyecto etiqueta;
		private final Collection<Artefacto> artefactos;
		private final String usuario;

		public PeticionPublicacion(
				String canal,
				Map<String, String> parametrosCanal,  
				EtiquetaProyecto etiqueta,
				Collection<Artefacto> artefactos,
				String usuario
		) {
			this.canal = canal;
			this.parametrosCanal = parametrosCanal;
			this.etiqueta = etiqueta;
			this.artefactos = artefactos;
			this.usuario = usuario;
		}

		public String getCanal() {
			return canal;
		}
		
		public Map<String, String> getParametrosCanal() {
			return parametrosCanal;
		}

		public EtiquetaProyecto getEtiqueta() {
			return etiqueta;
		}

		public Collection<Artefacto> getArtefactos() {
			return artefactos;
		}

		public String getUsuario() {
			return usuario;
		}

	}

	// -----
	
	public PublicarEtiqueta(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	@Override
	protected String nombreUnicoOperacion(PeticionPublicacion datos, Operacion op) {
		return "LANZAR PUBLICACIÓN DE LA ETIQUETA '" 
				+ datos.getEtiqueta().getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(PeticionPublicacion datos, Operacion op) {
		// relacionamos con la etiqueta, el proyecto de la etiqueta y con todos los artefactos
		registraRelacionConOperacion(op, datos.getEtiqueta());
		registraRelacionConOperacion(op, datos.getEtiqueta().getProyecto());
		
		for(Artefacto a: datos.getArtefactos())
			registraRelacionConOperacion(op, a);
	}

	@Override
	protected Boolean hazlo(PeticionPublicacion datos, Operacion op) throws ErrorInesperadoOperacion {

		// recorremos todos los canales de publicación enviándole la petición
		// lo procesará el que le corresponda
		for(ICanalPublicacion cp: ce.contextoAplicacion().getBeansOfType(ICanalPublicacion.class).values()) {
			escribeMensaje("Encontrado un canal: " + cp.getClass().getSimpleName());
			escribeMensaje("Invocando la ejecución");
			cp.ejecutarPublicacion(datos);
		}
		
		return true;
	}
	
}
