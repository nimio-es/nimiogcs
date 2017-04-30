package es.nimio.nimiogcs.servicios.externos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.junit.Test;

import es.nimio.nimiogcs.servicios.externos.util.ConsoleDependencyGraphDumper;

/**
 * De momento solamente para probar Aether
 */
public class MavenRepositoryTest {

	private static RepositorySystem newRepositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
		
		return locator.getService( RepositorySystem.class );
	}
	
	private static RepositorySystemSession newSession( RepositorySystem system ) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		
		LocalRepository localRepo = new LocalRepository( "/tmp/local-repo" );
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ));
		
		return session;
	}
	
	private static List<RemoteRepository> newRepositoryList() {
		return new ArrayList<RemoteRepository>(
				Arrays.asList(
						new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2").build()
					)
				);
	}
	
	/**
	 * Adaptado de http://wiki.eclipse.org/Aether/Resolving_Dependencies
	 */
	@Test
	public void leerTodasLasDependenciasDeSpringFrameworkCore() throws DependencyCollectionException, DependencyResolutionException {
		
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession( repoSystem );
		
		Dependency dependency = new Dependency( new DefaultArtifact("org.springframework:spring-context:4.1.7.RELEASE"), "compile" );

		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		collectRequest.setRepositories(newRepositoryList());
		DependencyNode node = repoSystem.collectDependencies( session, collectRequest )
				.getRoot();
		
		DependencyRequest dependencyRequest = new DependencyRequest();
		dependencyRequest.setRoot( node );
		
		repoSystem.resolveDependencies( session, dependencyRequest ); 
		
		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		node.accept(nlg);
		
		System.out.println( nlg.getClassPath() );
	}

	
	/**
	 * Adaptado de http://git.eclipse.org/c/aether/aether-demo.git/tree/aether-demo-snippets/src/main/java/org/eclipse/aether/examples/GetDependencyTree.java
	 * @throws DependencyCollectionException 
	 */
	@Test
	public void arbolDependenciasSpringFramework() throws DependencyCollectionException {
		
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession( repoSystem );
		
		Artifact artifact = new DefaultArtifact("org.springframework:spring-context:4.2.0.RELEASE");
		
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot( new Dependency(artifact, ""));
		collectRequest.setRepositories(newRepositoryList());
	
		CollectResult collectResult = repoSystem.collectDependencies( session, collectRequest );
		
		collectResult.getRoot().accept( new ConsoleDependencyGraphDumper());
	}
}
