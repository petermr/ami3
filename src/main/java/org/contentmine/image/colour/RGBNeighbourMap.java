package org.contentmine.image.colour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** delegate for neighbourMap
 * 
 * @author pm286
 *
 */
public class RGBNeighbourMap {
	private static final Logger LOG = Logger.getLogger(RGBNeighbourMap.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Multimap<RGBColor, RGBColor> neighbourMap;
	private List<Entry<RGBColor>> rgbList;
	private Multiset<RGBColor> colorSet;

	public RGBNeighbourMap(Multiset<RGBColor> colorSet) {
		this.colorSet = colorSet;
		calculateRGBNeighbours();
	}
	
	public List<RGBColor> get(RGBColor rgbValue) {
		return neighbourMap == null || rgbValue == null ? null : new ArrayList<RGBColor>(neighbourMap.get(rgbValue));
	}

	public Set<RGBColor> keySet() {
		return neighbourMap == null ? null : neighbourMap.keySet();
	}

	public void put(RGBColor ii, RGBColor jj) {
		if (ii != null && neighbourMap != null) {
			neighbourMap.put(ii, jj);
		}
	}

	public void calculateRGBNeighbours() {
		int maxStep = 0x7f;
		getOrCreateRGBList();
		ensureNeighbourMap();
		for (int i = 0; i < rgbList.size() - 1; i++) {
			Entry<RGBColor> rgbEntryi = rgbList.get(i);
			RGBColor rgbi = rgbEntryi.getElement();
			for (int j = i + 1; j < rgbList.size(); j++) {
				Entry<RGBColor> rgbEntryj = rgbList.get(j);
				RGBColor rgbj = rgbEntryj.getElement();
				Integer diff = rgbi.absDiff(rgbj, maxStep);
				String ss;
				if (rgbi.isGray() || rgbj.isGray()) {
					ss = "gry";
				} else {
					ss = ColorUtilities.createPaddedHex(diff);
					ss = ss.replace("7f", "1").replace("ff", "2").replaceAll("00", "0");
				}
				
				if (ss.equals("100") || ss.equals("010") || ss.equals("001")) {
					put(rgbi, rgbj);
					put(rgbj, rgbi);
				}
			}
		}
	}

	private void ensureNeighbourMap() {
		if (neighbourMap == null) {
			neighbourMap = ArrayListMultimap.create();
		}
	}

	private List<Entry<RGBColor>> getOrCreateRGBList() {
		if (rgbList == null) {
			if (colorSet != null) {
				rgbList = RGBColor.createRGBListSortedByCount(colorSet);
			}
			if (rgbList == null) {
				throw new RuntimeException("null RGBList "+colorSet);
			}
		}
		return rgbList;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		getOrCreateRGBList();
		for (int i = 0; i < rgbList.size() - 1; i++) {
			sb.append(ColorUtilities.createPaddedHex(rgbList.get(i).getElement().getRGBInteger())+"> ");
			for (int j = i + 1; j < rgbList.size(); j++) {
				sb.append(" "+ColorUtilities.createPaddedHex(rgbList.get(j).getElement().getRGBInteger()));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public int size() {
		getOrCreateRGBList();
		return neighbourMap.size();
	}
	
	public boolean containsValue(Object value) {
		return neighbourMap == null ? false : neighbourMap.containsValue(value);
	}

	public void clear() {
		if (neighbourMap != null) {
			clear();
		}
	}

	public Collection<RGBColor> values() {
		return neighbourMap == null ? null : neighbourMap.values();
	}

	public boolean containsEntry(Object key, Object value) {
		return neighbourMap == null ? false : neighbourMap.containsEntry(key, value);
	}

	public boolean remove(Object key, Object value) {
		return neighbourMap == null ? false : neighbourMap.remove(key, value);
	}

	public boolean putAll(RGBColor key, Iterable<? extends RGBColor> values) {
		return neighbourMap == null ? false : neighbourMap.putAll(key, values);
	}

	public boolean putAll(Multimap<? extends RGBColor, ? extends RGBColor> multimap) {
		return neighbourMap == null ? false : neighbourMap.putAll(multimap);
	}

	RGBColor getMoreFrequentRGBNeighbour(ColorFrequenciesMap colorFrequenciesMap, RGBColor rgbColor) {
		int thisFrequency = colorFrequenciesMap.get(rgbColor);
		List<RGBColor> rgbValueNeighbourList = new ArrayList<RGBColor> (this.get(rgbColor));
		if (rgbValueNeighbourList != null) { 
			// find largest colourNeighbour
			int maxRgbNeighbourCount = -1;
			int maxRgbNeighbourValue = -1;
			// go through rgb values
			for (int ival = 0; ival < rgbValueNeighbourList.size(); ival++) {
				RGBColor rgbNeighbourValue = rgbValueNeighbourList.get(ival);
				Integer rgbNeighbourCount = colorFrequenciesMap.get(rgbNeighbourValue);
				if (rgbNeighbourCount != null) {
					if (rgbNeighbourCount > maxRgbNeighbourCount) {
						maxRgbNeighbourCount = rgbNeighbourCount;
						maxRgbNeighbourValue = rgbNeighbourValue.getRGBInteger();
					}
				}
			}
			if (maxRgbNeighbourCount > thisFrequency) {
				RGBColor rgbColor1 = new RGBColor(maxRgbNeighbourValue);
//				LOG.debug("orig "+rgbColor.getHex()+" => "+rgbColor1);
				rgbColor = rgbColor1;
			}
		}
		return rgbColor;
	}

	// these methods in the multimap interface aren't compatible with neighbourMap, no idea why
	
//	public Set<java.util.Map.Entry<Integer, Integer>> entrySet() {
//	return neighbourMap == null ? null : neighbourMap.entrySet();
//}

//	public Collection<Integer> replaceValues(Integer key, Iterable<? extends Integer> values) {
//		return neighbourMap == null ? false : neighbourMap.replaceValues(key, values);
//	}

//	public Collection<Integer> removeAll(Object key) {
//		return neighbourMap == null ? false : neighbourMap.removeAll(key);
//	}

//	public Multiset<Integer> keys() {
//		return neighbourMap == null ? false : neighbourMap.keys();
//	}

//	public Collection<java.util.Map.Entry<Integer, Integer>> entries() {
//		return neighbourMap == null ? false : neighbourMap.entries();
//	}

//	public Map<Integer, Collection<Integer>> asMap() {
//		return neighbourMap == null ? false : neighbourMap.asMap();
//	}
}
