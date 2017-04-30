package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEsperaRespuestaDeployer;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacion;

public abstract class Operaciones 
	extends RepositorioBase<Operacion> {

	public abstract ProcesoEspera procesoEsperaConTicket(String ticket);
	
	public abstract ProcesoEsperaRespuestaDeployer procesoEsperaRespuestaDeployerConEtiquetaPase(String idEtiqueta);
	
	public abstract Relaciones relaciones();
	
	// ----
	
	public static abstract class Relaciones extends RepositorioBase<RelacionOperacion> {

		public abstract Collection<RelacionOperacion> relacionesDeUnaOperacion(String id);
	}
}
