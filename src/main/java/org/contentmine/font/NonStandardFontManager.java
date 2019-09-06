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

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.pdf2svg.AMIFont;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
public class NonStandardFontManager {


	private final static Logger LOG = Logger.getLogger(NonStandardFontManager.class);
	
	
	public static final String FONT_TRUE_TYPE = "TrueType";
	public static final String FONT_TYPE1 = "Type1";
	public static final String FONT_TYPE0 = "Type0";
	public static final String BADCHAR_E = "?}";
	public static final String BADCHAR_S = "{?";
	public static final String FONT_NAME = "fontName";
	public static final String BOLD = "bold";
	public static final String ITALIC = "italic";
	public static final String INCLINED = "inclined";
	public static final String OBLIQUE = "oblique";

	public static final String CHARNAME = "charname";
	public static final String CODEPOINT = "codepoint";

	private static final String NAME = "name";
	private static final String FONT_ENCODING = "fontEncoding";
	private static final String FONTS = "fonts";
	private static final String FONT = "font";
	private static final String IS_SYMBOL = "isSymbol";
	private static final String TYPE = "type";

	private Map<String, AMIFont> amiFontByFontNameMap;
	private FontFamilySet standardUnicodeFontFamilySet;
	private FontFamilySet standardNonUnicodeFontFamilySet;
	private FontFamilySet nonStandardFontFamilySet;
	private FontFamilySet unknownFontFamilySet;

	private Map<String, Integer> symbol2UnicodeHackMap;
	private boolean nullFontDescriptorReport = true;
	private boolean guessNonStandardEncoding = true;
	
	public static final int UNKNOWN_CHAR = (char)0X274E; // black square with white cross

	public NonStandardFontManager() {
		ensureAMIFontMaps();
	}
	
	public void ensureAMIFontMaps() {
		if (amiFontByFontNameMap == null) {
			amiFontByFontNameMap = new HashMap<String, AMIFont>();
			standardUnicodeFontFamilySet = FontFamilySet.readFontFamilySet(FontFamilySet.STANDARD_UNICODE_FONT_FAMILY_SET_XML);
			standardNonUnicodeFontFamilySet = FontFamilySet.readFontFamilySet(FontFamilySet.STANDARD_NON_UNICODE_FONT_FAMILY_SET_XML);
			nonStandardFontFamilySet = FontFamilySet.readFontFamilySet(FontFamilySet.NON_STANDARD_FONT_FAMILY_SET_XML);
			unknownFontFamilySet = new FontFamilySet();
		}
	}

	
	public Map<String, AMIFont> getAmiFontByFontNameMap() {
		ensureAMIFontMaps();
		return amiFontByFontNameMap;
	}
	
	public AMIFont getAmiFontByFontName(String fontName) {
		getAmiFontByFontNameMap();
		return amiFontByFontNameMap.get(fontName);
	}

	private AMIFont lookupOrCreateFont(int level, COSDictionary dict) {
		/**
Type = COSName{Font}
Subtype = COSName{Type1}
BaseFont = COSName{Times-Roman}
Name = COSName{arXivStAmP}		
LastChar = COSInt{32}
Widths = COSArray{[COSInt{19}]}
FirstChar = COSInt{32}
FontMatrix = COSArray{[COSFloat{0.0121}, COSInt{0}, COSInt{0}, COSFloat{-0.0121}, COSInt{0}, COSInt{0}]}
ToUnicode = COSDictionary{(COSName{Length}:COSInt{212}) (COSName{Filter}:COSName{FlateDecode}) }
FontBBox = COSArray{[COSInt{0}, COSInt{0}, COSInt{1}, COSInt{1}]}
Resources = COSDictionary{(COSName{ProcSet}:COSArray{[COSName{PDF}, COSName{ImageB}]}) }
Encoding = COSDictionary{(COSName{Differences}:COSArray{[COSInt{32}, COSName{space}]}) (COSName{Type}:COSName{Encoding}) }
CharProcs = COSDictionary{(COSName{space}:COSDictionary{(COSName{Length}:COSInt{67}) (COSName{Filter}:COSName{FlateDecode}) }) }*/
		
		AMIFont amiFont = null;
		String fontName = AMIFont.getFontName(dict);
		
		String typeS = null;
		amiFont = getAmiFontByFontName(fontName);
		if (amiFont == null) {
			// some confusion here between fontName and fontFamilyName
			amiFont = new AMIFont(fontName, null, typeS, dict);
			amiFont.setFontName(fontName);
			amiFontByFontNameMap.put(fontName, amiFont);
	
			String indent = "";
			for (int i = 0; i < level; i++) {
				indent += " ";
			}
	
			LOG.debug(String.format("%s****************** level %d font dict:",
					indent, level));
	
			level++;
			indent += "    ";
	
			for (COSName key : dict.keySet()) {
				String keyName = key.getName();
				Object object = dict.getDictionaryObject(key);
				LOG.debug(String.format("%s****************** %s = %s", indent,
						keyName, object));
			}
	
			COSArray array = (COSArray) dict
					.getDictionaryObject(COSName.DESCENDANT_FONTS);
			if (array != null) {
				LOG.debug(String.format(
						"%s****************** descendant fonts (%d):", indent,
						array.size()));
				amiFont = lookupOrCreateFont(level, (COSDictionary) array.getObject(0));
			}
		}
		return amiFont;
	}


