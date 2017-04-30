package es.nimio.nimiogcs.componentes.publicacion;

import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IErrores;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IPeticionPublicacion;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;

public interface ICanalPublicacion {

	/**
	 * Devuelve el descriptor del canal sin recurrir a comprobar el artefacto.
	 * @return
	 */
	DescripcionCanal descripcionCanal();
	
	DescripcionCanal teoricamentePosiblePublicarArtfacto(Artefacto artefacto);
	
	/**
	 * Evalúa si un artefacto determinado puede ser publicado por un canal determinado.
	 * De serlo, devolverá el descriptor del canal para que pueda ser utilizado
	 * en la interacción con el usuario.
	 * En caso contrario devolverá nulo.
	 */
	DescripcionCanal posiblePublicarArtefacto(ElementoBaseProyecto elementoProyecto, Artefacto artefacto);
	
	/**
	 * Solicita que se entreguen los parámetros adicionales que deben solicitarse a la hora 
	 * de publicar.
	 */
	void datosPeticion(String idCanal, IPeticionPublicacion peticion);
	
	/**
	 * Valida una petición antes de proceder a su solicitud formal.
	 * @throws Throwable 
	 */
	void validarPeticion(String idCanal, IPeticionPublicacion peticion, IErrores errores) throws ErrorInesperadoOperacion;
	
	/**
	 * Lanza la publicación
	 * @throws Throwable 
	 */
	void ejecutarPublicacion(IDatosPeticionPublicacion publicacion) throws ErrorInesperadoOperacion;
	
}
