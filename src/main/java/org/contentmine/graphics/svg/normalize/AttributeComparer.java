package org.contentmine.graphics.svg.normalize;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGConstants;
import org.contentmine.graphics.svg.SVGText;

import nu.xom.Attribute;
import nu.xom.Element;

/** compares SVG attributes by name and value.
 * understand much but not all of the SVG spec
 * Primary role is to help group svg:text elements together and preserve styling
 * 
 * @author pm286
 *
 */
public class AttributeComparer {
	private static final Logger LOG = Logger.getLogger(AttributeComparer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}



public static String[] FONT = {
    "font",
    "font-family",
    "font-size",
    "font-size-adjust",
    "font-stretch",
    "font-style",
    "font-variant",
    "font-weight",
    
    "font-name", // not SVG standard
};
    public static String[] TEXT = {
    "direction",
    "letter-spacing",
    "text-decoration",
    "unicode-bidi",
    "word-spacing",
    "alignment-baseline",
    "baseline-shift",
    "dominant-baseline",
    "glyph-orientation-horizontal",
    "glyph-orientation-vertical",
    "kerning",
    "text-anchor",
    "writing-mode",
    };
    public static String[] OTHER_VISUAL = {
    "clip",
    "color", 
    "cursor",
    "display",
    "overflow", 
    "visibility",
    };
    public static String[] CLIPPING_MASKING_COMPOSITION = {
    "clip-path",
    "clip-rule",
    "mask",
    "opacity",
    };
    public static String[] FILTER_EFFECTS = {
    "enable-background",
    "filter",
    "flood-color",
    "flood-opacity",
    "lighting-color",
    };
    public static String[] GRADIENT = {
    "stop-color",
    "stop-opacity",
    };
    public static String[] INTERACTIVITY = {
    "pointer-events",
    };
    public static String[] COLOR_PAINTING = {
    "color-interpolation",
    "color-interpolation-filters",
    "color-profile",
    "color-rendering",
    "fill",
    "fill-opacity",
    "fill-rule",
    "image-rendering",
    "marker",
    "marker-end",
    "marker-mid",
    "marker-start",
    "shape-rendering",
    "stroke",
    "stroke-dasharray",
    "stroke-dashoffset",
    "stroke-linecap",
    "stroke-linejoin",
    "stroke-miterlimit",
    "stroke-opacity",
    "stroke-width",
    "text-rendering",
    };
    public static String[][] STYLES = new String[][] {
    	FONT,
    	TEXT, 
    	OTHER_VISUAL,
    	CLIPPING_MASKING_COMPOSITION,
    	FILTER_EFFECTS,
    	GRADIENT,
    	INTERACTIVITY,
    	COLOR_PAINTING,
    };
    
    public final static String X = "x";
	public final static String Y = "y";
	public final static String DX = "dx";
	public final static String DY = "dy";
	public final static String TRANSFORM = "transform";
	public final static String ROTATE = "rotate";
	public final static String TEXT_LENGTH = "textLength";
	public final static String WIDTH = "width";
	public final static String HEIGHT = "height";

    public static String[] COORDS = {
		X,
		Y,
		DX,
		DY,
		TRANSFORM,
		ROTATE,
		TEXT_LENGTH,
		WIDTH,
		HEIGHT,
    };
	public final static String ID = "id";
    public static String [] IDS = {
		"id"
    };
    
    public static Set<String> STYLE_SET = new HashSet<String>();
    private static Set<String> VARIANT_SET = new HashSet<String>();
    static {
    	addToSet(STYLE_SET, 
    			FONT,
    	    	TEXT, 
    	    	OTHER_VISUAL,
    	    	CLIPPING_MASKING_COMPOSITION,
    	    	FILTER_EFFECTS,
    	    	GRADIENT,
    	    	INTERACTIVITY,
    	    	COLOR_PAINTING);
    	addToSet(VARIANT_SET, COORDS, IDS);
    };

	private static void addToSet(Set<String> set, String[] ... stringArrays ) {
		for (String[] stringArray : stringArrays) {
			set.addAll(Arrays.asList(stringArray));
		}
	}

