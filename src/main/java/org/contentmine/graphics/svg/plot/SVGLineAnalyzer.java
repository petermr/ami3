package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGLine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SVGLineAnalyzer {

	private final static Logger LOG = Logger.getLogger(SVGLineAnalyzer.class);

	private final static Double EPS = 0.0001;
	private final static Vector2 XAXIS = new Vector2(1.0, 0.0);
	private List<SVGLine> lines;
	private Multimap<Integer, SVGLine> lineAngleMap;
	private Axis horizontalAxis;
	private Axis verticalAxis;
	private List<GraphPlotBox> plotBoxList;
	private GraphPlotBox plotBox;
	private Double eps = EPS;
	private AbstractCMElement svgg;

	public SVGLineAnalyzer() {
	}
	
	/** copy lines for analysis
	 * 
	 * @param lines
	 */
	private void addLines(List<SVGLine> lines) {
		this.lines = new ArrayList<SVGLine>();
		for (SVGLine line : lines) {
			this.lines.add(line);
		}
	}
	
	public void setEpsilon(double eps) {
		this.eps = eps;
	}
	
	public Multimap<Integer, SVGLine> getLineAngleMap() {
		if (lineAngleMap == null) {
			lineAngleMap = ArrayListMultimap.create();
			if (lines != null) {
				for (SVGLine line : lines) {
					Vector2 vector = line.getEuclidLine().getUnitVector();
					Angle angle = vector.getAngleMadeWith(XAXIS);
					Integer degrees =  (int) Math.round(angle.getDegrees());
					// normalize direction
					if (degrees > 180) {
						degrees = degrees - 180;
					}
					if (degrees < 0) {
						degrees = degrees + 180;
					}
					lineAngleMap.put(degrees, line);
				}
			}
		}
		return lineAngleMap;
	}
	
	public List<GraphPlotBox> findGraphPlotBoxList(AbstractCMElement svgg) {
		this.svgg = svgg;
		ensureLines();
		return findGraphPlotBoxList();
	}

	private void ensureLines() {
		if (lines == null && svgg != null) {
			lines = SVGLine.extractSelfAndDescendantLines(svgg);
			this.addLines(lines);
		}
	}

	private List<GraphPlotBox> findGraphPlotBoxList() {
		plotBoxList = new ArrayList<GraphPlotBox>();
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(svgg);
		axisAnalyzer.setEpsilon(eps);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		plotBox = axisAnalyzer.getPlotBox();
		if (plotBox != null) {
			plotBoxList.add(plotBox);
		}
		return plotBoxList;
	}

	public String debug() {
		getLineAngleMap();
		StringBuilder sb = new StringBuilder("\nAngles: "+"\n");
		Integer[] deg = lineAngleMap.keySet().toArray(new Integer[0]);
		Arrays.sort(deg);
		List<Integer> degreeList = (Arrays.asList(deg));
		for (Integer degrees : degreeList) {
			sb.append("> "+degrees+" "+lineAngleMap.get(degrees).size()+"\n");
		}
		return sb.toString();
	}

	public GraphPlotBox getPlotBox() {
		return plotBox;
	}
}
