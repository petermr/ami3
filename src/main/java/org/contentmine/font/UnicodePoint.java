package org.contentmine.font;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;

/** represent a Unicode codepoint.
 * Does not current manage surrogates
 * 
 * @author pm286
 *
 */
public class UnicodePoint {

	private static final int DFFF = 0XDFFF;
	private static final int DC00 = 0XDC00;
	private static final int DBFF = 0XDBFF;
	private static final int D800 = 0xD800;
	private static final int _0400 = 0x0400;
	private static final int _10000 = 0x10000;
	private static final Logger LOG = Logger.getLogger(UnicodePoint.class);
	/** this displays a filled glyph so that it stands out as an error */
	private static final String POINT_REPRESENTING_UNKNOWN = "U+274E"; // negative square cross 
	
	public static final String UNICODE_PREFIX = "U+";
	public static final String HEX_PREFIX = "0X";
	public static final UnicodePoint UNKNOWN = UnicodePoint.createUnicodeValue(POINT_REPRESENTING_UNKNOWN);

	private Integer decimalValue;
	private String unicodeValue;  // e.g. U+1234
	private String unicodeName; // e.g. "LEFT PAREN TOP", normalized to Uppercase 
	private Character unicodeCharacter; // e.g. "A",  
	private String stringRepresentation; // for surrogates 
	private UnicodePoint[] replacementPoints; // a sequence of one of more concatenated code points that 
	                                      //could be used for replacement

	private UnicodePoint() {
	}
	
	public UnicodePoint(Integer value) {
		this.decimalValue = value;
		String hex = Integer.toHexString((int)decimalValue).toUpperCase();
		hex = hex.replaceAll(HEX_PREFIX, "");
		hex = padWithLeadingZeros(hex);
		unicodeValue = UNICODE_PREFIX + hex;
		createCharacterAndOrStringFromCodePoint(value);
	}

	private void createCharacterAndOrStringFromCodePoint(Integer codePoint) {
		if (codePoint > 0XFFFF) {
			stringRepresentation = createStringFromCodePoint(codePoint);
			unicodeCharacter = null;
		} else {
			unicodeCharacter = new Character((char)(int)codePoint);
			stringRepresentation = String.valueOf(unicodeCharacter);
		}
	}
	
	/** from WP
	 * The 2,048 surrogates are not characters, but are reserved for use in UTF-16 to specify code points outside the Basic Multilingual Plane. 
	 * They are divided into leading or "high surrogates" (D800–DBFF) and trailing or "low surrogates" (DC00–DFFF). 
	 * In UTF-16, they must always appear in pairs, as a high surrogate followed by a low surrogate, thus using 32 bits to denote one code point.

A surrogate pair denotes the code point

    0x10000 + (H − 0xD800) × 0x400 + (L − 0xDC00)

where H and L are the numeric values of the high and low surrogates respectively.

Since high surrogate values in the range DB80–DBFF always produce values in the Private Use planes, 
the high surrogate range can be further divided into (normal) high surrogates (D800–DB7F) and "high private use surrogates" (DB80–DBFF).
*/
    /**
	 * @param codePoint
	 * @return
	 */

	public static int[] makeSurrogatePair(Integer codePoint) {
// 		combinedSurrogate = 0x10000 + (highSurrogate - 0xD800) * 0x0400 + (lowSurrogate - 0xDC00);
		Integer high = codePoint - _10000;
		int highCharacter = high / _0400 + D800;
		int lower = codePoint - (_10000 + (highCharacter -D800)* _0400);
		int lowCharacter = lower  + 0xDC00;
		return new int[]{highCharacter, lowCharacter};
	}
	
	public static Integer createCodePointFromSurrogates(int[] surrogatePair) {
		Integer codePoint = null;
		if (isValidSurrogatePair(surrogatePair)) {
			codePoint = 0x10000 + (surrogatePair[0] - 0xD800) * 0x0400 + (surrogatePair[1] - 0xDC00);
		}
		return codePoint;
	}
	
	public static String createStringFromSurrogatePairs(int[] surrogatePair) {
		String s = null;
		if (isValidSurrogatePair(surrogatePair)) {
			s = new String(surrogatePair, 0, surrogatePair.length);
		}
		return s;
	}

	public static String createStringFromCodePoint(int codePoint) {
		String s = null;
		if (codePoint < 0) {
			
		} else if (codePoint <= 0XFFFF) {
			s = String.valueOf((char) codePoint);
		} else {
			int[] surrogatePair = makeSurrogatePair(codePoint);
			s = new String(surrogatePair, 0, surrogatePair.length);
		}
		return s;
	}

