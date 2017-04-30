package es.nimio.nimiogcs.web.dto.w;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que nos ayudará a llevar el control de en qué estado se encuentra la operación 
 * para incluir un artefacto en un proyecto
 */
public class AsistenteAgregarNuevoArtefactoAProyecto implements Serializable {

	public static final String ORIGEN_NUEVA_RAMA = "ARTEFACTO";
	public static final String ORIGEN_DE_PROYECTO = "PROYECTO";
	
	private static final long serialVersionUID = -634768351187314386L;

	public AsistenteAgregarNuevoArtefactoAProyecto() {
		pasos = new ArrayList<Paso>();
		pasos.add(new Paso1_ElegirOrigenArtefacto());
	}
	
	public AsistenteAgregarNuevoArtefactoAProyecto(String idProyeto) {
		this();
		this.idProyecto = idProyeto;
	}
	
	// -----
	// Estado
	// -----

	private String idProyecto;
	
	private int pasoActual = 0;
	
	private final ArrayList<Paso> pasos;
	
	private final HashMap<String, Serializable> cache = new HashMap<String, Serializable>();

	// -----
	// Leer y escribir estado
	// -----

	public String getIdProyecto() {
		return idProyecto;
	}

	public void setIdProyecto(String idProyecto) {
		this.idProyecto = idProyecto;
	}
	
	public Paso getPasoAnterior() { return pasos.get(pasoActual-1); }
	public Paso getPasoActual() { return pasos.get(pasoActual); }
	
	public Map<String, Serializable> getCache() {
		return cache;
	}
	
	// ------------------------------
	// Realizado y pendiente
	// ------------------------------

	public AccionARealizar getAccionActual() {
		return pasos.get(pasoActual).accion();
	}

	public List<String> listaPendienteDeRealizar() {
		ArrayList<String> cadenas = new ArrayList<String>();
		
		for(int p = pasoActual; p < pasos.size(); p++) {
			cadenas.add(pasos.get(p).comoPendiente());
		}
		
		return cadenas;
	}
	
	public List<String> listaDeLoRealizado() {
		ArrayList<String> cadenas = new ArrayList<String>();
		
		for(int p = 0; p < pasoActual; p++) {
			cadenas.add(pasos.get(p).comoRealizado());
		}
		
		return cadenas;
	}

	
	// ------------------------------
	// Finalizando los pasos
	// ------------------------------
	
	public void cerrarPaso1Con(String eleccion) {

		pasos.get(0).setValor(eleccion);

		// incrementamos el paso actual
		this.pasoActual++;
		
		// según la elección, tenemos que rellenar el resto del camino con los pasos que corresponden
		if(eleccion.equalsIgnoreCase(ORIGEN_NUEVA_RAMA)) {
			
			pasos.add(new Paso2_1_ElegirTipoArtefactoProyectable());
			pasos.add(new Paso3_1_ElegirArtefactoActivo());
			pasos.add(new Paso4_1_Confirmar());
			
		} else {
			
			pasos.add(new Paso2_2_ElegirProyectoActivo());
			pasos.add(new Paso3_2_ElegirArtefactoDeProyecto());
			pasos.add(new Paso4_2_ConfirmarOrigenProyecto());
			
		}
	}
	
	public void cerrarPasoCon(Serializable valor) {
		
		pasos.get(pasoActual).setValor(valor);
		pasoActual++;
	}
	
	// ------------------------------
	// Clases auxiliares
	// ------------------------------
	
	public static enum AccionARealizar {

		ElegirOrigenArtefacto,
		ElegirTipoArtefactoProyectable,
		ElegirArtefactoActivo,
		ConfirmarOrigenNuevaRama,
		ElegirProyectoActivo,
		ElegirArtefactoDeProyecto,
		ConfirmarOrigenProyecto
	}
	
	public static interface ConPaginacion {
		public int getPaginaActual();
		public void setPaginaActual(int pagina);
	}
	
	public abstract static class Paso implements Serializable {
		
		private static final long serialVersionUID = -439698306627668115L;
		
		protected Serializable elegido;

		public abstract AccionARealizar accion();
		
		public void setValor(Serializable valor) { elegido = valor; }
		
		public Serializable getValor() { return elegido; }
		
		public abstract String comoPendiente();

		public abstract String comoRealizado();
	}
	
	public static final class Paso1_ElegirOrigenArtefacto extends Paso {

		private static final long serialVersionUID = -8701121471525751480L;

		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ElegirOrigenArtefacto;
		}
		
		@Override
		public String comoPendiente() {
			return "Elegir origen del artefacto a agregar";
		}
		
		@Override
		public String comoRealizado() {
			return "Ha elegido como origen: " + elegido.toString();
		}
	}

	public static final class Paso2_1_ElegirTipoArtefactoProyectable extends Paso {
		
		private static final long serialVersionUID = -7936914600646393953L;

		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ElegirTipoArtefactoProyectable;
		}
		
		@Override
		public String comoPendiente() {
			return "Elegir un tipo de artefacto proyectable";
		}
		
		@Override
		public String comoRealizado() {
			return "Ha elegido como tipo: " + elegido.toString();
		}
	}

	public static final class Paso3_1_ElegirArtefactoActivo extends Paso implements ConPaginacion {
		
		private static final long serialVersionUID = 6270403926421222854L;

		private int paginaActual = 1;
		
		@Override
		public int getPaginaActual() {
			return paginaActual;
		}
		
		@Override
		public void setPaginaActual(int pagina) {
			paginaActual = pagina;
		}
		
		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ElegirArtefactoActivo;
		}

		@Override
		public String comoPendiente() {
			return "Elegir un artefacto activo";
		}
		
		@Override
		public String comoRealizado() {
			return "Se ha elegido el artefacto: " + elegido.toString();
		}
	}

	public static final class Paso4_1_Confirmar extends Paso {
		
		private static final long serialVersionUID = -4575519989281998672L;

		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ConfirmarOrigenNuevaRama;
		}

		@Override
		public String comoPendiente() {
			return "Confirmar la elección";
		}

		@Override
		public String comoRealizado() {
			return "No sé ";
		}
	}

	public static final class Paso2_2_ElegirProyectoActivo extends Paso {
		
		private static final long serialVersionUID = 661963215973775399L;

		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ElegirProyectoActivo;
		}
		
		@Override
		public String comoPendiente() {
			return "Elegir el proyecto con el que compartir artefacto";
		}

		@Override
		public String comoRealizado() {
			return "No sé ";
		}
	}

	public static final class Paso3_2_ElegirArtefactoDeProyecto extends Paso {
		
		private static final long serialVersionUID = 1897628116190463279L;

		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ElegirArtefactoDeProyecto;
		}

		@Override
		public String comoPendiente() {
			return "Elegir artefacto dentro del proyecto";
		}

		@Override
		public String comoRealizado() {
			return "No sé ";
		}
	}

	public static final class Paso4_2_ConfirmarOrigenProyecto extends Paso {
		
		private static final long serialVersionUID = -4806905465628683969L;

		@Override
		public AccionARealizar accion() {
			return AccionARealizar.ConfirmarOrigenProyecto;
		}
	
		@Override
		public String comoPendiente() {
			return "Confirmar la elección";
		}

		@Override
		public String comoRealizado() {
			return "No sé ";
		}
	}
}
