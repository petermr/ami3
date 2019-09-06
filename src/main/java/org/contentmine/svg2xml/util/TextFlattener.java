package org.contentmine.svg2xml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/** "flattens" numbers and dates in text to canonical forms.
 * Used for comparing strings which differ only be nunbers ands dates
 * e.g. 25, 33, are both flattened to 00
 * @author pm286
 *
 */
public class TextFlattener {

	private final static Logger LOG = Logger.getLogger(TextFlattener.class);
	
	public final static String META = "\\(){}[]?-+*|&^.$\"\'#";
	
	private Pattern integerPattern;
	private Matcher matcher;
	
	public TextFlattener() {
	}
	
	public Pattern createIntegerPattern(String template) {
		this.integerPattern = TextFlattener.createDigitStringMatchingPatternCapture(template);
		return integerPattern;
	}

	public List<Integer> captureIntegers(String s) {
		List<Integer> integerList = new ArrayList<Integer>();
		matcher = (s == null || integerPattern == null) ? null : integerPattern.matcher(s);
		if (matcher != null && matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				String g = matcher.group(i+1);
				Integer ii = new Integer(g);
				integerList.add(ii);
			}
		}
		return integerList;
	}


	/** replaces all digits with zero
	 * e.g. "221B Baker Street" =>  "000B Baker Street"
	 * @param s
	 * @return
	 */
	public static String flattenDigits(String s) {
		return s == null ? null : s.replaceAll("\\d", "0");
	}
	
	/** replaces all integers (\d+) with zero
	 * e.g. "221B Baker Street" =>  "0B Baker Street"
	 * @param s
	 * @return
	 */
	public static String flattenDigitStrings(String s) {
		return s == null ? null : s.replaceAll("\\d+", "0");
	}
	
	/** replaces all signed integers with zero
	 * e.g. "a = -100" =>  "a = 0"
	 * @param s
	 * @return
	 */
	public static String flattenSignedIntegers(String s) {
		return s == null ? null : s.replaceAll("[\\-\\+]?\\d+", "0");
	}
	
/** replace all digits by \d+ in a pattern
 *  see http://stackoverflow.com/questions/16034337/generating-a-regular-expression-from-a-string#16034486
 *  thanks to @dasblinkenlight
 *  
 *  This would let an expression produced from Page 3 of 23 match strings like Page 13 of 23 and Page 6 of 8.
 *
 * String p = Pattern.quote(orig).replaceAll("\\d+", "\\\\\\\\d+");

 * This would produce "Page \\d+ of \\d+" no matter what page numbers and counts were there originally.
 * @param ss
 * @return
 */
	public static Pattern createDigitStringMatchingPattern(String ss) {
        return createPattern(ss, "\\d+", "\\\\E\\\\d+\\\\Q");
	}

	public static Pattern createDigitStringMatchingPatternCapture(String ss) {
        return createPattern(ss, "\\d+", "\\\\E"+"("+"\\\\d+"+")"+"\\\\Q");
	}

	/** replace all digits by \d in a pattern
	 *  see http://stackoverflow.com/questions/16034337/generating-a-regular-expression-from-a-string#16034486
	 *  thanks to @dasblinkenlight
	 *  
	 *  This would let an expression produced from Page 3 of 23 match strings like Page 8 of 17 
	 *  but not Page 6 of 8.
	 *
	 * String p = Pattern.quote(orig).replaceAll("\\d", "\\\\\\\\d");

	 * This would produce "Page \\d+ of \\d+" no matter what page numbers and counts were there originally.
	 * @param ss
	 * @return
	 */
	public static Pattern createDigitMatchingPattern(String ss) {
		return createPattern(ss, "\\d", "\\\\\\\\d");
	}

	/**
	 * quotes metacharacters
	 * @param s
	 * @return
	 */
	public static String quoteMetaCharacters(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (META.indexOf(ch) != -1) {
				sb.append("\\");
			}
			sb.append(ch);
		}
		return sb.toString();
	}
	
	
	private static Pattern createPattern(String ss, String regexIn, String regexOut) {
		Pattern pp = null;
		if (ss != null && regexIn != null && regexOut != null) {
			String p = (ss == null) ? null : Pattern.quote(ss).replaceAll(regexIn, regexOut);
			pp = Pattern.compile(p);
		}
		return pp;
	}

	/** NYI
	 * 
	 * @param s
	 * @return
	 */
	public static String flattenFloats(String s) {
		return null;
	}

	public Pattern getIntegerPattern() {
		return integerPattern;
	}

	/** splits string into integers where possible
	 * splits Abc12def34g into "Abc", 12, "def", 34, "g"
	 * ignores minus and plus 
	 * @param filename0
	 * @return list of Strings and Integers
	 */
	public static List<Object> splitAtIntegers(String s) {
//		Pattern SPLIT = Pattern.compile("((\\D*\\d+)*)(\\D*)");
		Pattern EXTRACT = Pattern.compile("(\\D*)(\\d+)");
		Matcher extractMatcher = EXTRACT.matcher(s);
		List<Object> objectList = new ArrayList<Object>();
		int start = 0;
		while (extractMatcher.find(start)) {
			String ss = extractMatcher.group(0);
			while (extractMatcher.find(start)) {
				String nonDigit = extractMatcher.group(1);
				String digits = extractMatcher.group(2);
				if (nonDigit.length() > 0) {
					objectList.add(nonDigit);
				}
				try {
				Integer ii = new Integer(digits);
					objectList.add(ii);
				} catch (Exception e) {
					objectList.add(0);
					LOG.error(e.getMessage());
				}
				start = extractMatcher.end();
			}
		}
		String last = s.substring(start);
		if (last.length() > 0) {
			objectList.add(last);
		}
		return objectList;
	}

	public static Pattern createFirstIntegerPattern(String htmlValue0) {
		List<Object> tokens = TextFlattener.splitAtIntegers(htmlValue0);
		Pattern pattern = null;
		if (tokens.size()>= 2) {
			pattern = Pattern.compile("(\\Q"+tokens.get(0)+"\\E)(\\d+).*");
		}
		return pattern;
	}


}
