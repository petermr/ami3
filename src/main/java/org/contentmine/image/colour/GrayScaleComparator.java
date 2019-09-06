package org.contentmine.image.colour;

import java.util.Comparator;

public class GrayScaleComparator implements Comparator<RGBColor> {

	public int compare(RGBColor o1, RGBColor o2) {
		// 24-bits so no sign problem
		return (o1.getOrCreateAverageGrayValue() - o2.getOrCreateAverageGrayValue());
	}

}
