package org.contentmine.image.pixel;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGRect;

public class Pixel {
	private final static Logger LOG = Logger.getLogger(Pixel.class);
	
	enum Marked {
		ALL,
		UNUSED,
		USED,
	};
	
	Point point;
	private PixelList neighbourList;
	PixelIsland island;
	int value = 0;

	public Pixel(Point p) {
		this.point = p;
	}

	public Pixel(int x, int y) {
		this(new Point(x, y));
	}

	public Pixel(Int2 xy) {
		this(new Point(xy.getX(), xy.getY()));
	}

	/** creates deep copy of Pixel without island.
	 * 
	 * neighbourList is null;
	 * 
	 * @param pixel
	 */
	public Pixel(Pixel pixel) {
		this(new Point(pixel.point));
	}

	public Int2 getInt2() {
		return point == null ? null : new Int2(point.x, point.y);
	}
	
	public PixelIsland getIsland() {
		return island;
	}

	/** tis can create neighbours even if not in PixelIsland.
	 * maybe bug
	 * @param island
	 * @return
	 */
	public PixelList getOrCreateNeighbours(PixelIsland island) {
		if (island != null) {
			island.ensurePopulatedMapAndRanges();
			this.island = island;
		}
		getOrCreateNeighbourList(island);
		return neighbourList;
	}

	/** find neighbours actually in set 
	 * messy
	 * creates a neighbour list and then eliminates pixels not in set.
	 * 
	 * @param island
	 * @param pixelSet
	 * @return
	 */
	public PixelList getOrCreateNeighboursIn(PixelIsland island, PixelSet pixelSet) {
		PixelList pixelList = getOrCreateNeighbours(island);
		for (int i = pixelList.size() - 1; i >= 0; i--) {
			Pixel pixel = pixelList.get(i);
			if (!pixelSet.contains(pixel)) {
				boolean ok = pixelList.remove(pixel);
			}
		}
		return pixelList;
	}

	private void getOrCreateNeighbourList(PixelIsland island) {
		if (neighbourList == null) {
			createNeighbourList(island);
		}
	}

	public PixelList createNeighbourList(PixelIsland island) {
		neighbourList = new PixelList();
		List<Int2> coordList = calculateNeighbourCoordList(island);
		for (Int2 coord : coordList) {
			Pixel pixel = island.getPixelByCoordMap().get(coord);
			if (pixel != null) {
				neighbourList.add(pixel);
			}
		}
		return neighbourList;
	}
	
