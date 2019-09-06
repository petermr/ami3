/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.font;


import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

public class CodePoint extends Element {

	private final static Logger LOG = Logger.getLogger(CodePoint.class);
	
	// XML
	static final String TAG = "codePoint";
	
	private static final String DECIMAL = "decimal";
	private static final String HEX = "hex";
	private static final String NAME = "name";
	private static final String NOTE = "note";
	private static final String REPLACE_BY_UNICODE = "replaceByUnicode";
	private static final String REPLACE_NAME = "replaceName";
	private static final String UNICODE = "unicode";
	private static final String UNICODE_NAME = "unicodeName";
	private static final String UNICODE_VALUE = "unicode";
	private static final String UNICODE_CHARACTER = "unicodeCharacter";
	private static final String STRING_REPRESENTATION = "stringRepresentation";
	
	private Integer nonUnicodeDecimal; // may or may not be the decimal equivalent of unicode    
	private String  name;               // a mnemonic (origin unspecified , ?Adobe, ?HTML-ent
	private String  note;               // some explanatory or other note
	private UnicodePoint unicodePoint;
	
	public CodePoint() {
		super(TAG);
	}

	/** codePoint when we don't know the Unicode
	 * will create an UNKNOWN unicode
	 * @param charCode
	 * @param charname
	 */
	public CodePoint(Integer charCode, String charname) {
		this();
		this.nonUnicodeDecimal = charCode;
		this.name = charname;
		this.unicodePoint = UnicodePoint.UNKNOWN;
	}

	public static CodePoint createFromElement(Element codePointElement, String encoding) {
		CodePoint codePoint = null;
		try {
			codePoint = new CodePoint();
			if (!(TAG.equals(codePointElement.getLocalName()))) {
				throw new RuntimeException("CodePointSet children must be <codePoint>");
			}
			String decimalS = codePointElement.getAttributeValue(DECIMAL);
			String hexS = codePointElement.getAttributeValue(HEX);
			String unicodeS = codePointElement.getAttributeValue(UNICODE);
			if (unicodeS == null) {
				throw new RuntimeException("All code points must have unicode: "+codePointElement.toXML());
			}
			codePoint.name = codePointElement.getAttributeValue(NAME);
			if (decimalS != null) {
				Integer decimal = new Integer(decimalS); 
				codePoint.nonUnicodeDecimal = decimal; 
			} else if (hexS != null) {
				if (hexS.toLowerCase().startsWith("0x")) {
					hexS = hexS.substring(2);
				}
				Integer decimal = Integer.parseInt(hexS, 16); 
				codePoint.nonUnicodeDecimal = decimal; 
			}
			codePoint.unicodePoint = UnicodePoint.createUnicodeValue(codePointElement.getAttributeValue(UNICODE));
			if (codePoint.unicodePoint == null) {
				throw new RuntimeException("missing or invalid unicode value in: "+codePointElement.toXML());
				
			}
			codePoint.unicodePoint.setUnicodeName(codePointElement.getAttributeValue(UNICODE_NAME));
			codePoint.unicodePoint.addReplacmentPoints(codePointElement.getAttributeValue(REPLACE_BY_UNICODE));
			codePoint.note = codePointElement.getAttributeValue(NOTE);
			if (decimalS == null && hexS == null && 
					codePoint.unicodePoint.getUnicodeName() == null && codePoint.name == null) {
				throw new RuntimeException("<codePoint> must have decimal-or-hex attribute or name or unicodeName");
			}

		} catch (Exception e) {
			throw new RuntimeException("invalid codePointElement: "+((codePointElement == null) ? null : codePointElement.toXML()), e);
		}
		LOG.trace("Created "+codePoint);
		return codePoint;
	}

	public Element createElement() {
		Element codePointElement = new Element(TAG);
		if (unicodePoint == null) {
			throw new RuntimeException("unicode must not be null");
		}
		codePointElement.addAttribute(new Attribute(UNICODE, unicodePoint.getUnicodeValue()));
		if (nonUnicodeDecimal == null && name == null && unicodePoint.getUnicodeName() == null) {
			throw new RuntimeException("decimal and name and unicodename must not all be null");
		}
		if (nonUnicodeDecimal != null) {
			codePointElement.addAttribute(new Attribute(DECIMAL, String.valueOf(nonUnicodeDecimal)));
		}
		if (name != null) {
			codePointElement.addAttribute(new Attribute(NAME, name));
		}
		if (note != null) {
			codePointElement.addAttribute(new Attribute(NOTE, note));
		}
		if (unicodePoint != null) {
			if (unicodePoint.getUnicodeName() != null) {
				codePointElement.addAttribute(new Attribute(UNICODE_NAME, unicodePoint.getUnicodeName()));
			}
			addUnicodeStringAttribute(codePointElement, unicodePoint.getUnicodeValue(), unicodePoint.getStringRepresentation());
		}
		String replacementPointString = unicodePoint.getReplacementPointString();
		if (replacementPointString != null) {
			codePointElement.addAttribute(new Attribute(REPLACE_BY_UNICODE, replacementPointString));
		}
		return codePointElement;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public Integer getDecimal() {
		return nonUnicodeDecimal;
	}

	public Integer getUnicodeDecimal() {
		return unicodePoint.getDecimalValue();
	}

	public void setStringRepresentation(String stringRep) {
		if (unicodePoint != null) {
			unicodePoint.setStringRepresentation(stringRep);
			this.addAttribute(new Attribute(STRING_REPRESENTATION, stringRep));
		}
	}
	public String toString() {
		return "\n"+
		"decimal: "+nonUnicodeDecimal+"\n" +
		"name: "+name+"\n" +
		"note: "+note+"\n" +
		"unicode: "+unicodePoint+"\n";
	}

	public UnicodePoint getUnicodePoint() {
		return unicodePoint;
	}

	public void setUnicodePoint(UnicodePoint unicodePoint) {
		this.unicodePoint = unicodePoint;
		if (unicodePoint != null) {
			String unicodeName = unicodePoint.getUnicodeName();
			if (unicodeName != null) this.addAttribute(new Attribute(UNICODE_NAME, unicodeName));
			String value = unicodePoint.getUnicodeValue();
			if (value != null) this.addAttribute(new Attribute(UNICODE_VALUE, value));
			String unicodeString = unicodePoint.getStringRepresentation();
			if (unicodeString != null) {
				addUnicodeStringAttribute(this, value, unicodeString);
			}
		}
	}

	private void addUnicodeStringAttribute(Element element, String value, String unicodeString) {
		if (unicodeString != null) {
			try {
				element.addAttribute(new Attribute(STRING_REPRESENTATION, unicodeString));
			} catch (Exception e) {
				// some characters may not be XML
				LOG.trace("Cannot add character: ("+value+") ("+unicodeString+")");
			}
		}
	}

	public String getUnicodeValue() {
		return unicodePoint == null ? null : unicodePoint.getUnicodeValue();
	}

	public String getUnicodeName() {
		return unicodePoint == null ? null : unicodePoint.getUnicodeName();
	}
}
