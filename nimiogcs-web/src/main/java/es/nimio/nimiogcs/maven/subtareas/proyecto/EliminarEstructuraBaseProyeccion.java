package es.nimio.nimiogcs.maven.subtareas.proyecto;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;

public class EliminarEstructuraBaseProyeccion extends ProcesoAsincronoModulo<ElementoBaseProyecto> {

	public EliminarEstructuraBaseProyeccion(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	
	// --

	@Override
	protected String nombreUnicoOperacion(ElementoBaseProyecto elementoProyecto, ProcesoAsincrono op) {
		return "ELIMINAR ESTRUCTURA MAVEN EN REPOSITORIO PARA '" + elementoProyecto.getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(ElementoBaseProyecto elementoProyecto, ProcesoAsincrono op) {

		// relacionamos con el elemento y, si es una etiqueta, con el proyecto
		registraRelacionConOperacion(op, elementoProyecto);
		if(elementoProyecto instanceof EtiquetaProyecto) 
			registraRelacionConOperacion(op, ((EtiquetaProyecto)elementoProyecto).getProyecto());
	}

	@Override
	protected void hazlo(ElementoBaseProyecto datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion {

		// se trata de coger el uso Maven que tenga y eliminar del repositorio la estructura, 
		// eliminando también el susodicho uso
		escribeMensaje("Buscar la proyección Maven dentro de los usos registrados.");
		ProyeccionMavenDeProyecto pmv = null;
		for(UsoYProyeccionProyecto upp: datos.getUsosYProyecciones()) {
			if(!(upp instanceof ProyeccionMavenDeProyecto)) continue;
			pmv = (ProyeccionMavenDeProyecto)upp;
			break;
		}
		
		if(pmv!=null) {
			
			escribeMensaje("Proyección Maven encontrada. URL de repositorio: " + pmv.getUrlRepositorio());
			
			// el registro de repositorio lo sacamos del proyecto
			final Proyecto p = datos instanceof Proyecto ? (Proyecto)datos : ((EtiquetaProyecto)datos).getProyecto();
			final RepositorioCodigo rc = p.getEnRepositorio();
			
			// creamos la conexión a subversion
			final Subversion svn = new Subversion(rc);
			
			// tanto confirmar existencia como la eliminación trabajan con subrutas
			final String subruta = pmv.getUrlRepositorio().replace(rc.getUriRaizRepositorio() + "/", "");
			
			// si existe la estructura, simplemente la eliminamos
			if(svn.existeElementoRemoto(subruta)) {
				escribeMensaje("Eliminar la estructura de repositorio.");
				svn.eliminarCarpeta(subruta);
			} else {
				escribeMensaje(">> No existe la estructura de repositorio.");
			}
			
			// terminamos eliminándola
			escribeMensaje("Eliminar registro de la proyección.");
			ce.usosProyecto().delete(pmv);
		}
	}
}
