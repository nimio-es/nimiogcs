package es.nimio.nimiogcs.web.dto.p.admin.entornos;

enum TabActiva { 
	DATOS("Datos"),
	SERVIDORES("Servidores"),
	OPERACIONES("Operaciones");

	private String titulo;
	public String titulo() { return this.titulo; }
	
	private TabActiva(String titulo) { this.titulo = titulo; }
}