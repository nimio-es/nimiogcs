package es.nimio.nimiogcs.web.dto;

import java.util.ArrayList;
import java.util.List;

public final class RespuestasDeployer {


	public static abstract class RespuestaBasePeticionDeployer {

		public abstract String getRespuesta();
	}


	static class ACK extends RespuestaBasePeticionDeployer {

		@Override
		public String getRespuesta() {
			return "OK";
		}
	}
	
	static class NACK extends RespuestaBasePeticionDeployer {

		private List<String> mensajesError = new ArrayList<String>();

		public NACK(String... mensajesError) {
			for(String mensaje: mensajesError) this.mensajesError.add(mensaje);
		}
		
		@Override
		public String getRespuesta() { return "ERROR";}
		
		public List<String> getMensajesError() {
			return mensajesError;
		}

		public void setMensajesError(List<String> mensajes) {
			this.mensajesError = mensajes;
		}
	}
	
	// métodos para preparar las respuestas
	public static RespuestaBasePeticionDeployer ack() {
		return new ACK();
	}
	
	public static RespuestaBasePeticionDeployer nack(String... errores) {
		return new NACK(errores);
	}
	
	
	// --
	// constructor privado para no poder instanciar la clase utilidad o módulo
	private RespuestasDeployer() {}
}