	public AMIFont getAmiFontByFont(PDFont pdFont) {
		ensureAMIFontMaps();
		String fontName = null;
		AMIFont amiFont = null;
		fontName = getFontName(pdFont);
		if (fontName == null) {
			throw new RuntimeException("No currentFontName");
		}
		amiFont = amiFontByFontNameMap.get(fontName);
		if (amiFont == null) {
			if (pdFont instanceof PDType1Font ||
				pdFont instanceof PDTrueTypeFont || 
				pdFont instanceof PDType0Font ||
				pdFont instanceof PDType3Font) {
				amiFont = new AMIFont(pdFont);
				amiFontByFontNameMap.put(fontName, amiFont);
				String fontFamilyName = amiFont.getFontFamilyName();
				amiFont.setNonStandardFontFamily(this.getFontFamilyByFamilyName(fontFamilyName));
				recordExistingOrAddNewFontFamily(fontFamilyName, amiFont);
			} else {
				throw new RuntimeException("Cannot find font type: "+pdFont+" / "+pdFont.getSubType()+", ");
			}
		}
		return amiFont;
	}

	private String getFontName(PDFont pdFont) {
		String fontName;
		AMIFont amiFont;
		PDFontDescriptor fd = AMIFont.getFontDescriptorOrDescendantFontDescriptor(pdFont);
		if (fd == null) {
			if (nullFontDescriptorReport) {
				LOG.error("****************** Null Font Descriptor : "+pdFont+"\n       FURTHER ERRORS HIDDEN");
				nullFontDescriptorReport = false;
			}
		}
		if (fd == null) {
			amiFont = this.lookupOrCreateFont(0, (COSDictionary) pdFont.getCOSObject());
			fontName = amiFont.getFontName();
			if (fontName == null) {
				throw new RuntimeException("No currentFontName");
			}
		} else {
			fontName = fd.getFontName();
		}
		return fontName;
	}

	/** uses PDFBox list of standard symbols to convert to characters.
	 * e.g. "two" converts to "2" (unicode codePoint 50)
	 * some are identity ops - "a" converts to "a"
	 * @param symbol
	 * @return
	 */
//	public static String convertToUnicodeWithPDFStandardEncoding(String symbol) {
//		return StandardEncoding.INSTANCE.getCharacter(symbol);
//	}
	
	public static Map<String, AMIFont> readAmiFonts() {
		return readAmiFonts(FontFamilySet.STANDARD_UNICODE_FONT_FAMILY_SET_XML);
	}

