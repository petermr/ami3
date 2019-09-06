package org.contentmine.graphics.svg.plot;

import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;
import org.contentmine.graphics.svg.linestuff.ComplexLine;

public class GraphPlotBox {


	public static final String AXES_BOX = "axesBox";

	private Axis horizontalAxis;
	private Axis verticalAxis;
	private RealRange horizontalRange;
	private RealRange verticalRange;
	private Real2Range boxRange;


	public GraphPlotBox(Axis horizontalAxis, Axis verticalAxis) {
		setHorizontalAxis(horizontalAxis);
		setVerticalAxis(verticalAxis);
	}
	
	public Axis getHorizontalAxis() {
		return horizontalAxis;
	}

	public void setHorizontalAxis(Axis horizontalAxis) {
		this.horizontalAxis = horizontalAxis;
		this.horizontalRange = (horizontalAxis == null) ? null : horizontalAxis.getAxisRangeInPixels();
	}

	public Axis getVerticalAxis() {
		return verticalAxis;
	}

	public void setVerticalAxis(Axis verticalAxis) {
		this.verticalAxis = verticalAxis;
		verticalRange = (verticalAxis == null ? null : verticalAxis.getAxisRangeInPixels());
	}

	public SVGRect createRect() {
		SVGRect boxRect = null; 
		if (horizontalRange != null && verticalRange != null) {
			boxRange = new Real2Range(horizontalRange, verticalRange);
			boxRect = SVGRect.createFromReal2Range(boxRange);
		}
		return boxRect;
	}

	public Real2Range getBoxRange() {
		return boxRange;
	}
	
	public boolean areAxesTouching(double eps) {
		boolean touching = false;
		ComplexLine horizontalComplexLine = (horizontalAxis == null ? null : horizontalAxis.getComplexLine());
		ComplexLine verticalComplexLine = (verticalAxis == null ? null : verticalAxis.getComplexLine());
		if (horizontalComplexLine != null && verticalComplexLine != null) {
			Real2Range horizontalBBox = BoundingBoxManager.createExtendedBox(horizontalComplexLine.getBackbone(), eps);
			Real2Range verticalBBox = BoundingBoxManager.createExtendedBox(verticalComplexLine.getBackbone(), eps);
			Real2Range overlap = horizontalBBox.intersectionWith(verticalBBox);
			touching = overlap != null;
		}
		return touching;
	}
	
	public AbstractCMElement drawBox() {
		AbstractCMElement g = new SVGG();
		SVGRect bbox = this.createRect();
		bbox.setSVGClassName(AXES_BOX);
		bbox.setOpacity(0.3);
		bbox.setStroke("cyan");
		bbox.setStrokeWidth(5.0);
		g.appendChild(bbox);
		return g;
	}
}
