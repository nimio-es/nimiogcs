package es.nimio.nimiogcs.operaciones.artefactos;

import java.util.Collection;

import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.modelo.enumerados.EnumEstadoValidezYActividad;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.operaciones.artefactos.externa.BuscarDependenciasAdicionales;
import es.nimio.nimiogcs.operaciones.artefactos.repo.CrearEstructuraRepositorioCodigo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

/**
 * Registra un nuevo artefacto de tipo JVM
 */
public final class RegistrarNuevoArtefacto<T extends Artefacto>  
	extends OperacionInternaModulo<Tuples.T2<T, Collection<DirectivaBase>>, T> {
	
	public RegistrarNuevoArtefacto(IContextoEjecucion contextoEjecucion) {
		super(contextoEjecucion);
	}

	private IContextoEjecucion ice() { return (IContextoEjecucion)ce; }

	@Override
	protected String nombreUnicoOperacion(T2<T, Collection<DirectivaBase>> datos, Operacion op) {
		return "ALTA DE ARTEFACTO '" + datos._1.getNombre() + "'";
	}
	
	@Override
	protected void relacionarOperacionConEntidades(T2<T, Collection<DirectivaBase>> datos, Operacion op) {
		// aquí no se puede hacer nada porque aún no se ha registrado el bicho 
	}
	
	@Override
	protected T hazlo(T2<T, Collection<DirectivaBase>> datos, Operacion op) throws ErrorInesperadoOperacion {

		// salvaguardamos el estado con el que queremos que se genere
		final EnumEstadoValidezYActividad e = datos._1.getEstadoValidezActivacion();

		// y lo cambiamos a inválido para garantizar que, si falla la 
		// creación del repositorio, no damos algo por válido
		datos._1.setEstadoValidez(false);
		
		// hay que garantizar que generamos las coordenadas, si es necesario
		generarCoordenadaMaven(datos);
		
		// primero guardamos el registro y lo relacionamos con la operación
		T f = guardarRegistro(datos);
		registraRelacionConOperacion(op, datos._1);
		
		// para el caso en que tengamos que lanzar opeaciones
		// asíncronas, vamos a crear la función de éxito
		final Function<Artefacto, Boolean> f_exito = new Function<Artefacto, Boolean>() {
			@Override
			public Boolean apply(Artefacto artefacto) {
				
				// buscamos una versión "limpia"
				Artefacto limpio = ice().artefactos().findOne(artefacto.getId());
				limpio.setEstadoValidezActivacion(e);
				ice().artefactos().saveAndFlush(limpio);
				return true;
			}
		};
		
		// y tras guardarlo lanzamos el subproceso asíncrono que
		// se encarga de crear las carpetas en el repositorio de
		// código
		if(P.of(f).repositorioCodigo()!=null) {
			
			// hay que crear una estructura de repositorio
			new CrearEstructuraRepositorioCodigo(ice())
			.ejecutarCon(f, f_exito);
		
		} else if(PT.of(f.getTipoArtefacto()).caracterizacion().getLibreriaExterna()) {
			
			// hay que buscar dependencias asicionales
			new BuscarDependenciasAdicionales(ice())
			.ejecutarCon(f, f_exito);

		} else {
			f_exito.apply(f);
		}
		
		return f;
	}
	
	// =================================================
	// métodos privados
	// =================================================
	
	private void generarCoordenadaMaven(Tuples.T2<T, Collection<DirectivaBase>> datos) {
		
		// cogemos la caracterización del artefacto
		DirectivaCaracterizacion dc = PT.of(datos._1.getTipoArtefacto()).caracterizacion();
		if(dc==null) 
			throw new ErrorArtefactoSinCaracterizacion();
		
		if(dc.getGenerarCoordenadasMaven()) {
			
			// buscamos la directiva de taxonomía en la lista
			DirectivaTaxonomia dt = null;
			for(DirectivaBase db: datos._2) { 
				if(db instanceof DirectivaTaxonomia) { 
					dt = (DirectivaTaxonomia)db; 
					break; 
				}
			}
			
			// cargamos los datos de configuración
			String raizGrupo = ice().global().findOne("MAVEN.COORDENADAS.GENERAR.GRUPO.RAIZ").getContenido().replace("\n", "").replace("\r", "");
			String version = ice().global().findOne("MAVEN.COORDENADAS.GENERAR.VERSION").getContenido().replace("\n", "").replace("\r", "");
			
			// creamos la directiva y la añadimos a la lista 
			DirectivaCoordenadasMaven dm = new DirectivaCoordenadasMaven();
			dm.setIdGrupo(
					dt != null ?
							raizGrupo + dt.getTaxonomia().replace("/", ".")
							: "sintaxonomia");
			dm.setIdArtefacto(datos._1.getNombre().toLowerCase());
			dm.setVersion(version);
			dm.setEmpaquetado(dc.getEmpaquetadoCoordenada());
			
			datos._2.add(dm);
		}
	}
	
	@SuppressWarnings("unchecked")
	private T guardarRegistro(Tuples.T2<T, Collection<DirectivaBase>> datos) {
		
		T artefacto = datos._1;
		Collection<DirectivaBase> directivas = datos._2;
		
		T resultado = ice().artefactos().saveAndFlush(artefacto);
		for(DirectivaBase d: directivas) { 
			ice().directivas().save(d);
			resultado.getDirectivasArtefacto().add(d);
		}
		ice().directivas().flush();
		ice().artefactos().saveAndFlush(resultado);
		
		return (T)ice().artefactos().findOne(artefacto.getId());
	}

	
	// ----
	
	static final class ErrorArtefactoSinCaracterizacion extends RuntimeException {
		
		private static final long serialVersionUID = -3500847796523821389L;

		public ErrorArtefactoSinCaracterizacion() {
			super("No se puede dar de alta un artefacto que no tiene caracterización.");
		}
	}
}
