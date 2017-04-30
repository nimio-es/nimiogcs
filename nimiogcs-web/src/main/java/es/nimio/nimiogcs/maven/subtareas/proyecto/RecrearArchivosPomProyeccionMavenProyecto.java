package es.nimio.nimiogcs.maven.subtareas.proyecto;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConAlcance;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaConModuloWeb;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.DependenciaPosicional;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccionMaven;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.maven.KMaven;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;
import es.nimio.nimiogcs.utils.DateTimeUtils;

public class RecrearArchivosPomProyeccionMavenProyecto 
	extends ProcesoAsincronoModulo<ElementoBaseProyecto>{

	public RecrearArchivosPomProyeccionMavenProyecto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	@Override
	protected String nombreUnicoOperacion(ElementoBaseProyecto proyecto, ProcesoAsincrono op) {
		
		String objeto = proyecto instanceof Proyecto ?
				"EL PROYECTO" : "LA ETIQUETA";
		
		return "(RE)GENERAR ARCHIVOS POM.XML PROYECCIÓN MAVEN PARA "
				+ objeto 
				+ " '"
				+ proyecto.getNombre()
				+ "' ";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(ElementoBaseProyecto elemento, ProcesoAsincrono op) {
		
		// de entrada relacionamos el proceso con el elemento de proyecto 
		// y con el proyecto, si se trata de una etiqueta
		registraRelacionConOperacion(op, elemento);
		if(elemento instanceof EtiquetaProyecto) 
			registraRelacionConOperacion(op, ((EtiquetaProyecto)elemento).getProyecto());
		
		// y con los artefactos asociados a él
		for(RelacionElementoProyectoArtefacto rpa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(elemento))) {
			
			registraRelacionConOperacion(op, rpa.getArtefacto());
			
			// y si el artefacto es un testaferro, con el artefacto que se evoluciona
			if(rpa.getArtefacto() instanceof ITestaferroArtefacto) {
				registraRelacionConOperacion(op, ((ITestaferroArtefacto)rpa.getArtefacto()).getArtefactoAfectado());
			}
		}
	}

	@Override
	protected void hazlo(ElementoBaseProyecto elemento, ProcesoAsincrono op) throws ErrorInesperadoOperacion {
		
		// 1. empezamos buscando todos los artefactos afectados
		List<Tuples.T3<Boolean, Artefacto, Artefacto>> modulos = clasificarArtefactosComoModulos(); 
		
		// 2. ordenamos los módulos en función de las dependencias
		List<Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>>> modulosConDependencias = ordenarModulosSegunInterrelacion(modulos);
		
		// 3. ya podemos decargar el código del proyecto en local 
		File rutaTemporal = descargarEstructuraProyecto();
		
		// 4. creamos el contenido de los pom.xml
		crearContenidoArchivosPomXml(rutaTemporal, modulosConDependencias);

		// mensaje de finalización
		escribeMensaje("--- fin ----");
	}
	
	// -------------------------------------------------------
	// Métodos auxiliares
	// -------------------------------------------------------

	private List<Tuples.T3<Boolean, Artefacto, Artefacto>> clasificarArtefactosComoModulos() {

		escribeMensaje("Clasificamos los artefactos afectados por el proyecto como módulos a relacionar.");

		List<Tuples.T3<Boolean, Artefacto, Artefacto>> modulos = new ArrayList<Tuples.T3<Boolean,Artefacto,Artefacto>>();
		
		// recorremos todas las relaciones del proyecto con artefactos
		for(RelacionElementoProyectoArtefacto rpa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(this.datos.getDatos()))) {

			// ¿es evolución?
			boolean esUnTestaferro = (rpa.getArtefacto() instanceof ITestaferroArtefacto);
			
			// el artefacto sobre el que buscar las directivas
			Artefacto sobreArtefacto = esUnTestaferro ? ((ITestaferroArtefacto)rpa.getArtefacto()).getArtefactoAfectado() : rpa.getArtefacto();
			
			// ¿tiene coordenadas maven?
			boolean tieneCoordenadasMaven = P.of(sobreArtefacto).coordenadasMaven() != null;
			
			// ¿tiene proyección maven?
			boolean tieneProyeccionMaven = P.of(sobreArtefacto).proyeccionMaven() != null;
			
			// si tiene coordenadas maven y proyección maven, es un módulo
			if(tieneCoordenadasMaven && tieneProyeccionMaven) {
				modulos.add(
						Tuples.tuple(
								esUnTestaferro,   		// si es evolución
								sobreArtefacto,         // de donde se sacan los parámetros maven y las proyecciones
								rpa.getArtefacto()		// de donde se sacan las relaciones evolucionadas (si es el caso)
						)
				);
				
				escribeMensaje("Clasificado el artefacto '" + sobreArtefacto.getNombre() + "' como módulo.");
			} else {
				escribeMensaje("Excluído el artefacto '" + sobreArtefacto.getNombre() + "' como módulo.");
			}
		}

		escribeMensaje("\n");
		escribeMensaje("\n");
		
		// la lista final de módulo
		return modulos;
	}
	
	/**
	 * Lista los artefactos en función de cuál depende de cual, poniendo al principio los que no dependen de ninguno y al final los que van dependiendo del resto
	 */
	private List<Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>>> ordenarModulosSegunInterrelacion(List<Tuples.T3<Boolean, Artefacto, Artefacto>> modulos) {

		escribeMensaje("Ordenamos los módulos según las dependencias, de forma que los primeros no tendrán dependencias con el resto.");

		// vamos a obtener, por cada uno de los artefactos, las dependencias con el resto de artefactos
		final List<Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>>> preModulosConDependencias = new ArrayList<Tuples.T4<Boolean,Artefacto,Artefacto,List<Dependencia>>>();
		
		// 1. recogemos todas las dependencias de cada artefacto y las ordenamos
		for(Tuples.T3<Boolean, Artefacto, Artefacto> modulo: modulos) {
			
			// obtenemos la lista de relaciones
			// el tercer parámetro de la tupla del módulo es el artefacto (evolutivo o no) desde el que sacar las relaciones actuales
			final List<Dependencia> relaciones = ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(modulo._3.getId());
			escribeMensaje("Leídas " + relaciones.size() + " relaciones para '" + modulo._2.getNombre() + "'");
			
			// vamos a crear una lista donde, además del artefacto, metamos un cálculo posicional
			// todas las relaciones que no tengan carácter posicional irán al principio
			final List<Tuples.T2<Integer, Dependencia>> dependenciasPosicionadas = new ArrayList<Tuples.T2<Integer, Dependencia>>();
			for(Dependencia rd: relaciones) {
				if(!(rd instanceof DependenciaPosicional)) dependenciasPosicionadas.add(Tuples.tuple(0, rd));
				else dependenciasPosicionadas.add(Tuples.tuple(((DependenciaPosicional)rd).getPosicion(), rd));
			}
			Collections.sort(
					dependenciasPosicionadas,
					new Comparator<Tuples.T2<Integer,Dependencia>>() {

						@Override
						public int compare(T2<Integer, Dependencia> o1, T2<Integer, Dependencia> o2) {
							return o1._1.compareTo(o2._1);
						}
					}
			);
			
			// ya con la lista ordenada, nos quedamos solo con los artefactos
			final List<Dependencia> dependencias = new ArrayList<Dependencia>();
			for(Tuples.T2<Integer,Dependencia> dp: dependenciasPosicionadas) dependencias.add(dp._2);
			
			// teniendo ya la lista ordenada según posición, creamos un registro del módulo con las dependencias
			preModulosConDependencias.add(Tuples.tuple(modulo._1, modulo._2, modulo._3, dependencias));
			
			// queremos dejar constancia
			final StringBuilder sb = new StringBuilder();
			for(Dependencia d: dependencias) {
				if(P.of(d.getRequerida()).coordenadasMaven() != null) {
					DirectivaCoordenadasMaven l = P.of(d.getRequerida()).coordenadasMaven();
					sb.append(l.getCoordenadaFinal()).append(", ");
				}
			}
			escribeMensaje("Dependencias para '" + modulo._2.getNombre() + "': " + sb.toString());
		}
		
		// 2. ahora, mientras nos queden premodulos, vamos a ir calculando cuántas interdependencias hay
		//    y las iremos pasando a la colección final
		final List<Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>>> modulosYDependencias = new ArrayList<Tuples.T4<Boolean,Artefacto,Artefacto,List<Dependencia>>>();
		int incluidosEnUltimaIteracion = Integer.MAX_VALUE;
		while(incluidosEnUltimaIteracion != 0 && preModulosConDependencias.size() > 0) {
			
			// en cada iteración iremos añadiendo alguno (aquél que no tenga otras dependencias)
			final List<Integer> posicionCandidato = new ArrayList<Integer>();
			
			// por cada módulo vamos a contar cuántas de sus dependencias requieren de los otros módulos
			// para ello primero haremos un diccionario con los módulos restantes
			final HashSet<String> quedan = new HashSet<String>(preModulosConDependencias.size()); 
			for(Tuples.T4<Boolean,Artefacto,Artefacto,List<Dependencia>> preModulo: preModulosConDependencias) quedan.add(preModulo._2.getId());  
			
			for(int i = 0; i < preModulosConDependencias.size(); i++) {
				final Tuples.T4<Boolean,Artefacto,Artefacto,List<Dependencia>> preModulo = preModulosConDependencias.get(i);
				int contador = 0;
				for(Dependencia d: preModulo._4) {
					contador += quedan.contains(d.getRequerida().getId()) ? 1 : 0;
				}
				if(contador==0) posicionCandidato.add(i);
			}
			
			// pasamos a la lista definitiva, los que no tienen más dependencias en esta iteración
			for(int posicion: posicionCandidato) {
				modulosYDependencias.add(preModulosConDependencias.get(posicion));
			}
			
			// y los eliminamos, pero en orden inverso
			Collections.reverse(posicionCandidato);
			for(int posicion: posicionCandidato) {
				preModulosConDependencias.remove(posicion);
			}

			// los que hemos incluido
			incluidosEnUltimaIteracion = posicionCandidato.size();
		}
		
		// si salimos teniendo aún módulos sin asignar, entonces algo malo hay  (ciclo)
		if(preModulosConDependencias.size() > 0) 
			throw new ErrorCicloEntreLosModulos();
		
		
		// a nivel informativo, dejamos constancia del orden de los módulos
		StringBuilder sb = new StringBuilder(); 
		for(Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>> moduloFinal: modulosYDependencias)
			sb.append(moduloFinal._2.getNombre().toLowerCase()).append(", ");
		escribeMensaje("El orden final para los módulos del proyecto '" + datos.getDatos().getNombre() + "': " + sb.toString());

		escribeMensaje("\n");
		escribeMensaje("\n");

		return modulosYDependencias;
	}
	
	/**
	 * Descargamos (checkout) el código del proyecto 
	 */
	private File descargarEstructuraProyecto() {

		escribeMensaje("Descargar la estructura de proyecto a una carpeta local/temporal antes de realizar la creación/modificación de los archivos pom.xml");

		// para la ruta necesitamos obtener la proyección maven asociada
		ProyeccionMavenDeProyecto proyeccion = null;
		for(UsoYProyeccionProyecto up: datos.getDatos().getUsosYProyecciones())
			if(up instanceof ProyeccionMavenDeProyecto) proyeccion = (ProyeccionMavenDeProyecto)up;
		
		if(proyeccion==null) 
			throw new ErrorArtefactoSinProyeccionMaven();

		escribeMensaje("La estructura de proyecto se descargará desde: " + proyeccion.getUrlRepositorio());
		
		// creamos la carpeta
		File rutaLocal =
				new File(
					new StringBuilder(ce.environment().getRequiredProperty("file.tmp.folder"))
						.append(File.separatorChar)
						.append(datos.getDatos().getNombre())
						.append(File.separatorChar)
						.append("_poms_")
						.append(DateTimeUtils.convertirAFormaYYYYMMDD(new Date(), true))
						.toString());

		// creamos la carpeta
		rutaLocal.mkdirs();
		rutaLocal.deleteOnExit();
		
		escribeMensaje("Carpeta temporal creada en '" + rutaLocal.toString() + "'");

		// vamos a necesitar, también, los datos del repositorio
		Proyecto proyecto = datos.getDatos() instanceof Proyecto ?
				(Proyecto)datos.getDatos()
				: ((EtiquetaProyecto)datos.getDatos()).getProyecto();
		RepositorioCodigo repositorio = proyecto.getEnRepositorio();

		// lanzamos la operación de descarga
		Subversion svn = new Subversion(repositorio);
		svn.checkout(
				proyeccion.getUrlRepositorio(), 
				rutaLocal, 
				true);
		
		escribeMensaje("Estructura de proyecto descargada");
		escribeMensaje("\n");
		escribeMensaje("\n");
		
		return rutaLocal;
	}
	
	/**
	 * Genera los módulos correspondientes
	 * @throws Throwable 
	 */
	 void crearContenidoArchivosPomXml(File rutaLocal, List<Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>>> modulos) throws ErrorInesperadoOperacion {

		escribeMensaje("Crear o recrear los archivos pom.xml padre y de cada módulo");
		
		Proyecto proyecto = datos.getDatos() instanceof Proyecto ?
				(Proyecto)datos.getDatos()
				: ((EtiquetaProyecto)datos.getDatos()).getProyecto();
		
		// 1. empezamos con el padre
		crearContenidoArhivoPomPadre(rutaLocal, modulos);
		
		// 2. para cada módulo vamos a crear el pom correspondiente
		//    -- para agilizar vamos a calcular un set con todos los nombres de todos los módulos
		HashSet<String> nombresModulos = new HashSet<String>();
		for(Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>> modulo: modulos) nombresModulos.add(modulo._2.getNombre());
		
		//    -- ahora vamos, módulo a módulo, creando el archivo pom.xml que le corresponde
		for(Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>> modulo: modulos) {
			crearContenidoArhivoPomModulo(rutaLocal, modulo._2, modulo._3, modulo._4, nombresModulos);
		}
		
		// 3. hacemos commit de los cambios
		escribeMensaje("Subimos los cambios al repositorio");
		new Subversion(
				proyecto.getEnRepositorio()
		)
		.commit(
				rutaLocal, 
				"Reconstrucción de los archivos pom.xml del proyecto '" + datos.getDatos().getNombre() + "'"
		);
		
		
		escribeMensaje("\n");
		escribeMensaje("\n");
	}
	
	/**
	 * Crea el archivo pom.xml padre 
	 */
	private void crearContenidoArhivoPomPadre(File rutaLocal, List<Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>>> modulos) throws ErrorInesperadoOperacion {

		escribeMensaje("Crear o recrear el archivo pom.xml padre");

		final Proyecto proyecto = datos.getDatos() instanceof Proyecto ?
				(Proyecto)datos.getDatos()
				: ((EtiquetaProyecto)datos.getDatos()).getProyecto();
		
		// tenemos a mano las lentes para las directivas de proyección maven para los artefactos/módulos y sus correspondientes tipos
		final HashMap<String, DirectivaProyeccionMaven> proyeccionesTipo = new HashMap<String, DirectivaProyeccionMaven>();
		final HashMap<String, DirectivaProyeccionMaven> proyeccionesArtefactos = new HashMap<String, DirectivaProyeccionMaven>();
		for(Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>> m: modulos) {
			
			// empezamos cogiendo la del tipo
			TipoArtefacto ta = m._2.getTipoArtefacto();
			if(!proyeccionesTipo.containsKey(ta.getNombre())) {
				DirectivaProyeccionMaven dr = PT.of(ta).proyeccionMaven();
				if(dr!=null) proyeccionesTipo.put(ta.getNombre(), dr);
			}
			
			// y comprobamos que haya del artefacto
			DirectivaProyeccionMaven dra = P.of(m._2).proyeccionMaven();
			if(dra!=null) proyeccionesArtefactos.put(m._2.getNombre(), dra);
		}

		// preparamos un contexto para pasárselo a velocity y que procese
		// el archivo final.
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("datos", datos.getDatos());
		
		// la ruta del archivo pom
		final File rutaPom = new File(rutaLocal.toString() + File.separator + "pom.xml");
		escribeMensaje("El archivo pom.xml se creará en '" + rutaPom.toString() + "'");
		
		// ¿existe ya?
		final boolean existe = rutaPom.exists();
		if(existe) escribeMensaje("El archivo pom.xml padre ya existía");
		else escribeMensaje("El archivo pom.xml padre no existe");
		
		// ---
		// la plantilla del archivo pom padre existe en los parámetros globales
		ParametroGlobal parametroPlantillaGlobal = ce.global().findOne(KMaven.PG_PLANTILLA_MAVEN_PADRE); 
		if(parametroPlantillaGlobal==null || Strings.isNullOrEmpty(parametroPlantillaGlobal.getContenido()))
			throw new ErrorInesperadoOperacion(
					"No existe un parámetro global '" + KMaven.PG_PLANTILLA_MAVEN_PADRE + "'"
			);
		
		// pasamos la plantilla a una cadena de texto
		String plantilla = parametroPlantillaGlobal.getContenido();
		
		// ---
		// calculamos cada una de las partes
		// 1. el identificador
		StringBuilder identificador = new StringBuilder()
			.append("<groupId>es.nimio.proyectos</groupId>\n")
			.append("<artifactId>").append(proyecto.getNombre().toLowerCase()).append("</artifactId>\n")
			.append("<packaging>pom</packaging>\n")
			.append("<version>").append(datos.getDatos().getNombre()).append("</version>\n")
			.append("<name>").append(datos.getDatos().getNombre()).append("</name>\n");

		// 2. las propiedades
		StringBuilder propiedades = new StringBuilder();
		
		// recorremos la de todos los tipos y las añadimos
		for(String tipo: proyeccionesTipo.keySet()) {
			
			String propiedadesTipo = proyeccionesTipo.get(tipo).getPropiedadesGlobales();
			if(Strings.isNotEmpty(propiedadesTipo)) {
				propiedades
					.append("<!-- propiedades globales para el tipo: ").append(tipo.toLowerCase()).append(" -->\n")
					.append(propiedadesTipo)
					.append("\n");
			}
		}
		
		// recorremos los artefactos
		for(String ar: proyeccionesArtefactos.keySet()) {
			
			String propiedadesArtefacto = proyeccionesArtefactos.get(ar).getPropiedadesGlobales();
			if(Strings.isNullOrEmpty(propiedadesArtefacto)) continue;
			
			propiedades
				.append("<!-- propiedades globales para el artefacto/módulo: ").append(ar.toLowerCase()).append(" -->\n")
				.append(propiedadesArtefacto)
				.append("\n");
		}
		
		// 3. las dependencias globales
		StringBuilder gestionDependencias = new StringBuilder();
		
		// recorremos todos los tipos
		for(String tipo: proyeccionesTipo.keySet()) {
			
			String depGestTipo = proyeccionesTipo.get(tipo).getGestionDependenciasGlobales();
			if(Strings.isNullOrEmpty(depGestTipo)) continue;
			
			gestionDependencias
				.append("<!-- gestión dependencias globales para el tipo: ").append(tipo.toLowerCase()).append(" -->")
				.append(depGestTipo)
				.append("\n");
		}

		// recorremos los artefactos
		for(String ar: proyeccionesArtefactos.keySet()) {
			
			String depGestArtefacto = proyeccionesArtefactos.get(ar).getGestionDependenciasGlobales();
			if(Strings.isNullOrEmpty(depGestArtefacto)) continue;
			
			gestionDependencias
				.append("<!-- gestión dependencias globales para el artefacto/módulo: ").append(ar.toLowerCase()).append(" -->\n")
				.append(depGestArtefacto)
				.append("\n");
		}
		
		// 4. módulos
		StringBuilder mods = new StringBuilder();
		for(Tuples.T4<Boolean, Artefacto, Artefacto, List<Dependencia>> modulo: modulos) {
			mods.append("<module>").append(modulo._2.getNombre().toLowerCase()).append("</module>\n");
		}
		
		// 5. gestión de los plugins globales
		StringBuilder gestPlugins = new StringBuilder();
		
		// recorremos todos los tipos
		for(String tipo: proyeccionesTipo.keySet()) {
			
			String plugInGestTipo = proyeccionesTipo.get(tipo).getGestionPluginsGlobales();
			if(Strings.isNullOrEmpty(plugInGestTipo)) continue;
			
			gestPlugins
				.append("<!-- gestión plugins globales para el tipo: ").append(tipo.toLowerCase()).append(" -->")
				.append(plugInGestTipo)
				.append("\n");
		}

		// recorremos los artefactos
		for(String ar: proyeccionesArtefactos.keySet()) {
			
			String plugInGestArtefacto = proyeccionesArtefactos.get(ar).getGestionPluginsGlobales();
			if(Strings.isNullOrEmpty(plugInGestArtefacto)) continue;
			
			gestPlugins
				.append("<!-- gestión plugins globales para el artefacto/módulo: ").append(ar.toLowerCase()).append(" -->\n")
				.append(plugInGestArtefacto)
				.append("\n");
		}

		// 6. plugins
		StringBuilder plugins = new StringBuilder();
		
		// recorremos todos los tipos
		for(String tipo: proyeccionesTipo.keySet()) {
			
			String plugInTipo = proyeccionesTipo.get(tipo).getPluginsGlobales();
			if(Strings.isNullOrEmpty(plugInTipo)) continue;
			
			plugins
				.append("<!-- plugins globales para el tipo: ").append(tipo.toLowerCase()).append(" -->")
				.append(plugInTipo)
				.append("\n");
		}

		// recorremos los artefactos
		for(String ar: proyeccionesArtefactos.keySet()) {
			
			String plugInArtefacto = proyeccionesArtefactos.get(ar).getPluginsGlobales();
			if(Strings.isNullOrEmpty(plugInArtefacto)) continue;
			
			plugins
				.append("<!-- plugins globales para el artefacto/módulo: ").append(ar.toLowerCase()).append(" -->\n")
				.append(plugInArtefacto)
				.append("\n");
		}

		String version = DateTimeUtils.formaReducida(parametroPlantillaGlobal.getModificacion());
		
		// ya tenemos todas las variables a sustituir
		
		// ---
		// incorporamos los bloques a sustituir en el contexto
		ctx.put("IDENTIFICACION", indentando(4, identificador.toString()));
		ctx.put("LISTAPROPIEDADES", indentando(8, propiedades.toString()));
		ctx.put("DEPENDENCIASGESTIONADAS", indentando(12, gestionDependencias.toString()));
		ctx.put("MODULOS", indentando(8, mods.toString()));
		ctx.put("PLUGINSGESTIONADAS", indentando(16, gestPlugins.toString()));
		ctx.put("PLUGINS", indentando(12, plugins.toString()));
		ctx.put("VERSIONPLANTILLA", version);
		
		
		// ---
		// y escribimos
		VelocityUtils.evaluate(ctx, plantilla, rutaPom);

		
		// ---
		// y si no existía, lo agregamos
		if(!existe) 
			new Subversion(
					proyecto.getEnRepositorio()
			)
			.addArchivoLocalACopiaTrabajo(rutaPom);
		
		escribeMensaje("\n");
		escribeMensaje("\n");
	}
	
	/**
	 * Crea el pom.xml del módulo
	 * @throws Throwable 
	 */
	private void crearContenidoArhivoPomModulo(File rutaLocalBase, Artefacto modulo, Artefacto evolutivo, List<Dependencia> dependencias, Set<String> modulos) throws ErrorInesperadoOperacion {
		
		escribeMensaje("Craer archivo pom.xml para el módulo '" + modulo.getNombre() + "'");
		
		final Proyecto proyecto = datos.getDatos() instanceof Proyecto ?
				(Proyecto)datos.getDatos()
				: ((EtiquetaProyecto)datos.getDatos()).getProyecto();
		
		// ----
		// tenemos las lentes a mano
		HashMap<String, DirectivaProyeccionMaven> proyeccionesMaven = new HashMap<String, DirectivaProyeccionMaven>();
		if(P.of(modulo).directiva(DirectivaProyeccionMaven.class)!=null)  // usar la proyecion maven devuelve directamente el del tipo 
			proyeccionesMaven.put(
					"artefacto", 
					P.of(modulo).directiva(DirectivaProyeccionMaven.class)
			);
		if(PT.of(modulo.getTipoArtefacto()).proyeccionMaven()!=null) 
			proyeccionesMaven.put(
					"tipo", 
					PT.of(modulo.getTipoArtefacto()).proyeccionMaven()
			);
		
		
		// ----
		// calculamos la ruta del archivo pom.xml
		final File rutaPom = 
				new File(
						rutaLocalBase.toString() 
						+ File.separator + modulo.getNombre().toLowerCase() 
						+ File.separator + "pom.xml"
				);
		final boolean existe = rutaPom.exists();
		
		// ----
		// cargamos la plantilla
		final ParametroGlobal registroPlantilla = ce.global().findOne(KMaven.PG_PLANTILLA_MAVEN_MODULO);
		if(registroPlantilla==null || Strings.isNullOrEmpty(registroPlantilla.getContenido())) 
			throw new ErrorInesperadoOperacion("No existe la variable global '" + KMaven.PG_PLANTILLA_MAVEN_MODULO + "' en el registro");
		final String plantilla = registroPlantilla.getContenido(); 
		
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("proyecto", proyecto);
		ctx.put("datos", datos.getDatos());

		// ----
		// toca calcular el contenido que habrá que añadirse
		// 1. el padre
		
		ctx.put("modulo", modulo);
		// 2. el identificador
		if(P.of(modulo).coordenadasMaven()!=null) {

			// al tener directiva vamos a coger los valores definidos a nivel del artefacto
			ctx.put("coordenadas", P.of(modulo).coordenadasMaven());
		}
		// 3. lista de propiedades
		// recorreremos la lista de directivas del tipo y del artefacto e iremos convirtiendo todos los valores en propiedades
		// siempre que la directiva (su id) forme parte de la lista de directivas a convertir
		ParametroGlobal directivasAPropiedad = ce.global().findOne("PROYECCION.MAVEN.DIRECTIVAS.PROPIEDADES");
		final List<String> directivasAIncluir = new ArrayList<String>();
		if(directivasAPropiedad != null && Strings.isNotEmpty(directivasAPropiedad.getContenido()))
			directivasAIncluir.addAll(Arrays.asList(directivasAPropiedad.getContenido().replace(" ", "").replace("\n", "").split(",")));
		
		// añadimos las propiedades calculadas para el artefacto y el tipo 
		List<Map<String, String>> props = new ArrayList< Map<String, String>>();

		
		// para el artefacto
		props.add(extractProperties(modulo.getDirectivasArtefacto(), directivasAIncluir, "artefacto"));
		
		// para el evolutivo, si es diferente
		if(!evolutivo.getId().equalsIgnoreCase(modulo.getId())) {
			props.add(extractProperties(evolutivo.getDirectivasArtefacto(), directivasAIncluir, "evolutivo"));
		}
		// para el tipo
		props.add(extractProperties(modulo.getTipoArtefacto().getDirectivasTipo(), directivasAIncluir, "tipo"));
		
		HashMap<String, String> mvnprops = new HashMap<String, String>();
		// además, tiraremos de lo que tenga definido la directiva de proyección maven
		for(Entry<String, DirectivaProyeccionMaven> cv: proyeccionesMaven.entrySet()) {
			String propsAdicinales = cv.getValue().getPropiedadesAdicionales();
			if(!Strings.isNullOrEmpty(propsAdicinales)) {
				HashMap<String, String> especiales = toMap(propsAdicinales);
				mvnprops.putAll(especiales);
			}
		}
		props.add(mvnprops);
		
		// por último quedan los casos especiales, como la versión java
		ParametroGlobal directivasEspeciales = ce.global().findOne("PROYECCION.MAVEN.DIRECTIVAS.PROPIEDADES.ESPECIALES");
		if(directivasEspeciales != null && Strings.isNotEmpty(directivasEspeciales.getContenido())) {
			
			// convertimos en un mapa
			HashMap<String, String> especiales = toMap(directivasEspeciales.getContenido());
			
			HashMap<String, String> especiales2 = new HashMap<String, String>();
			// recorremos las claves buscando la directiva en cada nivel.
			// el primero encontrado es el que se tomará
			for(String ds: especiales.keySet()) {
				DirectivaBase dv = P.of(evolutivo).buscarDirectiva(ds);
				if(dv == null) dv = P.of(modulo).buscarDirectiva(ds);
				if(dv == null) dv = PT.of(modulo.getTipoArtefacto()).buscarDirectiva(ds);
				
				if(dv != null) {
					for(Entry<String, String> claveValor: dv.getMapaValores().entrySet()) {
						// pasamos la directiva a NULL porque nos interesa que no incorpore el valor de la directiva, 
						// sino que lo deje como como el prefijo y el valor clave 
						addDirectiva(especiales.get(ds), null, claveValor, especiales2);
					}
				}
			}
			props.add(especiales2);
		}
		
		ctx.put("props", props);
		
		// 4. dependencias
		StringBuilder deps = new StringBuilder("\n");
		// añadimos las definidias por artefacto o tipo como previas
		for(Entry<String, DirectivaProyeccionMaven> cv: proyeccionesMaven.entrySet()) {
			String preDeps = cv.getValue().getDependenciasAdicionalesPrevias();
			if(!Strings.isNullOrEmpty(preDeps))
				deps.append("<!-- dependencias fijas definidas a nivel de tipo -->\n")
				.append(VelocityUtils.evaluate(ctx, preDeps))
				.append("\n");
		}
		ctx.put("PREDEPS", indentando(8, deps.toString()));
		
		List<Map<String, Object>> deps_relaciones = new ArrayList<Map<String, Object>>();
		// calculamos las dependencias desde las relaciones
		for(Dependencia dependencia: dependencias) {
			
			// tenemos que cargar la directiva de coordenada maven del destino
			DirectivaCoordenadasMaven coordenada = P.of(dependencia.getRequerida()).coordenadasMaven();

			// si no tiene coordenada definida, ignoramos la dependencia
			if(coordenada != null) {
				Map<String, Object> p = new HashMap<String, Object>();
				
				p.put("coordenadas", coordenada);
				p.put("dependencia", dependencia);
				
				boolean esModulo = modulos.contains(dependencia.getRequerida().getNombre());
				p.put("esModulo", esModulo);
				
				if(dependencia instanceof DependenciaConAlcance) {
					DependenciaConAlcance ra = (DependenciaConAlcance)dependencia;
					p.put("scope", ra.getAlcanceElegido().scopeMaven());
					p.put("opcional", ra.getAlcanceElegido().esOpcional());
				}
				if(dependencia instanceof DependenciaConModuloWeb) {
					DependenciaConModuloWeb ra = (DependenciaConModuloWeb)dependencia;
					p.put("contextRoot", ra.getContextRoot());
				}
				deps_relaciones.add(p);
				
			}
		}
		ctx.put("deps_relaciones", deps_relaciones);
		
		// y añadimos las dependencias definidas a nivel de artefacto o de tipo como posteriores
		StringBuilder postDependencies = new StringBuilder("\n");
		for(Entry<String, DirectivaProyeccionMaven> cv: proyeccionesMaven.entrySet()) {
			String postDeps = cv.getValue().getDependenciasAdicionalesPosteiores();
			if(!Strings.isNullOrEmpty(postDeps))
				postDependencies.append("<!-- dependencias fijas definidas a nivel de tipo -->\n")
				.append(VelocityUtils.evaluate(ctx, postDeps))
				.append("\n");
		}
		ctx.put("POSTDEPS", indentando(8, postDependencies.toString()));
		
		// 6. plugins
		StringBuilder plugins = new StringBuilder();

		for(Entry<String, DirectivaProyeccionMaven> cv: proyeccionesMaven.entrySet()) {
			String pluginsV = cv.getValue().getPlugins();
			if(!Strings.isNullOrEmpty(pluginsV))
				plugins
				.append("<!-- plugins para ").append(cv.getKey()).append(" -->\n")
				.append(VelocityUtils.evaluate(ctx, pluginsV))
				.append("\n");
		}

		// 7. las carpetas
		StringBuilder carpetas = new StringBuilder();
		for(Entry<String, DirectivaProyeccionMaven> cv: proyeccionesMaven.entrySet()) {
			String carpetasV = cv.getValue().getCarpetas();
			if(!Strings.isNullOrEmpty(carpetasV)) 
				carpetas
				.append("<!-- carpetas para ").append(cv.getKey()).append(" -->\n")
				.append(VelocityUtils.evaluate(ctx, carpetasV))
				.append("\n");
		}
		
		// 8. perfiles
		StringBuilder perfiles = new StringBuilder();
		
		for(Entry<String, DirectivaProyeccionMaven> cv: proyeccionesMaven.entrySet()) {
			String perfilesV = cv.getValue().getPerfiles();
			if(!Strings.isNullOrEmpty(perfilesV))
				perfiles
				.append("<!-- perfiles para ").append(cv.getKey()).append(" -->\n")
				.append(VelocityUtils.evaluate(ctx, perfilesV))
				.append("\n");
		}

		// 9. la versión
		String version = DateTimeUtils.formaReducida(registroPlantilla.getModificacion());
		
		ctx.put("PLUGINS", indentando(12, plugins.toString()));
		ctx.put("CARPETAS", indentando(8, carpetas.toString()));
		ctx.put("PERFILES", indentando(8, perfiles.toString()));
		ctx.put("VERSIONPLANTILLA", version);
		
		VelocityUtils.evaluate(ctx, plantilla, rutaPom);
		
		// ---
		// y si no existía, lo agregamos
		if(!existe) 
			new Subversion(
					proyecto.getEnRepositorio()
			)
			.addArchivoLocalACopiaTrabajo(rutaPom);
		
		escribeMensaje("\n");
		escribeMensaje("\n");
	}

	private HashMap<String, String> toMap(String directiva) {
		HashMap<String, String> especiales = new HashMap<String, String>();
		for(String linea: directiva.split("\n")) {
			String[] claveValor = linea.split("=");
			especiales.put(claveValor[0], sanitize(claveValor[1]));
		}
		return especiales;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> extractProperties(Collection<?> directivas, 
			final List<String> directivasAIncluir, String prefix) {
		Map<String, String> p = new HashMap<String, String>();
		for(DirectivaBase directiva: (Collection<DirectivaBase>)directivas) {
			if(directivasAIncluir.contains(directiva.getDirectiva().getId())) {
				for(Entry<String, String> claveValor: directiva.getMapaValores().entrySet()) {
					if(Strings.isNullOrEmpty(claveValor.getValue())) continue;
					addDirectiva(prefix, directiva, claveValor, p);
				}
			}
		}
		return p;
	}
	
	private void addDirectiva(String prefix, DirectivaBase directiva, Entry<String, String> claveValor, Map<String, String> p ){
		String propiedad = 
				prefix
				+ (directiva != null ? "." + toDot(directiva.getDirectiva().getId()) : "")
				+ "." + toDot(claveValor.getKey());
		final String valor = sanitize(claveValor.getValue());
		p.put(propiedad, valor);
	}
	
	private String toDot(String s){
		return s.toLowerCase().replace("-", ".").replace("_", ".");
	}
	private String sanitize(String s){
		return s.replace("\n", ";").replace("\r", "");
	}
	
	/**
	 * Indenta una cadena de texto (donde habrá saltos de línea) según los caracteres indicados
	 * @param caracteres
	 * @param original
	 * @return
	 */
	private String indentando(int espacios, String original) {
		
		String[] lineas = original.split("\n");
		StringBuilder indentado = new StringBuilder();
		for(String linea: lineas) {
			for(int i=0; i<espacios;i++) indentado.append(" ");
			indentado.append(linea).append("\n");
		}
		
		return indentado.toString();
	}
	
	
	// ---
	
	static final class ErrorCicloEntreLosModulos extends RuntimeException {

		private static final long serialVersionUID = -2492626641558152922L;
		
		public ErrorCicloEntreLosModulos() {
			super("Todo apunta a la existencia de ciclos entre las relaciones de los módulos.");
		}
	}
	
	static final class ErrorArtefactoSinProyeccionMaven extends RuntimeException {
		
		private static final long serialVersionUID = 6735988664934205881L;

		public ErrorArtefactoSinProyeccionMaven() {
			super("No podemos recrear la estructura de un elemento que no tiene un uso Maven definido.");
		}
	}

}
