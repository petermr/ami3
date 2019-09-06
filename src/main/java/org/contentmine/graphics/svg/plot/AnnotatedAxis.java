package org.contentmine.graphics.svg.plot;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealArray.Monotonicity;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.plot.AbstractPlotBox.AxisType;

import com.google.common.collect.Multiset;

/**
 * An axis (vertical of horizontal) with (probably) one or more
 *   axial line (SVGLine)
 *   tick marks (major and minor)
 *   scales (list of numbers aligned with ticks)
 *   axial titles
 *   
 * @author pm286
 *
 */
public class AnnotatedAxis {

	private static final Logger LOG = Logger.getLogger(AnnotatedAxis.class);
	static final double AXIS_END_EPS = 1.0;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static double EPS = 0.01;
	LineDirection lineDirection;
	RealRange range;
	SVGLine singleLine;
	private AxisTickBox axisTickBox;
	private AxisScaleBox axialScaleTextBox;
	private AxisScaleBox axialTitleTextBox;
	private AbstractPlotBox plotBox;
	private AxisType axisType;
	private Double screenToUserScale;
	private Double screenToUserConstant;


	protected AnnotatedAxis(AbstractPlotBox plotBox) {
		this.plotBox = plotBox;
	}
	
	public AnnotatedAxis(AbstractPlotBox plotBox, AxisType axisType) {
		this(plotBox);
		this.axisType = axisType;
		this.lineDirection = axisType == null ? null : axisType.getLineDirection();		
	}

//	private void setRange(RealRange range) {
//		this.range = range;
//	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("type: "+axisType+ "; dir: "+lineDirection+"; ");
		sb.append("range: "+range+"\n");
		sb.append("axisTickBox: "+axisTickBox+"\n");
		sb.append("tickValues: "+axialScaleTextBox+"\n");
		return sb.toString();
	}

	public RealRange getRange() {
		return range;
	}
	public void setSingleLine(SVGLine singleLine) {
		this.singleLine = singleLine;
	}

	public SVGElement getSingleLine() {
		return singleLine;
	}
	
	public LineDirection getLineDirection() {
		if (lineDirection == null && axisType != null) {
			lineDirection = axisType.getLineDirection();
		}
		return lineDirection;
	}

	public void setLineDirection(LineDirection direction) {
		this.lineDirection = direction;
	}

	public AxisTickBox getAxisTickBox() {
		return axisTickBox;
	}

	public void setScreenToUserScale(Double screenToUserScale) {
		this.screenToUserScale = screenToUserScale;
	}

	public Double getScreenToUserConstant() {
		return screenToUserConstant;
	}

	public AxisScaleBox getValueTextBox() {
		return axialScaleTextBox;
	}

	public void setValueTextBox(AxisScaleBox valueTextBox) {
		this.axialScaleTextBox = valueTextBox;
	}

	public Double getScreenToUserScale() {
		return screenToUserScale;
	}


	public AbstractPlotBox getPlotBox() {
		return plotBox;
	}

	public void setAxisTickBox(AxisTickBox axisTickBox) {
		this.axisTickBox = axisTickBox;
	}

	public AxisType getAxisType() {
		return axisType;
	}

	SVGElement getOrCreateSingleLine() {
		LineCache lineCache = plotBox.getComponentCache().getOrCreateLineCache();
		if (singleLine == null) {
			if (lineCache.getFullLineBox() != null) {
				Real2Range bbox = lineCache.getFullLineBox().getBoundingBox();
				Real2[] corners = bbox.getLLURCorners();
				if (AxisType.TOP.equals(axisType)) {
					singleLine = new SVGLine(corners[0], new Real2(corners[1].getX(), corners[0].getY())); 
				} else if (AxisType.BOTTOM.equals(axisType)) {
					singleLine = new SVGLine(new Real2(corners[0].getX(), corners[1].getY()), corners[1]); 
				} else if (AxisType.LEFT.equals(axisType)) {
					singleLine = new SVGLine(corners[0], new Real2(corners[0].getX(), corners[1].getY())); 
				} else if (AxisType.RIGHT.equals(axisType)) {
					singleLine = new SVGLine(new Real2(corners[1].getX(), corners[0].getY()), corners[1]); 
				} else {
					LOG.error("Unknown axis type: "+axisType);
				}
			} else {
				LOG.trace("no fullLineBox");
			}
		}
		return singleLine;
	}

	private void processTitle() {
		LOG.trace("AxisTitle title NYI");
	}

	void createAxisRanges() {
		if (singleLine == null) {
			LOG.trace("null singleLine in :"+this);
		} else if (axisTickBox == null) {
			LOG.trace("null axisTickBox :"+this);
		} else if (axisTickBox.getTickLines() != null && axisTickBox.getTickLines().size() > 0) {
			Real2Range bbox = singleLine.getBoundingBox();
			range = (lineDirection.isHorizontal()) ? bbox.getXRange() : bbox.getYRange();
			range.format(decimalPlaces());
			// assume sorted - we'll need to add sort later
			Real2Range tick2Range = SVGLine.getReal2Range(axisTickBox.getTickLines());
			axisTickBox.setTickRange(lineDirection.isHorizontal() ? tick2Range.getXRange() : tick2Range.getYRange());
		}
	}

	private int decimalPlaces() {
		return plotBox.getNdecimal();
	}

	void extractScaleTextsAndMakeScales() {
		if (axisTickBox == null) {
			LOG.trace("no ticks so no scale texts captured");
			return;
		}
		this.axialScaleTextBox = new AxisScaleBox(this);
		axialScaleTextBox.makeCaptureBox();
		this.axialScaleTextBox.setTexts(plotBox.getHorizontalTexts(), plotBox.getVerticalTexts());
		axialScaleTextBox.extractScaleValueList();
		RealArray tickValues = axialScaleTextBox.getTickNumberValues();
		RealArray tickValueCoords = axialScaleTextBox.getTickValueScreenCoords();
		Monotonicity tickValueCoordsMonotonicity = tickValueCoords == null ? null : tickValueCoords.getMonotonicity();
		RealArray tickCoords = axisTickBox.getMajorTicksScreenCoords();
		Monotonicity tickMonotonicity = (tickCoords == null) ? null : tickCoords.getMonotonicity();
		LOG.trace("TICK coords\n"
				+ " tick coords "+tickCoords+": "+tickMonotonicity+"\n"
				+ " tick value coords "+tickValueCoords+": "+tickValueCoordsMonotonicity+"\n"
				+ " tickValues: "+tickValues);
		if (tickValues != null && !tickValues.hasNaN() && tickCoords != null) {
			int nplaces = 1;
			Multiset<Double> deltaValueSet = tickValues.createDoubleDifferenceMultiset(nplaces);
			Multiset<Integer> deltaValueCoordSet = tickValueCoords.createIntegerDifferenceMultiset();
			Multiset<Integer> deltaTickCoordSet = tickCoords.createIntegerDifferenceMultiset();
			LOG.trace("DELTA coords\n"
					+ " delta tick Coords "+deltaTickCoordSet+"\n"
					+ " delta value Coords "+deltaValueCoordSet+"\n"
					+ " delta values: "+deltaValueSet);
			
			if (true) { // fill conditions for equality
				matchTicksToValuesAndCalculateScales(tickValues, tickValueCoords, tickCoords, nplaces);
			}
		}
	}

	private void matchTicksToValuesAndCalculateScales(RealArray tickValues, RealArray tickValueCoords, RealArray tickCoords, int nplaces) {
		if (tickValueCoords.size() <= 1) {
			return;
		}
		if (tickValueCoords.size() - tickCoords.size() == 2) { // probably missing end points
			LOG.trace("missing 2 ticks; taking axes as ends ticks"); 
			tickCoords.addElement(range.getMax());
			tickCoords.insertElementAt(0, range.getMin());
		} else if (tickValueCoords.size() - tickCoords.size() == 1) { // have to work out which end point
			LOG.trace("cannot match ticks with values; single missing tick; try to add at ends");
			double delta00 = Math.abs(tickCoords.get(0) - tickValueCoords.get(0));
			double delta01 = Math.abs(tickCoords.get(0) - tickValueCoords.get(1));
			if (delta00 < delta01) {
				tickCoords.addElement(range.getMax());
			} else {
				tickCoords.insertElementAt(0, range.getMin());
			}
		} else if (tickValueCoords.size() == tickCoords.size() - 1 ) {
			// maybe a spurious end tick - not monotonically increasing
			int nticks = tickCoords.size();
			if (tickCoords.get(nticks - 1) < tickCoords.get(nticks - 2)) {
				LOG.trace("removing spurious tick: "+tickCoords.get(nticks - 1));
				tickCoords.deleteElement(nticks - 1);
			}
		} else if (tickValueCoords.size() == tickCoords.size() ) {
			LOG.trace("ok");
		} else {
//			LOG.error(axisType+" cannot match ticks with valueCoords: \n"+tickValueCoords+"; ticks "+tickCoords);
//			throw new RuntimeException("cannot match ticks with values; "+axisType+" tickValues: "+tickValueCoords.size()+"; ticks: " + tickCoords.size());
//			LOG.error("cannot match ticks with values; "+axisType+" tickValues: "+tickValueCoords.size()+"; ticks: " + tickCoords.size());
			return;
		}
		RealArray tick2ValueDiffs = tickCoords.subtract(tickValueCoords);
		tick2ValueDiffs.format(0);
		Multiset<Double> tick2ValueSet = tick2ValueDiffs.createDoubleDifferenceMultiset(nplaces);
		screenToUserScale = getOrCreateScreenToUserScale(tickValues, tickCoords);
		screenToUserConstant = getOrCreateScreenToUserConstant(tickValues, tickCoords);
	}

	private Double getOrCreateScreenToUserConstant(RealArray tickValues, RealArray tickCoords) {
		if (tickCoords != null && tickCoords.getRange() != null && tickValues != null && tickValues.getRange() != null) {	
			screenToUserConstant = tickCoords.getRange().getConstantTo(tickValues.getRange());
		} else {
			LOG.trace("No tickBox info: "+this.axisType);
		}
		return screenToUserConstant;
	}

	private Double getOrCreateScreenToUserScale(RealArray tickValues, RealArray tickCoords) {
		if (tickCoords != null && tickCoords.getRange() != null && tickValues != null && tickValues.getRange() != null) {	
			screenToUserScale = tickCoords.getRange().getScaleTo(tickValues.getRange());
		} else {
			LOG.trace("No tickBox info: "+this.axisType);
		}
		return screenToUserScale;
	}

	void extractTitleTextsAndMakeTitles() {
		axialTitleTextBox = new AxisScaleBox(this);
		this.axialTitleTextBox.setTexts(plotBox.getHorizontalTexts(), plotBox.getVerticalTexts());
		axialTitleTextBox.extractText();
	}

	boolean isHorizontal() {
		return getLineDirection().isHorizontal();
	}

	public AbstractCMElement getSVGElement() {
		SVGG g = new SVGG();
		g.setSVGClassName("axis");
		if (axisTickBox != null) {
			g.appendChild(axisTickBox.createSVGElement());
		}
		if (axialScaleTextBox != null) {
			g.appendChild(axialScaleTextBox.createSVGElement());
		}
		return g;
	}

	private void buildTickBoxContents(AxisTickBox axisTickBox) {
		setAxisTickBox(axisTickBox);
		SVGLineList potentialTickLines = axisTickBox.getPotentialTickLines();
		axisTickBox.createMainAndTickLines(this, potentialTickLines.getLineList());
	}

	/** make tick box from knowing only the axis Type
	 * 
	 * 
	 * @param line
	 * @param lineDirection
	 */
	private AxisTickBox createAxisTickBox() {
		AxisTickBox axisTickBox = null;
		if (getSingleLine() != null && getAxisType() != null) {
			axisTickBox = new AxisTickBox(this);
			axisTickBox.makeCaptureBox();
		}
		return axisTickBox;
	}

	AxialBox createAndFillTickBox(List<SVGLine> horizontalLines, List<SVGLine> verticalLines) {
		AxisTickBox axisTickBox = createTickBoxAndAxialLines(horizontalLines, verticalLines);
		if (axisTickBox != null) {
			buildTickBoxContents(axisTickBox);
		} else {
			LOG.trace("Null axisTickBox");
		}
		return axisTickBox;
	}

	private AxisTickBox createTickBoxAndAxialLines(List<SVGLine> horizontalLines, List<SVGLine> verticalLines) {
		AxisTickBox axisTickBox = null;
		if (singleLine != null) {
			List<SVGLine> possibleTickLines = lineDirection.isHorizontal() ? verticalLines : horizontalLines;
			if (possibleTickLines.size() > 0) {
				axisTickBox = createAxisTickBox();
				axisTickBox.extractIntersectingLines(horizontalLines, verticalLines);
			}
		} else {
			LOG.trace("no single line for "+this);
		}
		return axisTickBox;
	}

	public void ensureScales() {
		if (axialScaleTextBox != null) {
			RealArray tickValues = axialScaleTextBox.getTickValueScreenCoords();
			RealArray tickCoords = axialScaleTextBox.getTickValueScreenCoords();
			if (screenToUserScale == null || screenToUserConstant == null) {
				getOrCreateScreenToUserScale(tickValues, tickCoords);
				getOrCreateScreenToUserConstant(tickValues, tickCoords);
			}
		} else {
			throw new RuntimeException("No axial tickbox: "+axisType);
		}
	}


	
}
