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


/** manages a generic set of fonts
 * should not depend on prefix, bold, italic, MT or PS suffixes, etc.
 * @author pm286
 *
 // standard
    <font family="Courier" fontType="PDType1Font" note="a standard14 font" serif="yes" unicode="yes"/>
    or
  // non-standard
    <font family="FooBar" fontType="PDType1Font" standardFont="Helvetica" note="" serif="" unicode="guessed"/>

 *
 */
public class NonStandardFontFamily {

	public final static Logger LOG = Logger.getLogger(NonStandardFontFamily.class);
	// XML
	public static final String CODE_POINT_SET = "codePointSet";
	public final static String FONT_FAMILY = "fontFamily";
	public static final String NAME = "name";
	public static final String FONT_TYPE = "fontType";
	public static final String NOTE = "note";
	
	public static final String STANDARD_FONT = "standardFont";
	public static final String UNICODE = "unicode";

	// these look wrong
	public static final String DEFAULT_MONOSPACED_FONT = "Courier";
	public static final String DEFAULT_SERIF_FONT = "TimesNewRoman";
	public static final String DEFAULT_SANS_SERIF_FONT = "Helvetica";

	public static final String IS_SERIF = "isSerif";

	public static final String FORCE_BOLD = "forceBold";
	public static final String FORCE_ITALIC = "forceItalic";
	public static final String FORCE_MONOSPACED = "forceMonospaced";
	public static final String FORCE_SYMBOL = "forceSymbol";

	private String name;
	private String fontType;
	private String standardFont;
	private String unicode;
	private String serif;
//	private String monospaced;
	private String note;
	private CodePointSet codePointSet;

	private String forceBold;
	private String forceItalic;
	private String forceMonospaced;
	private String forceSymbol;

	public NonStandardFontFamily() {
		
	}

	public NonStandardFontFamily(String name) {
		this.name = name;
	}

	public static NonStandardFontFamily createFromElement(Element fontFamilyElement) {
		NonStandardFontFamily fontFamily = null;
		try {
			fontFamily = new NonStandardFontFamily();
			if (!(FONT_FAMILY.equals(fontFamilyElement.getLocalName()))) {
				throw new RuntimeException("FontFamilySet children must be: "+FONT_FAMILY);
			}
			fontFamily.name = fontFamilyElement.getAttributeValue(NAME);
			if (fontFamily.name == null) {
				throw new RuntimeException("<fontFamily> must have name attribute");
			}
			fontFamily.fontType = fontFamilyElement.getAttributeValue(FONT_TYPE);
			fontFamily.standardFont = fontFamilyElement.getAttributeValue(STANDARD_FONT);
			fontFamily.unicode = fontFamilyElement.getAttributeValue(UNICODE);
			fontFamily.serif = fontFamilyElement.getAttributeValue(IS_SERIF);
			fontFamily.forceBold = fontFamilyElement.getAttributeValue(FORCE_BOLD);
			fontFamily.forceItalic = fontFamilyElement.getAttributeValue(FORCE_ITALIC);
			fontFamily.forceMonospaced = fontFamilyElement.getAttributeValue(FORCE_MONOSPACED);
			fontFamily.note = fontFamilyElement.getAttributeValue(NOTE);
			
			String codePointSetName = fontFamilyElement.getAttributeValue(CODE_POINT_SET);
			if (codePointSetName != null) {
				CodePointSet codePointSet = CodePointSet.readCodePointSet(codePointSetName);
				if (codePointSet == null) {
					throw new RuntimeException("Cannot read codePointSet: "+codePointSetName);
				}
				fontFamily.setCodePointSet(codePointSet);
				LOG.trace("CPS: "+fontFamily.getCodePointSet());
			}
		} catch (Exception e) {
			throw new RuntimeException("invalid FontFamilyElement: "+((fontFamilyElement == null) ? null : fontFamilyElement.toXML()), e);
		}
		return fontFamily;
	}

	private void setCodePointSet(CodePointSet codePointSet) {
		this.codePointSet = codePointSet;
	}

	public Element createElement() {
		Element FontFamilyElement = new Element(FONT_FAMILY);
		if (name == null) {
			throw new RuntimeException("familyName must not be null");
		}
		FontFamilyElement.addAttribute(new Attribute(NAME, String.valueOf(name)));
		if (standardFont != null) {
			FontFamilyElement.addAttribute(new Attribute(STANDARD_FONT, standardFont));
		}
		if (note != null) {
			FontFamilyElement.addAttribute(new Attribute(NOTE, note));
		}
		if (unicode != null) {
			FontFamilyElement.addAttribute(new Attribute(UNICODE, unicode));
		}
		if (serif != null) {
			FontFamilyElement.addAttribute(new Attribute(IS_SERIF, serif));
		}
		if (forceBold != null) {
			FontFamilyElement.addAttribute(new Attribute(FORCE_BOLD, forceBold));
		}
		if (forceItalic != null) {
			FontFamilyElement.addAttribute(new Attribute(FORCE_ITALIC, forceItalic));
		}
		if (forceMonospaced != null) {
			FontFamilyElement.addAttribute(new Attribute(FORCE_MONOSPACED, forceMonospaced));
		}
		if (forceSymbol != null) {
			FontFamilyElement.addAttribute(new Attribute(FORCE_SYMBOL, forceSymbol));
		}
		if (fontType != null) {
			FontFamilyElement.addAttribute(new Attribute(FONT_TYPE, fontType));
		}
		return FontFamilyElement;
	}

	public String getUnicode() {
		return unicode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CodePointSet getCodePointSet() {
		return codePointSet;
	}

	public String convertSymbol2UnicodeValue(String charname) {
		String unicodeValue = null;
		if (codePointSet != null) {
			CodePoint codePoint = codePointSet.getByName(charname);
			unicodeValue = (codePoint == null) ? null : codePoint.getUnicodeValue(); 
		}
		return unicodeValue;
	}

	public Integer convertSymbol2UnicodePoint(String charname) {
		String unicodeValue = convertSymbol2UnicodeValue(charname);
		return (unicodeValue == null || unicodeValue.length() == 0) ? null : (Integer) (int) unicodeValue.charAt(0);
	}

	public Boolean isForceBold() {
		Boolean fb = null;
		if (forceBold != null) {
			fb = new Boolean(forceBold);
		}
		return fb;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FontFamily      : "+name+"\n");
		sb.append("fontType        :"+this.fontType+"\n"); ;
		sb.append("standardFont    :"+this.standardFont+"\n");
		sb.append("unicode         :"+this.unicode+"\n");
		sb.append("serif           :"+this.serif+"\n");
		sb.append("forceBold       :"+this.forceBold+"\n");
		sb.append("forceItalic     :"+this.forceItalic+"\n");
		sb.append("forceMonospaced :"+this.forceMonospaced+"\n");
		sb.append("note            :"+this.note+"\n");
		sb.append("getCodePointSet :"+this.getCodePointSet()+"\n");
		return sb.toString();
	}
}
