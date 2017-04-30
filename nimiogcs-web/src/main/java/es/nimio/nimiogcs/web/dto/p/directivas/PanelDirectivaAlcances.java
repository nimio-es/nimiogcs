package es.nimio.nimiogcs.web.dto.p.directivas;

import java.util.Arrays;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaAlcances;
import es.nimio.nimiogcs.modelo.enumerados.EnumAlcanceDependencia;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

public final class PanelDirectivaAlcances extends PanelDirectivaBase<DirectivaAlcances> {

	public PanelDirectivaAlcances(DirectivaAlcances directiva) {
		super(directiva);
	}
	
	public PanelDirectivaAlcances(DirectivaAlcances directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaAlcances(DirectivaAlcances directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Los alcances con los que puede ser empleado este artefacto son:"));
		
		DirectivaAlcances alcances = (DirectivaAlcances)this.directiva;
		this.conComponente(
				new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(3)
						.conComponentes(
								new TextoSimple()
								.conTexto(EnumAlcanceDependencia.CompilarYEmpaquetar.getTextoDescripcion())
								.exitoSi(Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.CompilarYEmpaquetar))
								.deClaseSi(!Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.CompilarYEmpaquetar), "tachado")
						)
						.sinFilas()
				)
		
				.conColumna(
						new Columnas.Columna()
						.conAncho(3)
						.conComponentes(
								new TextoSimple()
								.conTexto(EnumAlcanceDependencia.CompilarSinEmpaquetar.getTextoDescripcion())
								.exitoSi(Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.CompilarSinEmpaquetar))
								.deClaseSi(!Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.CompilarSinEmpaquetar), "tachado")
						)
						.sinFilas()
				)
				
		
				.conColumna(
						new Columnas.Columna()
						.conAncho(3)
						.conComponentes(
								new TextoSimple()
								.conTexto(EnumAlcanceDependencia.Provisto.getTextoDescripcion())
								.exitoSi(Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.Provisto))
								.deClaseSi(!Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.Provisto), "tachado")
						)
						.sinFilas()
				)
		
				.conColumna(
						new Columnas.Columna()
						.conAncho(3)
						.conComponentes(
								new TextoSimple()
								.conTexto(EnumAlcanceDependencia.Testing.getTextoDescripcion())
								.exitoSi(Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.Testing))
								.deClaseSi(!Arrays.asList(alcances.getAlcances()).contains(EnumAlcanceDependencia.Testing), "tachado")
						)
						.sinFilas()
				)
		);
	}
	
}
