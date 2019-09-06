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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.pdf2svg.util.PConstants;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

/** local implementation of codePointSet
 * 
    <codePoint decimal="9702" unicode="U+25E6" charName="WHITE BULLET" note="probably for lists and graph symbols" 
        confusions="ring operator U+2218 inverse bullet U+25D8"/>
    <codePoint decimal="12296" unicode="U+3008" charName="LEFT ANGLE BRACKET" 
        note="quasi-synonym" replaceByUnicode="U+003C" replaceName="LESS-THAN SIGN"/>
        
 * @author pm286
 *
 */
public class CodePointSet extends Element {

	public static final String TAG = "codePointSet";
	private final static Logger LOG = Logger.getLogger(CodePointSet.class);

	public static final String ENCODING = "encoding";
	public static final String HREF =   "href";
	public static final String ID   =   "id";
	public static final String IDREF  =   "idRef";
	public static final String RESOURCE = "resource";
	public static final String UNICODE = "Unicode";
	
	public static final String CODEPOINT_DIR = PConstants.PDF2SVG_ROOT+"/codepoints/";
	public static final String UNICODE_DIR = CODEPOINT_DIR+"unicode/";
	public static final String UNICODE_POINT_SET_XML = UNICODE_DIR+"unicode.xml";

	private Map<UnicodePoint, CodePoint> codePointByUnicodePointMap;
	private Map<String, CodePoint> codePointByUnicodeValueMap;
	private Map<Integer, CodePoint> codePointByDecimalMap;
	private Map<String, CodePoint> codePointByUnicodeNameMap;
	private Map<String, CodePoint> codePointByNameMap;
	private String encoding = null;
	private String id       = null;
	private String resource = null;

	public CodePointSet() {
		super(TAG);
		ensureMaps();
	}

	private void ensureMaps() {
		if (codePointByDecimalMap == null) {
			codePointByDecimalMap =      new HashMap<Integer, CodePoint>();
			codePointByUnicodePointMap = new HashMap<UnicodePoint, CodePoint>();
			codePointByUnicodeNameMap =  new HashMap<String, CodePoint>();
			codePointByUnicodeValueMap = new HashMap<String, CodePoint>();
			codePointByNameMap =         new HashMap<String, CodePoint>();
		}
	}

	public static CodePointSet readCodePointSet(String codePointSetXmlResource) {
		CodePointSet codePointSet = new CodePointSet();
		try {
			Element codePointSetElement = new Builder().build(
					Util.getResourceUsingContextClassLoader(codePointSetXmlResource, CodePointSet.class)).getRootElement();
			codePointSet = createFromElement(codePointSetElement);

		} catch (Exception e) {
			throw new RuntimeException("Cannot read CodePointSet: "+codePointSetXmlResource, e);
		}
		return codePointSet;
	}

