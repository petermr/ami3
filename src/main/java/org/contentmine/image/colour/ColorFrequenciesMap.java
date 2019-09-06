package org.contentmine.image.colour;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.Multiset;

/** frequencies of colors.
 * key is RGB value
 * values are counts
 * 
 * @author pm286
 *
 */
public class ColorFrequenciesMap /*implements Map<RGBColor, Integer>*/ {
	private static final Logger LOG = Logger.getLogger(ColorFrequenciesMap.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	// might also use Entry<RGBColor> ???
	private Map<RGBColor, Integer> frequenciesMap;

	ColorFrequenciesMap() {
		
	}
	
	public static ColorFrequenciesMap createMap(Multiset<RGBColor> colorSet) {
		ColorFrequenciesMap colorFrequenciesMap = null;
		if (colorSet != null) {
			colorFrequenciesMap = new ColorFrequenciesMap();
			colorFrequenciesMap.frequenciesMap = RGBColor.createRGBColorFrequencyMap(colorSet);
		}
		return colorFrequenciesMap;
	}

	public Integer get(Integer rgbValue) {
		return frequenciesMap == null || rgbValue == null ? null : frequenciesMap.get(rgbValue);
	}

	public int size() {
		return frequenciesMap == null ? 0 : frequenciesMap.size();
	}

	public boolean isEmpty() {
		return frequenciesMap == null ? true : frequenciesMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		if (key instanceof Integer) {
			return frequenciesMap == null ? null : frequenciesMap.containsValue((Integer)key);
		} else {
			return false;
		}
	}

	public boolean containsValue(Object value) {
		if (value instanceof Integer) {
			return frequenciesMap == null ? null : frequenciesMap.containsValue((Integer)value);
		} else {
			return false;
		}
	}

	public Integer get(RGBColor key) {
		return frequenciesMap == null ? null : frequenciesMap.get(key);
	}

	public Integer put(RGBColor key, Integer value) {
		LOG.debug("put: "+key);
		return frequenciesMap == null ? null : frequenciesMap.put(key, value);
	}

	public Integer remove(Object key) {
		return frequenciesMap == null ? null : frequenciesMap.remove(key);
	}

	public void putAll(Map<? extends RGBColor, ? extends Integer> m) {
		if (frequenciesMap != null) {
			putAll(m);
		}
	}

	public void clear() {
		if (frequenciesMap != null) {
			clear();
		}
	}

	public Set<RGBColor> keySet() {
		return frequenciesMap == null ? null : frequenciesMap.keySet();
	}

	public Collection<Integer> values() {
		return frequenciesMap == null ? null : frequenciesMap.values();
	}

	public Set<java.util.Map.Entry<RGBColor, Integer>> entrySet() {
		return frequenciesMap == null ? null : frequenciesMap.entrySet();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (frequenciesMap != null) {
			sb.append("size: "+frequenciesMap.size() + " ");
			for (RGBColor key : keySet()) {
				sb.append(key+"=>"+this.get(key)+" ");
			}
		}
		return sb.toString();
	}

}
