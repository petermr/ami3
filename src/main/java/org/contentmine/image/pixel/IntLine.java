package org.contentmine.image.pixel;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.image.pixel.IntLine.ChangeDirection;

public class IntLine {

	private final static Logger LOG = Logger.getLogger(IntLine.class);
	
	public enum ChangeDirection {
		LEFT,
		AHEAD,
		RIGHT,
		BACK;
	}

	
//	// Y goes down the page...
	public final static Int2 EAST_DELTA = new Int2(1, 0);
	public final static Int2 SOUTH_DELTA = new Int2(0, 1);
	public final static Int2 WEST_DELTA = new Int2(-1, 0);
	public final static Int2 NORTH_DELTA = new Int2(0, -1);
	
	private Int2 xy0;
	private Int2 xy1;
	private Int2 pixelOrigin;
	private Pixel currentPixel;

	public IntLine(Int2 xy0, Int2 xy1, Int2 pixelOrigin, Pixel currentPixel) {
		this.xy0 = new Int2(xy0);
		this.xy1 = new Int2(xy1);
		this.pixelOrigin = pixelOrigin;
		this.currentPixel = currentPixel;
	}

	public Int2 getXY(int xy) {
		if (xy == 0) return xy0;
		if (xy == 1) return xy1;
		return null;
	}
	public SVGElement createSVG() {
		SVGLine line = new SVGLine(new Real2(xy0), new Real2(xy1));
		line.setStrokeWidth(0.1);
		return line;
	}

	public LineDirection getDirection() {
		return new LineDirection(xy1.subtract(xy0));
	}
	
	public LineDirection getNextDirection(ChangeDirection change) {
		return getDirection().getNewDirection(change);
	}
	
	public IntLine getNextLine(LineDirection oldLineDirection, LineDirection newLineDirection, ChangeDirection change, PixelList pixelList) {
		Int2 delta = newPixelOriginDelta(oldLineDirection.dir, change);
		Int2 nextOrigin = pixelOrigin.plus(delta);
		Pixel pixel = pixelList.getPixelByCoordinate(nextOrigin);
		if (pixel != null) {
			currentPixel = pixel;
			IntLine newLine = new IntLine(xy1, xy1.plus(newLineDirection.dir),nextOrigin, currentPixel);
			return newLine;
		}
		return null;
	}
	
	Pixel getCurrentPixel() {
		return currentPixel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pixelOrigin == null) ? 0 : pixelOrigin.hashCode());
		result = prime * result + ((xy0 == null) ? 0 : xy0.hashCode());
		result = prime * result + ((xy1 == null) ? 0 : xy1.hashCode());
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
		IntLine other = (IntLine) obj;
		if (pixelOrigin == null) {
			if (other.pixelOrigin != null)
				return false;
		} else if (!pixelOrigin.equals(other.pixelOrigin))
			return false;
		if (xy0 == null) {
			if (other.xy0 != null)
				return false;
		} else if (!xy0.equals(other.xy0))
			return false;
		if (xy1 == null) {
			if (other.xy1 != null)
				return false;
		} else if (!xy1.equals(other.xy1))
			return false;
		return true;
	}

	/** posaition of new pixel origin.
	 * 
	 * This is a CLOCKWISE crawl round the edge. Always go left first, else ahead, else right
	 * @param dir
	 * @param change
	 * @return offest of new pixel origin
	 */
	private Int2 newPixelOriginDelta(Int2 dir, ChangeDirection change) {
		if (ChangeDirection.LEFT.equals(change)) {
			if (EAST_DELTA.equals(dir))  return new Int2(1, -1);
			if (SOUTH_DELTA.equals(dir)) return new Int2(1, 1);
			if (WEST_DELTA.equals(dir))  return new Int2(-1, 1);
			if (NORTH_DELTA.equals(dir)) return new Int2(-1, -1);
		}
		if (ChangeDirection.AHEAD.equals(change)) {
			if (EAST_DELTA.equals(dir))  return new Int2(1, 0);
			if (SOUTH_DELTA.equals(dir)) return new Int2(0, 1);
			if (WEST_DELTA.equals(dir))  return new Int2(-1, 0);
			if (NORTH_DELTA.equals(dir)) return new Int2(0, -1);
		}
		if (ChangeDirection.RIGHT.equals(change)) {
			return new Int2(0, 0);
		}
		return new Int2(0, 0);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{"+xy0+","+xy1+"} "+pixelOrigin);
		return sb.toString();
	}

	public Real2 getMidPoint() {
		return new Real2(xy0).getMidPoint(new Real2(xy1));
	}
}

class LineDirection {
	
	Int2 dir;
	
	public LineDirection(Int2 dir) {
		this.dir = dir;
	}

	public LineDirection getNewDirection(ChangeDirection change) {
		Int2 dirNew = new Int2(dir);
		if (ChangeDirection.LEFT.equals(change)) {
			dirNew.setX( dir.getY());
			dirNew.setY(-dir.getX());
		} else if (ChangeDirection.RIGHT.equals(change)) {
			dirNew.setX(-dir.getY());
			dirNew.setY( dir.getX());
		} else if (ChangeDirection.BACK.equals(change)) {
			dirNew.setX(-dir.getX());
			dirNew.setY(-dir.getY());
		} else {
			// no change for AHEAD
		}
		return new LineDirection(dirNew);
	}
	
	@Override
	public String toString() {
		return "dir:{"+dir.toString()+"}";
	}
}



