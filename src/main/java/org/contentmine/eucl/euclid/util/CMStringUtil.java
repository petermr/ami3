package org.contentmine.eucl.euclid.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CMStringUtil {
	private static final Logger LOG = LogManager.getLogger(CMStringUtil.class);
/** pattern of type foo1234plugh.bar */
	public static Pattern EMBEDDED_NUMERIC = Pattern.compile("([^\\d]*)(\\d+)(([^\\d].*)?)");

	/** sorts unique strings by embedded integers.
	 * the rest of the string is expected to be a constant framework.
	 * e.g. foo1bar, foo11bar, foo2bar is
	 * reordered to 
	 * e.g. foo1bar, foo2bar, foo11bar
	 * 
	 * if any string does not fit the regex pattern and constantFramework returns empty list.
	 * 
	 * @param strings
	 * @return list of sorted strings (empty if any conditions are violated)
	 */
	public static List<String> sortUniqueStringsByEmbeddedIntegers(List<String> strings) {
		List<String> sortedStrings = new ArrayList<String>();
		if (strings.size() > 1) {
			Matcher matcher = EMBEDDED_NUMERIC.matcher(strings.get(0));
			matcher.matches();
			// why so complicated?
			if (matcher.matches() && matcher.groupCount() == 4) {
				String first = matcher.group(1);
				String last = matcher.group(3);
				// create new Pattern
				Pattern explictPattern = Pattern.compile(first+"(\\d+)"+last);
				Map<Integer, String> stringByInteger = new HashMap<Integer, String>();
				for (String s : strings) {
					matcher = explictPattern.matcher(s);
					if (!matcher.matches() || matcher.groupCount() != 1) {
						LOG.warn(s+" does not fit pattern: "+explictPattern);
						continue;
					}
					Integer ii = new Integer(matcher.group(1));
					if (stringByInteger.keySet().contains(ii)) {
						throw new RuntimeException("Duplicate string: "+s);
					}
					stringByInteger.put(ii, s);
				}
				List<Integer> intList = new ArrayList<Integer>(stringByInteger.keySet());
				Collections.sort(intList);
				for (Integer integer : intList) {
					sortedStrings.add(stringByInteger.get(integer));
				}
			}
		}
		return sortedStrings;
	}

	/** extracts positive number embedded in String.
	 * Examples foo1234  foo1234pugh.bar 1234bar.plugh
	 * but not foo12bar34.plugh
	 * foo-1234-bar gives +1234
	 * 
	 * @param string
	 * @return number or null; 
	 */
	public static Integer getEmbeddedInteger(String string) {
		Matcher matcher = EMBEDDED_NUMERIC.matcher(string);
		Integer ii = null;
		if (matcher.matches()) {
			ii = new Integer(matcher.group(2));
		}
		return ii;
	}

	/** rightpads string to totalLength usingspaces
	 * if s >= totalLength no action
	 *  
	 * @param s
	 * @param totalLength
	 * @return
	 */
	public static String addPaddedSpaces(String s, int totalLength) {
		if (s == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(s);
		for (int i = s.length(); i < totalLength; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	/** unescapes percent-encoded strings
	 * replaces all %hh with their character values
	 * thus ...%7bxy%7d.. becomes ...{xy}..
	 * 
	 * wraps Java URLdecoder as it fails to trap nulls
	 * defaults to "UTF-8" input
	 */
	public static String urlDecode(String s) {
		if (s == null) return null;
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/** unescapes percent-encoded strings
	 * replaces all %hh with their character values
	 * thus ...%7bxy%7d.. becomes ...{xy}..
	 * 
	 * @param s
	 * @return
	 * @deprecated // use URLdecoder
	 */
	public static String urlDecodeOld(String s) {
		if (s == null) return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '%') {
				if (i < s.length() - 3) {
					String ss = extractHexChar(s, i);
					if (ss != null) {
						i += 2;
						sb.append(ss);
					} else {
						sb.append('%');
					}
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/** extracts hex value of character substring
	 * assumes ...%hh... where h are hex
	 *            i 
	 * @param s string in which value us encoded
	 * @param i index
	 * @return
	 */
	private static String extractHexChar(String s, int i) {
		String ss = null;
		char c1 = s.charAt(i+1);
		char c2 = s.charAt(i+2);
		if (isHexCharacter(c1) && isHexCharacter(c2)) {
			try {
				int ii = (int) Long.parseLong(s.substring(i+1, i+3), 16);
				ss = String.valueOf((char) ii);
			} catch (Exception e) {
				throw new RuntimeException("BUG", e);
			}
		}
		return ss;
	}

	/** is a single char interpreyable as hex character?
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isHexCharacter(char c) {
		if (c >= '0' && c <= '9') return true;
		if (c >= 'A' && c <= 'F') return true;
		if (c >= 'a' && c <= 'f') return true;
		return false;
	}


}
