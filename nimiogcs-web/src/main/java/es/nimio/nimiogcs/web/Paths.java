package es.nimio.nimiogcs.web;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.web.componentes.ItemBasadoEnUrl;
import es.nimio.nimiogcs.web.componentes.Localizacion;

/**
 * Clase de utilidad para registrar todas las rutas a las que responderá la aplicación.
 */
public final class Paths {

	public static final String BASE = "/";
	
	public static final ItemBasadoEnUrl TO_HOME = new ItemBasadoEnUrl("Home", BASE); 
	
	public static final String redirect(String to) { return "redirect:" + to; }

	public static final Localizacion localizacion() {
		return new Localizacion().conItem(TO_HOME);
	}
	
	// --
	
	/**
	 * Rutas para la parte de administración
	 */
	public final static class Admin {
		
		public static final String BASE = "/admin";
		
		public static final ItemBasadoEnUrl TO_ADMIN = new ItemBasadoEnUrl("Administración");
		
		public static final Localizacion localizacion() {
			return Paths.localizacion().conItem(TO_ADMIN);
		}
		
		// --
		
		public final static class Diccionarios {
			
			public static final String BASE = "/admin/directivas/diccionarios";

			public static final String PTRN_REQ_DATOS = "/{id}";
			public static final String PTRN_REQ_NUEVO = "/nuevo";
			public static final String PTRN_REQ_ELIMINAR = "/{id}/eliminar";
			
			private static final String DATOS = BASE + PTRN_REQ_DATOS;
			public static final String NUEVO = BASE + PTRN_REQ_NUEVO;
			private static final String ELIMINAR = BASE + PTRN_REQ_ELIMINAR;
			
			public static final ItemBasadoEnUrl TO_DICCIONARIOS = new ItemBasadoEnUrl("Diccionarios de directivas", BASE);
			
			public static final String datos(TipoDirectivaDiccionario d) { return DATOS.replace("{id}", d.getId()); }
			public static final ItemBasadoEnUrl toDatos(TipoDirectivaDiccionario d) {
				return new ItemBasadoEnUrl(d.getId(), datos(d));
			}
			public static final String eliminar(TipoDirectivaDiccionario d) { return ELIMINAR.replace("{id}", d.getId()); }
			
			public static final String redirect() { return Paths.redirect(BASE); }
			public static final String redirectDatos(TipoDirectivaDiccionario d) { return Paths.redirect(datos(d)); }
			
			public static final Localizacion localizacion() {
				return Admin.localizacion().conItem(TO_DICCIONARIOS);
			}

			public static final Localizacion localizacionDatos(TipoDirectivaDiccionario d) {
				return localizacion().conItem(toDatos(d));
			}

			// --
			
			public final static class Definiciones {
				
				public static final String SUBBASE = "/{diccionario}/definiciones";
				
				public static final String PTRN_REQ_NUEVA = SUBBASE + "/nueva";
				public static final String PTRN_REQ_ELIMINAR = SUBBASE + "/{id}/eliminar";
				
				private static final String NUEVA = Diccionarios.BASE + PTRN_REQ_NUEVA;
				private static final String ELIMINAR = Diccionarios.BASE + PTRN_REQ_ELIMINAR;
				
				public static final String nueva(TipoDirectivaDiccionario d) {
					return NUEVA.replace("{diccionario}", d.getId());
				}
				
				public static final String eliminar(TipoDirectivaDiccionario d, TipoDirectivaDiccionarioDefinicion c) {
					return 
							ELIMINAR
							.replace("{diccionario}", d.getId())
							.replace("{id}", c.getId());
				}
				
				public static final Localizacion localizacion(TipoDirectivaDiccionario d) {
					return 
							Diccionarios.localizacionDatos(d)
							.conTexto("Definiciones");
				}

				public static final Localizacion localizacionDatos(TipoDirectivaDiccionario d, TipoDirectivaDiccionarioDefinicion c) {
					return 
							localizacion(d)
							.conTexto(c.getClave());
				}

				// --
				private Definiciones() {}
			}
			
			private Diccionarios() {}
		}
		
		public final static class RepositoriosCodigo {
		
			public static final String BASE = "/admin/repositorio";

			public static final String PTRN_REQ_DATOS = "/{id}";
			public static final String PTRN_REQ_NUEVO = "/nuevo";
			public static final String PTRN_REQ_EDITAR_GET = "/{id}/editar";
			public static final String PTRN_REQ_EDITAR_POST = "/editar";
			public static final String PTRN_REQ_ELIMINAR = "/{id}/eliminar";

			private static final String DATOS_GET = BASE + PTRN_REQ_DATOS;
			public static final String NUEVO = BASE + PTRN_REQ_NUEVO;
			private static final String EDITAR_GET = BASE + PTRN_REQ_EDITAR_GET;
			public static final String EDITAR_POST = BASE + PTRN_REQ_EDITAR_POST;
			private static final String ELIMINAR = BASE + PTRN_REQ_ELIMINAR;

			public static final ItemBasadoEnUrl TO_REPOSITORIOS = new ItemBasadoEnUrl("Repositorios", BASE);

			public static final String datos(RepositorioCodigo r) { return DATOS_GET.replace("{id}", r.getId()); }
			public static final String editar(RepositorioCodigo r) { return EDITAR_GET.replace("{id}", r.getId()); }
			public static final String eliminar(RepositorioCodigo r) { return ELIMINAR.replace("{id}", r.getId()); } 
			public static final ItemBasadoEnUrl toDatos(RepositorioCodigo r) { 
				return new ItemBasadoEnUrl(r.getNombre(), datos(r));
			}
			
			public static final String redirect() { return Paths.redirect(BASE); }
			public static final String redirectDatos(RepositorioCodigo repositorio) { return Paths.redirect(datos(repositorio)); }
			
			private RepositoriosCodigo() {}
		}
		
		private Admin() {}
	}
	// --
	
	// --
	private Paths() {}
}