	private Map<String, Attribute> elementAttributeByName0;
	private Map<String, Attribute> elementAttributeByName1;
	private Element element0;
	private Element element1;
	private Set<String> ignoreAttNameSet;

	public AttributeComparer() {
		setIgnoreAttNameSet(VARIANT_SET);
	}
	
	public AttributeComparer(SVGText text0) {
		this();
		this.setElement0(text0);
	}
	
	public void setIgnoreAttNameSet(Set<String> ignoreNames) {
		this.ignoreAttNameSet = ignoreNames;
	}
	
	public void setElement0(Element element0) {
		this.element0 = element0;
		elementAttributeByName0 = getOrCreateAttributeByNameMap(element0);
	}
	public void setElement1(Element element1) {
		this.element1 = element1;
		elementAttributeByName1 = getOrCreateAttributeByNameMap(element1);
	}
	
	public Set<String> getAttNames1Not0() {
		return getDiffSet(elementAttributeByName0, elementAttributeByName1);
	}

	public Set<String> getAttNames0Not1() {
		return getDiffSet(elementAttributeByName1, elementAttributeByName0);
	}

	private Set<String> getDiffSet(Map<String, Attribute> map0, Map<String, Attribute> map1) {
		Set<String> diff = new HashSet<String>();
		if (map0 == null || map1 == null) {
			throw new RuntimeException("map null: "+map0+" || "+map1);
		} 
		diff = new HashSet<String>(map1.keySet());
		diff.removeAll(map0.keySet());
		return diff;
	}

	/** maps non-SVG namespaces onto a single string.
	 * svgx:foo => svgx_foo
	 * 
	 * @param attribute
	 * @return munged attribute name
	 */
	public static String makeLocalName(Attribute attribute) {
		String localName = attribute.getLocalName();
		if (SVGConstants.SVGX_NS.equals(attribute.getNamespaceURI())) {
			localName = SVGConstants.SVGX_PREFIX + localName;
		}
		return localName;
	}

	public Map<String, Attribute> getOrCreateAttributeByNameMap(Element element) {
		Map<String, Attribute> attributeByNameMap = new HashMap<String, Attribute>();
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute attribute = element.getAttribute(i);
			String localName = AttributeComparer.makeLocalName(attribute);
			if (!ignoreAttNameSet.contains(localName)) {
				if (attributeByNameMap.containsKey(localName)) {
					throw new RuntimeException("Duplicate attribute name: "+localName);
				}
				attributeByNameMap.put(localName, attribute);
			}
		}
		return attributeByNameMap;
	}

	public Set<Pair<Attribute, Attribute>> getUnequalTextValues() {
		Set<Pair<Attribute, Attribute>> diffSet = new HashSet<Pair<Attribute, Attribute>>();
		addToDiffSet(diffSet, elementAttributeByName1, getAttNames1Not0());
		addToDiffSet(diffSet, elementAttributeByName0, getAttNames0Not1());
		for (int i = 0; i < element0.getAttributeCount(); i++) {
			Attribute attribute0 = element0.getAttribute(i);
			String localName = attribute0.getLocalName();
			String value0 = element0.getAttributeValue(localName);
			if (!ignoreAttNameSet.contains(localName)) {
				Attribute attribute1 = element1.getAttribute(localName);
				String value1 = attribute1 == null ? null : attribute1.getValue();
				if (!isEqual(value0, value1)) {
					Pair<Attribute, Attribute> pair = new MutablePair<Attribute, Attribute>(attribute0, attribute1);
					diffSet.add(pair);
				}
			}
		}
		return diffSet;
	}

	private void addToDiffSet(Set<Pair<Attribute, Attribute>> diffSet, Map<String, Attribute> map, Set<String> attNames) {
		for (String localName : attNames) {
			Attribute att = map.get(localName);
			diffSet.add(new MutablePair<Attribute, Attribute>(att, (Attribute)null));
		}
	}

	private boolean isEqual(String value0, String value1) {
		if (value0 == null) {
			return (value1 == null);
		}
		return value0.equals(value1);
	}


}
