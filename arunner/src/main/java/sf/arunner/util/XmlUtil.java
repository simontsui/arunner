/*
 * Copyright (c) 2004, simontsui, Chris Leung. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package sf.arunner.util;

import java.util.TreeMap;

/**
 * Static utilities for XML processing.
 */
public class XmlUtil {

	private static final TreeMap<String, Integer> CHARREFS = new TreeMap<String, Integer>();
	static {
		CHARREFS.put("amp", (int)'&');
		CHARREFS.put("lt", (int)'<');
		CHARREFS.put("gt", (int)'>');
		CHARREFS.put("quot", (int)'"');
		CHARREFS.put("apos", (int)'\'');
	}

	public static CharSequence escAttrValue(CharSequence value) {
		return escAttrValue(value, '"');
	}

	/**
	 * Escape XML reserved characters in attribute values.
	 */
	public static CharSequence escAttrValue(CharSequence value, char delim) {
		if (value == null)
			return "";
		StringBuilder ret = new StringBuilder();
		escAttrValue(ret, value, delim);
		return ret;
	}

	public static void escAttrValue(StringBuilder ret, CharSequence value, char delim) {
		for (int i = 0, len = value.length(); i < len; i++) {
			char c = value.charAt(i);
			switch (c) {
			case '&':
				if (isXmlEntityRef(value, i))
					ret.append(c);
				else
					ret.append("&amp;");
				break;
			case '<':
				ret.append("&lt;");
				break;
			case '\'':
				if (c == delim)
					ret.append("&apos;");
				else
					ret.append(c);
				break;
			case '"':
				if (c == delim)
					ret.append("&quot;");
				else
					ret.append(c);
				break;
			default :
				ret.append(c);
	}}}


	/**
	 * @return true if substring started at 'start' is a XML entity reference.
	 */
	public static boolean isXmlEntityRef(CharSequence str, int start) {
		int len = str.length();
		if (start >= len || str.charAt(start) != '&')
			return false;
		if (++start >= len)
			return false;
		if (str.charAt(start) == '#')
			return isXmlCharRefPart(str, start + 1);
		int i = start;
		if (!isNameStart(str.charAt(i)))
			return false;
		for (++i; i < len; ++i) {
			if (!isName(str.charAt(i)))
				break;
		}
		return (i > start && i < len && str.charAt(i) == ';');
	}

	/**
	 * NOTE: No well-formness check for the referenced char.
	 *
	 * @param start Index to char after "&#".
	 */
	public static boolean isXmlCharRefPart(CharSequence str, int start) {
		int len = str.length();
		if (start >= len)
			return false;
		char c;
		if (str.charAt(start) == 'x') {
			// &#xhex;
			++start;
			int i = start;
			for (; i < len; ++i) {
				c = str.charAt(i);
				if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
					continue;
				break;
			}
			return (i > start && i < len && str.charAt(i) == ';');
		}
		// &#dec;
		int i = start;
		for (; i < len; ++i) {
			c = str.charAt(i);
			if (c < '0' || c > '9')
				break;
		}
		return (i > start && i < len && str.charAt(i) == ';');
	}


	////////////////////////////////////////////////////////////////////

	public static boolean isNameStart(int c) {
		return c == ':'
			|| c >= 'A' && c <= 'Z'
			|| c == '_'
			|| c >= 'a' && c <= 'z'
			|| c >= '\u00C0' && c <= '\u00D6'
			|| c >= '\u00D8' && c <= '\u00F6'
			|| c >= '\u00F8' && c <= '\u02FF'
			|| c >= '\u0370' && c <= '\u037D'
			|| c >= '\u037F' && c <= '\u1FFF'
			|| c >= '\u200C' && c <= '\u200D'
			|| c >= '\u2070' && c <= '\u218F'
			|| c >= '\u2C00' && c <= '\u2FEF'
			|| c >= '\u3001' && c <= '\uD7FF'
			|| c >= '\uF900' && c <= '\uFDCF'
			|| c >= '\uFDF0' && c <= '\uFFFD'
		// | '\x00010000'..'\x000EFFFF'
		;
	}

	public static boolean isName(int c) {
		return isNameStart(c)
			|| c == '-'
			|| c == '.'
			|| c >= '0' && c <= '9'
			|| c == '\u00B7'
			|| c >= '\u0300' && c <= '\u036F'
			|| c >= '\u203F' && c <= '\u2040';
	}

	////////////////////////////////////////////////////////////////////
}
