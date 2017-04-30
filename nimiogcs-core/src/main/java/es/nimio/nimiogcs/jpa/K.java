package es.nimio.nimiogcs.jpa;

public final class K {
	
	private K() {}
	
	// ----------------------------
	// Constantes lógicas
	// ----------------------------

	public final static class L {
		
		private L() {}
		
		public static final String SI = "SI";
		public static final String NO = "NO";
		
		public static boolean desde(String texto) {
			return (texto.equalsIgnoreCase(SI));
		}
		
		public static String para(boolean v) {
			return v ? SI : NO;
		}
		
	}
	
	// ----------------------------
	// PUBLICACIONES
	// ----------------------------
	
	public final static class X {
		
		private X() {}
		
		public static final String EJECUCION = "EJECUCION";
		public static final String ERROR = "ERROR";
		public static final String OK = "OK";
		
	}
}
