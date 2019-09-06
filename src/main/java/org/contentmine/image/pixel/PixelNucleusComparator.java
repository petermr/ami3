package org.contentmine.image.pixel;

import java.util.Comparator;

import org.contentmine.image.pixel.PixelComparator.ComparatorType;

public class PixelNucleusComparator extends AbstractCoordinateComparator implements Comparator<PixelNucleus> {

	private PixelNucleus nucleus0;
	private PixelNucleus nucleus1;
	public PixelNucleusComparator(ComparatorType major) {
		this(major, (ComparatorType) null);
	}
	
	/**
	 * 
	 * @param major
	 * @param minor
	 */
	public PixelNucleusComparator(ComparatorType major, ComparatorType minor) {
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
	public PixelNucleusComparator(ComparatorType major, ComparatorType minor, double deltaMajor) {
		this.deltaMajor = Math.abs(deltaMajor);
		this.major = major;
		this.minor = minor;
	}
	
	public int compare(PixelNucleus nucleus0, PixelNucleus nucleus1) {
		this.nucleus0 = nucleus0;
		this.nucleus1 = nucleus1;
		if (nucleus0 == null || nucleus1 == null || major == null) {
			return 0;
		}
		return coordCompare();
	}

	private int coordCompare() {
		xy0 = nucleus0.getCentrePixel().getInt2();
		xy1 = nucleus1.getCentrePixel().getInt2();
		int result = coordCompare(major);
		if (result == 0) {	
			result = coordCompare(minor);
		}
		return result;
	}

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
