package org.contentmine.image.pixel;

import java.util.Comparator;

import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;

public class PixelEdgeComparator implements Comparator<PixelEdge>{

	private ComparatorType major;
	private ComparatorType minor;
	private Int2Range r2r0;
	private Int2Range r2r1;
	private PixelEdge edge1;
	private PixelEdge edge0;
	private double deltaMajor = 0.0;
	
	public PixelEdgeComparator(ComparatorType major) {
		this(major, (ComparatorType) null);
	}
	
	/**
	 * 
	 * @param major
	 * @param minor
	 */
	public PixelEdgeComparator(ComparatorType major, ComparatorType minor) {
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
	public PixelEdgeComparator(ComparatorType major, ComparatorType minor, double deltaMajor) {
		this.deltaMajor = Math.abs(deltaMajor);
		this.major = major;
		this.minor = minor;
	}
	
	public int compare(PixelEdge edge0, PixelEdge edge1) {
		this.edge0 = edge0;
		this.edge1 = edge1;
		if (edge0 == null || edge1 == null || major == null) {
			return 0;
		}
		if (major.equals(ComparatorType.SIZE)) {
			return edge0.size() - edge1.size();
		} else {
			return boxCompare();
		}
	}

	private int boxCompare() {
		r2r0 = edge0.getInt2BoundingBox();
		r2r1 = edge1.getInt2BoundingBox();
		int result = boxCompare(major);
		if (result == 0) {	
			result = boxCompare(minor);
		}
		return result;
	}

	private int boxCompare(ComparatorType type) {
		if (type == null) return 0;
		if (r2r0 == null ||r2r1 == null) {
			return 0;
		}
		if (type.equals(ComparatorType.LEFT)) {
			return compare(r2r0.getXRange().getMin(), r2r1.getXRange().getMin());
		}
		if (type.equals(ComparatorType.RIGHT)) {
			return compare(r2r0.getXRange().getMax(), r2r1.getXRange().getMax());
		}
		if (type.equals(ComparatorType.TOP)) {
			return compare(r2r0.getYRange().getMin(), r2r1.getYRange().getMin());
		}
		if (type.equals(ComparatorType.BOTTOM)) {
			return compare(r2r0.getYRange().getMax(), r2r1.getYRange().getMax());
		}
		return 0;
		
	}

	private int compare(double a, double b) {
		double delta = a - b;
		return (Math.abs(delta) < deltaMajor)  ? 0 : (int) Math.signum(delta);
	}



}