	/** creates neighbourList for neighbours of neighbours.
	 * 
	 * @param island
	 */
	public PixelList createNeighbourNeighbourList(PixelIsland island) {
		PixelList neighbourNeighbourList = new PixelList();
		PixelList neighbourList = createNeighbourList(island);
		for (Pixel neighbour : neighbourList) {
			PixelList neighbourList1 = neighbour.createNeighbourList(island);
			if (LOG.isTraceEnabled()) {
				LOG.trace("neighbours1 "+neighbourList1);
			}
			for (Pixel nl1 : neighbourList1) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("   nl1 "+nl1);
				}
				if (!neighbourNeighbourList.contains(nl1)) {
					neighbourNeighbourList.add(nl1);
				}
				if (LOG.isTraceEnabled()) {
					LOG.trace("neighboursNeighbours "+neighbourNeighbourList);
				}
			}
		}
		return neighbourNeighbourList;
	}
	
	public List<Int2> calculateNeighbourCoordList(boolean diagonal) {
		List<Int2> coordList = new ArrayList<Int2>();
		coordList.add(new Int2(point.x + 1, point.y));
		coordList.add(new Int2(point.x - 1, point.y));
		if (diagonal) {
			coordList.add(new Int2(point.x + 1, point.y + 1));
			coordList.add(new Int2(point.x - 1, point.y + 1));
		}
		coordList.add(new Int2(point.x, point.y + 1));
		coordList.add(new Int2(point.x, point.y - 1));
		if (diagonal) {
			coordList.add(new Int2(point.x + 1, point.y - 1));
			coordList.add(new Int2(point.x - 1, point.y - 1));
		}
		return coordList;
	}

	/** calculate neighbours
	 * 
	 * @param island if null use diagonal, else use value in island
	 * @return
	 */
	public List<Int2> calculateNeighbourCoordList(PixelIsland island) {
		boolean diagonal = island == null ? true : island.getDiagonal();
		return calculateNeighbourCoordList(diagonal);
	}

	public void setIsland(PixelIsland island) {
		this.island = island;
	}

	/**
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(""+((point == null) ? "null" : this.getInt2()));
		return sb.toString();
	}

	public String neighboursString() {
		StringBuilder sb = new StringBuilder();
		sb.append("; neighbours: ");
		if (neighbourList == null) {
			sb.append("null");
		} else {
			for (Pixel neighbour : neighbourList) {
				sb.append(" "+neighbour.getInt2());
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pixel other = (Pixel) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

	public void remove(PixelIsland island) {
		island.remove(this);
	}

	public void setInt2(Int2 int2) {
		point = new Point(int2.getX(), int2.getY());
	}

	public void clearNeighbours() {
		this.neighbourList = null;
	}

	public SVGRect getSVGRect() {
		return getSVGRect(1, "red");
	}

	public SVGRect getSVGRect(int size, String color) {
		SVGRect rect = new SVGRect(
				new Real2(this.point.getX(), this.point.getY()),
				new Real2(this.point.getX() + size, this.point.getY() + size));
		rect.setFill(color);
		rect.setStroke(null);
		return rect;
	}

	public SVGRect getSVGRect(double width, double height, String color) {
		SVGRect rect = new SVGRect(
				new Real2(this.point.getX(), this.point.getY()),
				new Real2(this.point.getX() + width, this.point.getY() + height));
		rect.setFill(color);
		rect.setStroke(null);
		return rect;
	}

	public static Real2Array createReal2Array(PixelList pixelList) {
		Real2Array array = null;
		if (pixelList != null) {
			array = new Real2Array();
			for (Pixel pixel : pixelList) {
				array.add(new Real2(pixel.getInt2()));
			}
		}
		return array;
	}

	public void setValue(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}

	public boolean isNeighbour(Pixel pixel) {
		return isOrthogonalNeighbour(pixel) || isDiagonalNeighbour(pixel);
	}

	public boolean isOrthogonalNeighbour(Pixel pixel) {
		if (pixel == null || pixel.getInt2() == null || this.getInt2() == null) {
			return false;
		}
		Int2 vector = this.getInt2().subtract(pixel.getInt2());
		return Math.abs(vector.getX()) + Math.abs(vector.getY()) == 1;
	}

	public boolean isDiagonalNeighbour(Pixel pixel) {
		if (pixel == null || pixel.getInt2() == null || this.getInt2() == null) {
			return false;
		}
		Int2 vector = this.getInt2().subtract(pixel.getInt2());
		return Math.abs(vector.getX()) == 1 && Math.abs(vector.getY()) == 1;
	}

	public boolean isKnightsMove(Pixel pixel) {
		Int2 vector = this.getInt2().subtract(pixel.getInt2());
		return Math.abs(vector.getX()) + Math.abs(vector.getY()) == 3 &&
				Math.abs(Math.abs(vector.getX()) - Math.abs(vector.getY())) == 1;
	}

	public boolean isKnightsMove(Pixel centre, Pixel target) {
		boolean b1 = this.isOrthogonalNeighbour(centre) && centre.isDiagonalNeighbour(target);
		boolean b2 = target.isOrthogonalNeighbour(centre) && centre.isDiagonalNeighbour(this);
		return b1 || b2;
	}

	/** compares pixels by x,y values.
	 * 
	 * first compares Y and if equal then X
	 * 
	 * @param pixel1
	 * @return -1 if this.Int2 < pixel1.Int2, 1 if this.Int2 > pixel1.Int2, else 0
	 */
	public int compareTo(Pixel pixel1) {
		Int2 xy0 = this.getInt2();
		Int2 xy1 = pixel1.getInt2();
		int compare = new Integer(xy0.getY()).compareTo(new Integer(xy1.getY()));
		if (compare == 0) {
			compare = new Integer(xy0.getX()).compareTo(new Integer(xy1.getX()));
		}
		return compare;
	}
	
	PixelList getDiagonalNeighbours(PixelIsland island) {
		PixelList neighbours = getOrCreateNeighbours(island);
		PixelList pixelList = new PixelList();
		for (Pixel neighbour : neighbours) {
			if (isDiagonalNeighbour(neighbour)) {
				pixelList.add(neighbour);
			}
		}
		return pixelList;
	}

	public PixelList getOrthogonalNeighbours(PixelIsland island) {
		PixelList neighbours = getOrCreateNeighbours(island);
		PixelList pixelList = new PixelList();
		for (Pixel neighbour : neighbours) {
			if (isOrthogonalNeighbour(neighbour)) {
				pixelList.add(neighbour);
			}
		}
		return pixelList;
	}
	
	boolean isConnectedAny(PixelIsland island, int neighbourCount) {
		PixelList neighbours = getOrCreateNeighbours(island);
		boolean connected = (neighbours.size() != neighbourCount) ? false : true;
		return connected;
	}

	boolean is1ConnectedAny(PixelIsland island) {
		PixelList neighbours = getOrCreateNeighbours(island);
		boolean connected = (neighbours.size() != 1) ? false : true;
		return connected;
	}

	boolean is2ConnectedAny(PixelIsland island) {
		PixelList neighbours = getOrCreateNeighbours(island);
		boolean connected = (neighbours.size() != 2) ? false : true;
		return connected;
	}

	/** get mass centre of pixels with units weights.
	 * 
	 * @param pixels
	 * @return mean or null if pixels is null or size 0
	 */
	public static Real2 getCentre(Collection<Pixel> pixels) {
		Real2 centre = null;
		if (pixels != null && pixels.size() > 0) {
			Real2Array coords = new Real2Array();
			for (Pixel pixel : pixels) {
				coords.add(new Real2(pixel.getInt2()));
			}
			centre = coords.getMean();
		}
		return centre;
	}

	public Pixel getNextNeighbourIn2ConnectedChain(Pixel last) {
		Pixel next = null;
		if (is2ConnectedAny(island)) {
			PixelList neighbours = getOrCreateNeighbours(island);
			next = (neighbours.get(0) == last ? neighbours.get(1) : neighbours.get(0));
		}
		return next;
	}

	/** 
	 * Sets pixel in image to black.
	 * 
	 * subtracts offset
	 * 
	 * @param image
	 * @param x0 x offset 
	 * @param y0 y offset
	 */
	public void setToBlack(BufferedImage image, Int2 xy0) {
		Int2 int2 = getInt2();
		int i = int2.getX() - xy0.getX();
		int j = int2.getY() - xy0.getY();
		image.setRGB(i, j, 0x00000000);
	}

	public boolean isTjunctionCentre(PixelIsland island) {
		return this.getOrthogonalNeighbours(island).size() == 3 && 
				this.getDiagonalNeighbours(island).size() == 0;
	}

	/** do 3 pixels form a right angle of neighbours.
	 * 
	 * @param pixel pixel at right angle
	 * @param pixel1
	 * @param pixel2
	 * @return
	 */
	public static boolean isRightAngle(Pixel pixel, Pixel pixel1, Pixel pixel2) {
		return pixel.isOrthogonalNeighbour(pixel1) &&
		pixel.isOrthogonalNeighbour(pixel2) &&
		pixel1.isDiagonalNeighbour(pixel2);
	}

	public void computeNeighbours(PixelIsland pixelIsland) {
		this.neighbourList = null;
		getOrCreateNeighbours(pixelIsland);
	}

	void recomputeNeighbours(PixelIsland island) {
		PixelList neighbourList = getOrCreateNeighbours(island);
		for (Pixel neighbour : neighbourList) {
			neighbour.computeNeighbours(island);
		}
		computeNeighbours(island);
	}

	void removeFromNeighbourNeighbourList(PixelIsland pixelIsland) {
		PixelList neighbours = getOrCreateNeighbours(pixelIsland);
		for (Pixel neighbour : neighbours) {
			PixelList neighbourList = neighbour.getOrCreateNeighbours(pixelIsland);
			boolean removed = neighbourList.remove(this);
		}
	}

	/**
	 * for pixels of type 
	 * 
	 * XXX
	 * Xo
	 * X
	 * 
	 * remove o
	 * 
	 * @return
	 */
	boolean form5Corner(PixelIsland island) {
		boolean corner = false;
		PixelList neighbours = this.getOrCreateNeighbours(island);
		Int2Range box = neighbours.getIntBoundingBox();
		int xmin = box.getXRange().getMin();
		int xmax = box.getXRange().getMax();
		int ymin = box.getYRange().getMin();
		int ymax = box.getYRange().getMax();
		if (xmax - xmin == 2 && ymax - ymin == 2) {
			int xminCount = 0;
			int xmaxCount = 0;
			int yminCount = 0;
			int ymaxCount = 0;
			for (Pixel neighbour : neighbours) {
				Int2 xy = neighbour.getInt2();
				int x = xy.getX();
				if (x == xmin) {
					xminCount++;
				} else if (x == xmax) {
					xmaxCount++;
				}
				int y = xy.getY();
				if (y == ymin) {
					yminCount++;
				} else if (y == ymax) {
					ymaxCount++;
				}
			}
			int dx = Math.abs(xminCount - xmaxCount);
			int dy = Math.abs(yminCount - ymaxCount);
			corner = dx == 2 && dy == 2;
		}
		return corner;
		
	}

	public PixelList createCompleteNeighbourNeighbourList(PixelIsland island) {
		PixelList listOfNeighboursOfNeighbours = new PixelList();
		List<Int2> neighbours = calculateNeighbourCoordList(island);
		Set<Int2> neighboursOfNeighbours = new HashSet<Int2>();
		for (Int2 neighbour : neighbours) {
			neighboursOfNeighbours.addAll(new Pixel(neighbour).calculateNeighbourCoordList(island.getDiagonal()));
		}
		for (Int2 neighbour : neighbours) {
			neighboursOfNeighbours.remove(neighbour);
		}
		for (Int2 coord : neighboursOfNeighbours) {
			Pixel pixel = island.getPixelByCoordMap().get(coord);
			if (pixel != null) {
				listOfNeighboursOfNeighbours.add(pixel);
			}
		}
		return listOfNeighboursOfNeighbours;
	}

	public Int2 subtract(Pixel pixel) {
		return this.getInt2().subtract(pixel.getInt2());
	}

	/**
	 * 
	 * @return true if coordinates are equal
	 */
	public boolean hasEqualCoordinates(Pixel pixel) {
		if (pixel == null) {
			return false;
		}
		return pixel.getInt2().equals(this.getInt2());
	}
}
