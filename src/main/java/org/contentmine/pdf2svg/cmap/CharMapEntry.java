package org.contentmine.pdf2svg.cmap;

import org.apache.log4j.Logger;

public class CharMapEntry {

	private static final Logger LOG = Logger.getLogger(CharMapEntry.class);
	
	private Integer serial;
	private Integer unicodePointValue;
	private String code;
	private String name;

	public CharMapEntry(String c1 , String c2) {
		this.serial = (Integer)toCmapPoint(c1.trim());
		c2 = c2.trim();
		name = c2;
		LOG.trace(c1+" / "+c2);
		Object obj = toCmapPoint(c2);
		if (obj instanceof String) {
			String ss = (String) obj;
			LOG.trace(ss);
			name = ss;
			code = (String) obj;
		} else if (obj instanceof Integer) {
			unicodePointValue = (Integer) obj;
		} else {
			throw new RuntimeException("Cannot parse as charMapEntry: "+c2);
		}
	}

	public Integer getSerial() {
		return serial;
	}

	public String getName() {
		return name;
	}

	public static Object toCmapPoint(String s) {
		Object stringOrInteger = null;
		try {
			if (s.startsWith("<") && s.endsWith(">")) {
				int len = s.length();
				s = s.substring(1, len-1);
				LOG.trace("interpret: "+s);
				len -= 2;
				if (len == 2) {
					stringOrInteger = Integer.decode("0X00"+s);
				} else if (len == 4) {
					stringOrInteger = Integer.decode("0X"+s);
				} else if (len%4 == 0) {
					stringOrInteger = decodeCharacters(s);
					if (stringOrInteger == null && len == 8) {
						stringOrInteger = s;
//						LOG.debug("skipping surrogates: "+s);
					}
				} else {
					throw new RuntimeException("Bad hex constant: ("+len+") "+s);
				}
			} else {
				stringOrInteger = Integer.decode(s);
			}
		} catch (NumberFormatException nfe) {
			LOG.error("Cannot parse: "+s+" as integer");
		}
		
		if (stringOrInteger == null) {
			LOG.error("Cannot parse: "+s);
		}
		return stringOrInteger;
	}
	/** interpret as 4 character strings encoding ascii characters (33-127)
	 * 
	 * @param s
	 * @return null if not all ascii
	 * @throws NumberFormatException
	 */
	public static String decodeCharacters(String s) throws NumberFormatException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i += 4) {
			Integer ii = Integer.decode("0X"+s.substring(i, i+4));
			if (ii >= 32 && ii < 127) {
				char cc = (char) (int) ii;
				sb.append(cc);
			} else {
				return null;
			}
		}
		return(sb.toString().trim());
	}
	
	public String toString() {
		return String.valueOf(serial)+" > "+((unicodePointValue != null) ? (String.valueOf(unicodePointValue)+"("+((char)(int)unicodePointValue)+")") : ("code: "+code));
	}

	public int getOriginal() {
		return serial;
	}

	public String getCode() {
		return code;
	}

	public Integer getUnicode() {
		return unicodePointValue;
	}
	
}
