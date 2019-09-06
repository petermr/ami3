package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.plot.AbstractPlotBox.AxisType;

/** managaes the contents of a region associated with an axis.
 * Generally an AnnotatedAxis contains some or all of:
 * 
 *  * AxisTickBox
 *  * AxisTextBox // for scales
 *  * AxisTextBox // for titles
 *  
 * @author pm286
 *
 */
public class AxialBox {

	private static final Logger LOG = Logger.getLogger(AxialBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/**
	 * default outside width of box (assumes ticks are on outside)
	 */
	public static final double DEFAULT_OUTSIDE_WIDTH = 18.0;
	/**
	 * default inside width of box (assumes ticks are on inside)
	 */
	public static final double DEFAULT_INSIDE_WIDTH = 10.0;
	/**
	 * default extension beyond the end of the main axis. Usually small.
	 * may not be necessary
	 */
	public static final double DEFAULT_LINE_EXTENSION = 5.0;

	/** these widths and extension are initially set by default to values which
	 * allow the capture of lines representing ticks. They are adjusted to reflect the actual extent.
	 * 
	 */
	protected double outsideWidth;
	protected double insideWidth;
	protected double lineExtension;
	protected Real2Range captureBox;
	protected List<SVGElement> containedGraphicalElements;
	protected AnnotatedAxis axis;
	protected Real2Range bbox;
	
	protected AxialBox() {
		super();
		setDefaults();
	}
	
	protected AxialBox(AnnotatedAxis axis) {
		this();
		this.axis = axis;
		if (axis == null) {
			LOG.warn("NULL AXIS");
		}
		containedGraphicalElements = new ArrayList<SVGElement>();
	}
	
	private void setDefaults() {
		this.outsideWidth = DEFAULT_OUTSIDE_WIDTH;
		this.insideWidth = DEFAULT_INSIDE_WIDTH;
		this.lineExtension = DEFAULT_LINE_EXTENSION;
	}
	
	public double getLineExtension() {
		return lineExtension;
	}

	public void setLineExtension(double lineExtension) {
		this.lineExtension = lineExtension;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("extendedBox: "+ (captureBox == null ? "null" : captureBox.toString()));
		sb.append(" bbox: "+ getBoundingBox()+"\n");
		sb.append("DIR: " + axis.getLineDirection() + "; inside/outside/line/extension deltas:" + insideWidth+", "+outsideWidth+", "+lineExtension+"\n");
		return sb.toString();
	}

	protected void makeCaptureBox() {
		AxisType axisType = axis.getAxisType();
		LineDirection lineDirection = axisType.getLineDirection();
		RealRange.Direction rrDirection = lineDirection.getRealRangeDirection();
		RealRange.Direction perpRRDirection = lineDirection.getPerpendicularLineDirection().getRealRangeDirection();
		captureBox = new Real2Range(axis.getSingleLine().getBoundingBox());
		double outside = (axisType.getOutsidePositive() == 1) ? this.outsideWidth : this.insideWidth;
		double inside = (axisType.getOutsidePositive() == 1) ? this.insideWidth : this.outsideWidth;
		captureBox.extendUpperEndBy(perpRRDirection, outside);
		captureBox.extendLowerEndBy(perpRRDirection, inside);
		captureBox.extendBothEndsBy(rrDirection,
				this.lineExtension, this.lineExtension);
		captureBox.format(decimalPlaces());
		return;
	}

	protected int decimalPlaces() {
		return axis.getPlotBox().getNdecimal();
	}
	
	private Real2Range getContainingBox() {
		Real2Range containingBox = SVGElement.createBoundingBox(containedGraphicalElements);
		return containingBox;
	}

	private Real2Range getBoundingBox() {
		return bbox;
	}

	public AbstractCMElement createSVGElement() {
		SVGG g = new SVGG();
		g.setSVGClassName("axialBox");
		for (AbstractCMElement element : containedGraphicalElements) {
			g.appendChild(element.copy());
		}
		addAnnotatedBox(g, captureBox, "yellow");
		addAnnotatedBox(g, bbox, "green");
		return g;
	}

	private void addAnnotatedBox(AbstractCMElement g, Real2Range bbox, String color) {
		if (bbox != null && bbox.isValid()) {
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			rect.setStrokeWidth(0.2);
			rect.setFill(color);
			rect.setOpacity(0.3);
			g.appendChild(rect);
		}
	}
}
