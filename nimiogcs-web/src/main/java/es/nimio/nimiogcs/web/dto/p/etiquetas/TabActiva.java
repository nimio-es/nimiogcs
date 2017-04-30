package es.nimio.nimiogcs.web.dto.p.etiquetas;

enum TabActiva { 
	DATOS("Datos"), 
	OPERACIONES("Operaciones");

	private String titulo;
	public String titulo() { return this.titulo; }
	
	private TabActiva(String titulo) { this.titulo = titulo; }
}