package org.contentmine.graphics.svg.linestuff;

import java.util.Comparator;

import org.contentmine.graphics.svg.SVGLine;

public class HorizontalLineComparator implements Comparator<SVGLine>{

	public int compare(SVGLine l1, SVGLine l2) {
		if (l1 == null || l2 == null) return -1;
		return (int)(l1.getXY(0).getY() - l2.getXY(0).getY());
	}
	
}
