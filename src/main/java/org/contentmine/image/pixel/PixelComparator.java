package org.contentmine.image.pixel;

import java.util.Comparator;

public class PixelComparator extends AbstractCoordinateComparator implements Comparator<Pixel>{

	public enum ComparatorType {
		ANY,      // undefined
		HORIZONTAL,
		VERTICAL,
		BOTTOM,
		LEFT,
		RIGHT,
		SIZE,
		TOP,
	}

	private Pixel pixel0;
	private Pixel pixel1;
	
	public PixelComparator(ComparatorType major) {
		this(major, (ComparatorType) null);
	}
	
	/**
	 * 
	 * @param major
	 * @param minor
	 */
	public PixelComparator(ComparatorType major, ComparatorType minor) {
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
	public PixelComparator(ComparatorType major, ComparatorType minor, double deltaMajor) {
		this.deltaMajor = Math.abs(deltaMajor);
		this.major = major;
		this.minor = minor;
	}
	
	public int compare(Pixel pixel0, Pixel pixel1) {
		this.pixel0 = pixel0;
		this.pixel1 = pixel1;
		if (pixel0 == null || pixel1 == null || major == null) {
			return 0;
		}
		return coordCompare();
	}

	private int coordCompare() {
		xy0 = pixel0.getInt2();
		xy1 = pixel1.getInt2();
		int result = coordCompare(major);
		if (result == 0) {	
			result = coordCompare(minor);
		}
		return result;
	}

}
