package org.contentmine.graphics.svg.text;

import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Util;

/** holds a (double) coordinate for x,y, fontSize, heights, widths, etc.
 * the coordinate is internally scaled by rounding to a given number of places
 * this rounded value is used for binning, maps, sets, etc
 * @author pm286
 *
 * moved from SVG2XML
 */
public class TextCoordinate {

	/** significant decimal places in font-size (appears to be set by PDF2SVG) */
	public final static int DEFAULT_DECIMAL = 2;
	/** comparing factor */
	public final static double DEFAULT_EPS = calculateEpsilon(DEFAULT_DECIMAL);

	private Double doubleCoordinate;
	private Integer decimalPlaces;
	private Double eps = DEFAULT_EPS;

	public TextCoordinate(Double size, int decimalPlaces) {
		this.doubleCoordinate = Util.format(size, decimalPlaces);
		this.decimalPlaces = decimalPlaces;
		this.eps =  calculateEpsilon(decimalPlaces);
	}
	
	public TextCoordinate(Double doubleCoordinate) {
		this.doubleCoordinate = Util.format(doubleCoordinate, DEFAULT_DECIMAL);
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof TextCoordinate) {
			TextCoordinate fs = (TextCoordinate)obj;
			equals = Real.isEqual(this.doubleCoordinate, fs.doubleCoordinate, DEFAULT_EPS);
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		return doubleCoordinate.hashCode();
	}

	/** gets the rounded value of the coordinate
	 * 
	 * @return
	 */
	public Double getDouble() {
		return doubleCoordinate;
	}
	
	public void setDouble(Double value) {
		this.doubleCoordinate = value;
	}
	
	private static double calculateEpsilon(int decimalPlaces) {
		return 1/(Math.pow(10.0, (double) decimalPlaces));
	} 
	
	@Override
	public String toString() {
		return String.valueOf(doubleCoordinate);
	}
}
