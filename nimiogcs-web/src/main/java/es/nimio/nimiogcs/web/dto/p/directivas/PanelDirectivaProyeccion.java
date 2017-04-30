package es.nimio.nimiogcs.web.dto.p.directivas;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaProyeccion;
import es.nimio.nimiogcs.web.componentes.basicos.AreaPre;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.GlyphIcon;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;

public final class PanelDirectivaProyeccion extends PanelDirectivaBase<DirectivaProyeccion> {

	public PanelDirectivaProyeccion(DirectivaProyeccion directiva) {
		super(directiva);
	}
	
	public PanelDirectivaProyeccion(DirectivaProyeccion directiva, String urlEditar) {
		super(directiva, urlEditar);
	}
	
	public PanelDirectivaProyeccion(DirectivaProyeccion directiva, String urlEditar, String urlQuitar) {
		super(directiva, urlEditar, urlQuitar);
	}
	
	// --
	
	@Override
	protected void construyeCuerpoPanel() {
		
		conComponentes(
				new ContinenteSinAspecto()
				.conComponentes(
						new TextoSimple("El número de puntos/carpetas a proyectar es:"),
						new TextoSimple(directiva.getNumeroCarpetas().toString()).principal()
				)
				.enColumna(12),
				
				new Parrafo(" ")
		);
		
		if(directiva.getNumeroCarpetas()>0) {
			conComponente(new Parrafo("Lista de carpetas a proyectar:"));

			conComponente(filaCarpetas(directiva.getCarpetaOrigen1(), directiva.getCarpetaDestino1()));
			if(directiva.getNumeroCarpetas()>1)
				conComponente(filaCarpetas(directiva.getCarpetaOrigen2(), directiva.getCarpetaDestino2()));
			if(directiva.getNumeroCarpetas()>2)
				conComponente(filaCarpetas(directiva.getCarpetaOrigen3(), directiva.getCarpetaDestino3()));
			if(directiva.getNumeroCarpetas()>3)
				conComponente(filaCarpetas(directiva.getCarpetaOrigen4(), directiva.getCarpetaDestino4()));
			if(directiva.getNumeroCarpetas()>4)
				conComponente(filaCarpetas(directiva.getCarpetaOrigen5(), directiva.getCarpetaDestino5()));
			
			conComponente(new Parrafo(" "));
		}
		
		conComponentes(
				new Parrafo("Cuando se proyecte en el repositorio Subversion, estos serán los elementos que se ignorarán:"),
				new AreaPre(directiva.getSvnIgnores())
		);
	}

	private ContinenteSinAspecto filaCarpetas(String origen, String destino) {
		return new ContinenteSinAspecto()
		.conComponentes(
				new TextoSimple(origen),
				new GlyphIcon().flechaDerecha(),
				new TextoSimple(destino)
		)
		.enColumna(12);
	}
}
