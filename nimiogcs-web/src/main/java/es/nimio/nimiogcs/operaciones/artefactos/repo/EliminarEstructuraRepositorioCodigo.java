package es.nimio.nimiogcs.operaciones.artefactos.repo;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.operaciones.ProcesoAsincrono;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.operaciones.ProcesoAsincronoModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.servicios.externos.Subversion;

public class EliminarEstructuraRepositorioCodigo 
	extends ProcesoAsincronoModulo<Artefacto> {

	public EliminarEstructuraRepositorioCodigo(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	// ----

	@Override
	protected String nombreUnicoOperacion(Artefacto artefacto, ProcesoAsincrono op) {
		return "ELIMINAR ESTRUCTURA DE REPOSITORIO DEL ARTEFACTO '" + artefacto.getNombre() + "'";
	}

	@Override
	protected void relacionarOperacionConEntidades(Artefacto artefacto, ProcesoAsincrono op) {
		// Aunque no tiene sentido relacionar una operación con un artefacto
		// que vamos a eliminar, igualmente vamos a dejar constancia por si falla
		// y, de ese modo, aparecerá en la pestaña correspondiente.
		registraRelacionConOperacion(op, artefacto);
		
		// Damos por sentado que se eliminará tan pronto concluya la ejecución normal
	}

	@Override
	protected void hazlo(Artefacto datos, ProcesoAsincrono op) throws ErrorInesperadoOperacion {
		
		// si hemos llegado aquí, es que tenemos una directiva de repositorio
		DirectivaRepositorioCodigo codigo = P.of(datos).repositorioCodigo();

		// vamos a realizar varias operaciones con el repositorio asociado
		RepositorioCodigo repositorioCodigo = codigo.getRepositorio();
			
		// enganche con el repositorio subversion
		Subversion subversion = new Subversion(repositorioCodigo);

		// si tiene una etiqueta o una rama de código, tenemos que empezar eliminándola
		DirectivaRamaCodigo rama = P.of(datos).ramaCodigo();
		if(rama!=null) {
			
			escribeMensaje("Eliminando la rama única: " + rama.getRamaCodigo());
			
			// las operaciones de existencia y eliminación deben usar subrutas
			final String subruta = rama.getRamaCodigo().replace(
					codigo.getRepositorio().getUriRaizRepositorio() + "/", 
					""
			);
			
			if(subversion.existeElementoRemoto(subruta))
				subversion.eliminarCarpeta(subruta);
		}
		
		// Eliminamos la raíz del artefacto
		// Que siempre será el nivel superior a los estables
		String rutaEstable = codigo.getParcialEstables();
		String[] partesRutaEstable = rutaEstable.split("/");
		String rutaFinal = "";
		for(int p=0; p < partesRutaEstable.length - 1; p++) 
			if(Strings.isNotEmpty(partesRutaEstable[p]))
				rutaFinal += Strings.isNotEmpty(rutaFinal) ? 
						"/" + partesRutaEstable[p]
						: partesRutaEstable[p];

		// eliminamos la carpeta del artefacto
		if(subversion.existeElementoRemoto(rutaFinal))
			subversion.eliminarCarpeta(rutaFinal);
	}
}
