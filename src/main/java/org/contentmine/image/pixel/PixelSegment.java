package org.contentmine.image.pixel;

import java.awt.Graphics2D;
import java.awt.geom.Line2D.Double;

import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;

/** holds a linear segment of a path extracted from a PixelIsland.
 * 
 * Generally created by a segmentation algorithm such as Douglas Peucker.
 * Primarily holds an SVGLine
 * 
 * @author pm286
 *
 */
public class PixelSegment {

	private SVGLine line;
	
	public PixelSegment() {
		
	}

	public PixelSegment(Real2 point0, Real2 point1) {
		if (point0 == null || point1 == null) {
			throw new RuntimeException("Null points in segment: "+point0+";"+point1);
		}
		line = new SVGLine(point0, point1);
	}

	/**
	 * @param x12
	 * @param serial
	 * @see org.contentmine.graphics.svg.SVGLine#setXY(org.contentmine.eucl.euclid.Real2, int)
	 */
	public void setXY(Real2 x12, int serial) {
		line.setXY(x12, serial);
	}

	/**
	 * @return
	 * @see org.contentmine.graphics.svg.SVGLine#getLine2()
	 */
	public Double getLine2() {
		return line.getLine2();
	}

	/**
	 * @param g2d
	 * @see org.contentmine.graphics.svg.SVGElement#draw(java.awt.Graphics2D)
	 */
	public void draw(Graphics2D g2d) {
		line.draw(g2d);
	}

	/**
	 * @param fill
	 * @see org.xmlccontentmineml.graphics.svg.SVGElement#setFill(java.lang.String)
	 */
	public void setFill(String fill) {
		line.setFill(fill);
	}

	/**
	 * @param stroke
	 * @see org.xmlcontentminecml.graphics.svg.SVGElement#setStroke(java.lang.String)
	 */
	public void setStroke(String stroke) {
		line.setStroke(stroke);
	}

	/**
	 * @param width
	 * @see org.contentmine.graphics.svg.SVGLine#setWidth(double)
	 */
	public void setWidth(double width) {
		line.setWidth(width);
	}

	/**
	 * @param stroke
	 * @param fill
	 * @param strokeWidth
	 * @param opacity
	 * @return
	 * @see org.contentmine.graphics.svg.SVGElement#drawBox(java.lang.String, java.lang.String, double, double)
	 */
	public SVGRect drawBox(String stroke, String fill, double strokeWidth,
			double opacity) {
		return line.drawBox(stroke, fill, strokeWidth, opacity);
	}

	/**
	 * @return
	 * @see nu.xom.Element#toXML()
	 */
	public final String toXML() {
		return line.toXML();
	}

	@Override
	public String toString() {
		return "PixelSegment [line=" + line + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PixelSegment other = (PixelSegment) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}

	public SVGLine getSVGLine() {
		return line;
	}

	public SVGElement getSVGLine(String color) {
		line.setStroke(color);
		return line;
	}
		
	public void setLine(SVGLine line) {
		this.line = line;
	}

	public Real2 getPoint(int i) {
		return (line == null || i < 0 || i > 1) ? null : line.getXY(i);
	}

	public Line2 getEuclidLine() {
		return line == null ? null : line.getEuclidLine();
	}

//	public void swapPoints() {
//		if (line != null) {
//			line.getEuclidLine().swapPoints();
//		}
//	}

	
}
