package org.contentmine.image.pixel;

import java.util.Comparator;

import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;

public class PixelIslandComparator implements Comparator<PixelIsland>{

	private ComparatorType major;
	private ComparatorType minor;
	private Real2Range r2r0;
	private Real2Range r2r1;
	private PixelIsland island1;
	private PixelIsland island0;
	private double deltaMajor = 0.0;
	
	public PixelIslandComparator(ComparatorType major) {
		this(major, (ComparatorType) null);
	}
	
	/**
	 * 
	 * @param major
	 * @param minor
	 */
	public PixelIslandComparator(ComparatorType major, ComparatorType minor) {
		this.major = major;
		this.minor = minor;
	}
	
	/** compares on major allowing for error, then minor.
	 * 
	 * equality is defined as within +- deltaMajor
	 * @param major
	 * @param minor
	 * @param deltaMajor
	 */
	public PixelIslandComparator(ComparatorType major, ComparatorType minor, double deltaMajor) {
		this.deltaMajor = Math.abs(deltaMajor);
		this.major = major;
		this.minor = minor;
	}
	
	public int compare(PixelIsland island0, PixelIsland island1) {
		this.island0 = island0;
		this.island1 = island1;
		if (island0 == null || island1 == null || major == null) {
			return 0;
		}
		if (major.equals(ComparatorType.SIZE)) {
			return island0.size() - island1.size();
		} else {
			return boxCompare();
		}
	}

	private int boxCompare() {
		r2r0 = island0.getBoundingBox();
		r2r1 = island1.getBoundingBox();
		int result = boxCompare(major);
		if (result == 0) {	
			result = boxCompare(minor);
		}
		return result;
	}

	private int boxCompare(ComparatorType type) {
		if (type == null) return 0;
		if (type.equals(ComparatorType.LEFT)) {
			return compare(r2r0.getXMin(), r2r1.getXMin());
		}
		if (type.equals(ComparatorType.RIGHT)) {
			return compare(r2r0.getXMax(), r2r1.getXMax());
		}
		if (type.equals(ComparatorType.TOP)) {
			return compare(r2r0.getYMin(), r2r1.getYMin());
		}
//		if (type.equals(ComparatorType.TOP_TEXT)) {
//			return compare(r2r0.getYMax(), r2r1.getYMax());
//		}
		if (type.equals(ComparatorType.BOTTOM)) {
			return compare(r2r0.getYMax(), r2r1.getYMax());
		}
		return 0;
		
	}

	private int compare(double a, double b) {
		double delta = a - b;
		return (Math.abs(delta) < deltaMajor)  ? 0 : (int) Math.signum(delta);
	}



}
