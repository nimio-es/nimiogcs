package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaVersionJava;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public final class PanelDirectivaVersionJava extends PanelDirectivaBase<DirectivaVersionJava> {

	public PanelDirectivaVersionJava(DirectivaVersionJava directiva) {
		super(directiva);
	}
	
	public PanelDirectivaVersionJava(DirectivaVersionJava directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaVersionJava(DirectivaVersionJava directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		this.conComponente(new Parrafo("Versión de la máquina virtual Java que se empleará para la fase de compilación y la final o de tiempo de ejecución:"));
		
		this.conComponentes(
				MetodosDeUtilidad.parClaveValor("Versión Java compilación:", directiva.getVersionCompila()),
				MetodosDeUtilidad.parClaveValor("Versión Java destino:", directiva.getVersionDestino())
		);
	}
}
