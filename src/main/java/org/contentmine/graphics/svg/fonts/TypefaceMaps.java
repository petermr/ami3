package org.contentmine.graphics.svg.fonts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/** manages a set of Typefaces.
 * likely to be created in analysis of one or more documents.
 * Also has Map functionality
 * 
 * @author pm286
 *
 */
public class TypefaceMaps implements Set<Typeface> {
	private static final Logger LOG = Logger.getLogger(TypefaceMaps.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Set<Typeface> typefaceSet;
	private Map<String, Typeface> typefaceByName;
	private Multimap<String, Typeface> typefaceByFontStyle;
	private Multimap<String, Typeface> typefaceByFontWeight;
	private Multimap<String, Typeface> typefaceByFill;
	private Multimap<String, Typeface> typefaceByStroke;
	private Multimap<Double, Typeface> typefaceByFontSize;
	private String setName;

	public TypefaceMaps() {
	}
	
	public TypefaceMaps(String name) {
		this();
		this.setName = name;
	}
	
	public int size() {
		return typefaceSet == null ? 0 : typefaceSet.size();
	}
	
	public boolean add(Typeface typeface) {
		getOrCreateTypefaceMaps();
		String name = typeface.getTypefaceName();
		typefaceByName.put(name, typeface);
		List<String> fontWeights = typeface.getFontWeights();
		for (String fontWeight : fontWeights) {
			typefaceByFontWeight.put(fontWeight, typeface);
		}
		List<String> fontStyles = typeface.getFontStyles();
		for (String fontStyle : fontStyles) {
			typefaceByFontStyle.put(fontStyle, typeface);
		}
		List<String> fills = typeface.getFills();
		for (String fill : fills) {
			typefaceByFill.put(fill, typeface);
		}
		List<String> strokes = typeface.getStrokes();
		for (String stroke : strokes) {
			typefaceByStroke.put(stroke, typeface);
		}
		List<Double> fontSizes = typeface.getFontSizes();
		for (Double fontSize : fontSizes) {
			typefaceByFontSize.put(fontSize, typeface);
		}
		
		return typefaceSet.add(typeface);
	}

	public boolean isEmpty() {
		getOrCreateTypefaceSet();
		return typefaceSet.isEmpty();
	}
	public boolean contains(Object o) {
		getOrCreateTypefaceSet();
		return typefaceSet.contains(o);
	}
	public Iterator<Typeface> iterator() {
		getOrCreateTypefaceSet();
		return typefaceSet.iterator();
	}
	public Object[] toArray() {
		getOrCreateTypefaceSet();
		return typefaceSet.toArray();
	}
	public <T> T[] toArray(T[] a) {
		getOrCreateTypefaceSet();
		return typefaceSet.toArray(a);
	}
	public boolean remove(Object o) {
		getOrCreateTypefaceSet();
		return typefaceSet.remove(o);
	}
	public boolean containsAll(Collection<?> c) {
		getOrCreateTypefaceSet();
		return typefaceSet.containsAll(c);
	}
	public boolean addAll(Collection<? extends Typeface> c) {
		getOrCreateTypefaceSet();
		return typefaceSet.addAll(c);
	}
	public boolean retainAll(Collection<?> c) {
		getOrCreateTypefaceSet();
		return typefaceSet.retainAll(c);
	}
	public boolean removeAll(Collection<?> c) {
		getOrCreateTypefaceSet();
		return typefaceSet.removeAll(c);
	}
	public void clear() {
		getOrCreateTypefaceSet();
		typefaceSet.clear();
	}
	public boolean equals(Object o) {
		getOrCreateTypefaceSet();
		return typefaceSet.equals(o);
	}
	public int hashCode() {
		getOrCreateTypefaceSet();
		return typefaceSet.hashCode();
	}

	public String getSetName() {
		getOrCreateTypefaceSet();
		return setName;
	}

	public void setMapsName(String setName) {
		getOrCreateTypefaceSet();
		this.setName = setName;
	}

	public Set<Typeface> getOrCreateTypefaceSet() {
		if (typefaceSet == null) {
			typefaceSet = new HashSet<Typeface>();
		}
		return typefaceSet;
	}
	
	private void getOrCreateTypefaceMaps() {
		getOrCreateTypefaceSet();
		getOrCreateTypefaceByName();
		getOrCreateTypefaceByFontStyle();
		getOrCreateTypefaceByFontWeight();
		getOrCreateTypefaceByFill();
		getOrCreateTypefaceByStroke();
		getOrCreateTypefaceByFontSize();
	}

	private void getOrCreateTypefaceByFontSize() {
		if (typefaceByFontSize == null) {
			typefaceByFontSize = ArrayListMultimap.create();
		}
	}

	public List<Typeface> getTypefaceListByFontSize(Double fontSize) {
		getOrCreateTypefaceByFontSize();
		return new ArrayList<Typeface>(typefaceByFontSize.get(fontSize));
	}

	private void getOrCreateTypefaceByStroke() {
		if (typefaceByStroke == null) {
			typefaceByStroke = ArrayListMultimap.create();
		}
	}

	public List<Typeface> getTypefaceListByStroke(String stroke) {
		getOrCreateTypefaceByStroke();
		return new ArrayList<Typeface>(typefaceByStroke.get(stroke));
	}

	private void getOrCreateTypefaceByFill() {
		if (typefaceByFill == null) {
			typefaceByFill = ArrayListMultimap.create();
		}
	}

	public List<Typeface> getTypefaceListByFill(String fill) {
		getOrCreateTypefaceByFill();
		return new ArrayList<Typeface>(typefaceByFill.get(fill));
	}

	private void getOrCreateTypefaceByFontWeight() {
		if (typefaceByFontWeight == null) {
			typefaceByFontWeight = ArrayListMultimap.create();
		}
	}

	public List<Typeface> getTypefaceListByFontWeight(String fontWeight) {
		getOrCreateTypefaceByFontWeight();
		return new ArrayList<Typeface>(typefaceByFontWeight.get(fontWeight));
	}

	private void getOrCreateTypefaceByFontStyle() {
		if (typefaceByFontStyle == null) {
			typefaceByFontStyle = ArrayListMultimap.create();
		}
	}
	
	public List<Typeface> getTypefaceListByFontStyle(String fontStyle) {
		getOrCreateTypefaceByFontStyle();
		return new ArrayList<Typeface>(typefaceByFontStyle.get(fontStyle));
	}

	public Typeface getTypefaceByName(String name) {
		getOrCreateTypefaceByName();
		return typefaceByName.get(name);
	}

	private void getOrCreateTypefaceByName() {
		if (typefaceByName == null) {
			typefaceByName = new HashMap<String, Typeface>();
		}
	}
	
	public String toString() {
		getOrCreateTypefaceSet();
		StringBuilder sb = new StringBuilder();
		sb.append("Typefaces:\n");
		
		List<Typeface> typefaceList = new ArrayList<Typeface>(typefaceSet);
		Collections.sort(typefaceList);
		return typefaceSet.toString();
	}

	public Set<Typeface> getTypefaceSet() {
		return typefaceSet;
	}
	
	public List<Typeface> getSortedTypefaceList() {
		List<Typeface> typefaceList = new ArrayList<Typeface>();
		if (typefaceSet != null) {
			typefaceList = new ArrayList<Typeface>(typefaceSet);
			Collections.sort(typefaceList);
		}
		return typefaceList;
	}
	
}
