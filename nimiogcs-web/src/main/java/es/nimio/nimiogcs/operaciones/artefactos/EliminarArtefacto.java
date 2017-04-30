package es.nimio.nimiogcs.operaciones.artefactos;

import java.util.ArrayList;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.operaciones.artefactos.repo.EliminarEstructuraRepositorioCodigo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public class EliminarArtefacto<T extends Artefacto> 
	extends OperacionInternaModulo<T, Boolean> {

	public EliminarArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ----

	private IContextoEjecucion ice() { return (IContextoEjecucion)ce; }

	@Override
	protected String nombreUnicoOperacion(T artefacto, Operacion op) {
		return "ELIMINAR ARTEFACTO '" + artefacto.getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(T artefacto, Operacion op) {
		// Aunque no tiene sentido relacionar una operación con un artefacto
		// que vamos a eliminar, igualmente vamos a dejar constancia por si falla
		// y, de ese modo, aparecerá en la pestaña correspondiente.
		registraRelacionConOperacion(op, artefacto);

		// Es de suponer que igualmente se eliminará cuando concluya la ejecución
	}

	@Override
	protected Boolean hazlo(T artefacto, final Operacion op) throws ErrorInesperadoOperacion {

		// comprobación, que nunca está de más
		validarPuedeEliminar(artefacto);
		
		// lo primero es pasar el registro a inválido
		artefacto.setEstadoActivacion(false);
		ice().artefactos().saveAndFlush(artefacto);
		final Artefacto inactivo = ice().artefactos().findOne(artefacto.getId());
		
		// lanzamos la operación de eliminación del repositorio
		// (si tiene)
		if(P.of(inactivo).repositorioCodigo()!=null) {
			
			new EliminarEstructuraRepositorioCodigo(ice())
			.ejecutarCon(
					artefacto,
					new Function<Artefacto, Boolean>() {
						@Override
						public Boolean apply(Artefacto v) {
							eliminarRegistros(inactivo, op);
							return true;
						}
					}
			);
		} else {
			
			// si no tiene nada relativo al repositorio de código, 
			// nos cargamos directamente el registro
			eliminarRegistros(inactivo, op);
		}
		
		// TODO Auto-generated method stub
		return true;
	}

	private void validarPuedeEliminar(Artefacto artefacto) {
		
		// ¿hay proyectos que estén editando el artefacto?
		if(ice().relacionesProyectos().existeProyectoAfectaArtefacto(artefacto.getId()) > 0)
			throw new ErrorInconsistenciaDatos(
					"Se está permitiendo eliminar '" 
					+ artefacto.getNombre() 
					+ "' y consta como afectado por un artefacto.");
			
		
		// si existe alguna relación en la que este artefacto esté como destino
		// (requerido) impedimos continuar
		if(ice().dependenciasArtefactos().totalRelacionesDependenciaConDestinoElArtefacto(artefacto.getId()) > 0) 
			throw new ErrorInconsistenciaDatos(
					"Se está permitiendo eliminar '" 
					+ artefacto.getNombre() 
					+ "' y consta como requerido en una dependencia.");
		
		// si existe algún evolutivo o alguna congelación del artefacto,
		// tampoco deberíamos poder eliminarlo
		if(ice().artefactos().testaferrosArtefacto(artefacto.getId()).size() > 0) 
			throw new ErrorInconsistenciaDatos(
					"Se está permitiendo eliminar '" 
					+ artefacto.getNombre() 
					+ "' y consta que tiene testaferros.");
	}
	
	private void eliminarRegistros(Artefacto artefacto, Operacion op) {
		
		try {
		
			// eliminamos todas las dependencias que tengan a este artefacto como origen
			ice().dependenciasArtefactos().delete(
					ice().dependenciasArtefactos().relacionesDependenciaDeUnArtefacto(artefacto.getId())
			);
			
			// eliminamos las operaciones en las que esté referido este artefacto
			ice().relacionesOperaciones().delete(
					ice().relacionesOperaciones().operacionesDeUnArtefacto(artefacto.getId())
			);
	
			// antes de borrar el propio artefacto vamos a salvaguardar
			// la lista de directivas para poder eliminarla a continuación
			ArrayList<String> idsDirectivasArtefacto = new ArrayList<String>();
			for(DirectivaBase db: artefacto.getDirectivasArtefacto()) idsDirectivasArtefacto.add(db.getId());
			
			// ya podemos eliminar la entidad y el registro de configuración
			ice().artefactos().delete(artefacto);

			ice().dependenciasArtefactos().flush();
			ice().relacionesOperaciones().flush();
			ice().artefactos().flush();

			
			// eliminamos todas las directivas
			for(String idDirectiva: idsDirectivasArtefacto)
				ice().directivas().delete(idDirectiva);
	
			ice().directivas().flush();
		
		} catch(Exception th) {
			
			// ojo, el registro de la operación ya ha sido guardado y cerrado
			// así que volvemos a cargar la operación
			Operacion opN = ice().operaciones().findOne(op.getId());
			seRompio(opN, th);
		}
	}
}
