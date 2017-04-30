package es.nimio.nimiogcs.servicios.externos;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import es.nimio.nimiogcs.errores.ErrorOperacionVersionadoCodigo;
import es.nimio.nimiogcs.servicios.externos.Subversion;

public class SubversionTest {

	private static final String path = "/tmp/testrepo";

	private static final class Configuracion
			implements
			es.nimio.nimiogcs.servicios.externos.ConfiguracionSubversion {

		SVNURL url;

		public Configuracion(SVNURL url) {
			this.url = url;
		}

		@Override
		public String getUsuario() {
			return "";
		}

		@Override
		public String getContrasena() {
			return "";
		}

		@Override
		public URI getRaiz() {
			return URI.create(url.toString());
		}

	};

	SVNURL tgtURL;

	@Before
	public void setUp() throws Exception {
		SVNRepositoryFactoryImpl.setup();
		tgtURL = SVNRepositoryFactory.createLocalRepository(new File(path),
				true, false);
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(new File(path));
	}

	/**
	 * Confirma que una carpeta no existe previamente para que no se den falsos
	 * positivos.
	 * @throws SVNException 
	 */
	@Test
	public void comprobacion_control_no_existe_carpeta() throws SVNException {

		Assert.assertFalse(checkUrlExists("EstaCarpetaNoExiste"));
	}

	@Test
	public void crear_carpeta() throws ErrorOperacionVersionadoCodigo,
			SVNException {

		Subversion svn = new Subversion(new Configuracion(this.tgtURL));

		svn.crearCarpeta("prueba1");

		// en este punto comprobamos que exista
		// usando otro mecanismo
		Assert.assertTrue(checkUrlExists("prueba1"));

	}

	public boolean checkUrlExists(String directoryToCheck) throws SVNException {

		SVNRepository repository = SVNRepositoryFactory.create(tgtURL);

		SVNNodeKind nodeKind = repository.checkPath(directoryToCheck, -1);
		return nodeKind == SVNNodeKind.DIR;
	}

}