	public static boolean isValidSurrogatePair(int[] surrogatePair) {
		return surrogatePair != null && surrogatePair.length == 2 
				&& isValidSurrogate(0, surrogatePair[0])
				&& isValidSurrogate(1, surrogatePair[1]);
	}

	public static boolean isValidSurrogate(int highLow, int surrogate) {
		boolean valid = false;
		LOG.trace(Integer.toHexString(surrogate));
		if (highLow == 0) {
			valid = (surrogate >= D800 && surrogate <= DBFF);
		} else if (highLow == 1) {
			valid = (surrogate >= DC00 && surrogate <= DFFF);
		}
		return valid;
	}

	public static String padWithLeadingZeros(String hex) {
		int l = 4 - hex.length();
		if (l > 0) {
			hex = "0000".substring(0,l) + hex;
		}
		return hex;
	}
	
	/** two UnicodePoints are equal if the have the same unicodeValue
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof UnicodePoint)) {
			return false;
		}
		return ((UnicodePoint)obj).unicodeValue.equals(this.unicodeValue);
	}

	@Override
	public int hashCode() {
		return unicodeValue.hashCode();
	}
	
	/** create from "U+1234"
	 * normalizes to uppercase.
	 * @param uString
	 * @return null if arg is null
	 */
	public static UnicodePoint createUnicodeValue(String uString) {
		UnicodePoint unicodePoint = null;
		if (uString != null && uString.length() > 2 && uString.length() < 8) { // U+0 ... U+123456
			uString = uString.toUpperCase();
			Integer decimalValue = translateToDecimal(uString);
			if (decimalValue == null) {
				throw new RuntimeException("Bad Unicode value: "+uString);
			}
			unicodePoint = new UnicodePoint();
			unicodePoint.decimalValue = decimalValue;
			unicodePoint.unicodeValue = uString;
			
		}
		return unicodePoint;
	}
	
	public static Integer translateToDecimal(String unicodeValue) {
		Integer codepoint = null;
		if (unicodeValue != null && unicodeValue.startsWith(UNICODE_PREFIX)) {
			String hex = HEX_PREFIX+unicodeValue.substring(UNICODE_PREFIX.length());
			try {
				codepoint = Integer.decode(hex);
			} catch (Exception e) {
				throw new RuntimeException("Bad hex: "+hex);
			}
		}
		return codepoint;
	}

	/** split a concatenated list of unicode points
	 * normalizes whitespace and case
	 * @param replace
	 * @return
	 */
	public static UnicodePoint[] getUnicodeValues(String stringToParse) {
		String replace = stringToParse;
		replace = replace.replaceAll("U", " U");
		replace = replace.replaceAll(XMLConstants.S_WHITEREGEX, " ");
		replace = replace.trim();
		String[] replaceStrings = replace.split(" ");
		int nStrings = replaceStrings.length;
		UnicodePoint[] points = new UnicodePoint[nStrings];
		for (int i = 0; i < nStrings; i++) {
			points[i] = UnicodePoint.createUnicodeValue(replaceStrings[i]);
			if (points[i] == null) {
				throw new RuntimeException("Cannot create Unicode point: "+points[i]+" in: "+ stringToParse);
			}
		}
		return points;
	}

	public void addReplacmentPoints(String replace) {
		if (replace != null) {
			replacementPoints = UnicodePoint.getUnicodeValues(replace);
		}
	}
	
	public Integer getDecimalValue() {
		return decimalValue;
	}

	public String getUnicodeValue() {
		return unicodeValue;
	}

	public String getUnicodeName() {
		return unicodeName;
	}

	public void setUnicodeName(String name) {
		unicodeName = name;
	}

	public void setStringRepresentation(String stringRep) {
		this.stringRepresentation = stringRep;
	}

	public String getStringRepresentation() {
		String s = null;
		if (unicodeCharacter != null) {
			s = String.valueOf(unicodeCharacter);
		} else {
			s = stringRepresentation;
		}
		return s;
	}

	public void setUnicodeCharacter(Character character) {
		unicodeCharacter = character;
	}

	public UnicodePoint[] getReplacmentPoints() {
		return replacementPoints;
	}

	/** string of form "U+1234" or "U+1234 U+2345... "
	 * @return
	 */
	public String getReplacementPointString() {
		String replacementString = null;
		if (replacementPoints != null) {
			for (int i = 0; i < replacementPoints.length; i++) {
				if (i > 0) {
					replacementString += " ";
				}
				replacementString += replacementPoints[i];
			}
		}
		return replacementString;
	}
	
	public String toString(){
		String s ="";
		s += " unicode: "+unicodeValue+";";
		s += " decimal: "+decimalValue+";";
		s += " unicodeName: "+unicodeName+";";
		String r = getReplacementPointString();
		if (r != null) {
			s += "replacement: "+r+";";
		}
		return s;
	}

}
