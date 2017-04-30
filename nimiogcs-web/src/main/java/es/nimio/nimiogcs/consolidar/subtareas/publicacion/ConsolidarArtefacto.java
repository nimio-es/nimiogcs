package es.nimio.nimiogcs.consolidar.subtareas.publicacion;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.consolidar.K;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.EvolucionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.dependencias.Dependencia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.Publicacion;
import es.nimio.nimiogcs.jpa.entidades.publicaciones.PublicacionArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.jpa.specs.Artefactos;
import es.nimio.nimiogcs.jpa.specs.ArtefactosProyecto;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.operaciones.artefactos.externa.ComprobarSincronizacionRamaEvolutiva;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;
import es.nimio.nimiogcs.utils.DateTimeUtils;

public class ConsolidarArtefacto 
	extends ProcesoAsincronoModulo<IDatosPeticionPublicacion> {

	public ConsolidarArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ---

	@Override
	protected String nombreUnicoOperacion(IDatosPeticionPublicacion datos, ProcesoAsincrono op) {
		return "CONSOLIDAR ARTEFACTOS DE LA ETIQUETA '" + datos.getEtiqueta().getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(IDatosPeticionPublicacion datos, ProcesoAsincrono op) {
		// relacionamos con la etiqueta, el proyecto de la etiqueta y con todos los artefactos
		registraRelacionConOperacion(op, datos.getEtiqueta());
		registraRelacionConOperacion(op, datos.getEtiqueta().getProyecto());
		
		for(Artefacto a: datos.getArtefactos())
			registraRelacionConOperacion(op, a);
	}

	@Override
	protected void hazlo(IDatosPeticionPublicacion datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion {
		
		// Todo esto se hará como parte de una publicación
		Publicacion publicacion = registrarPublicacion(datos);
		registraRelacionConOperacion(op, publicacion);
		
		try {
			
			// items que tendremos que procesar
			List<ItemRelacionArtefactosConsolidar> items = itemsAConsolidar(datos);
			
			// tenemos el congelado y el artefacto
			// queda ver qué cosas hay que consolidar
			for(ItemRelacionArtefactosConsolidar irac: items) {
				final boolean conRamas = P.of(irac.base).evolucion().esPorProyecto();
				final boolean conRepositorio = P.of(irac.base).repositorioCodigo() != null;
				
				if(conRamas) {
					consolidarDependencias(irac);
				}
	
				if(conRepositorio) {
					
					consolidarRepositorio(
							datos.getEtiqueta(), 
							irac
					);
					
					// y si, efectivamente es un evolutivo, 
					// lanzamos la resincronización
					if(irac.evolutivo instanceof EvolucionArtefacto) {
						
						// hay que revisar todas los evolutivos y
						// todos los congelados para garantizar 
						// que están o no en consonancia con la rama troncal.

						final List<ITestaferroArtefacto> testaferros = new ArrayList<ITestaferroArtefacto>();
						for(Artefacto testaferro: ce.artefactos().findAll(Artefactos.testaferrosDeUnArtefacto(irac.base)))
							if(testaferro instanceof ITestaferroArtefacto)
								testaferros.add((ITestaferroArtefacto)testaferro);
						final List<MetaRegistro> otros = new ArrayList<MetaRegistro>();
						otros.add(datos.getEtiqueta());
						otros.add(datos.getEtiqueta().getProyecto());
						
						new ComprobarSincronizacionRamaEvolutiva(ce)
						.setUsuarioAnd(this.getUsuario())
						.ejecutarCon(
								new ComprobarSincronizacionRamaEvolutiva.Peticion(
										testaferros,
										otros
								)
						);
					}
				}
			}
			
			// finalmente damos la publicación por terminada
			publicacion.correcta();
			ce.publicaciones().saveAndFlush(publicacion);
		
		} catch(Exception ex) {
			
			// la publicación queda como errónea
			publicacion.conError();
			ce.publicaciones().saveAndFlush(publicacion);
			
			throw new ErrorInesperadoOperacion(ex);
		}
	}
	
	// ------
	
	private Publicacion registrarPublicacion(IDatosPeticionPublicacion peticion) {
		
		escribeMensaje("Crear registro de la publicación.");
		
		Publicacion pb = new Publicacion();
		pb.setCanal(peticion.getCanal());
		pb.setIdDestinoPublicacion(K.ID_CONSOLIDAR);
		pb.enEjecucion();
		pb = ce.publicaciones().save(pb);
		
		escribeMensaje("Registro con ID '" + pb.getId() + "' creado.");
		
		for(Artefacto a: peticion.getArtefactos()) {
			Artefacto b = a instanceof ITestaferroArtefacto ? 
					((ITestaferroArtefacto)a).getArtefactoAfectado()
					: a;
				
			PublicacionArtefacto pa = new PublicacionArtefacto();
			pa.setPublicacion(pb);
			pa.setProyecto(peticion.getEtiqueta().getProyecto());
			pa.setIdEtiqueta(peticion.getEtiqueta().getId());
			pa.setNombreEtiqueta(peticion.getEtiqueta().getNombre());
			pa.setArtefacto(b);
			pa.setDirecto(true);
			ce.artefactosPublicados().save(pa);
			
			escribeMensaje("Asociado artefacto '" + b.getNombre() + "' con la publicación.");
		}
		
		escribeMensaje("");
		
		return pb;
	}
	
	private List<ItemRelacionArtefactosConsolidar> itemsAConsolidar(IDatosPeticionPublicacion datos) {

		escribeMensaje("Calcular la relación de items a consolidar.");
		
		// cogemos la etiqueta y sacamos los datos de los artefactos a publicar 
		final EtiquetaProyecto etiqueta = datos.getEtiqueta();
		
		// recorremos los datos de la petición para rellenar 
		// una lista con los items que hay que consolidar
		final ArrayList<ConsolidarArtefacto.ItemRelacionArtefactosConsolidar> items = new ArrayList<ConsolidarArtefacto.ItemRelacionArtefactosConsolidar>();
		for(final Artefacto a: datos.getArtefactos()) {
			ItemRelacionArtefactosConsolidar irac = new ItemRelacionArtefactosConsolidar();
			if(a instanceof ITestaferroArtefacto) {
				irac.congelado = a;
				irac.base = ((ITestaferroArtefacto)a).getArtefactoAfectado();
			} else {
				irac.base = a;
			}
			items.add(irac);
		}
		
		// para cuando se ha pasado el artefacto y no el congelado
		// queremos relacionarlo con el correspondiente de la etiqueta
		for(RelacionElementoProyectoArtefacto repa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(etiqueta))) {
			
			Artefacto artefacto = repa.getArtefacto();
			Artefacto base = ((ITestaferroArtefacto)artefacto).getArtefactoAfectado();
			
			// recorremos la lista de items buscando el que cuadre con los valores
			// de la relación con la etiqueta
			for(ItemRelacionArtefactosConsolidar irac: items) {
				if(irac.base != null && irac.congelado != null) continue;
				if(irac.congelado == null) {
					if(irac.base.getId().equalsIgnoreCase(base.getId())) 
						irac.congelado = artefacto;
				}
			}
		}
		
		// también queremos localizar el evolutivo del proyecto actual 
		// que supuestamente tiene la rama de código que tenemos que 
		// marcar
		for(RelacionElementoProyectoArtefacto repa: ce.relacionesProyectos().findAll(ArtefactosProyecto.relacionesProyectoArtefactoParaProyecto(etiqueta.getProyecto()))) {
			
			Artefacto artefacto = repa.getArtefacto();
			Artefacto base = ((ITestaferroArtefacto)artefacto).getArtefactoAfectado();
			
			// recorremos la lista de items buscando el que cuadre con los valores
			// de la relación con la etiqueta
			for(ItemRelacionArtefactosConsolidar irac: items) {
				if(irac.base.getId().equalsIgnoreCase(base.getId())) 
					irac.evolutivo = artefacto;
			}
		}
		
		return items;
	}
	
	private void consolidarDependencias(final ItemRelacionArtefactosConsolidar irac) {
		
		final Artefacto artefacto = irac.base;
		final Artefacto congelado = irac.congelado;
		
		escribeMensaje("Consolidar definiciones de dependencias.");
		
		// como parte de la consolidación hay que coger todas las dependencias definidas 
		// en el artefacto congelado y trasladarlas al artefacto final.
		List<Dependencia> dependenciasCongelado = 
				ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(congelado.getId()); 
		List<Dependencia> dependenciasFinal = 
				ce.dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(artefacto.getId());
		
		// Hay tres posibles casos.
		// 1. Las que ya no están y que habrá que eliminar
		// 2. Las que están que deben modificar algún parámetro
		for(Dependencia df: dependenciasFinal) {
			boolean existe = false;
			boolean actualizar = false;
			for(Dependencia dc: dependenciasCongelado) {
				
				// si no son pseudoequivalentes, seguimos
				if(!dc.pseudoEquivalente(df)) continue;
				
				// hemos encontrado una relación potencialmente igual
				existe = true;
				if(!dc.equivalente(df)) {
					df.actualizaDesde(df);
					actualizar = true;
				}
			}
			
			// 1.
			if (!existe) ce.dependenciasArtefactos().delete(df);
			if (actualizar) ce.dependenciasArtefactos().save(df);
		}
		
		// 3. añadir todas las nuevas
		for(Dependencia dc: dependenciasCongelado) {
			boolean existe = false;
			for(Dependencia df: dependenciasFinal) {
				if(df.pseudoEquivalente(dc)) {
					existe = true;
					break;  // no hace falta seguir buscando
				}
			}
			
			// si no la hemos encontrado hay que replicarla
			if(!existe) {
				Dependencia ndf = dc.reproducir();
				ndf.setDependiente(artefacto);
				ce.dependenciasArtefactos().save(ndf);
			}
		}
		
		escribeMensaje("");
	}
	
	private void consolidarRepositorio(EtiquetaProyecto etiqueta, final ItemRelacionArtefactosConsolidar irac) {

		final Artefacto artefacto = irac.base;
		final Artefacto congelado = irac.congelado;
		final Artefacto evolutivo = irac.evolutivo;
		
		escribeMensaje("Consolidar el código de la etiqueta sobre la rama estable.");
		
		// aunque estemos en una estrategia sin ramas, mantemos la carpeta
		// estables y siempre haremos un merge desde la etiqueta publicada.
		final DirectivaRepositorioCodigo drc = P.of(artefacto).repositorioCodigo(); 
		final RepositorioCodigo rc = drc.getRepositorio();
		final String urlEstable = 
				rc.getUriRaizRepositorio()
				+ "/" 
				+ drc.getParcialEstables();
		
		final String urlEtiqueta =
				P.of(congelado)
				.ramaCodigo()
				.getRamaCodigo();
		
		final String urlRama =
				P.of(evolutivo)
				.ramaCodigo()
				.getRamaCodigo();
		
		escribeMensaje("URL estable: " + urlEstable);
		escribeMensaje("URL etiqueta: " + urlEtiqueta);
		escribeMensaje("URL rama: " + urlRama);
		
		// preparamos el entorno local
		final File rutaLocal =
				new File(
					new StringBuilder(ce.environment().getRequiredProperty("file.tmp.folder"))
						.append(File.separatorChar)
						.append(artefacto.getNombre())
						.append(File.separatorChar)
						.append("_reintegrar_")
						.append(DateTimeUtils.convertirAFormaYYYYMMDD(new Date(), true))
						.toString());

		// creamos la carpeta
		rutaLocal.mkdirs();
		rutaLocal.deleteOnExit();

		escribeMensaje("Carpeta temporal creada en '" + rutaLocal.toString() + "'");
		
		// checkout de la rama estable
		Subversion svn = new Subversion(rc);
		svn.checkout(urlEstable, rutaLocal);
		
		escribeMensaje("Código estable descargado en la carpeta temporal.");
		
		// merge del código de la etiqueta sobre la copia de trabajo
		escribeMensaje("Lanzando la reintegración del código.");
		svn.reintegrarEnLocal(rutaLocal, urlEtiqueta);
		escribeMensaje("Código reintegrado, pendiente del commit.");
		svn.commit(rutaLocal, "Reintegrado el código de la etiqueta '" + etiqueta.getNombre() + "'");
		escribeMensaje("Código en el repositorio tras el commit.");
		
		// ahora tenemos que marcar la rama para que no exija el merge 
		// desde la rama estable
		escribeMensaje("Preparando el marcaado de la reintegración para la rama de proyecto");
		final File rutaRama = new File(
				new StringBuilder(ce.environment().getRequiredProperty("file.tmp.folder"))
				.append(File.separatorChar)
				.append(artefacto.getNombre())
				.append(File.separatorChar)
				.append("_marca_")
				.append(DateTimeUtils.convertirAFormaYYYYMMDD(new Date(), true))
				.toString());

		// creamos la carpeta
		rutaRama.mkdirs();
		rutaRama.deleteOnExit();

		escribeMensaje("Carpeta temporal creada en '" + rutaRama.toString() + "'");
				
		// checkout de la rama de trabajo
		svn.checkout(urlRama, rutaRama);
		escribeMensaje("Código de la rama de trabajo descargado en la carpeta temporal.");

		// hacemos el marcado 
		escribeMensaje("Lanzando la marcado de la rama de trabajo.");
		svn.mezclado(rutaRama, urlEstable, true);
		escribeMensaje("Mezclado de la rama de trabajo con la rama estable finalizado pendiente de commit.");
		svn.commit(rutaLocal, "Marcado de la rama de trabajo del proyecto '" + etiqueta.getProyecto().getNombre() + "' usando la rama estable.");
		escribeMensaje("Código en el repositorio tras el commit.");
		
		escribeMensaje("");
	}
	
	// ------
	
	static final class ItemRelacionArtefactosConsolidar {
		
		public Artefacto base = null;
		public Artefacto congelado = null;
		public Artefacto evolutivo = null;
	}
	
}
