package org.contentmine.graphics.svg.util;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;

/** a rectangular array of integer squares representing graphic components
 * will later have a variable step size representing different granularities (min size =1)
 * allows for offset origin
 * 
 * currently crude and uses whole page from (0,0) with int=1 steps
 * 
 * 
 * each pixel can hold a non-negative integer (usually a count).
 * @author pm286
 *
 */
public class SuperPixelArray {
	private static final Logger LOG = Logger.getLogger(SuperPixelArray.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static String[] COLOR = {
			"white",
			"red",
			"green",
			"blue",
			"cyan",
			"magenta",
			"yellow",
			"gray",
			"pink",
			
	};
	private Int2Range int2Range;
	private IntMatrix intMatrix;
	// convenience fields
	private IntRange xRange;
	private IntRange yRange;
	private int xSize;
	private int ySize;
	private int dx = 1;
	private int dy = 1;

	public SuperPixelArray(Int2Range int2Range) {
		init(int2Range);
	}

	private void init(Int2Range int2Range) {
		this.int2Range = new Int2Range(int2Range);
		this.xRange = int2Range.getXRange();
		this.xSize = xRange.getRange();
		this.yRange = int2Range.getYRange();
		this.ySize = yRange.getRange();
		this.intMatrix = new IntMatrix(xRange.getMax()+1, yRange.getMax()+1, 0);
	}
	
	public void setPixel(int value, Int2 xy) {
		intMatrix.setElementAt(xy.getX(), xy.getY(), value);
	}

	public void setPixels(int value, Int2Range int2Range) {
		IntRange xrange = int2Range.getXRange();
		int xSize = xrange.getRange();
		int xMin = xrange.getMin();
		IntRange yrange = int2Range.getYRange();
		int ySize = yrange.getRange();
		int yMin = yrange.getMin();
//		LOG.debug(intMatrix.getRows()+"; "+int2Range+"; " + intMatrix.getCols());
		int[][] matrix = intMatrix.getMatrix();
		dx = 5;
		dy = 5;
		int x0 = format(xMin, dx);
		int x1 = format(xMin + xSize, dx);
		int y0 = format(yMin, dy);
		int y1 = format(yMin + ySize, dx);
		for (int i = x0; i < x1; i+=dx) {
			for (int j = y0; j < y1; j+=dy) {
				matrix[i][j] = value;
			}
		}
	}

	private int format(int value, int granularity) {
		return granularity * (value / granularity);
	}

	public void setPixels(int value, List<Real2Range> boundingBoxList) {
		for (Real2Range box : boundingBoxList) {
			if (box != null && box.isValid()) {	
//				LOG.debug(box);
				this.setPixels(value, new Int2Range(box));
			}
		}
	}

	public void draw(SVGG g, File file) {
		draw(g, file, false);
	}

	public void draw(SVGG g, File file, boolean useCount) {
		int dx = 5;
		int dy = 5;
		for (int i = 0; i < xSize; i+= dx) {
			for (int j = 0; j < ySize; j+= dy) {
				Int2 xy = new Int2(i,j);
				int value = intMatrix.elementAt(xy);
				if (value > 0) {
					SVGElement elem = new SVGCircle(new Real2(xy), dx/2);
					elem.setFill(COLOR[1]);
					if (useCount) {
						elem.setOpacity((double)value/10.);
					}
					g.appendChild(elem);;
				}
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, file);
	}

	public SuperPixelArray plus(SuperPixelArray superPixelArray) {
		Int2Range bbox = superPixelArray == null ? this.int2Range : this.int2Range.plus(superPixelArray.int2Range);
		SuperPixelArray resultPixelArray = new SuperPixelArray(bbox);
		if (superPixelArray == null) {
			resultPixelArray.intMatrix = new IntMatrix(this.intMatrix);
		} else {
			try {
				resultPixelArray.intMatrix = this.intMatrix.plus(superPixelArray.intMatrix);
			} catch (Exception e) {
				LOG.debug(e);
			}
		}
		return resultPixelArray;
	}

}
