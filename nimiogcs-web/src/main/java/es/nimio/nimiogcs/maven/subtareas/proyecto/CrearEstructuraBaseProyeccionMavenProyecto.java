package es.nimio.nimiogcs.maven.subtareas.proyecto;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.proyecto.proyeccion.ITareaPostProyeccionProyecto;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.maven.KMaven;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;
import es.nimio.nimiogcs.utils.DateTimeUtils;

/**
 * Lanza la tarea de crear la estructura de proyección para un proyecto
 */
public class CrearEstructuraBaseProyeccionMavenProyecto 
	extends ProcesoAsincronoModulo<ElementoBaseProyecto>{

	public CrearEstructuraBaseProyeccionMavenProyecto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	@Override
	protected String nombreUnicoOperacion(ElementoBaseProyecto elemento, ProcesoAsincrono op) {
		
		String objeto = elemento instanceof Proyecto ? "EL PROYECTO" : "LA ETIQUETA";
		
		return "CREAR ESTRUCTURA PROYECCIÓN MAVEN PARA "
				+ objeto + " '"
				+ elemento.getNombre()
				+ "' ";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(ElementoBaseProyecto elemento, ProcesoAsincrono op) {

		// de entrada relacionamos el proceso con el elemento de proyecto
		registraRelacionConOperacion(op, elemento);
		if(elemento instanceof EtiquetaProyecto) 
			registraRelacionConOperacion(op, ((EtiquetaProyecto)elemento).getProyecto());
		
		// y con los artefactos asociados a él
		for(RelacionElementoProyectoArtefacto rpa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(elemento))) {
			
			registraRelacionConOperacion(op, rpa.getArtefacto());
			
			// y si el artefacto es evolutivo, con el artefacto que se evoluciona
			if(rpa.getArtefacto() instanceof ITestaferroArtefacto) {
				
				registraRelacionConOperacion(op, ((ITestaferroArtefacto)rpa.getArtefacto()).getArtefactoAfectado());
			}
		}
	}

	@Override
	protected void hazlo(ElementoBaseProyecto elemento, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		// creamos la estructura base en el repositorio
		String subrutaRepoProyecto = crearRutaBaseEnSubversion(elemento);
		
		// creamos las subcarpetas correspondientes a cada uno de los artefactos asociados al proyecto
		crearSubcarpetasArtefactosProyecto(subrutaRepoProyecto, elemento);
		
		// para finalizar, mientras se ejecutan el resto de tareas asociadas, damos de alta el uso en el repositorio
		// pero solamente si no existe ya un uso de este tipo en el proyecto
		final String uriRaiz = elemento instanceof Proyecto ? 
				((Proyecto)elemento).getEnRepositorio().getUriRaizRepositorio()
				: ((EtiquetaProyecto)elemento).getProyecto().getEnRepositorio().getUriRaizRepositorio();
		if(!tieneUsoMaven(elemento)) {
			ProyeccionMavenDeProyecto proyeccion = new ProyeccionMavenDeProyecto();
			proyeccion.setIdElemento(elemento.getId());
			proyeccion.setUrlRepositorio(uriRaiz + "/" + subrutaRepoProyecto);
			ce.usosProyecto().saveAndFlush(proyeccion);
		}
		
		// aquí diferenciamos entre el proyecto y la etiqueta
		// TODO: Esto debería aplicar también a las etiquetas para el análisis de código
		if(elemento instanceof Proyecto) {

			Proyecto proyecto = (Proyecto)elemento;
			
			// procedemos a la postproyección
			for(ITareaPostProyeccionProyecto pp: ce.contextoAplicacion().getBeansOfType(ITareaPostProyeccionProyecto.class).values()) {
				pp.ejecutarTareasPostProyeccion("MAVEN", proyecto, this);
			}
			
		}
		
		// Tanto con el alta/modificación de un proyecto como con el alta de una etiqueta
		// tenemos que reconstruir el entorno de proyecto maven
		// creamos la tarea...
		ProcesoAsincronoModulo<ElementoBaseProyecto> t = new RecrearArchivosPomProyeccionMavenProyecto(ce);
		t.setUsuario(this.getUsuario());
		t.escribeMensajeExterior("Siendo invocada como parte de la ejecución de otra operación.");
	
		// ... y ejecutamos
		//     teniendo en cuenta que ha podido añadirse el uso maven como parte de la operación
		t.ejecutarCon(elemento instanceof Proyecto ? ce.proyectos().findOne(elemento.getId()) : ce.etiquetas().findOne(elemento.getId()));
	}
	
	private boolean tieneUsoMaven(ElementoBaseProyecto elemento) {
		for(UsoYProyeccionProyecto uso: elemento.getUsosYProyecciones())
			if(uso instanceof ProyeccionMavenDeProyecto) return true;
		
		return false;
	}
	
	private String crearRutaBaseEnSubversion(ElementoBaseProyecto elemento) {

		Proyecto proyecto = elemento instanceof Proyecto ? 
				(Proyecto)elemento 
				: ((EtiquetaProyecto)elemento).getProyecto();
		
		// nombre del proyecto
		String finalRutaUso = elemento.getNombre().toLowerCase();

		// subruta donde se creará el proyecto
		String subrutaRepositorio = 
				elemento instanceof Proyecto ?
						proyecto.getEnRepositorio().getSubrutaProyectos()
						: proyecto.getEnRepositorio().getSubrutaPublicacion();
		String rutaUsosDefinitiva =  subrutaRepositorio
				+ "/" + KMaven.SUBRUTA_REPOSITORIO_USO 
				+ "/" + finalRutaUso;

		// creamos la instancia asociada
		Subversion subversion = new Subversion(proyecto.getEnRepositorio());
		
		// si existiera es porque ya ha sido creada en otra reconstrucción
		if(subversion.existeElementoRemoto(rutaUsosDefinitiva)) return rutaUsosDefinitiva;
		
		// si no existe, creamos la carpeta
		subversion.crearCarpeta(rutaUsosDefinitiva);
		
		// la ruta donde se creará la estructura de carpetas
		return rutaUsosDefinitiva;
	}
	
	private void crearSubcarpetasArtefactosProyecto(String rutaProyecto, ElementoBaseProyecto elemento) {

		Proyecto proyecto = elemento instanceof Proyecto ? 
				(Proyecto)elemento 
				: ((EtiquetaProyecto)elemento).getProyecto();

		// Creamos una carpeta local donde descargar el uso de proyecto y poder manipular 
		// las carpetas que representarán cada artefacto en el uso concreto.
		File rutaLocal =
				new File(
					new StringBuilder(ce.environment().getRequiredProperty("file.tmp.folder"))
						.append(File.separatorChar)
						.append(elemento.getNombre())
						.append(File.separatorChar)
						.append("_maven_")
						.append(DateTimeUtils.convertirAFormaYYYYMMDD(new Date(), true))
						.toString());

		// creamos la carpeta
		rutaLocal.mkdirs();
		rutaLocal.deleteOnExit();
		
		// y descargamos el código a local
		// instanciamos la interfaz con el repositorio Subversion
		Subversion subversion = new Subversion(proyecto.getEnRepositorio());
		
		// hacemos el checkout
		subversion.checkout(
				proyecto.getEnRepositorio().getUriRaizRepositorio() + "/" + rutaProyecto, 
				rutaLocal);
		
		// siempre vamos a añadir una serie de ignores a la raíz
		subversion.establecePropiedadAArchivoEnCopiaLocal(
				rutaLocal, "svn:ignore", "target\n.project\n.settings\n.classpath"
		);
		
		// --- ya tenemos la carpeta raíz donde trabajar
		// --- queda ver qué modificaciones hay que realizar
		
		// subcarpetas que, presumiblemente, tenemos que crear
		Map<String, Artefacto> mapaCarpetasArtefactos = new HashMap<String, Artefacto>(); 
		
		// recorremos todos los artefactos asociados
		for(RelacionElementoProyectoArtefacto rpa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(elemento))) {
			
			Artefacto relacionado = rpa.getArtefacto();
			String nombre = (
					relacionado instanceof ITestaferroArtefacto ? 
							((ITestaferroArtefacto)relacionado).getArtefactoAfectado().getNombre() 
							: relacionado.getNombre()
					)
					.toLowerCase(); 
			mapaCarpetasArtefactos.put(nombre, rpa.getArtefacto());
		}
		
		// ya tenemos los nombres de las subcarpetas que deberemos crear, pero tenemos que descargar la estructura en alguna parte
		// preparamos la carpeta de trabajo
		// tomamos las carpetas que ya existen
		List<String> nombresCarpetasExistentes = new ArrayList<String>();
		for(File protoCarpeta: rutaLocal.listFiles()) 
			if(protoCarpeta.isDirectory()) 
				if(!protoCarpeta.getName().equalsIgnoreCase(".svn"))  // excluimos .svn como carpeta eliminable 
					nombresCarpetasExistentes.add(protoCarpeta.getName());
		
		// por un lado tendremos los que hay que añadir  A - B
		// y por otro los que hay que quitar B - A
		// Empezamos eliminando
		for(String e: nombresCarpetasExistentes) {
			if(!mapaCarpetasArtefactos.keySet().contains(e)) {
				subversion.eliminarArchivoEnCopiaDeTrabajo(new File(rutaLocal.toString() + File.separator + e));
			}
		}

		// Continuamos añadiendo las carpetas necesarias
		for(String c: mapaCarpetasArtefactos.keySet()) {
			if(!nombresCarpetasExistentes.contains(c)) {
				File nuevaCarpeta = new File(rutaLocal.toString() + File.separator + c);
				nuevaCarpeta.mkdir();
				subversion.addArchivoLocalACopiaTrabajo(nuevaCarpeta);
				
				// ya tenemos la carpeta añadida en local, pero queda saber si hay que hacer algo relacionado con
				// el código (suponiendo que tenga repositorio de código detrás y rama ya creada)
				final Artefacto artefacto = mapaCarpetasArtefactos.get(c);

				// lo primero es saber si tratamos con un evolutivo o con el artefacto final, que es el que queremos
				final Artefacto f = (artefacto instanceof ITestaferroArtefacto) ? 
						((ITestaferroArtefacto)artefacto).getArtefactoAfectado()
						: artefacto;
						
				// si tiene rama de código, entonces hay que ver qué tenemos que proyectar
				DirectivaRamaCodigo rama = P.of(artefacto).ramaCodigo();
				if(rama!=null) {

					
					// creamos la lente proyectable que necesitamos para definir los atributos
					DirectivaProyeccion proyectable = P.of(f).proyeccion();
							
					// vamos creando los puntos de exportación
					final StringBuilder pe = new StringBuilder();
							
					// revisamos cuántas carpetas externas hay que añadir
					for(P.CarpetaExportacion cpext: P.carpetasExportar(proyectable)) {
						final String p = cpext.getPuntoAnclaje();
						final String r = rama.getRamaCodigo() + "/" + cpext.getRutaExterna();
						
						pe.append(r).append(" ").append(p).append(Strings.LF);
					}
					
					// y las añadimos como externas
					if(pe.length() > 0) {
						subversion.establecePropiedadAArchivoEnCopiaLocal(
								nuevaCarpeta, "svn:externals", pe.toString()
						);
					}
				}
				
				// nos queda añadir los elementos de exclusión, que solo depende 
				// de tener directiva de proyección
				DirectivaProyeccion proyectable = P.of(f).proyeccion();
				if(!Strings.isNullOrEmpty(proyectable.getSvnIgnores())) {
					subversion.establecePropiedadAArchivoEnCopiaLocal(
							nuevaCarpeta, "svn:ignore", proyectable.getSvnIgnores()
					);
				}
			}
		}
		
		// por último hacemos commit
		subversion.commit(rutaLocal, "Cambios en la estructura de carpetas del proyecto '" + elemento.getNombre() + "'");
	}
	
}
