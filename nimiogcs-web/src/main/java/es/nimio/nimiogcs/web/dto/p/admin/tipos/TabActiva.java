package es.nimio.nimiogcs.web.dto.p.admin.tipos;

enum TabActiva { 
	DATOS("Datos");

	private String titulo;
	public String titulo() { return this.titulo; }
	
	private TabActiva(String titulo) { this.titulo = titulo; }
}