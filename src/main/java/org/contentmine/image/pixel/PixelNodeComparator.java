package org.contentmine.image.pixel;

import java.util.Comparator;

import org.contentmine.eucl.euclid.Int2;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;

public class PixelNodeComparator implements Comparator<PixelNode>{

	private ComparatorType major;
	private ComparatorType minor;
	private Int2 xy20;
	private Int2 xy21;
	private PixelNode node1;
	private PixelNode node0;
	private double deltaMajor = 0.0;
	
	public PixelNodeComparator(ComparatorType major) {
		this(major, (ComparatorType) null);
	}
	
	/**
	 * 
	 * @param major
	 * @param minor
	 */
	public PixelNodeComparator(ComparatorType major, ComparatorType minor) {
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
	public PixelNodeComparator(ComparatorType major, ComparatorType minor, double deltaMajor) {
		this.deltaMajor = Math.abs(deltaMajor);
		this.major = major;
		this.minor = minor;
	}
	
	public int compare(PixelNode node0, PixelNode node1) {
		this.node0 = node0;
		this.node1 = node1;
		if (node0 == null || node1 == null || major == null) {
			return 0;
		}
		return xy2Compare();
	}

	private int xy2Compare() {
		xy20 = node0.getCentrePixel().getInt2();
		xy21 = node1.getCentrePixel().getInt2();
		int result = xy2Compare(major);
		if (result == 0) {	
			result = xy2Compare(minor);
		}
		return result;
	}

	private int xy2Compare(ComparatorType type) {
		if (type == null) return 0;
		if (type.equals(ComparatorType.LEFT)) {
			return compare(xy20.getX(), xy21.getX());
		} else if (type.equals(ComparatorType.RIGHT)) {
			return compare(xy21.getX(), xy20.getX());
		} else if (type.equals(ComparatorType.TOP)) {
			return compare(xy20.getY(), xy21.getY());
		} else if (type.equals(ComparatorType.BOTTOM)) {
			return compare(xy21.getY(), xy20.getY());
		}
		return 0;
		
	}

	private int compare(double a, double b) {
		double delta = a - b;
		return (Math.abs(delta) < deltaMajor)  ? 0 : (int) Math.signum(delta);
	}



}
