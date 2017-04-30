package es.nimio.nimiogcs.web.dto.p.artefactos;

enum TabActiva { 
	DATOS("Datos"), 
	DEPENDENCIAS("Dependencias"), 
	CODIGO("Código"), 
	PUBLICACIONES("Publicaciones"),
	OPERACIONES("Operaciones");

	private String titulo;
	public String titulo() { return this.titulo; }
	
	private TabActiva(String titulo) { this.titulo = titulo; }
}