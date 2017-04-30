package es.nimio.nimiogcs.jpa.repositorios;

import java.util.Collection;

import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.relaciones.RelacionElementoProyectoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;

public abstract class ElementosProyecto {

	public abstract Proyectos proyectos();
	public abstract Etiquetas etiquetas();
	public abstract RelacionesArtefactos relaciones();
	public abstract UsosYProyecciones usos();
	
	// ---
	
	public static abstract class Proyectos extends RepositorioBase<Proyecto> {
		
		public abstract RelacionesArtefactos relaciones(); 
		
		// ----
		
		public static abstract class RelacionesArtefactos extends RepositorioBase<RelacionElementoProyectoArtefacto> {}
		
	}
	
	public static abstract class Etiquetas extends RepositorioBase<EtiquetaProyecto> {
		
		public abstract RelacionesArtefactos relaciones();
		
		// ----
		
		public static abstract class RelacionesArtefactos extends RepositorioBase<RelacionElementoProyectoArtefacto> {
			
			public abstract Collection<RelacionElementoProyectoArtefacto> artefactosAfectados(EtiquetaProyecto etiqueta); 
		}
	}
	
	public static abstract class RelacionesArtefactos extends RepositorioBase<RelacionElementoProyectoArtefacto> {
		
		public abstract Collection<RelacionElementoProyectoArtefacto> artefactosAfectados(ElementoBaseProyecto elementoProyecto);
	}
	
	public static abstract class UsosYProyecciones extends RepositorioBase<UsoYProyeccionProyecto> {}
	
}
