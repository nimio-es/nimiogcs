package es.nimio.nimiogcs.web.dto.f.directivas;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Relación entre el id de una directiva y el formulario para un tipo
 */
public final class RDFT {

	private final static Map<String, Class<? extends FormularioBaseDirectiva>> _formularios = new HashMap<String, Class<? extends FormularioBaseDirectiva>>();
	
	public static Map<String, Class<? extends FormularioBaseDirectiva>> formularios() {
		return Collections.unmodifiableMap(_formularios);
	}
	
	static {
		
		_formularios.put("ALCANCES", FormularioDirectivaAlcances.class);
		_formularios.put("COOR_MAVEN", FormularioDirectivaCoordenadasMaven.class);
		_formularios.put("INVENTARIO", FormularioDirectivaInventario.class);
		_formularios.put("CARACTERIZACION", FormularioDirectivaCaracterizacion.class);
		_formularios.put("EVOLUCION", FormularioDirectivaEstrategia.class);
		_formularios.put("ESTRUCT_CODIGO", FormularioDirectivaEstructuraCodigo.class);
		_formularios.put("PROYECCION", FormularioDirectivaProyeccion.class);
		_formularios.put("PROYECCION_MAVEN", FormularioDirectivaProyMaven.class);
		_formularios.put("PUBLICACION_DEPLOYER", FormularioDirectivaPubDeployer.class);
		_formularios.put("PARAMETROS_DEPLOYER", FormularioDirectivaParamDeployer.class);
		_formularios.put("PUBLICACION_JENKINS", FormularioDirectivaPublicacionJenkins.class);
		_formularios.put("REFERENCIAR", FormularioDirectivaReferenciar.class);
		_formularios.put("REPO-CODIGO", FormularioDirectivaRepositorioCodigo.class);
		_formularios.put("TAXONOMIA", FormularioDirectivaTaxonomia.class);
		_formularios.put("VERSION_JAVA", FormularioDirectivaVersionJava.class);
		
	}

}
