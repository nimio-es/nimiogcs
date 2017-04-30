package es.nimio.nimiogcs.web.dto.p.admin.tipos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.datos.PT;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectiva;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.dto.p.directivas.FactoriaPanelesDirectivas;

/**
 * Variante de la página para los artefactos JVM
 */
public final class PaginaDatosTipoArtefacto extends PaginaBaseTipoArtefactos {

	long numeroArtefactosDelTipo;
	Collection<TipoDirectiva> directivasExistentes;
	
	public PaginaDatosTipoArtefacto(
			TipoArtefacto tipoArtefacto,
			long numeroArtefactosDelTipo,
			Collection<TipoDirectiva> directivasExistentes) {
		
		super(TabActiva.DATOS, tipoArtefacto);
		
		this.numeroArtefactosDelTipo = numeroArtefactosDelTipo;
		this.directivasExistentes = directivasExistentes;
		
		estructuraBaseArtefacto();
	}

	// -----------------------------------------------------
	// Construcción de la página
	// -----------------------------------------------------
	
	@Override
	protected List<IComponente> componentesPagina() {
		
		// panel de acciones
		PanelContinente acciones = null;
		if(tipoArtefacto.getDeUsuario()) {
			acciones = new PanelContinente()
					.conTitulo("Acciones")
					.paraTipoDefecto()
					.conComponente(
							new Parrafo().conTexto("Puede realizar las siguientes operaciones.")
					)
					.conComponenteSi(
							numeroArtefactosDelTipo == 0,
							new Botonera()
							.conAlineacionALaDerecha()
							.incrustarEnLinea()
							.conBoton(
									new BotonEnlace()
									.conTexto("Eliminar el tipo")
									.deTipoAviso()
									.paraUrl("/admin/tipos/eliminar/" + tipoArtefacto.getId())
									.conTamañoPequeño()
							)
					)
					.conComponentesSi(
							numeroArtefactosDelTipo > 0,
							new Parrafo(" "),
							new PanelContinente()
							.paraTipoPeligro()
							.conTitulo("Limitación !")
							.conLetraPeq()
							.siendoContraible()
							.conComponentes(
									new Parrafo()
									.conTexto("No se puede eliminar un tipo de artefacto que cuenta ya con artefactos creados.")
									.deTipoAviso()
									.conLetraPeq(),
									new Parrafo()
									.conTexto("El total de artefactos creados que son del tipo actual asciende a: " + numeroArtefactosDelTipo)
									.conLetraPeq()
							)
					);
		}

		// dos columnas
		return Arrays.asList(
				new IComponente[] {
						
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(8)
								.conComponentes(
										componentesFicha()
								)
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(4)
								.conComponentesSi(acciones != null, acciones)
								.conComponentes(insertarDirectivas())
						)
				}
		);
	}
	
	private Collection<IComponente> componentesFicha() {
		
		List<IComponente> componentes = new ArrayList<IComponente>();
		
		// el primero siempre será el panel con los datos básicos
		componentes.add(
				new PanelContinente()
				.conTitulo("Datos básicos")
				.paraTipoDefecto()
				.conComponentes(
						MetodosDeUtilidad.parClaveValor("Id.", tipoArtefacto.getId()),
						MetodosDeUtilidad.parClaveValor("Descripción", tipoArtefacto.getNombre()),
						MetodosDeUtilidad.parClaveValor("Uso", tipoArtefacto.getDeUsuario() ? "USUARIO" : "INTERNO" )
				)
				.conComponentesPie(
						new ContinenteSinAspecto()
						.alineacionDerecha()
						.conComponente(
								new EnlaceSimple("Editar descripción", "admin/tipos/editar/" + tipoArtefacto.getId())
								.conLetraPeq()
						)
				)
		);
		
		componentes.add(
				new PanelInformativo()
				.tipoAviso()
				.conTexto( 
						"Introducir cambios en la configuración de directivas de un tipo que ya cuenta con artefactos vinculados y en uso puede traer consecuencias inesperadas. Por favor, preste mucha atención a lo que hace."
				)
		);
		
		// recorremos las directivas del tipo y vamos pintando paneles conforme el tipo de cada directiva
		for(DirectivaBase db: tipoArtefacto.getDirectivasTipo()) {

			componentes.add(
					FactoriaPanelesDirectivas.fabricaPanel(
							db, 
							"admin/tipos/" + tipoArtefacto.getId() + "/directiva/editar/" + db.getDirectiva().getId(), 
							"admin/tipos/" + tipoArtefacto.getId() + "/directiva/quitar/" + db.getDirectiva().getId()
					)
			);
		}
		
		return componentes;
	}
	
	private PanelContinente insertarDirectivas() {
		
		List<IComponente> accionesDirectivas = new ArrayList<IComponente>();
		
		if(tipoArtefacto.getDeUsuario()) {
		
			// vamos a crear los enlaces de las directivas que podemos añadir
			// Eso sí, primeor quitamos las que ya están y las que no sean aplicables a un tipo
			ArrayList<TipoDirectiva> posibles = new ArrayList<TipoDirectiva>();
			for(TipoDirectiva db: directivasExistentes) {
				
				// si no es para tipo, saltamos
				if(!db.getParaTipoArtefacto()) continue;
				
				// si la tiene, saltamos
				if(PT.of(tipoArtefacto).buscarDirectiva(db.getId())!=null) continue;
				
				posibles.add(db);
			}
			
			// añsdimos la explicación y los enlaces
			if(posibles.size() > 0) {
				
				accionesDirectivas.add(new Parrafo("A continuación se ofrecen una serie de enlaces con las posibles directivas que se pueden añadir al tipo de artefacto:"));
				for(TipoDirectiva tp: posibles) {
					accionesDirectivas.add(
							new ContinenteSinAspecto()
							.conComponentes(
									new GlyphIcon()
									.mas()
									.info(),
									
									new EnlaceSimple()
									.conTexto(tp.getNombre())
									.paraUrl("admin/tipos/" + tipoArtefacto.getId() + "/directiva/incorporar/" + tp.getId())
							)
							.enColumna(12)
					);
				}
				
			} else {
			
				accionesDirectivas.add(new Parrafo("No hay directivas de tipo aplicables que se puedan añadir."));
			}
		} else {
			
			accionesDirectivas.add(
					new Parrafo()
					.conTexto("Un artefacto de uso interno de la aplicación no puede ser modificado.")
					.deTipoInfo()
			);
		}
		
		return new PanelContinente()
				.conTitulo("Nuevas directivas")
				.paraTipoDefecto()
				.conComponentes(
						accionesDirectivas
				);
	}
}
