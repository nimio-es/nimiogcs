package es.nimio.nimiogcs.operaciones.artefactos.externa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.CongelarEvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;

public class ComprobarSincronizacionRamaEvolutiva 
	extends ProcesoAsincronoModulo<ComprobarSincronizacionRamaEvolutiva.Peticion> {

	public static final class Peticion {
		
		private final List<ITestaferroArtefacto> testaferros;
		private final List<MetaRegistro> afectados;
		
		public Peticion(ITestaferroArtefacto... testaferros) {
			this(Arrays.asList(testaferros));
		}
		
		public Peticion(List<ITestaferroArtefacto> testaferros) {
			this(testaferros, null);
		}
		
		public Peticion(
				List<ITestaferroArtefacto> testaferros,
				List<MetaRegistro> arrayList) {
			this.testaferros = testaferros;
			this.afectados = arrayList != null ? arrayList : new ArrayList<MetaRegistro>();
		}

		public List<ITestaferroArtefacto> getTestaferros() {
			return testaferros;
		}

		public List<MetaRegistro> getAfectados() {
			return afectados;
		}
		
		
	}
	
	// ---

	public ComprobarSincronizacionRamaEvolutiva(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}
	
	// ---

	@Override
	protected String nombreUnicoOperacion(ComprobarSincronizacionRamaEvolutiva.Peticion peticion, ProcesoAsincrono op) {
		
		// el mensaje lo diferenciamos según tengamos uno o más testaferros en proceso
		if(peticion.getTestaferros().size() == 1) {
			
			return 
					"COMPROBAR QUE LA RAMA DE TRABAJO/ETIQUETA DEL ARTEFACTO '"
					+ peticion.getTestaferros().get(0).getArtefactoAfectado().getNombre()
					+ "' SE ENCUENTRA SINCRONIZADA";
			
		} else {

			return 
					"COMPROBAR SI VARIOS REGISTROS DE EVOLUCIÓN SE ENCUENTRAN SINCRONIZADOS CON LAS RAMAS ESTABLES";
		}
	}
	
	@Override
	protected void relacionarOperacionConEntidades(ComprobarSincronizacionRamaEvolutiva.Peticion peticion, ProcesoAsincrono op) {
		
		for(ITestaferroArtefacto ta: peticion.getTestaferros()) {
			registraRelacionConOperacion(op, (Artefacto)ta);
			registraRelacionConOperacion(op, ta.getArtefactoAfectado());
		}
		
		// si hay más objetos afectados por la operación, los metemos también
		for(MetaRegistro mr: peticion.getAfectados()) {
			if(mr instanceof Artefacto) registraRelacionConOperacion(op, (Artefacto)mr);
			if(mr instanceof DestinoPublicacion) registraRelacionConOperacion(op, (DestinoPublicacion)mr);
			if(mr instanceof ElementoBaseProyecto) registraRelacionConOperacion(op, (ElementoBaseProyecto)mr);
			if(mr instanceof Publicacion) registraRelacionConOperacion(op, (Publicacion)mr);
			if(mr instanceof Servidor) registraRelacionConOperacion(op, (Servidor)mr);
		}
	}

	@Override
	protected void hazlo(final ComprobarSincronizacionRamaEvolutiva.Peticion evolutivo, ProcesoAsincrono op) throws ErrorInesperadoOperacion {
		
		for(ITestaferroArtefacto ta: evolutivo.getTestaferros()) {
		
			final Artefacto testaferro = (Artefacto)ta;
			final Artefacto artefacto = ta.getArtefactoAfectado(); 
			
			escribeMensaje("Artefacto '" +  testaferro.getNombre() + "' que representa a '" + artefacto.getNombre() + "'");
			escribeMensaje("Comprobar el estado de sincronización de la rama de trabajo con la rama troncal.");
			
			final DirectivaRepositorioCodigo drc = P.of(artefacto).repositorioCodigo(); 
			final RepositorioCodigo rc = drc.getRepositorio();
			final String urlEstable = 
					rc.getUriRaizRepositorio()
					+ "/" 
					+ drc.getParcialEstables();
			
			final String urlRama =
					P.of(testaferro)
					.ramaCodigo()
					.getRamaCodigo();
	
			escribeMensaje("URL estable: " + urlEstable);
			escribeMensaje("URL rama: " + urlRama);
	
			escribeMensaje("Lanzar la operación de consulta de información elegible para sincronizar las ramas.");
			Subversion svn = new Subversion(rc);
			List<String> candidatos = svn.infoElegiblesMezclado(urlRama, urlEstable);
			if(candidatos.size() > 0) {
				escribeMensaje("Se han encontrado las siguientes revisiones candidatas a llevar a la rama de trabajo:");
				final StringBuilder sb = new StringBuilder();
				for(int i = 0; i < candidatos.size(); i++) {
					sb.append(candidatos.get(i));
					if(i< candidatos.size()) sb.append(", ");
				}
				escribeMensaje(sb.toString());
			} else {
				escribeMensaje("No hay candidatos. Se consideran sincronizadas.");
			}
			
			// con lo que resulte de la consulta mnodificamos el registro
			Artefacto aModificar = ce.artefactos().findOne(testaferro.getId());
			if(aModificar instanceof EvolucionArtefacto) {
				((EvolucionArtefacto)aModificar)
				.setSincronizadoEstable(candidatos.size() == 0);
			} else {
				((CongelarEvolucionArtefacto)aModificar)
				.setSincronizadoEstable(candidatos.size() == 0);
			}
			ce.artefactos().saveAndFlush(aModificar);
			
			escribeMensaje(" ");
		}
	}
	
}