	public static Map<String, AMIFont> readAmiFonts(String resourceName) {
		Map<String, AMIFont> fontMap = new HashMap<String, AMIFont>();
		try {
			InputStream is = Util.getResourceUsingContextClassLoader(resourceName, NonStandardFontManager.class);
			Element amiFontList = new Builder().build(is).getRootElement();
			for (int i = 0; i < amiFontList.getChildElements().size(); i++) {
				Element amiFontElement = amiFontList.getChildElements().get(i);
				String familyName = amiFontElement.getAttributeValue(NAME);
				String encoding = amiFontElement.getAttributeValue(FONT_ENCODING); 
				String type = amiFontElement.getAttributeValue(TYPE);
				if (
						familyName == null 
//						|| encoding == null 
						|| type == null) {
					throw new RuntimeException("Must have family and type for font");
				}
				if (fontMap.get(familyName) != null) {
					throw new RuntimeException("AMIFont map ("+resourceName+") already contains family: "+familyName);
				}
//				String symbol = amiFontElement.getAttributeValue(IS_SYMBOL);
//				Boolean isSymbol = (symbol == null) ? false : new Boolean(symbol);
				AMIFont amiFont = new AMIFont(familyName, encoding, type);
				fontMap.put(familyName, amiFont);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/parse AMI fonts: "+resourceName, e);
		}
		return fontMap;
	}
			
	public static Element createAmiFontList(String resourceName, Map<String, AMIFont> fontMap) {
		Element fontList = new Element(FONTS);
		String[] families = fontMap.keySet().toArray(new String[0]);
		Arrays.sort(families);
		for (String family : families) {
			AMIFont amiFont = fontMap.get(family);
			Element font = new Element(FONT);
			fontList.appendChild(font);
			font.addAttribute(new Attribute(NAME, family));
			String encoding = amiFont.getFontEncoding();
			if (encoding != null) {
				font.addAttribute(new Attribute(FONT_ENCODING, encoding));
			}
			font.addAttribute(new Attribute(TYPE, amiFont.getFontType()));
//			Boolean isSymbol = amiFont.isSymbol();
//			if (isSymbol != null) {
//				font.addAttribute(new Attribute(IS_SYMBOL, isSymbol.toString()));
//			}
		}
		return fontList;
	}

	public NonStandardFontFamily getFontFamilyByFamilyName(String fontFamilyName) {
		NonStandardFontFamily fontFamily = standardUnicodeFontFamilySet.getFontFamilyByName(fontFamilyName);
		if (fontFamily == null) {
			fontFamily = standardNonUnicodeFontFamilySet.getFontFamilyByName(fontFamilyName);
		}
		if (fontFamily == null && guessNonStandardEncoding) {
			fontFamily = nonStandardFontFamilySet.getFontFamilyByName(fontFamilyName);
		}
		if (fontFamily == null && guessNonStandardEncoding) {
			fontFamily = unknownFontFamilySet.getFontFamilyByName(fontFamilyName);
		}
		return fontFamily;
	}

	public NonStandardFontFamily recordExistingOrAddNewFontFamily(String fontName, AMIFont amiFont) {
		String fontFamilyName = amiFont.getFontFamilyName();
		NonStandardFontFamily fontFamily = amiFont.getOrCreateNonStandardFontFamily(this); // looks hairy...
		if (standardUnicodeFontFamilySet.containsKey(fontFamilyName)) {
			LOG.trace(fontFamilyName+" is a standard FontFamily");
		} else if (nonStandardFontFamilySet.containsKey(fontFamilyName)) {
			LOG.trace(fontFamilyName+" is a known non-standard FontFamily");
		} else if (unknownFontFamilySet.containsKey(fontFamilyName)) {
			LOG.trace(fontFamilyName+" is a known newFontFamily");
		} else {
			LOG.trace(fontName+" is being added as new FontFamily ("+fontFamilyName+")");
			if (fontFamily == null) {
				LOG.trace("ami: "+amiFont.toString());
				fontFamily = new NonStandardFontFamily();
				fontFamily.setName(String.valueOf(fontName));
				LOG.trace("created new FontFamily: "+fontFamilyName);
			}
			unknownFontFamilySet.add(fontName, fontFamily);
		}
		return fontFamily;
	}
	
	public FontFamilySet getNewFontFamilySet() {
		return unknownFontFamilySet;
	}

	public static String getUnknownCharacterSymbol() {
		return String.valueOf((char)UNKNOWN_CHAR);
	}

	public void setNullFontDescriptorReport(boolean b) {
		this.nullFontDescriptorReport = b;
	}
	
}
