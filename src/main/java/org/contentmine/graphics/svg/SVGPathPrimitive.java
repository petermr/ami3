package org.contentmine.graphics.svg;

import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.contentmine.graphics.svg.path.SVGPathParser;

/**
 * parts of path (M, L, C, Z) currently not LHSQTA
 * @author pm286
 *
 */
public abstract class SVGPathPrimitive {
	private static Logger LOG = LogManager.getLogger(SVGPathPrimitive.class);

	public static final char ARC              = 'A';
	public static final char CLOSE        = 'Z';
	public static final char CUBIC        = 'C';
	public static final char CUBIC_SMOOTH = 'S';
	public static final char HORIZ        = 'H';
	public static final char LINE         = 'L';
	public static final char MOVE         = 'M';
	public static final char QUAD         = 'Q';
	public static final char QUAD_SMOOTH  = 'T';
	public static final char VERT         = 'V';

	public static final String MOVE_S     = "M";

	protected Real2Array coordArray;
	protected Real2 zerothCoord; // from preceding primitive
	static NumberFormat FORMAT3 = new DecimalFormat("#0.000");


	public SVGPathPrimitive() {
		
	}
	
	public abstract String getTag();
	
		
	public static String formatDString(String d, int places) {
		PathPrimitiveList pathPrimitiveList = null;
		try {
			pathPrimitiveList = new SVGPathParser().parseDString(d);
		} catch (RuntimeException e) {
			LOG.warn("Cannot parse: "+d);
			throw e;
		}
		for (SVGPathPrimitive pathPrimitive : pathPrimitiveList) {
			if (pathPrimitive != null) {
				pathPrimitive.format(places);
			}
		}
		d = pathPrimitiveList.createD();
		return d;
	}
	
	public static String formatD(String d, int places) {
		PathPrimitiveList primitiveList = new SVGPathParser().parseDString(d);
		for (SVGPathPrimitive primitive : primitiveList) {
			primitive.format(places);
		}
		d = primitiveList.createD();
		return d;
	}

	/** transforms THIS.
	 * 
	 * @param t2
	 */
	public void transformBy(Transform2 t2) {
		
		if (coordArray != null) {
			coordArray.transformBy(t2);
		}
	}
	
	public Real2Array getCoordArray() {
		return coordArray;
	}

	/** replace coordinate array.
	 * 
	 * Use with care. Currently no checks on size.
	 * 
	 * @param coordArray
	 */
	public void setCoordArray(Real2Array coordArray) {
		this.coordArray = coordArray;
	}

	public String toString() {
		throw new RuntimeException("Must override toString() in SVGPathPrimitive");
	}
	
	protected String formatCoords(Real2 coords) {
		if (coords == null) {return null;}
		StringBuilder sb = new StringBuilder();
		formatCoordsIntoStringBuilder(coords, sb);
		return sb.toString();
	}

	public void formatCoordsIntoStringBuilder(Real2 coords, StringBuilder sb) {
		sb.append( format3(coords.getX()));
		sb.append(" ");
		sb.append( format3(coords.getY()));
		sb.append(" ");
	}

	protected String format3(double xy) {
//		return String.valueOf(((int) (xy * 1000.))/1000);
		return FORMAT3.format(xy);
	}
	
	public void format(int places) {
		// skip for Z etc
		if (coordArray != null) {
			coordArray.format(places);
		}
	}

	public Real2 getZerothCoord() {
		return zerothCoord;
	}
	
	protected void setZerothCoord(Real2 coord) {
		this.zerothCoord = coord;
	}

	/** first coordinate in explicit coordinate array
	 * thus "C110.88 263.1 110.64 262.8 110.7 262.44 " gives "110.88 263.1"
	 * the zeroth coordinate will have been set by the preceding primitive
	 * 
	 * @return
	 */
	public Real2 getFirstCoord() {
		Real2Array coordArray = getCoordArray();
		return (coordArray  == null || coordArray.size() == 0) ? null : coordArray.get(0);
	}
	
	public Real2 getLastCoord() {
		Real2Array coordArray = getCoordArray();
		return (coordArray) == null ? null : coordArray.getLastElement();
	}

	public abstract void operateOn(GeneralPath path2);

	/** the angle of change of direction (only for curves)
	 * firstPoint must have been set with setFirstPoint()
	 * @return change as Angle
	 */
	public abstract Angle getAngle();
	
	/** returns translation from first point to lastPoint
	 * firstPoint must have been set with setFirstPoint()
	 * @return translation
	 *
	 */
	public Real2 getTranslation() {
		Real2 trans = null;
		if (zerothCoord != null && this.getLastCoord() != null) {
			trans = this.getLastCoord().subtract(zerothCoord);
		}
		return trans;
	}

	public void setFirstPoint(Real2 lastPoint) {
//		System.out.println("avoid calling this?");
		this.zerothCoord = lastPoint;
	}
	
	public Real2Range getBoundingBox() {
		return coordArray == null || coordArray.size() == 0 ? null : coordArray.getRange2();
	}

//	public static void setFirstPoints(PathPrimitiveList primitiveList) {
//		throw new RuntimeException("NYI");
//	}
	
	/** from SO .
	https://stackoverflow.com/questions/8553672/a-faster-alternative-to-decimalformat-format
	 * @param builder
	 * @param d
	 */
	public static void appendTo6PeterLawrey(StringBuilder builder, double d) {
	    if (d < 0) {
	        builder.append('-');
	        d = -d;
	    }
	    if (d * 1e6 + 0.5 > Long.MAX_VALUE) {
	        // TODO write a fall back.
	        throw new IllegalArgumentException("number too large");
	    }
	    long scaled = (long) (d * 1e6 + 0.5);
	    long factor = 1000000;
	    int scale = 7;
	    long scaled2 = scaled / 10;
	    while (factor <= scaled2) {
	        factor *= 10;
	        scale++;
	    }
	    while (scale > 0) {
	        if (scale == 6)
	            builder.append('.');
	        long c = scaled / factor % 10;
	        factor /= 10;
	        builder.append((char) ('0' + c));
	        scale--;
	    }
	}

}
