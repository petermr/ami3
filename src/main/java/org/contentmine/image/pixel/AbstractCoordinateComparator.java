package org.contentmine.image.pixel;

import org.contentmine.eucl.euclid.Int2;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;

public abstract class AbstractCoordinateComparator {

	protected ComparatorType major;
	protected ComparatorType minor;
	protected Int2 xy0;
	protected Int2 xy1;
	protected double deltaMajor = 0.0;
	protected int coordCompare(ComparatorType type) {
		if (type == null) return 0;
		if (type.equals(ComparatorType.LEFT) ||
			type.equals(ComparatorType.RIGHT)) {
			return compare(xy0.getX(), xy1.getX());
		}
		if (type.equals(ComparatorType.TOP) ||
		    type.equals(ComparatorType.BOTTOM)) {
			return compare(xy0.getY(), xy1.getY());
		}
		return 0;
		
	}
	private int compare(double a, double b) {
		double delta = a - b;
		return (Math.abs(delta) < deltaMajor)  ? 0 : (int) Math.signum(delta);
	}

}
