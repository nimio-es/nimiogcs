package es.nimio.nimiogcs.componentes.proyecto.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.KSonarEtiqueta;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.proyecto.web.DefinicionOperacionPosible;
import es.nimio.nimiogcs.componentes.proyecto.web.IOperacionesPosiblesSobreProyecto;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.ProyeccionMavenDeProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.usos.UsoYProyeccionProyecto;

@Component
public class OperacionesPosiblesAnalisisSonarEtiqueta implements IOperacionesPosiblesSobreProyecto {

	private final IContextoEjecucionBase ce;
	
	@Autowired
	public OperacionesPosiblesAnalisisSonarEtiqueta(final IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	@Override
	public Collection<DefinicionOperacionPosible> defineAcciones(Proyecto proyecto) {
		return new ArrayList<DefinicionOperacionPosible>(); // para el proyecto, nada
	}

	@Override
	public Collection<DefinicionOperacionPosible> defineAcciones(EtiquetaProyecto projectTag) {
		
		final ArrayList<DefinicionOperacionPosible> ops = new ArrayList<DefinicionOperacionPosible>();
		
		// confirmamos que esté activa
		if(!ce.repos().global().buscar(KSonarEtiqueta.PG_ACTIVO).comoValorIgualA("1")) 
			return ops;  // se devuelve vacía
		
		final boolean withMavenProjection = 
				Streams.of(projectTag.getUsosYProyecciones())
				.exists(
						new Predicate<UsoYProyeccionProyecto>() {

							@Override
							public boolean test(UsoYProyeccionProyecto useOrProjection) {
								return useOrProjection instanceof ProyeccionMavenDeProyecto;
							}
						}
				);
		
		if(withMavenProjection) 
			ops.add(
					new DefinicionOperacionPosible(
							"QA_SONAR", 
							"Análisis calidad Sonar", 
							"proyectos/op/qa/sonar/etiqueta"
					)
			);
		
		return ops;
	}

}
