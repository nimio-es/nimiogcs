package es.nimio.nimiogcs.consolidar.datos;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.consolidar.K;
import es.nimio.nimiogcs.datos.IniciadorBase;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Service
public class IniciadorDatosConsolidacion {

	private IContextoEjecucion ce;
	
	@Autowired
	public IniciadorDatosConsolidacion(IContextoEjecucion ce) {
		this.ce = ce;
	}

	@Scheduled(initialDelay=5*1000, fixedRate=365*24*60*60*1000)
	public void iniciador() {
		new Iniciador(ce).ejecutar();
	}
	
	// ---
	
	static final class Iniciador extends IniciadorBase {
	
		public Iniciador(IContextoEjecucion contextoEjecucion) {
			super(contextoEjecucion);
		}
		
		// --

		@Override
		protected Collection<Tuples.T5<String, String, Boolean, Boolean, Boolean>> listaTiposDeDirectivas() {
			return new ArrayList<Tuples.T5<String,String,Boolean,Boolean,Boolean>>();
		}

		@Override
		protected Collection<Tuples.T3<String, String, Boolean>> listaTiposDeArtefactos() {
			return new ArrayList<Tuples.T3<String,String,Boolean>>();
		}

		@Override
		protected Collection<Tuples.T3<String, String, String>> listaParametrosGlobales() {
			ArrayList<Tuples.T3<String,String,String>> registros = new ArrayList<Tuples.T3<String,String,String>>();
			registros.add(
					Tuples.tuple(
							K.GLOBAL_CANAL_PUBLICACION_CONSOLIDACION_USOMANUAL, 
							"Permite (valor 1) o no (valor 0) que se pueda elegir el canal de consolidación para lanzar la publicación de un artefacto de una etiqueta. Para pruebas o ajustes. En producción dejar desactivado.", 
							"0"
					)
			);
			return registros;
		}

		@Override
		protected Collection<T2<String, String>> listaDestinosPublicacion() {
			ArrayList<T2<String, String>> registros = new ArrayList<Tuples.T2<String,String>>();
			registros.add(Tuples.tuple(K.ID_CONSOLIDAR, "Consolidación"));
			return registros;
		}
		
		@Override
		protected String generaNombreUnico() {
			return "AUTOREGISTRAR DATOS NECESARIOS PARA PUBLICACIÓN USANDO EL CANAL 'CONSOLIDACIÓN'";
		}

	}	
}
