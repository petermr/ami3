package org.contentmine.graphics.svg;

import java.util.Comparator;

public class SVGTextComparator implements Comparator<SVGText> {

	public enum TextComparatorType {
		ALPHA,
		X_COORD,
		Y_COORD,
	}
	
	private TextComparatorType type;
	
	public SVGTextComparator(TextComparatorType type) {
		this.type = type;
	}
	
	/** compares 2 SVGText on lexical value or coordinates
	 * the type is already set by the constructor
	 * if any values are null , return 0;
	 */
	public int compare(SVGText t1, SVGText t2) {
		if (TextComparatorType.ALPHA.equals(type)) {
			return t1.getText().compareTo(t2.getText());
		} else if (TextComparatorType.X_COORD.equals(type)) {
			Double x1 = t1.getX();
			Double x2 = t2.getX();
			return (x1 == null || x2 == null) ? -1 : (int) (x1 - x2);
		} else if (TextComparatorType.Y_COORD.equals(type)) {
			Double y1 = t1.getY();
			Double y2 = t2.getY();
			return (y1 == null || y2 == null) ? -1 : (int) (y1 - y2);
		} else {
			return 0;
		}
	}

}
