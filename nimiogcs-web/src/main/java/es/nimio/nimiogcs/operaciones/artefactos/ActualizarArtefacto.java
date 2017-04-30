package es.nimio.nimiogcs.operaciones.artefactos;

import java.util.Collection;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public class ActualizarArtefacto<T extends Artefacto> 
	extends OperacionInternaModulo<Tuples.T2<T, Collection<DirectivaBase>>, T> {

	public ActualizarArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// =================================================
	// Particularidades de la operación
	// =================================================

	private IContextoEjecucion ice() { return (IContextoEjecucion)ce; }
	
	@Override
	protected String nombreUnicoOperacion(Tuples.T2<T, Collection<DirectivaBase>> datos, Operacion op) {

		return "MODIFICAR DATOS '" + datos._1.getNombre() + "' (cod. '" + op.getId() + "')";
	};

	@Override
	protected void relacionarOperacionConEntidades(Tuples.T2<T, Collection<DirectivaBase>> datos, Operacion op) {
		registraRelacionConOperacion(op, datos._1);
	}

	@Override
	protected T hazlo(Tuples.T2<T, Collection<DirectivaBase>> datos, Operacion op) {
		T persistido = ice().artefactos().saveAndFlush(datos._1);
		for(DirectivaBase d: datos._2)
			ice().directivas().save(d);
		ice().directivas().flush();
		
		return persistido;
	}
}
