package es.nimio.nimiogcs.componentes.chequeo.modelo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class DescripcionSubsistemasExternos {

	private final String modulo;
	private final Collection<DescripcionSubsistemasExternos.Subsistema> subsistemas;
	
	public DescripcionSubsistemasExternos(
			final String modulo,
			final Collection<DescripcionSubsistemasExternos.Subsistema> subsistemas) {
		
		this.modulo = modulo;
		this.subsistemas = subsistemas;
	}
	
	public DescripcionSubsistemasExternos(
			final String modulo,
			final DescripcionSubsistemasExternos.Subsistema... subsistemas)
	{
		this(modulo, Arrays.asList(subsistemas));
	}
	
	// --
	
	/**
	 * Extracts the part of domain name from URL
	 */
	public static String extractDomainName(final String url) {

		final String urlWithoutProtocol = url.replace("http://", "").replace("https://", "");
		
		final int firstAppearanceOfColon = urlWithoutProtocol.indexOf(":");
		final int firstAppearanceOfSlash = urlWithoutProtocol.indexOf("/");
		final boolean hasNoColon = firstAppearanceOfColon == -1;
		final boolean hasNoSlash = firstAppearanceOfSlash == -1;
		if(hasNoColon && hasNoSlash) return urlWithoutProtocol;
		if(hasNoColon) return urlWithoutProtocol.substring(0, firstAppearanceOfSlash);
		return urlWithoutProtocol.substring(0, firstAppearanceOfColon);
	}

	// --
	
	public String getModulo() {
		return modulo;
	}

	public Collection<DescripcionSubsistemasExternos.Subsistema> getSubsistemas() {
		return Collections.unmodifiableCollection(subsistemas);
	}
	
	
	// ---

	public static final class Subsistema {
		
		private final String nombre;
		private final String domainName;
		private final String url;
		private final String usuario;
		private final String password;
		
		public Subsistema(
				final String nombre,
				final String domainName,
				final String url,
				final String usuario,
				final String password) {
			this.nombre = nombre;
			this.domainName = domainName;
			this.url = url;
			this.usuario = usuario;
			this.password = password;
		}

		public String getNombre() {
			return nombre;
		}
		
		public String getDomainName() {
			return domainName;
		}

		public String getUrl() {
			return url;
		}

		public String getUsuario() {
			return usuario;
		}

		public String getPassword() {
			return password;
		}
	}
}
