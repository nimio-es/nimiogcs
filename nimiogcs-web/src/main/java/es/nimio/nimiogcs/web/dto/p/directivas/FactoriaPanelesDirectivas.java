package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCaracterizacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstrategiaEvolucion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaEstructuraCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaInventario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaParametrosDeployer;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccionMaven;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionJenkins;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRamaCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaReferenciar;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaRepositorioCodigo;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaTaxonomia;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaVersionJava;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

public final class FactoriaPanelesDirectivas {

	private FactoriaPanelesDirectivas() {}
	
	/**
	 * Devuelve el panel que corresponde a la directiva que se le facilita
	 */
	public static PanelContinente fabricaPanel(DirectivaBase directiva, String urlEditar, String urlQuitar) {
		if(Strings.isNullOrEmpty(urlQuitar)) return fabricaPanel(directiva, urlEditar);
		
		if(directiva instanceof DirectivaAlcances) {
			return new PanelDirectivaAlcances((DirectivaAlcances)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaCaracterizacion) {
			return new PanelDirectivaCaracterizacion((DirectivaCaracterizacion)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaCoordenadasMaven) {
			return new PanelDirectivaCoordenadasMaven((DirectivaCoordenadasMaven)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaEstrategiaEvolucion) {
			return new PanelDirectivaEstrategia((DirectivaEstrategiaEvolucion)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaEstructuraCodigo) {
			return new PanelDirectivaEstructuraCodigo((DirectivaEstructuraCodigo)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaInventario) {
			return new PanelDirectivaInventariar((DirectivaInventario)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaProyeccion) {
			return new PanelDirectivaProyeccion((DirectivaProyeccion)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaProyeccionMaven) {
			return new PanelDirectivaProyeccionMaven((DirectivaProyeccionMaven)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaPublicacionDeployer) {
			return new PanelDirectivaPublicacionDeployer((DirectivaPublicacionDeployer)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaParametrosDeployer) {
			return new PanelDirectivaParametrosDeployer((DirectivaParametrosDeployer)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaPublicacionJenkins) {
			return new PanelDirectivaPublicacionJenkins((DirectivaPublicacionJenkins)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaRamaCodigo) {
			return new PanelDirectivaRamaCodigo((DirectivaRamaCodigo)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaReferenciar) {
			return new PanelDirectivaReferenciar((DirectivaReferenciar)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaRepositorioCodigo) {
			return new PanelDirectivaRepositorioCodigo((DirectivaRepositorioCodigo)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaTaxonomia) {
			return new PanelDirectivaTaxonomia((DirectivaTaxonomia)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaVersionJava) {
			return new PanelDirectivaVersionJava((DirectivaVersionJava)directiva, urlEditar, urlQuitar);
		} else if(directiva instanceof DirectivaDiccionario) {
			return new PanelDirectivaDiccionario((DirectivaDiccionario)directiva, urlEditar, urlQuitar);
		}

		return mierda(directiva);
	}
	
	public static PanelContinente fabricaPanel(DirectivaBase directiva, String urlEditar) {
		if(Strings.isNullOrEmpty(urlEditar)) return fabricaPanel(directiva);

		if(directiva instanceof DirectivaAlcances) {
			return new PanelDirectivaAlcances((DirectivaAlcances)directiva, urlEditar);
		} else if(directiva instanceof DirectivaCaracterizacion) {
			return new PanelDirectivaCaracterizacion((DirectivaCaracterizacion)directiva, urlEditar);
		} else if(directiva instanceof DirectivaCoordenadasMaven) {
			return new PanelDirectivaCoordenadasMaven((DirectivaCoordenadasMaven)directiva, urlEditar);
		} else if(directiva instanceof DirectivaEstrategiaEvolucion) {
			return new PanelDirectivaEstrategia((DirectivaEstrategiaEvolucion)directiva, urlEditar);
		} else if(directiva instanceof DirectivaEstructuraCodigo) {
			return new PanelDirectivaEstructuraCodigo((DirectivaEstructuraCodigo)directiva, urlEditar);
		} else if(directiva instanceof DirectivaInventario) {
			return new PanelDirectivaInventariar((DirectivaInventario)directiva, urlEditar);
		} else if(directiva instanceof DirectivaProyeccion) {
			return new PanelDirectivaProyeccion((DirectivaProyeccion)directiva, urlEditar);
		} else if(directiva instanceof DirectivaProyeccionMaven) {
			return new PanelDirectivaProyeccionMaven((DirectivaProyeccionMaven)directiva, urlEditar);
		} else if(directiva instanceof DirectivaPublicacionDeployer) {
			return new PanelDirectivaPublicacionDeployer((DirectivaPublicacionDeployer)directiva, urlEditar);
		} else if(directiva instanceof DirectivaParametrosDeployer) {
			return new PanelDirectivaParametrosDeployer((DirectivaParametrosDeployer)directiva, urlEditar);
		} else if(directiva instanceof DirectivaPublicacionJenkins) {
			return new PanelDirectivaPublicacionJenkins((DirectivaPublicacionJenkins)directiva, urlEditar);
		} else if(directiva instanceof DirectivaRamaCodigo) {
			return new PanelDirectivaRamaCodigo((DirectivaRamaCodigo)directiva, urlEditar);
		} else if(directiva instanceof DirectivaReferenciar) {
			return new PanelDirectivaReferenciar((DirectivaReferenciar)directiva, urlEditar);
		} else if(directiva instanceof DirectivaRepositorioCodigo) {
			return new PanelDirectivaRepositorioCodigo((DirectivaRepositorioCodigo)directiva, urlEditar);
		} else if(directiva instanceof DirectivaTaxonomia) {
			return new PanelDirectivaTaxonomia((DirectivaTaxonomia)directiva, urlEditar);
		} else if(directiva instanceof DirectivaVersionJava) {
			return new PanelDirectivaVersionJava((DirectivaVersionJava)directiva, urlEditar);
		} else if(directiva instanceof DirectivaDiccionario) {
			return new PanelDirectivaDiccionario((DirectivaDiccionario)directiva, urlEditar);
		}

		return mierda(directiva);
	}
	
	public static PanelContinente fabricaPanel(DirectivaBase directiva) {
		
		if(directiva instanceof DirectivaAlcances) {
			return new PanelDirectivaAlcances((DirectivaAlcances)directiva);
		} else if(directiva instanceof DirectivaCaracterizacion) {
			return new PanelDirectivaCaracterizacion((DirectivaCaracterizacion)directiva);
		} else if(directiva instanceof DirectivaCoordenadasMaven) {
			return new PanelDirectivaCoordenadasMaven((DirectivaCoordenadasMaven)directiva);
		} else if(directiva instanceof DirectivaEstrategiaEvolucion) {
			return new PanelDirectivaEstrategia((DirectivaEstrategiaEvolucion)directiva);
		} else if(directiva instanceof DirectivaEstructuraCodigo) {
			return new PanelDirectivaEstructuraCodigo((DirectivaEstructuraCodigo)directiva);
		} else if(directiva instanceof DirectivaInventario) {
			return new PanelDirectivaInventariar((DirectivaInventario)directiva);
		} else if(directiva instanceof DirectivaProyeccion) {
			return new PanelDirectivaProyeccion((DirectivaProyeccion)directiva);
		} else if(directiva instanceof DirectivaProyeccionMaven) {
			return new PanelDirectivaProyeccionMaven((DirectivaProyeccionMaven)directiva);
		} else if(directiva instanceof DirectivaPublicacionDeployer) {
			return new PanelDirectivaPublicacionDeployer((DirectivaPublicacionDeployer)directiva);
		} else if(directiva instanceof DirectivaParametrosDeployer) {
			return new PanelDirectivaParametrosDeployer((DirectivaParametrosDeployer)directiva);
		} else if(directiva instanceof DirectivaPublicacionJenkins) {
			return new PanelDirectivaPublicacionJenkins((DirectivaPublicacionJenkins)directiva);
		} else if(directiva instanceof DirectivaRamaCodigo) {
			return new PanelDirectivaRamaCodigo((DirectivaRamaCodigo)directiva);
		} else if(directiva instanceof DirectivaReferenciar) {
			return new PanelDirectivaReferenciar((DirectivaReferenciar)directiva);
		} else if(directiva instanceof DirectivaRepositorioCodigo) {
			return new PanelDirectivaRepositorioCodigo((DirectivaRepositorioCodigo)directiva);
		} else if(directiva instanceof DirectivaTaxonomia) {
			return new PanelDirectivaTaxonomia((DirectivaTaxonomia)directiva);
		} else if(directiva instanceof DirectivaVersionJava) {
			return new PanelDirectivaVersionJava((DirectivaVersionJava)directiva);
		} else if(directiva instanceof DirectivaDiccionario) {
			return new PanelDirectivaDiccionario((DirectivaDiccionario)directiva);
		}
		
		return mierda(directiva);
	}
	
	
	private static PanelContinente mierda(DirectivaBase directiva) {
		return new PanelContinente()
				.conTitulo("Mierda! Esto no debería aparecer!")
				.siendoContraible()
				.paraTipoDefecto()
				.conComponentes(new Parrafo("Se ve que aún no hemos hecho el panel que representa a la directiva: '" + directiva.getDirectiva().getNombre() + '"'));
	}
}
