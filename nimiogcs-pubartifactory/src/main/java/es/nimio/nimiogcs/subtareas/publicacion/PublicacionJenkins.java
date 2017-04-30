package es.nimio.nimiogcs.subtareas.publicacion;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import es.nimio.nimiogcs.KArtifactory;
import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionDestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionPublicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.enumerados.EnumEstadoEjecucionProceso;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;

public class PublicacionJenkins extends ProcesoAsincronoBase<IContextoEjecucionBase, IDatosPeticionPublicacion> {

	public PublicacionJenkins(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}

	// -------
	
	@Override
	protected String nombreUnicoOperacion(IDatosPeticionPublicacion datos, ProcesoAsincrono op) {
		return "PUBLICAR POR JENKINS LA ETIQUETA '" + datos.getEtiqueta().getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(IDatosPeticionPublicacion datos, ProcesoAsincrono op) {
		// relacionamos con la etiqueta, el proyecto de la etiqueta y con todos los artefactos
		registraRelacionConOperacion(op, datos.getEtiqueta());
		registraRelacionConOperacion(op, datos.getEtiqueta().getProyecto());
		
		for(Artefacto a: datos.getArtefactos())
			registraRelacionConOperacion(op, a);
	}

	@Override
	protected void hazlo(IDatosPeticionPublicacion datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		// hacemos un listado de qué tarea le corresponde a cada artefacto que queremos publicar
		// NOTA: Esto debería ser mejorado para hacer un árbol de dependencia (como cuando construimos
		//       los archivos pom.xml de los módulos) y ordenar las publicaciones de esta forma.
		final Map<String, ArrayList<Artefacto>> tarea_artefactos = new HashMap<String, ArrayList<Artefacto>>();
		for(Artefacto artefacto: datos.getArtefactos()) {
			
			// si es un evolutivo, tenemos que usar el artefacto real
			final Artefacto base = artefacto instanceof ITestaferroArtefacto ? 
					((ITestaferroArtefacto)artefacto).getArtefactoAfectado()
					: artefacto;
			
			// buscamos la directiva de publicación Jenkins 
			final String tarea = P.of(base).publicacionJenkins().getTarea(); 
			
			// añadimos el artefacto en la lista de tareas que corresponda
			if(!tarea_artefactos.containsKey(tarea))
				tarea_artefactos.put(tarea, new ArrayList<Artefacto>());
			
			tarea_artefactos.get(tarea).add(base);
		}

		// calculamos la URL donde se encuentra el código de la etiqueta (parámetro 1)
		String rutaEtiqueta = 
				datos.getEtiqueta().getProyecto().getEnRepositorio().rutaTotalPublicacion()
				+ "/mvn/"
				+ datos.getEtiqueta().getNombre().toLowerCase();

		// preparamos la conexión con Jenkins
		final URI uriJenkins = URI.create(ce.repos().global().buscar(KArtifactory.PUBLICACION_JENKINS_SERVIDOR).getContenido());
		final String usuarioJenkins = ce.repos().global().buscar(KArtifactory.PUBLICACION_JENKINS_SERVIDOR_USUARIO).getContenido();
		final String passwordJenkins = ce.repos().global().buscar(KArtifactory.PUBLICACION_JENKINS_SERVIDOR_SECRETO).getContenido();

		JenkinsServer servidor = Strings.isNotEmpty(usuarioJenkins) ?
				new JenkinsServer(uriJenkins, usuarioJenkins, passwordJenkins)
				: new JenkinsServer(uriJenkins);

		// ahora, por cada una de los artefactos que se han marcado, 
		// procederemos a lanzar la tarea Jenkins correspondiente.
		for(Entry<String, ArrayList<Artefacto>> tareaArtefactos: tarea_artefactos.entrySet()) {
			
			// para cada petición habrá que registrar un ticket
			final String ticket = generarTicket();

			// el identificador de la tarea
			final String tarea = tareaArtefactos.getKey();
			
			// recorremos los artefactos
			for(Artefacto artefacto: tareaArtefactos.getValue()) {
				
				final String modulo = artefacto.getNombre().toLowerCase();

				// Hay que dar de alta el registro de publicación en curso
				Publicacion pb = new Publicacion();
				pb.setCanal(datos.getCanal());
				pb.setIdDestinoPublicacion(ce.repos().destinosPublicacion().buscar("ARTIFACTORY").getId());
				pb.setEstado(es.nimio.nimiogcs.jpa.K.X.EJECUCION);
				pb = ce.repos().publicaciones().guardarYVolcar(pb);
				
				PublicacionArtefacto pa = new PublicacionArtefacto();
				pa.setPublicacion(pb);
				pa.setArtefacto(artefacto);
				pa.setIdEtiqueta(datos.getEtiqueta().getId());
				pa.setNombreEtiqueta(datos.getEtiqueta().getNombre());
				pa.setProyecto(datos.getEtiqueta().getProyecto());
				ce.repos().artefactosPublicados().guardarYVolcar(pa);
				
				// creamos la tarea de seguimiento
				ProcesoEspera pe = new ProcesoEspera(ticket, 30 * 60);
				pe.setDescripcion("PUBLICACIÓN JENKINS MÓDULO '" + modulo + "'");
				pe.setUsuarioEjecuta(datos.getUsuario().toUpperCase());
				pe = ce.repos().operaciones().guardarYVolcar(pe);
				
				// que relacionamos con todos los elementos
				ce.repos().operaciones().relaciones().guardarYVolcar(new RelacionOperacionElementoProyecto(pe, datos.getEtiqueta().getProyecto()));
				ce.repos().operaciones().relaciones().guardarYVolcar(new RelacionOperacionElementoProyecto(pe, datos.getEtiqueta()));
				ce.repos().operaciones().relaciones().guardarYVolcar(new RelacionOperacionArtefacto(pe, artefacto));
				ce.repos().operaciones().relaciones().guardarYVolcar(new RelacionOperacionDestinoPublicacion(
						pe, 
						ce.repos().destinosPublicacion().buscar("ARTIFACTORY"))
				);
				ce.repos().operaciones().relaciones().guardarYVolcar(new RelacionOperacionPublicacion(pe, pb));
				
				try {
					// y lanzamos la tarea Jenkins
					JobWithDetails job = servidor.getJob(tarea);
					if(job==null) 
						throw new ErrorJobJenkinsInalcanzableOInexistente();
					
					// el convenio con jenkins es incluir los parámetros...
					HashMap<String, String> parametros = new HashMap<String, String>();
					parametros.put("TICKET", ticket);      // el ticket
					parametros.put("SVN", rutaEtiqueta);   // la ruta Subversion
					parametros.put("MODULO", modulo);      // el módulo/artefacto
					
					job.build(parametros);
				} catch(Exception th) {
					
					pe.setMensajeError(th.toString());
					pe.setEstadoEjecucionProceso(EnumEstadoEjecucionProceso.ERROR);
					pe.setFinalizado(true);
					ce.repos().operaciones().guardarYVolcar(pe);
					throw new ErrorInesperadoOperacion(th);
					
				}
				
			}
		}
	}
	
	protected String generarTicket() {

		UUID r = UUID.randomUUID();
		UUID r2 = UUID.randomUUID();
		String ticket = new StringBuilder(40) 
				.append(Long.toString(Math.abs(System.currentTimeMillis()), Character.MAX_RADIX))
				.append(
						Long.toString(
								Math.abs(r.getMostSignificantBits()), Character.MAX_RADIX
						)
				)
				.append(
						Long.toString(
								Math.abs(r.getLeastSignificantBits()), Character.MAX_RADIX
						)
				)
				.append(
						Long.toString(
								Math.abs(r2.getMostSignificantBits()), Character.MAX_RADIX
						)
				)
				.toString()
				.toUpperCase();

		// y lo devolvemos
		return ticket.substring(0, ticket.length() < 50 ? ticket.length() : 50);
	}
	
}
