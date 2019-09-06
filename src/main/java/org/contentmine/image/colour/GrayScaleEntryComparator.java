package org.contentmine.image.colour;

import java.util.Comparator;

import com.google.common.collect.Multiset.Entry;

public class GrayScaleEntryComparator implements Comparator<Entry<RGBColor>> {

	public int compare(Entry<RGBColor> o1, Entry<RGBColor> o2) {
		return o1.getElement().getOrCreateAverageGrayValue() - o2.getElement().getOrCreateAverageGrayValue();
	}

}
