package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaCoordenadasMaven;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaCoordenadasMaven extends PanelDirectivaBase<DirectivaCoordenadasMaven> {

	public PanelDirectivaCoordenadasMaven(DirectivaCoordenadasMaven directiva) {
		super(directiva);
	}
	
	public PanelDirectivaCoordenadasMaven(DirectivaCoordenadasMaven directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaCoordenadasMaven(DirectivaCoordenadasMaven directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {

		this.conComponente(new Parrafo("El artefacto puede presentarse al resto como un artefacto Maven con las siguientes coordenadas:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Grupo:", directiva.getIdGrupo()),
				MetodosDeUtilidad.parClaveValor("Artefacto:", directiva.getIdArtefacto()),
				MetodosDeUtilidad.parClaveValor("Clasificador:", Strings.isNotEmpty(directiva.getClasificador()) ? directiva.getClasificador() : ""),
				MetodosDeUtilidad.parClaveValor("Version:", directiva.getVersion()),
				MetodosDeUtilidad.parClaveValor("Empaquetado:", directiva.getEmpaquetado())
		);
	}
	
}
