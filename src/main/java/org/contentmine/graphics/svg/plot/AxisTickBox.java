package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.List;
/**
 * Contains the axis and tick marks (major and minor). 
 * 
 * Dynamically alters the bounding box (bbox) size to try to determine the actual size of the axis
 * and ticks.
 * Does not attempt to contain text, though it may coincidentally do so.
 * in diagram below - and | are lines in diagram, dots are bounding box.
 * This is a BOTTOM axis
 * 
 * leftAxialLine         rightAxialLine
 *     |                       |
 *     |        plot           |
 *     |                       |
 *   ..|.......................|.. // inner edge of bounding box
 *   .---------------------------. // axial line with possible extension/tick
 *   . |   |   |   |   |   |   | . // row of ticks
 *   ............................. // outer edge of bbox
 * left edge                  right edge
 * bbox                       bbox
 * 
 * The bbox is adjusted till it just includes the tickmarks and possibly
 * axial extension. (This might be a tick on the perpendicular axis).
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class AxisTickBox extends AxialBox {
	
	static final Logger LOG = Logger.getLogger(AxisTickBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/**
	 * default max tickLength (to filter out axes)
	 */
	public static final double DEFAULT_MAX_TICKLENGTH = 25.0;

	
	private double maxTickLineLength;
	
	private SVGLineList intersectingHorizontalLines;
	private SVGLineList intersectingVerticalLines;
	private List<SVGLine> tickLines;
	private RealRange tickRange;
	private RealArray majorTicksScreenCoords; // the position of the major ticks
	private RealArray minorTicksScreenCoords; // the position of the minor ticks

	private String tickSignature;
	private Double majorTickLength;
	private Double minorTickLength;


	AxisTickBox() {
		setDefaults();
	}

	private void setDefaults() {
		this.maxTickLineLength = DEFAULT_MAX_TICKLENGTH;
	}
	
	AxisTickBox(AnnotatedAxis axis) {
		super(axis);
		setDefaults();
	}
	
	void createMainAndTickLines(AnnotatedAxis axis, List<SVGLine> tickLines) {
		if (tickLines.size() == 0) {
			LOG.warn("NO tickLines");
		}
		this.tickLines = tickLines;
		Multiset<Double> tickLengths = HashMultiset.create();
		for (SVGLine tickLine : tickLines) {
			double tickLineLength = tickLine.getLength();
			if (tickLineLength < this.getMaxTickLineLength()) {
				tickLengths.add((Double)Real.normalize(tickLineLength, 2));
			}
		}
		if (tickLengths.elementSet().size() == 1) {
			setMajorTickLength(tickLengths.elementSet().iterator().next());
			this.getTickLinesAndSignature();
		} else if (tickLengths.elementSet().size() == 2) {
			analyzeMajorAndMinorTickLengths(tickLengths);
			this.getTickLinesAndSignature();
		} else {
			LOG.trace("cannot process ticks: "+tickLengths);
		}
		return;
	}

	public double getMaxTickLineLength() {
		return maxTickLineLength;
	}

	public void setMajorScreenCoords(RealArray majorTicksPixels) {
		this.majorTicksScreenCoords = majorTicksPixels;
	}

	public RealArray getMinorTicksPixels() {
		return minorTicksScreenCoords;
	}

	public void setMinorTicksPixels(RealArray minorTicksScreenCoords) {
		this.minorTicksScreenCoords = minorTicksScreenCoords;
	}

	public RealArray getMajorTicksScreenCoords() {
		return majorTicksScreenCoords;
	}

	public void setTickSignature(String string) {
		this.tickSignature = string;
	}

	public void setMajorTickLength(Double majorTickLength) {
		this.majorTickLength = majorTickLength;
	}

	public void setMinorTickLength(Double minorTickLength) {
		this.minorTickLength = minorTickLength;
	}


	public String getTickSignature() {
		return tickSignature;
	}

	public Double getMajorTickLength() {
		return majorTickLength;
	}

	public Double getMinorTickLength() {
		return minorTickLength;
	}

	public List<SVGLine> getTickLines() {
		return tickLines;
	}

	public RealRange getTickRange() {
		return tickRange;
	}

	public void setTickRange(RealRange tickRange) {
		this.tickRange = tickRange;
	}

	void extractIntersectingLines(List<SVGLine> horizontalLines, List<SVGLine> verticalLines) {
		this.intersectingHorizontalLines = extractIntersectingLines(new SVGLineList(horizontalLines));
		this.intersectingVerticalLines = extractIntersectingLines(new SVGLineList(verticalLines));
	}

	/** get all lines intersecting with this.boundingBox.
	 * 
	 * @param lines
	 * @return
	 */
	private SVGLineList extractIntersectingLines(SVGLineList lines) {
		SVGLineList lineList = new SVGLineList();
		for (SVGLine line : lines) {
			Real2Range lineBBox = line.getBoundingBox();
			Real2Range inter = lineBBox.intersectionWith(this.captureBox);
			if (inter!= null && inter.isValid()) {
				line.format(decimalPlaces());
				lineList.add(line);
			}
		}
		return lineList;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("box: "+super.toString());
		sb.append("HOR: " + intersectingHorizontalLines.size() + "; " + intersectingHorizontalLines+"\n");
		sb.append("VERT: " + intersectingVerticalLines.size() + "; " + intersectingVerticalLines+"\n");
		sb.append("majorTicks: "+this.majorTicksScreenCoords+"\n");
		sb.append("minorTicks: "+this.minorTicksScreenCoords+"\n");
		return sb.toString();
	}
	
	SVGLineList getPotentialTickLines() {
		return (axis.getLineDirection().isHorizontal()) ? intersectingVerticalLines : intersectingHorizontalLines;
	}

	private void getTickLinesAndSignature() {
		StringBuilder sb = new StringBuilder();
		List<SVGLine> majorTickLines = new ArrayList<SVGLine>();
		List<SVGLine> minorTickLines = new ArrayList<SVGLine>();
		for (SVGLine tickLine : this.tickLines) {
			Double l = tickLine.getLength();
			String ss = null;
			if (Real.isEqual(l,  this.getMajorTickLength(), AnnotatedAxis.EPS)) {
				ss = XYPlotBox.MAJOR_CHAR;
				majorTickLines.add(tickLine);
			} else if (l < this.getMajorTickLength()) { // crude
				ss = XYPlotBox.MINOR_CHAR;
				minorTickLines.add(tickLine);
			}
			sb.append(ss);
		}
		this.setMajorScreenCoords(this.getPixelCoordinatesForTickLines(majorTickLines));
		this.setMinorTicksPixels(this.getPixelCoordinatesForTickLines(minorTickLines));
		this.setTickSignature(sb.toString());
		Real2Range bbox0 = SVGElement.createBoundingBox(majorTickLines);
		Real2Range bbox1 = SVGElement.createBoundingBox(minorTickLines);
		bbox = bbox0.plus(bbox1);
	}
	
	private void analyzeMajorAndMinorTickLengths(Multiset<Double> tickLengths) {
		Double majorTickLength = null;
		Double minorTickLength = null;
		for (Double d : tickLengths.elementSet()) {
			if (majorTickLength == null) {
				majorTickLength = d;
			} else {
				if (d < majorTickLength) {
					minorTickLength = d;
				} else {
					minorTickLength = majorTickLength;
					majorTickLength = d;
				}
			}
		}
		setMajorTickLength(majorTickLength);
		setMinorTickLength(minorTickLength);
	}
	
	private RealArray getPixelCoordinatesForTickLines( List<SVGLine> tickLines) {
		double[] coord = new double[tickLines.size()];
		for (int i = 0; i < tickLines.size(); i++) {
			SVGLine tickLine = tickLines.get(i);
			Real2 xy = tickLine.getXY(0);
			coord[i] = (axis.isHorizontal()) ? xy.getX() : xy.getY();
		}
		RealArray tickLineCoordArray = new RealArray(coord);
		return tickLineCoordArray;
	}

	int addMissingEndTicks(AnnotatedAxis annotatedAxis) {
		int added = 0;
		Double lowAxis = annotatedAxis.range.getMin();
		Double lowTickPosition = majorTicksScreenCoords.get(0);
		if (lowTickPosition - lowAxis > AnnotatedAxis.AXIS_END_EPS) {
			majorTicksScreenCoords.insertElementAt(0, lowAxis);
			added++;
		}
		Double hiAxis = annotatedAxis.range.getMax();
		Double hiTickPosition = majorTicksScreenCoords.get(majorTicksScreenCoords.size() - 1);
		if (hiAxis - hiTickPosition > AnnotatedAxis.AXIS_END_EPS) {
			majorTicksScreenCoords.addElement(hiAxis);
			added++;
		}
		return added;
	}

	public AbstractCMElement createSVGElement() {
		SVGG g = (SVGG) super.createSVGElement();
		g.setSVGClassName("axisTickBox");
		for (AbstractCMElement element : containedGraphicalElements) {
			g.appendChild(element.copy());
		}
		return g;
	}


}
