package es.nimio.nimiogcs.config;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MavenRepositoryConfig {

	@Bean(name="repositorioMaven")
	@Lazy
	public RepositorySystem repositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
		
		return locator.getService( RepositorySystem.class );
	}
	
	@Bean(name="sesionMaven")
	@Lazy
	public RepositorySystemSession session( RepositorySystem system ) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		
		LocalRepository localRepo = new LocalRepository( "/tmp/local-repo" );
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ));
		
		return session;
	}
	
}
