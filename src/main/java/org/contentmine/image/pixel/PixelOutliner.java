package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.image.pixel.IntLine.ChangeDirection;

public class PixelOutliner {

	private static Logger LOG = Logger.getLogger(PixelOutliner.class);

	private final static ChangeDirection[] DIRECTIONS = { ChangeDirection.LEFT,
			ChangeDirection.AHEAD, ChangeDirection.RIGHT };

	private PixelList pixelList;
	private int maxIter = 1000; // for testing and checking
	private List<IntLine> lineList;
	private PixelList usedPixels;
	private List<SVGPolygon> polygonList;
	private boolean failedConverge;
	private int minPolySize = 20;

	public PixelOutliner(PixelList pixelList) {
		this.pixelList = pixelList;
	}

	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}

	/**
	 * create outline starting at Northern extreme pixel.
	 * 
	 * goes horizontally East/right and continues clockwise round pixelList.
	 * 
	 * @return
	 */
	public List<SVGPolygon> createOutline() {
		polygonList = new ArrayList<SVGPolygon>();
		while (pixelList.size() > 0) {
			PixelList extremes = pixelList.findExtremePixels();
			Pixel startPixel = extremes.get(0);
			iterateClockwiseRoundPerimeter(startPixel);
			SVGPolygon polygon = getSVGPolygon();
			if (polygon.size() > minPolySize) {
				polygonList.add(polygon);
				LOG.trace("poly size " + polygon.size() + " pixelList "
						+ pixelList.size());
			}
			for (Pixel pixel : usedPixels) {
				while (pixelList.remove(pixel)) {
					
				}
			}
			if (failedConverge) {
				LOG.error("FAILED TO CONVERGE");
				break;
			}
		}
		LOG.trace("polygons " + polygonList.size());
		return polygonList;
	}

	public List<SVGPolygon> getPolygonList() {
		return polygonList;
	}
	
	public PixelList iterateClockwiseRoundPerimeter(Pixel startPixel) {
		return iterateClockwiseRoundPerimeter(startPixel, new Int2(1, 0));
	}

	public PixelList iterateClockwiseRoundPerimeter(Pixel startPixel, Int2 initialDirection) {
		failedConverge = false;
		lineList = new ArrayList<IntLine>();
		usedPixels = new PixelList();
		Int2 current = startPixel.getInt2().plus(new Int2((initialDirection.getY() > initialDirection.getX() ? 1 : 0), (initialDirection.getX() + initialDirection.getY() < 0 ? 1 : 0)));
		Int2 next = new Int2(current);
		next = next.plus(initialDirection);
		IntLine line = new IntLine(current, next, startPixel.getInt2(),
				startPixel);
		int count = 0;
		Pixel previousPixel = line.getCurrentPixel();
		usedPixels.add(previousPixel);
		while (!next.equals(current)) {
			lineList.add(line);
			IntLine nextLine = createNextLine(line);
			if (nextLine == null) {
				throw new RuntimeException("No line found");
			}
			Pixel currentPixel = nextLine.getCurrentPixel();
			if (currentPixel != null) {
				if (currentPixel != previousPixel) {
					if (previousPixel != null) {// && currentPixel.isDiagonalNeighbour(previousPixel)) {
						Int2 difference = currentPixel.getInt2().subtract(previousPixel.getInt2());
						Int2 toFindCornerPixel = null;
						if (difference.isEqualTo(new Int2(1, 1))) {
							toFindCornerPixel = new Int2(-1, 0);
						} else if (difference.isEqualTo(new Int2(1, -1))) {
							toFindCornerPixel = new Int2(0, 1);
						} else if (difference.isEqualTo(new Int2(-1, 1))) {
							toFindCornerPixel = new Int2(0, -1);
						} else if (difference.isEqualTo(new Int2(-1, -1))) {
							toFindCornerPixel = new Int2(1, 0);
						}
						if (toFindCornerPixel != null) {
							Pixel possiblePixel = pixelList.getPixelByCoordinate(currentPixel.getInt2().plus(toFindCornerPixel));
							if (possiblePixel != null) {
								usedPixels.add(possiblePixel);
							}
						}
					}
					usedPixels.add(currentPixel);
				}
			} else {
				LOG.error("null current pixel");
			}
			previousPixel = currentPixel;
			line = nextLine;
			if (nextLine.equals(lineList.get(0))) {
				break;
			}
			if (count++ >= maxIter) {
				LOG.error("failed to converge after " + count);
				failedConverge = true;
				break;
			}
		}
		return usedPixels;
	}

	/**
	 * creates line from current position to next.
	 * 
	 * Iterates through left, ahead, right and breaks after first which provide
	 * pixel on the right
	 * 
	 * @param line
	 * @return
	 */
	private IntLine createNextLine(IntLine line) {
		LineDirection dir = line.getDirection();
		IntLine nextLine = null;
		for (ChangeDirection change : DIRECTIONS) {
			LineDirection newDirection = dir.getNewDirection(change);
			nextLine = line.getNextLine(line.getDirection(), newDirection,
					change, pixelList);
			if (nextLine != null) {
				break;
			}
		}
		if (nextLine == null) {
			throw new RuntimeException("Failed to find next line");
		}
		return nextLine;
	}

	public SVGPolygon getSVGPolygon() {
		// createOutline();
		Real2Array r2a = new Real2Array();
		for (IntLine line : lineList) {
			Real2 midPoint = line.getMidPoint();
			r2a.add(midPoint);
		}
		SVGPolygon polygon = new SVGPolygon(r2a);
		polygon.setStrokeWidth(0.1);
		polygon.setFill("none");
		return polygon;
	}

	public void setMinPolySize(int size) {
		this.minPolySize = size;
	}

}
