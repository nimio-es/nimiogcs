package es.nimio.nimiogcs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeUtils {

	private static SimpleDateFormat formatYYYYMMDD() { return new SimpleDateFormat("yyyyMMdd"); }
	private static SimpleDateFormat formatYYYYMMDDHHmm() { return new SimpleDateFormat("yyyyMMddHHmm"); }
	private static SimpleDateFormat formatYYMMDDHHmmss() { return new SimpleDateFormat("yyMMddHHmmss"); }
	private static SimpleDateFormat formatYYYYMMDDHHmmssS() { return new SimpleDateFormat("yyyyMMddHHmmssSSS"); }
	private static SimpleDateFormat formatSeparatedDDMMYYHHMMSS() { return new SimpleDateFormat("dd/MM/yy HH:mm:ss"); }
	
	private DateTimeUtils() {}
	
	public static String convertirAFormaYYYYMMDD(Date fecha) {
		return convertirAFormaYYYYMMDD(fecha, false);
	}

	public static String convertirAFormaYYYYMMDD(Date fecha, boolean incluirMarcaTiempo) {

		return incluirMarcaTiempo ? formatYYYYMMDDHHmmssS().format(fecha) : formatYYYYMMDD().format(fecha); 
	}

	public static String convertirAFormaYYYYMMDDHHMM(Date fecha) {
		return formatYYYYMMDDHHmm().format(fecha);
	}

	public static String convertirAFormaYYMMDDHHMMSS(Date fecha) {
		return formatYYMMDDHHmmss().format(fecha);
	}
	
	public static String formaReducida(Date fecha) {
		return formatSeparatedDDMMYYHHMMSS().format(fecha);
	}
}
