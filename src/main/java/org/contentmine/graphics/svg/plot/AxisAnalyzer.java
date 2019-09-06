package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.CombType;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;

public class AxisAnalyzer {

	static final Logger LOG = Logger.getLogger(AxisAnalyzer.class);

	public static final double _MAJOR_MINOR_TICK_RATIO = 1.1;

	private List<SVGLine> svgLines;
	private List<ComplexLine> horizontalComplexLines;
	private List<SVGLine> horizontalLines;
	private List<ComplexLine> verticalComplexLines;
	private List<SVGLine> verticalLines;

	private double maxTickLength = 50.d;
	private double minTickLength = 1.0d;
	public double jointEps = 0.5;
	private int minJointCount = 2;
	private int maxJointCount = 999;
	private double boxThickness = 100.;
	private double boxLengthExtension = 50.;

	private List<Axis> horizontalAxisList;
	private List<Axis> verticalAxisList;
	private List<GraphPlotBox> plotBoxList;
	private Axis horizontalAxis;
	private Axis verticalAxis;
	private GraphPlotBox plotBox;
	
	private AbstractCMElement g;
	public double eps;

	public AxisAnalyzer(AbstractCMElement g) {
		super();
		this.g = g;
		ensureSVGLines();
	}
	
	public void setEpsilon(double eps) {
		this.eps = eps;
	}
	public void createVerticalHorizontalAxisListAndPlotBox() {
		createVerticalAxisList();
		createHorizontalAxisList();
		createPlotBoxListOrPlotBox();
	}

	public void createPlotBoxListOrPlotBox() {
		createVerticalAxisList();
		createHorizontalAxisList();
		if (horizontalAxis != null && verticalAxis != null) {
			plotBox = createPlotBox();
		} else if (verticalAxisList.size() > 1 && horizontalAxisList.size() > 1) {
			plotBoxList = createPlotBoxList();
		} else {
		}
	}

	public void createHorizontalAxisList() {
		if (horizontalAxisList == null) {
			ensureHorizontalAndVerticalLines();
			this.horizontalComplexLines = ComplexLine.createComplexLines(this.horizontalLines, this.verticalLines, eps);
			horizontalAxisList = createAxisList(horizontalComplexLines, LineOrientation.HORIZONTAL);
			if (horizontalAxisList.size() == 1) {
				this.horizontalAxis = horizontalAxisList.get(0);
			} 
		}
	}

	private void ensureHorizontalAndVerticalLines() {
		ensureSVGLines();
		ensureHorizontalLines();
		ensureVerticalLines();
	}

	private void ensureVerticalLines() {
		if (verticalLines == null) {
			this.verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, eps);
		}
	}

	private void ensureHorizontalLines() {
		if (horizontalLines == null) {
			this.horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, eps);
		}
	}

	public void createVerticalAxisList() {
		if (verticalAxisList == null) {
			ensureSVGLines();
			ensureHorizontalAndVerticalLines();
			this.verticalComplexLines = ComplexLine.createComplexLines(this.verticalLines, this.horizontalLines, eps);
			verticalAxisList = createAxisList(verticalComplexLines, LineOrientation.VERTICAL);
			if (verticalAxisList.size() == 1) {
				this.verticalAxis = verticalAxisList.get(0);
			}
		}
	}
	
	private void ensureSVGLines() {
		if (svgLines == null && g != null) {
			svgLines = SVGLine.extractSelfAndDescendantLines(g);
		}
	}

	private GraphPlotBox createPlotBox() {
		if (horizontalAxis != null && verticalAxis != null) {
			plotBox = new GraphPlotBox(horizontalAxis, verticalAxis);
			LOG.trace("PLOT BOX "+plotBox);
		}
		return plotBox;
	}
	
	private List<GraphPlotBox> createPlotBoxList() {
		plotBoxList = new ArrayList<GraphPlotBox>();
		if (horizontalAxisList != null && verticalAxisList != null) {
			for (int i = 0; i < horizontalAxisList.size(); i++) {
				Axis horAxis = horizontalAxisList.get(i);
				for (int j = 0; j < verticalAxisList.size(); j++) {
					Axis vertAxis = verticalAxisList.get(j);
					plotBox = new GraphPlotBox(horAxis, vertAxis);
					if (plotBox != null && plotBox.areAxesTouching(eps)) {
						plotBoxList.add(plotBox);
					}
				}
			}
		}
		return plotBoxList;
	}
	

	public Axis getVerticalAxis() {
		createVerticalAxisList();
		return verticalAxis;
	}

	public Axis getHorizontalAxis() {
		createHorizontalAxisList();
		return horizontalAxis;
	}

	public List<Axis> getVerticalAxisList() {
		createVerticalAxisList();
		return verticalAxisList;
	}

	public List<Axis> getHorizontalAxisList() {
		createHorizontalAxisList();
		return horizontalAxisList;
	}


	/** create axis for given orientation
	 * 
	 * @param complexLines
	 * @param orientation
	 * @return
	 */
	private List<Axis> createAxisList(List<ComplexLine> complexLines, LineOrientation orientation) {
		 List<Axis> axisList = new ArrayList<Axis>();
		 if (complexLines != null) {
			for (ComplexLine complexLine : complexLines) {
				Axis axis = createAxis(complexLine, orientation);
				if (axis != null) {
					axisList.add(axis);
					axis.processScaleValuesAndTitlesNew(g);
					axis.createAxisGroup();
					LOG.trace("************  AXIS "+axis);
				}
			}
		}
		 return axisList;
	}

	private Axis createAxis(ComplexLine complexLine, LineOrientation orientation) {
		Axis axis = new Axis(this);
		if (!orientation.equals(axis.getOrientation())) {
//			throw new RuntimeException("Inconsistent axis orientation");
		}
		axis.setId("a_"+complexLine.getBackbone().getId());
		axis.setComplexLine(complexLine);
		complexLine.setMinMaxJointLength(minTickLength, maxTickLength);
		complexLine.setMinJointCount(2);
		complexLine.setRequirePerpendicularJoints(true);
		CombType combType = complexLine.getCombType();
		if (combType != null) {
			axis.trimJointList(complexLine.getJointList(), minTickLength, maxTickLength);
			axis.setCombType(ComplexLine.getCombType(axis.getMinorTickJointList(), minJointCount, maxJointCount));
		}
		if (axis.getCombType() != null) {
			axis.analyzeMajorMinorTicks(complexLine);
			LOG.trace(" ++++++++ AXIS "+axis.toString());
		} else {
			axis = null;
		}
		return axis;
	}

	public double getMaxTickLength() {
		return maxTickLength;
	}

	public void setMaxTickLength(double maxTickLength) {
		this.maxTickLength = maxTickLength;
	}

	public double getBoxLengthExtension() {
		return boxLengthExtension;
	}

	public void setBoxLengthExtension(double boxLengthExtension) {
		this.boxLengthExtension = boxLengthExtension;
	}

	public double getBoxThickness() {
		return boxThickness;
	}

	public void setBoxThickness(double boxThickness) {
		this.boxThickness = boxThickness;
	}

	public GraphPlotBox getPlotBox() {
		return plotBox;
	}

	public List<GraphPlotBox> getPlotBoxList() {
		return plotBoxList;
	}

}
