package es.nimio.nimiogcs.web.dto.p.proyectos;

enum TabActiva { 
	DATOS("Datos"), 
	ETIQUETAS("Etiquetas"),
	PUBLICACIONES("Publicaciones"),
	ANOTACIONES("Anotaciones"),
	OPERACIONES("Operaciones");

	private String titulo;
	public String titulo() { return this.titulo; }
	
	private TabActiva(String titulo) { this.titulo = titulo; }
}