package org.contentmine.svg2xml.page;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.svg2xml.page.PageCropper.Units;

/** box defining a page area (e.g. mediaBox or cropBox)
 * 
 * @author pm286
 *
 */
public class BoxProcessor {
	private static final Logger LOG = Logger.getLogger(BoxProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String X0 = "x0";
	private static final String Y0 = "y0";
	private static final String X1 = "x1";
	private static final String Y1 = "y1";
	private static final String HEIGHT = "height";
	private static final String WIDTH = "width";
	private static final String UNITS = "units";
	private static final String XRIGHT = "xright";
	private static final String XLEFT = "xleft";
	private static final String YDOWN = "ydown";
	private static final String YUP = "yup";
	
	private Double x0;
	private Double x1;
	private Double y0;
	private Double y1;
	private Double x0out;
	private Double x1out;
	private Double y0out;
	private Double y1out;
	private Double height;
	private Double width;
	private Units units = Units.PX;
	private Units unitsOut = Units.PX;
	private boolean xright = true;
	private boolean ydown = true;
	private static Double YMAX = 800.; // horrible kludge until we get coords sorted

	public BoxProcessor() {
		return;
	}
	public void parseArguments(List<String> args) {
		int i = 0;
		for (; i < args.size(); ) {
			String arg = args.get(i);
			if (X0.equals(arg)) {
				x0 = Double.parseDouble(args.get(++i)); i++;
			} else if (Y0.equals(arg)) {
				y0 = Double.parseDouble(args.get(++i)); i++;
			} else if (X1.equals(arg)) {
				x1 = Double.parseDouble(args.get(++i)); i++;
			} else if (Y1.equals(arg)) {
				y1 = Double.parseDouble(args.get(++i)); i++;
			} else if (HEIGHT.equals(arg)) {
				height = Double.parseDouble(args.get(++i)); i++;
			} else if (WIDTH.equals(arg)) {
				width = Double.parseDouble(args.get(++i)); i++;
			} else if (UNITS.equals(arg)) {
				String argx = args.get(++i);
				units = Units.getUnitsFromAbbrev(argx); i++;
				if (units == null) {
					throw new RuntimeException("Cannot find units: "+argx);
				}
			} else if (YDOWN.equals(arg)) {
				ydown = true; i++;
			} else if (YUP.equals(arg)) {
				ydown = false; i++;
			} else {
				throw new RuntimeException("Unexpected arg: "+arg+" in pageBox (crop/media)");
			}
		}
		createBox();
	}

	/** uses x0 and x1 and y0 y1.
	 * if x1 and/or y1 = null then these are calculated from width and height
	 * if both are present x1/y1 takes precedence.
	 */
	private void createBox() {
		if (x0 == null || y0 == null) {
			throw new RuntimeException("must give x0 and y0");
		}
		if (x1 != null) {
			width = (xright) ? x1 - x0 : x0 - x1; // X takes precedence over width
		} else {
			if (width == null) {
				throw new RuntimeException("must give x1 or width");
			}
			x1 = (xright) ? x0 + width : x0 - width;
		}
		if (y1 != null) {
			height = (ydown) ? y0 - y1 : y1 - y0; // Y takes precedence over height
		} else {
			if (height == null) {
				throw new RuntimeException("must give y1 or height");
			}
			y1 = (ydown) ? y0 + height : y0 - height;
		}
		applyUnits();
	}
	
	private void applyUnits() {
//		if (Units.PX.equals(units)) {
//			// do nothing
//		} else {
			double user2Px = units.getUser2Px();
			x0out = x0 * user2Px;
			x1out = x1 * user2Px;
			y0out = y0 * user2Px;
			if (ydown) {
				y0out = YMAX - y0out;
			}
			y1out = y1 * user2Px;
			if (ydown) {
				y1out = YMAX - y1out;
			}
			unitsOut = Units.PX;
			LOG.trace(this.toString());
//		}
	}

	public String toString() {
		/*
	private Double x0;
	private Double x1;
	private Double y0;
	private Double y1;
	private Double height;
	private Double width;
	private Units units = Units.PX;
	private boolean xright = true;
	private boolean ydown = true;

		 */
		StringBuilder sb = new StringBuilder();
		sb.append(" x0 = "+x0+";");
		sb.append(" x1 = "+x1+";");
		sb.append(" y0 = "+y0+";");
		sb.append(" y1 = "+y1+";");
		sb.append(" height = "+height+";");
		sb.append(" width = "+width+";");
		sb.append(" units = "+units+";");
		sb.append(" xright = "+xright+";");
		sb.append(" ydown = "+ydown+";");
		sb.append("final:");
		sb.append(" x0out = "+x0out+";");
		sb.append(" x1out = "+x1out+";");
		sb.append(" y0out = "+y0out+";");
		sb.append(" y1out = "+y1out+";");
		sb.append(" unitsOut = "+unitsOut+";");

		return sb.toString();
	}
	
	public boolean isValid() {
		return x0 != null && x1 != null && y0 != null && y1 != null;
	}

	/** this may change with different directions, etc.
	 * 
	 * @return
	 */
	public Real2 getTopLeft() {
		Real2 tl = new Real2(x0out, y0out);
		LOG.trace("TL "+tl);
		return tl;
	}

	/** this may change with different directions, etc.
	 * 
	 * @return
	 */
	public Real2 getBottomRight() {
		Real2 br = new Real2(x1out, y1out);
		LOG.trace("BR "+br);
		return br;
	}

}
