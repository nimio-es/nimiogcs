package es.nimio.nimiogcs.operaciones.chequeo;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.chequeo.ISubsistemasExternos;
import es.nimio.nimiogcs.componentes.chequeo.modelo.DescripcionSubsistemasExternos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoBase;

/**
 * Comprobación de las conexiones con los sistemas externos.
 */
public class ChequeoConexionServiciosExternos 
	extends ProcesoAsincronoBase<IContextoEjecucionBase, Boolean> {

	public ChequeoConexionServiciosExternos(IContextoEjecucionBase contextoEjecucion) {
		super(contextoEjecucion);
	}


	// ---
	
	@Override
	protected String nombreUnicoOperacion(Boolean datos, ProcesoAsincrono op) {
		return "COMPROBAR ESTADO DE LAS CONEXIONES DE LOS MÓDULOS";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(Boolean datos, ProcesoAsincrono op) {
		// Esta operación no se relaciona con nada
	}

	@Override
	protected void hazlo(Boolean datos, ProcesoAsincrono op) {
		
		boolean todoOk = true;
		
		// recorremos todos los descriptores de sistemas externos
		// y vamos lanzando consultas para confirmar que funcionan.
		for(ISubsistemasExternos se: ce.componentes(ISubsistemasExternos.class)) {
			
			final DescripcionSubsistemasExternos dse = se.subsistemas();
			
			escribeMensaje("Módulo: " + dse.getModulo());
			
			for(DescripcionSubsistemasExternos.Subsistema s: dse.getSubsistemas()) {

				try {
					
					escribeMensaje("Comprobar '" + s.getNombre() + "'");
					escribeMensaje("Url: '" + s.getUrl() + "'");
					
					// comprobamos la IP
					final InetAddress inetAddress = InetAddress.getByName(s.getDomainName());
					escribeMensaje("IP: " + inetAddress.getHostAddress());
					
					try {
						// comprobamos
						final HttpURLConnection connection = (HttpURLConnection)new URL(s.getUrl()).openConnection();
						connection.setRequestMethod("HEAD");
						if(Strings.isNotEmpty(s.getUsuario())) {
							final String userPassword = s.getUsuario() + ":" + s.getPassword();
							final String encoding = Base64.encodeBase64String(userPassword.getBytes());
							connection.setRequestProperty("Authorization", "Basic " + encoding);						
						}
						connection.connect();

						final int codigoRespuesta = connection.getResponseCode(); 
						if(codigoRespuesta != HttpURLConnection.HTTP_OK) 
							throw new ErrorInesperadoOperacion(
									"No hay conectividad. Código devuelto: " 
									+ codigoRespuesta
							); 
						
						escribeMensaje("... OK");
						escribeMensaje("");
						
					} catch(Exception ex) {
						
						todoOk = false;
						
						escribeMensaje("... ¡¡ERROR!!");
						escribeMensaje("");
						throw ex;
					}
				} catch(Exception ex) {

					// Excepción que mostraremos como un error en la salida y seguimos
					final String mensajeError = Strings.isNullOrEmpty(op.getMensajeError())? "" : op.getMensajeError();
					final StringBuilder sb = 
							new StringBuilder()
							.append(mensajeError.length() > 0 ? mensajeError + "\n" : "")
							.append(dse.getModulo()).append(": ERROR DURANTE LA COMPROBACIÓN\n")
							.append("Url: ").append(s.getUrl()).append("\n")
							.append(ex.getMessage())
							.append("\n");
					op.setMensajeError(sb.toString());
				}
			}

			// --
			escribeMensaje("");
		}
		
		// forzamos una excepción en caso de que haya alguna conexión errónea
		if(!todoOk) throw new ErrorInesperadoOperacion("Existen subsistemas inalcanzables.");
	}
}