	public static CodePointSet createFromElement(Element codePointSetElement) {
		CodePointSet codePointSet = new CodePointSet();
		if (!(TAG.equals(codePointSetElement.getLocalName()))) {
			throw new RuntimeException("CodePointSet must have rootElement: "+TAG);
		}
		codePointSet.addEncoding(codePointSetElement);
		codePointSet.addId(codePointSetElement);
		codePointSet.resource  = codePointSetElement.getAttributeValue(RESOURCE);
		Elements childElements = codePointSetElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element element = childElements.get(i);
			if (CodePoint.TAG.equals(element.getLocalName())) {
				codePointSet.createCodePoint(element);
			} else if (CodePointSet.TAG.equals(element.getLocalName())) {
				codePointSet.createCodePointSet(element);
			} else {
				throw new RuntimeException("Unknown/forbidden child of codePointSet: "+element.toXML());
			}
		}
		return codePointSet;
	}

	private void addEncoding(Element codePointSetElement) {
		this.encoding = codePointSetElement.getAttributeValue(ENCODING);
		if (this.encoding == null) {
			throw new RuntimeException("Must give encoding on: "+TAG);
		}
	}

	private void addId(Element codePointSetElement) {
		this.id  = codePointSetElement.getAttributeValue(ID);
		if (this.id == null) {
			throw new RuntimeException("Must give id on: "+TAG);
		}
	}

	private void createCodePoint(Element element) {
		CodePoint codePoint = CodePoint.createFromElement(element, this.encoding);
		this.addCodePoint(codePoint);
	}

	private void addCodePoint(CodePoint codePoint) {
		UnicodePoint unicodePoint = createUnicodePointAndCheckUniqueness( codePoint);
		this.codePointByUnicodePointMap.put(unicodePoint, codePoint);
		this.add(codePoint);
		LOG.trace("CodePoint "+codePoint);
	}

	private UnicodePoint createUnicodePointAndCheckUniqueness(CodePoint codePoint) {
		UnicodePoint unicodePoint = codePoint.getUnicodePoint();
		if (unicodePoint == null) {
			throw new RuntimeException("codePoint must contain unicode value");
		}
		if (this.containsKey(unicodePoint) && UNICODE.equals(this.encoding)) {
			throw new RuntimeException("Duplicate unicode in unicode encoding: "+unicodePoint);
		}
		return unicodePoint;
	}

	/** currently does not check for cyclic dependencies
	 * 
	 * @param codePointSet
	 * @param element
	 */
	private void createCodePointSet(Element element) {
		String href = element.getAttributeValue(HREF);
		String idRef = element.getAttributeValue(IDREF);
		if (idRef == null || href == null) {
			throw new RuntimeException("Must give idRef and href");
		}
		if (!href.startsWith("org")) {
			href = (resource != null) ? resource+"/"+href : href;
			href = href.replace("//", "/");
			if (href.contains("..") || href.contains("//")) {
				throw new RuntimeException("cannot resolve classpath with '..' or '//'");
			}
		}
		CodePointSet subCodePointSet = CodePointSet.readCodePointSet(href);
		if (subCodePointSet == null) {
			throw new RuntimeException("Cannot find codePointSet: "+href+"("+idRef+")");
		}
		if (!idRef.equals(subCodePointSet.id)) {
			throw new RuntimeException("Expected idRef: "+idRef+"; found: "+subCodePointSet.id);
		}
		List<CodePoint> subCodePoints = subCodePointSet.getCodePoints();
		for (CodePoint subCodePoint : subCodePoints) {
			subCodePoint.detach();
			this.addCodePoint(subCodePoint);
		}
	}

	private boolean containsKey(UnicodePoint unicodePoint) {
		return codePointByUnicodePointMap.containsKey(unicodePoint);
	}
	
	boolean containsKey(Integer decimal) {
		return codePointByDecimalMap.containsKey(decimal);
	}
	
	public Element createElementWithSortedIntegers() {
		Element codePointsElement = new Element(TAG);
		Integer[] codePointIntegers = codePointByDecimalMap.keySet().toArray(new Integer[0]);
		Arrays.sort(codePointIntegers);
		for (Integer codePointInteger : codePointIntegers) {
			CodePoint codePoint = codePointByDecimalMap.get(codePointInteger);
			Element codePointElement = (Element) codePoint.createElement().copy();
			codePointsElement.appendChild(codePointElement);
		}
		return codePointsElement;
		
	}
	
	public Set<String> getUnicodeNames() {
		return codePointByUnicodeNameMap.keySet();
	}

	public Set<String> getNames() {
		return codePointByNameMap.keySet();
	}

	public int size() {
		return codePointByDecimalMap.size();
	}

	/** adds and indexes codePoints checking for duplicates etc.
	*/
	public void add(CodePoint codePoint) {
		if (encoding == null) {
			throw new RuntimeException("CodePointSet must have encoding");
		}
		UnicodePoint unicodePoint = codePoint.getUnicodePoint();
		if (unicodePoint == null) {
			throw new RuntimeException("CodePoint must have unicodePoint");
		}
		Element element = codePoint.createElement();
		this.appendChild(element);
		makeIndexes(codePoint, unicodePoint);
	}

	private void makeIndexes(CodePoint codePoint, UnicodePoint unicodePoint) {
		if (codePoint.getDecimal() != null) {
			this.codePointByDecimalMap.put(codePoint.getDecimal(), codePoint);
		} else {
			Integer decimal = (unicodePoint == null) ? null : unicodePoint.getDecimalValue();
			if (decimal != null) {
				this.codePointByDecimalMap.put(decimal, codePoint);
			}
		}
		this.codePointByUnicodePointMap.put(unicodePoint, codePoint);
		this.codePointByUnicodeValueMap.put(unicodePoint.getUnicodeValue(), codePoint);
		if (codePoint.getName() != null) {
			this.codePointByNameMap.put(codePoint.getName(), codePoint);
		}
		if (codePoint.getUnicodeName() != null) {
			this.codePointByUnicodeNameMap.put(codePoint.getUnicodeName(), codePoint);
		}
	}
	
	public List<CodePoint> getCodePoints() {
		return (List<CodePoint>) Arrays.asList(codePointByUnicodePointMap.values().toArray(new CodePoint[0]));
	}

	public CodePoint getByUnicodePoint(UnicodePoint unicodePoint) {
		ensureMaps();
		return codePointByUnicodePointMap.get(unicodePoint);
	}
	
	public CodePoint getByUnicodeValue(String unicode) {
		ensureMaps();
		return codePointByUnicodeValueMap.get(unicode);
	}
	
	public CodePoint getByName(String name) {
		ensureMaps();
		return codePointByNameMap.get(name);
	}
	
	public CodePoint getByDecimal(Integer decimal) {
		ensureMaps();
		return codePointByDecimalMap.get(decimal);
	}

	public CodePoint getByUnicodeName(String unicodeName) {
		ensureMaps();
		return codePointByUnicodeNameMap.get(unicodeName);
	}
	public void ensureEncoding(String encoding) {
		if (this.encoding == null) {
			this.encoding = encoding;
		}
	}

	public boolean isUnicodeEncoded() {
		return UNICODE.equals(encoding);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
		this.addAttribute(new Attribute(ENCODING, encoding));
	}
	
	public void setId(String id) {
		this.id = id;
		this.addAttribute(new Attribute(ID, id));
	}
	
}
