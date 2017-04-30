package es.nimio.nimiogcs;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Strings {

	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String LF = "\n";
	public static final String CR = "\r";
	
	public static final char C_LF = 10;
	public static final char C_CR = 13;


	public static final int INDEX_NOT_FOUND = -1;
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	private static char[] hex_table = { 
			'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' 
	};

	/**
	 * Patrón que reconoce todos los caracteres en blanco 
	 * que haya antes del primer carácter que no sea un espacio
	 * o un tabulador.
	 */
	private final static Pattern LTRIM = Pattern.compile("^\\s+");
	
	/**
	 * Patrón que reconoce todos los caracteres en blanco 
	 * que haya tras el último carácter que no sea un espacio
	 * o un tabulador.
	 */
	private final static Pattern RTRIM = Pattern.compile("\\s+$");
	
	// ----
	
	public static String dash2CamelCase(String dashed) {
		StringBuffer cc = new StringBuffer();
		StringTokenizer tz = new StringTokenizer(dashed.toLowerCase(), "-");
		
		while (tz.hasMoreTokens()) {
			cc.append(capitalize(tz.nextToken()));
		}
		return cc.toString();
	}
	
	public static String maskPassword(String inString) {
		return mask(inString);
	}

	public static String mask(String inString) {
		String outString = null;

		if (inString != null) {
			char[] outStringBuffer = new char[inString.length()];

			for (int i = 0; i < inString.length(); ++i) {
				outStringBuffer[i] = '*';
			}

			outString = new String(outStringBuffer);
		}

		return outString;
	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}

	public static String substringBefore(final String str,
			final String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.isEmpty()) {
			return EMPTY;
		}
		final int pos = str.indexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}

	public static String substringAfter(final String str, final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return EMPTY;
		}
		final int pos = str.indexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	public static String substringBeforeLast(final String str,
			final String separator) {
		if (isEmpty(str) || isEmpty(separator)) {
			return str;
		}
		final int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}

	public static String substringAfterLast(final String str,
			final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return EMPTY;
		}
		final int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	public static String substringBetween(final String str, final String open,
			final String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		final int start = str.indexOf(open);
		if (start != INDEX_NOT_FOUND) {
			final int end = str.indexOf(close, start + open.length());
			if (end != INDEX_NOT_FOUND) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	public static String[] substringsBetween(final String str,
			final String open, final String close) {
		if (str == null || isEmpty(open) || isEmpty(close)) {
			return null;
		}
		final int strLen = str.length();
		if (strLen == 0) {
			return EMPTY_STRING_ARRAY;
		}
		final int closeLen = close.length();
		final int openLen = open.length();
		final List<String> list = new ArrayList<String>();
		int pos = 0;
		while (pos < strLen - closeLen) {
			int start = str.indexOf(open, pos);
			if (start < 0) {
				break;
			}
			start += openLen;
			final int end = str.indexOf(close, start);
			if (end < 0) {
				break;
			}
			list.add(str.substring(start, end));
			pos = end + closeLen;
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public static String capitalize(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		final char firstChar = str.charAt(0);
		final char newChar = Character.toUpperCase(firstChar);
		if (firstChar == newChar) {
			// already capitalized
			return str;
		}
		char[] newChars = new char[strLen];
		newChars[0] = newChar;
		str.getChars(1, strLen, newChars, 1);
		return String.valueOf(newChars);
	}

	public static String uncapitalize(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		final char firstChar = str.charAt(0);
		final char newChar = Character.toLowerCase(firstChar);
		if (firstChar == newChar) {
			// already uncapitalized
			return str;
		}
		char[] newChars = new char[strLen];
		newChars[0] = newChar;
		str.getChars(1, strLen, newChars, 1);
		return String.valueOf(newChars);
	}

	public static String toEncodedString(final byte[] bytes,
			final Charset charset) {
		return new String(bytes, charset != null ? charset
				: Charset.defaultCharset());
	}

	public static String wrap(final String str, final String wrapWith) {
		if (isEmpty(str) || isEmpty(wrapWith)) {
			return str;
		}
		return wrapWith.concat(str).concat(wrapWith);
	}

	/**
	 *  Concatena las cadenas devolviendo una única cadena de texto
	 * @param parts
	 * @param intervalue
	 * @return
	 */
	public static String join(final String[] parts, final String intervalue) {
		if(parts==null) 
			return Strings.EMPTY;
		return join(Arrays.asList(parts), intervalue);
	}
	
	/**
	 *  Concatena las cadenas devolviendo una única cadena de texto
	 * @param parts
	 * @param intervalue
	 * @return
	 */
	public static String join(final List<String> parts, final String intervalue) {

		// si no se define intervalor se supone cadena vacía
		final String _inter = intervalue == null ? Strings.EMPTY : intervalue;
		
		// se recorrerá cada elemnto y se añadirá.
		final Iterator<String> iterator = parts.iterator();
		if(!iterator.hasNext()) 
			return Strings.EMPTY;  // no hay elementos => cadena vacía
		
		final StringBuilder sb = new StringBuilder();
		while(true) {
			sb.append(iterator.next());
			if(!iterator.hasNext()) break;
			sb.append(_inter);
		}
		return sb.toString();
	}
	
	/**
	 * Devuelve una versión de la cadena de texto rellenando por la izquierda 
	 * con el carácter indicado hasta que se alcance el tamaño especificado.
	 * Si la cadena tiene un tamaño igual o mayor que el indicado se devuelve 
	 * intacta. 
	 */
	public static String padLeft(final String str, final int finalLength, final char fillChar) {
		
		final int currentLength = str.length(); 
		if(currentLength >= finalLength) 
			return str;
		
		final int sizeToFill = finalLength - currentLength;
		return 
				replicateChar(fillChar, sizeToFill)
				.concat(str);
	}
	
	/**
	 * Devuelve el contenido de la cadena original tras eliminar 
	 * los caracteres en blanco que haya por la izquierda. 
	 * Respeta los que haya por la derecha.
	 */
	public static String ltrim(final String str) {
		return LTRIM.matcher(str).replaceAll("");
	}
	
	/**
	 * Devuelve el contenido de la cadena original tras eliminar 
	 * los caracteres en blanco que haya por la derecha. 
	 * Respeta los que haya por la izquierda.
	 */
	public static String rtrim(final String str) {
		return RTRIM.matcher(str).replaceAll("");
	}
	
	/**
	 * Devuelve una cadena repitiendo un carácter las veces indicadas
	 */
	public static String replicateChar(final char fillChar, final int finalLength) {

		final StringBuilder futureString = new StringBuilder();
		for(int p=0; p < finalLength; p++) 
			futureString.append(fillChar);
		
		return futureString.toString();
	}
	
	public static boolean isAlphanumeric(final CharSequence cs) {
		if (isEmpty(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isLetterOrDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static String abbreviate(final String str, int offset,
			final int maxWidth) {
		if (str == null) {
			return null;
		}
		if (maxWidth < 4) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width is 4");
		}
		if (str.length() <= maxWidth) {
			return str;
		}
		if (offset > str.length()) {
			offset = str.length();
		}
		if (str.length() - offset < maxWidth - 3) {
			offset = str.length() - (maxWidth - 3);
		}
		final String abrevMarker = "...";
		if (offset <= 4) {
			return str.substring(0, maxWidth - 3) + abrevMarker;
		}
		if (maxWidth < 7) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width with offset is 7");
		}
		if (offset + maxWidth - 3 < str.length()) {
			return abrevMarker
					+ abbreviate(str.substring(offset), 0, maxWidth - 3);
		}
		return abrevMarker + str.substring(str.length() - (maxWidth - 3));
	}
	
	/**
	 * Indica si una cadena es nula o se considera que es vacía.
	 * @param cs
	 * @return
	 */
	public static boolean isNullOrEmpty(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric(final CharSequence cs) {
		if (isEmpty(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static String left(final String str, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return EMPTY;
		}
		if (str.length() <= len) {
			return str;
		}
		return str.substring(0, len);
	}

	public static String right(final String str, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return EMPTY;
		}
		if (str.length() <= len) {
			return str;
		}
		return str.substring(str.length() - len);
	}

	public static String mid(final String str, int pos, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0 || pos > str.length()) {
			return EMPTY;
		}
		if (pos < 0) {
			pos = 0;
		}
		if (str.length() <= pos + len) {
			return str.substring(pos);
		}
		return str.substring(pos, pos + len);
	}

	public static <T extends CharSequence> T defaultIfBlank(final T str,
			final T defaultStr) {
		return isNullOrEmpty(str) ? defaultStr : str;
	}

	public static <T extends CharSequence> T defaultIfEmpty(final T str,
			final T defaultStr) {
		return isEmpty(str) ? defaultStr : str;
	}

	public static String normalizeSpace(final String str) {
		// LANG-1020: Improved performance significantly by normalizing manually
		// instead of using regex
		// See
		// https://github.com/librucha/commons-lang-normalizespaces-benchmark
		// for performance test
		if (isEmpty(str)) {
			return str;
		}
		final int size = str.length();
		final char[] newChars = new char[size];
		int count = 0;
		int whitespacesCount = 0;
		boolean startWhitespaces = true;
		for (int i = 0; i < size; i++) {
			char actualChar = str.charAt(i);
			boolean isWhitespace = Character.isWhitespace(actualChar);
			if (!isWhitespace) {
				startWhitespaces = false;
				newChars[count++] = (actualChar == 160 ? 32 : actualChar);
				whitespacesCount = 0;
			} else {
				if (whitespacesCount == 0 && !startWhitespaces) {
					newChars[count++] = SPACE.charAt(0);
				}
				whitespacesCount++;
			}
		}
		if (startWhitespaces) {
			return EMPTY;
		}
		return new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0));
	}

	public static String stripAccents(final String input) {
		if (input == null) {
			return null;
		}
		final Pattern pattern = Pattern
				.compile("\\p{InCombiningDiacriticalMarks}+");//$NON-NLS-1$
		final String decomposed = Normalizer.normalize(input,
				Normalizer.Form.NFD);
		// Note that this doesn't correctly remove ligatures...
		return pattern.matcher(decomposed).replaceAll("");//$NON-NLS-1$
	}

	public static String replaceString(String target, String match,
			String replace) {
		if (target == null) {
			return null;
		}

		if ((match == null) || (match.equals(""))) {
			return target;
		}

		String temp = new String(target);
		StringBuffer newString = new StringBuffer();
		int loc;
		while ((loc = temp.indexOf(match)) != -1) {
			newString.append(temp.substring(0, loc));
			newString.append(replace);

			temp = temp.substring(loc + match.length());
		}

		newString.append(temp);

		return newString.toString();
	}
	public static String toHexString(byte[] paramArrayOfByte, int paramInt1,
			int paramInt2) {
		StringBuffer localStringBuffer = new StringBuffer(paramInt2 * 2);
		int i = paramInt1 + paramInt2;
		for (int j = paramInt1; j < i; ++j) {
			int k = (paramArrayOfByte[j] & 0xF0) >>> 4;
			int l = paramArrayOfByte[j] & 0xF;
			localStringBuffer.append(hex_table[k]);
			localStringBuffer.append(hex_table[l]);
		}
		return localStringBuffer.toString();
	}

	public static byte[] fromHexString(String paramString, int paramInt1,
			int paramInt2) {
		if (paramInt2 % 2 != 0)
			return null;
		byte[] arrayOfByte = new byte[paramInt2 / 2];
		int i = 0;
		int j = paramInt1 + paramInt2;
		for (int k = paramInt1; k < j; k += 2) {
			int l = Character.digit(paramString.charAt(k), 16);
			int i1 = Character.digit(paramString.charAt(k + 1), 16);
			if ((l == -1) || (i1 == -1))
				return null;
			arrayOfByte[(i++)] = (byte) (l << 4 & 0xF0 | i1 & 0xF);
		}
		return arrayOfByte;
	}
	
	public static String hexDump(byte[] paramArrayOfByte) {
		StringBuffer localStringBuffer = new StringBuffer(
				paramArrayOfByte.length * 3);
		localStringBuffer.append("Hex dump:\n");
		for (int j = 0; j < paramArrayOfByte.length; j += 16) {
			String str = Integer.toHexString(j);
			int k;
			for (k = str.length(); k < 8; ++k)
				localStringBuffer.append("0");
			localStringBuffer.append(str);
			localStringBuffer.append(":");
			int l;
			for (k = 0; (k < 16) && (j + k < paramArrayOfByte.length); ++k) {
				int i = paramArrayOfByte[(j + k)];
				if (k % 2 == 0)
					localStringBuffer.append(" ");
				l = (byte) ((i & 0xF0) >>> 4);
				int i1 = (byte) (i & 0xF);
				localStringBuffer.append(hex_table[l]);
				localStringBuffer.append(hex_table[i1]);
			}
			localStringBuffer.append("  ");
			for (k = 0; (k < 16) && (j + k < paramArrayOfByte.length); ++k) {
				l = (char) paramArrayOfByte[(j + k)];
				if (Character.isLetterOrDigit(l))
					localStringBuffer.append(String.valueOf(l));
				else
					localStringBuffer.append(".");
			}
			localStringBuffer.append("\n");
		}
		return localStringBuffer.toString();
	}
	
	public static String quoteString(String paramString, char paramChar) {
		StringBuffer localStringBuffer = new StringBuffer(
				paramString.length() + 2);
		localStringBuffer.append(paramChar);
		for (int i = 0; i < paramString.length(); ++i) {
			char c = paramString.charAt(i);
			if (c == paramChar)
				localStringBuffer.append(paramChar);
			localStringBuffer.append(c);
		}
		localStringBuffer.append(paramChar);
		return localStringBuffer.toString();
	}

	public static String quoteStringLiteral(String paramString) {
		return quoteString(paramString, '\'');
	}
}
