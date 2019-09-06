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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.pdf2svg.util.PConstants;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

/** set of FontFamily
 * 
 * @author pm286
 *
 */
public class FontFamilySet {

	private final static Logger LOG = Logger.getLogger(FontFamilySet.class);
	
	private static final String FONT_FAMILY_SET = "fontFamilySet";
	public static final String FONT_FAMILY_DIR = PConstants.PDF2SVG_ROOT+"/"+"fontFamilySets";
	public static final String STANDARD_UNICODE_FONT_FAMILY_SET_XML = FONT_FAMILY_DIR+"/"+"standardUnicodeFontFamilySet.xml";
	public static final String STANDARD_NON_UNICODE_FONT_FAMILY_SET_XML = FONT_FAMILY_DIR+"/"+"standardNonUnicodeFontFamilySet.xml";
	public static final String NON_STANDARD_FONT_FAMILY_SET_XML = FONT_FAMILY_DIR+"/"+"nonStandardFontFamilySet.xml";

	private Map<String, NonStandardFontFamily> fontFamilyByFamilyName;

	public FontFamilySet() {
		ensureMaps();
	}

	private void ensureMaps() {
		if (fontFamilyByFamilyName == null) {
			fontFamilyByFamilyName = new HashMap<String, NonStandardFontFamily>();
		}
	}

	/**
      <font family="Courier" fontType="PDType1Font" note="a standard14 font" serif="yes" unicode="yes"/>
      
     * @param fontFamilySetXmlResource
	 * @return
	 */
	public static FontFamilySet readFontFamilySet(String fontFamilySetXmlResource) {
		LOG.trace("FFS"+fontFamilySetXmlResource);
		FontFamilySet fontFamilySet = null;
		try {
			Element fontFamilySetElement = new Builder().build(
					Util.getResourceUsingContextClassLoader(fontFamilySetXmlResource, FontFamilySet.class)).getRootElement();
			fontFamilySet = createFromElement(fontFamilySetElement);

		} catch (Exception e) {
			throw new RuntimeException("Cannot read FontFamilySet: "+fontFamilySetXmlResource, e);
		}
		return fontFamilySet;
	}

	public static FontFamilySet createFromElement(Element fontFamilySetElement) {
		FontFamilySet fontFamilySet = new FontFamilySet();
		String rootName = fontFamilySetElement.getLocalName();
		if (!(FONT_FAMILY_SET.equals(rootName))) {
			throw new RuntimeException("FontFamilySet must have rootElement "+FONT_FAMILY_SET+"; found: "+rootName);
		}
		Elements childElements = fontFamilySetElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element fontFamilyElement = childElements.get(i);
			NonStandardFontFamily fontFamily = NonStandardFontFamily.createFromElement(fontFamilyElement);
			if (fontFamily == null) {
				throw new RuntimeException("Cannot read/parse fontFamilyElement: "+((fontFamilyElement == null) ? null : fontFamilyElement.toXML()));
			}
			String family = fontFamily.getName();
			if (fontFamilySet.containsKey(family)) {
				throw new RuntimeException("Duplicate name: "+family);
			}
			fontFamilySet.fontFamilyByFamilyName.put(family, fontFamily);
		}
		return fontFamilySet;
	}
	
	boolean containsKey(String name) {
		return fontFamilyByFamilyName.containsKey(name);
	}

	public NonStandardFontFamily getFontFamilyByName(String fontFamilyName) {
		return fontFamilyByFamilyName.get(fontFamilyName);
	}

	void add(String fontFamilyName, NonStandardFontFamily fontFamily) {
		if (fontFamily == null) {
			throw new RuntimeException("Cannot add null fontFamily");
		}
		fontFamilyByFamilyName.put(fontFamilyName, fontFamily);
	}

	public Element createElement() {
		Element fontsElement = new Element(FONT_FAMILY_SET);
		for (String fontFamilyName : fontFamilyByFamilyName.keySet()) {
			NonStandardFontFamily fontFamily = fontFamilyByFamilyName.get(fontFamilyName);
			if (fontFamily == null) {
				throw new RuntimeException("BUG null fontFamily should never happen: ");
			}
			Element fontFamilyElement = fontFamily.createElement();
			fontsElement.appendChild(fontFamilyElement);
		}
		return fontsElement;
	}
}
