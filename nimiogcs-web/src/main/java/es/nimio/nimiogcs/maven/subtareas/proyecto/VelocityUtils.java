package es.nimio.nimiogcs.maven.subtareas.proyecto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;

public class VelocityUtils {

	private static final Logger logger = LoggerFactory.getLogger(VelocityUtils.class);
	
	private VelocityUtils() {
	}
	
	static {
		initVelocityEngine();
	}
	
	private static void initVelocityEngine() {
		try {
				Properties p = new Properties();
				p.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.JdkLogChute"); 
				p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
				p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
				p.setProperty("classpath.resource.loader.cache", "true");
				p.setProperty("input.encoding", "UTF-8");
				p.setProperty("output.encoding", "UTF-8");
				
				Velocity.init( p );
		
		} catch (Exception e) {
			logger.error("ERROR: " + e.getMessage(), e);
		}
	}	

	public static String evaluate(final Map<String, Object> context, final String template) throws ErrorInesperadoOperacion {	
		StringWriter writer = new StringWriter();
		evaluate(context, template, writer);
		return writer.getBuffer().toString();
	}
	
	public static void evaluate(final Map<String, Object> context, final String template, final File file) throws ErrorInesperadoOperacion {		
		file.getParentFile().mkdirs();
		try {
			FileWriter writer = new FileWriter(file);
			evaluate(context, template, writer);
		} catch (IOException e) {
			throw new ErrorInesperadoOperacion(e);
		}
	}
	
	private static void evaluate(final Map<String, Object> context, final String template, Writer writer) throws ErrorInesperadoOperacion {		
		VelocityContext ctx = new VelocityContext(context);
		Velocity.evaluate(ctx, writer, "module", template);		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new ErrorInesperadoOperacion(e);
		}
	}
	
}
