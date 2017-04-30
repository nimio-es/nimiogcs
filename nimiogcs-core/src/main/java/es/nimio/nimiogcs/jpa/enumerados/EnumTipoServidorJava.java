package es.nimio.nimiogcs.jpa.enumerados;

import java.util.Arrays;

public enum EnumTipoServidorJava {

	TOMCAT_7("Tomcat v7.x"),
	TOMCAT_8("Tomcat v8.x"),
	JBOSS_8("JBoss/Wildfly 8.x"),
	JBOSS_9("JBoss/Wildfly 9.x"),
	JBOSS_10("JBoss/Wildfly 10.x"),
	WEBSPHERE_6("WebSphere 6.x"),
	WEBSPHERE_8_5("WebSphere 8.5.x");
	
	// ----------------------------

	private static final EnumTipoServidorJava[] ALL = {
				TOMCAT_7, TOMCAT_8, JBOSS_8, JBOSS_9, JBOSS_10, WEBSPHERE_6, WEBSPHERE_8_5
	};
	
	public static EnumTipoServidorJava[] getAll() {
		return Arrays.copyOf(ALL, ALL.length);
	}
	
	// ----------------------------
	
	private EnumTipoServidorJava(String descripcion) {
		this.descripcion = descripcion;
	}

	private String descripcion;
	
	public String getDescripcion() { return this.descripcion; }
	
}
