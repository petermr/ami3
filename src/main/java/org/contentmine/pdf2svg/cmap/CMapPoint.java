package org.contentmine.pdf2svg.cmap;

import org.apache.log4j.Logger;
import org.contentmine.font.UnicodePoint;

/**
 * interpretation of TeX characters and character sequences
 * 
 * there are several separate ways that TeX characters are defied in CMap
 * - numeric codepoint
 * - surrogate pair
 * - modifier
 * - ligatures, etc.
 * 
 * http://www.unicode.org/reports/tr15/tr15-23.html gives Unicode normalization
 * @author pm286
 *
 */
public class CMapPoint {

	private static final Logger LOG = Logger.getLogger(CMapPoint.class);
	public static final CMapPoint NULL = new CMapPoint(0); // this is Unicode NULL
	
	private Integer unicodePointValue;
	private String stringRep;
	private Integer highSurrogate;
	private Integer lowSurrogate;
//	private Integer codePoint;
	private String firstCharacterHex; // of a 2 character string
	private String secondCharacterHex; // of a 2 character string
	private String name;
	
	public CMapPoint(int value) {
		this.unicodePointValue = value;
	}
	
	public CMapPoint(String code) {
		this.stringRep = code.trim();
		evaluateStrings();
	}
	
	public CMapPoint(String code, int offset) {
		this.stringRep = code.trim();
		evaluateStrings();
		if (unicodePointValue != null) {
			unicodePointValue += offset;
			code = null;
		}
	}

	/** a complex set of independent heuristics, at least
	 * (a) TeX macros
	 * (b) surrogate pairs
	 * (c) postfix
	 */
	private void evaluateStrings() {
/**
 * Surrogate
 * supplementary characters are represented as a pair of char values, the first from the 
 * high-surrogates range, (\uD800-\uDBFF), the second from the low-surrogates range (\uDC00-\uDFFF). 
 A surrogate pair denotes the code point 
    0x10000 + (H − 0xD800) × 0x400 + (L − 0xDC00) 
 */
		name = stringRep;
		highSurrogate = null;
		lowSurrogate = null;
		firstCharacterHex = null;
		secondCharacterHex = null;
		stringRep = (stringRep == null) ? null : stringRep.trim();
		if (stringRep == null) {
			throw new RuntimeException("null srep");
		} else if (stringRep.length() == 0) {
			throw new RuntimeException("empty srep");
		} else if (CMap.texMacroSet.contains(stringRep)) {
			// already know this representation (e.g. \\big\\#)
			// probably TeX macro
		} else if (stringRep.length() == 8) {
			// 2-character group may be surrogate or postifx
			firstCharacterHex = stringRep.substring(0, 4);
			secondCharacterHex = stringRep.substring(4, 8);
			try {
				highSurrogate = Integer.decode("0X"+firstCharacterHex);
				lowSurrogate = Integer.decode("0X"+secondCharacterHex);
				if (highSurrogate >= 0xD800 && highSurrogate <= 0xDBFF &&
					lowSurrogate >= 0XDC00 && lowSurrogate <= 0XDFFF) {
					processSurrogates();
				} else {
					processModifier();
				}
			} catch (Exception e) {
				LOG.debug("Cannot parse as 2-characters "+stringRep+" "+e);
				throw new RuntimeException("Cannot parse as 2-characters "+stringRep, e);
			}
		} else if (stringRep.length() == 1) {
			char cc = stringRep.charAt(0);
			if (cc >= 32 && cc <= 127) {
				unicodePointValue = (int) cc;
			} else {
				LOG.debug("cannot find 1-character stringRep "+stringRep+" "+stringRep.length()+" "+(int)stringRep.charAt(0));
			}
		} else {
			// probably unknown macro
			LOG.trace("cannot find stringRep "+stringRep+" "+stringRep.length()+" "+(int)stringRep.charAt(0));
		}
		
	}

	private void processModifier() {
		String hs = String.valueOf((char)(int)highSurrogate);
		String ls = String.valueOf((char)(int)lowSurrogate);
		stringRep = hs + ls;
		if (lowSurrogate.equals(0X0020)) {
			LOG.trace("trailing space (ignored)");
			stringRep = hs;
			unicodePointValue = highSurrogate;
		} else if (highSurrogate.equals(0X0020)) {
			LOG.trace("leading space (ignored)");
			stringRep = ls;
			unicodePointValue = lowSurrogate;
		} else if (lowSurrogate.equals(0X0338)) {
			LOG.trace("strikethrough slash modifier");
		} else if (lowSurrogate.equals(0X20D2)) {
			LOG.trace("strikethrough vertical bar modifier");
		} else if (lowSurrogate >= (0XFE00) && lowSurrogate < 0XFE10) {
			LOG.trace("glyph modifier (ignored)");
			stringRep = hs;
		} else {
			throw new RuntimeException("Unknown second character: "+ls+"_"+(int)lowSurrogate);
		}
	}

	private void processSurrogates() {
		int[] surrogatePair = {highSurrogate, lowSurrogate};
		unicodePointValue = UnicodePoint.createCodePointFromSurrogates(surrogatePair);
		int offset = 0;
		int length = 2;
		stringRep = new String(surrogatePair, offset, length);
	}

	public Integer getHighSurrogate() {
		return highSurrogate;
	}

	public Integer getLowSurrogate() {
		return lowSurrogate;
	}

	public Integer getCombinedSurrogate() {
		return unicodePointValue;
	}

	public void setValue(Integer value) {
		this.unicodePointValue = value;
	}
	
	public Integer getValue() {
		return unicodePointValue;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	public String getHexStringOfValue() {
		return unicodePointValue == null ? null : Integer.toHexString((int)unicodePointValue);
	}

	public String getStringRepresentation() {
		if (unicodePointValue != null) {
			stringRep = UnicodePoint.createStringFromCodePoint(unicodePointValue);
		}
		return stringRep;
	}

	public Character getCharOfValue() {
		return unicodePointValue == null ? null : new Character((char)(int)unicodePointValue);
	}

	public String getFirstCharacter() {
		return firstCharacterHex;
	}

	public String getSecondCharacter() {
		return secondCharacterHex;
	}

	public String toString() {
		String s = "";
		s += (unicodePointValue != null) ? (int) unicodePointValue+" / " : null;
		s += " "+stringRep;
		return s;
	}

	
}
