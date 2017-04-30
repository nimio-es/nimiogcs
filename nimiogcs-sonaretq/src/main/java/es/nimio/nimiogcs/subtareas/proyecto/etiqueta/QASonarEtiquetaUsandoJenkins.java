package es.nimio.nimiogcs.subtareas.proyecto.etiqueta;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;

import es.nimio.nimiogcs.KSonarEtiqueta;
import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoEspera;
import es.nimio.nimiogcs.jpa.entidades.operaciones.relaciones.RelacionOperacionElementoProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;

public class QASonarEtiquetaUsandoJenkins extends ProcesoAsincronoBase<IContextoEjecucionBase, EtiquetaProyecto>{

	public QASonarEtiquetaUsandoJenkins(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}
	
	// --

	@Override
	protected String nombreOperacion(EtiquetaProyecto etiqueta, ProcesoAsincrono op) {
		return "LANZAR ANÁLISIS CALIDAD ETIQUETA '" + etiqueta.getNombre() + "' (VÍA JENKINS)";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(EtiquetaProyecto etiqueta, ProcesoAsincrono op) {
		registraRelacionConOperacion(op, etiqueta);
		registraRelacionConOperacion(op, etiqueta.getProyecto());
	}

	@Override
	protected void hazlo(final EtiquetaProyecto protoEtiqueta, ProcesoAsincrono op) throws ErrorInesperadoOperacion {
		
		// recargamos los datos de la etiqueta
		escribeMensaje("Recargar datos de la etiqueta");
		final EtiquetaProyecto etiqueta = ce.repos().elementosProyectos().etiquetas().buscar(protoEtiqueta.getId());
		
		// leemos las variables globales
		final URI uriJenkins 	= URI.create(ce.repos().global().buscar(KSonarEtiqueta.PG_URL_BASE_JENKINS).getComoValorSimple());
		final String user 		= ce.repos().global().buscar(KSonarEtiqueta.PG_USER_JENKINS).getComoValorSimple();
		final String secret 	= ce.repos().global().buscar(KSonarEtiqueta.PG_PWD_JENKINS).getComoValorSimple();
		final String jobName	= ce.repos().global().buscar(KSonarEtiqueta.PG_JOB_JENKINS).getComoValorSimple();
		escribeMensaje("");
		escribeMensaje("Parámetros de ejecución:");
		escribeMensaje(" ... Uri Jenkins: " + uriJenkins.toString());
		escribeMensaje(" ... Nombre tarea: " + jobName);
		escribeMensaje(" ... Usuario/Contraseña: " + user + "/**********");
		escribeMensaje("");
		
		// calculamos la URL donde se encuentra el código de la etiqueta (parámetro 1)
		final String urlSvnLabel = 
				etiqueta.getProyecto().getEnRepositorio().rutaTotalPublicacion()
				+ "/mvn/"
				+ etiqueta.getNombre().toLowerCase();
		
		// proxy al servidor jenkins
		final JenkinsServer server = Strings.isNullOrEmpty(user) ?
				new JenkinsServer(uriJenkins)
				: new JenkinsServer(uriJenkins, user, secret);
				
		try {
			// proxy a la tarea
			final JobWithDetails job = server.getJob(jobName);
			if(job==null)
				throw new ErrorInconsistenciaDatos("Nombre de JOB inexistente en la instancia Jenkins");
			
			// hay que circunscribir la ejecución dentro de un proceso de espera
			final String ticket = newTicket();
			ProcesoEspera waitProc = new ProcesoEspera(ticket, 20 * 60);
			waitProc.setDescripcion("ESPERA EJECUCION ANALISIS SONAR");
			waitProc.setUsuarioEjecuta(this.getUsuario() != null ? this.getUsuario().getNombre().toUpperCase() : "DISCOLO");
			ce.repos().operaciones().guardar(waitProc);
			ce.repos().operaciones().relaciones().guardar(new RelacionOperacionElementoProyecto(waitProc, etiqueta));
			ce.repos().operaciones().relaciones().guardar(new RelacionOperacionElementoProyecto(waitProc, etiqueta.getProyecto()));
			escribeMensaje("Proceso espera (20 minutos) registrado con ticket: " + ticket);
			escribeMensaje("");
			
			// parámetros de la tarea
			final HashMap<String, String> jobParams = new HashMap<String, String>();
			jobParams.put("TICKET", ticket);
			jobParams.put("SVN", urlSvnLabel);
			
			escribeMensaje("Lanzar la tarea Jenkins:");
			escribeMensaje(" ... Ticket: " + ticket);
			escribeMensaje(" ... Url Svn: " + urlSvnLabel);
			escribeMensaje("");
			
			// ejecutamos la tarea
			job.build(jobParams);
		} catch(Exception th) {
			throw new ErrorInesperadoOperacion(th);
		}
	}

	
	private String newTicket() {

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
