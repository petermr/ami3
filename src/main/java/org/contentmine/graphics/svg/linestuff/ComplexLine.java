package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;

/** aggregates of Lines
 * 
 * @author pm286
 *
 */
public class ComplexLine {

	private final static Logger LOG = Logger.getLogger(ComplexLine.class);
	private static final double JOINT_EPS = 0.3;
	
	public enum LineOrientation {
		HORIZONTAL,
		VERTICAL, 
		ZERO;
		public static LineOrientation getOther(LineOrientation orientation) {
			return (HORIZONTAL.equals(orientation)) ? VERTICAL : HORIZONTAL;
		}
		
		public LineOrientation getOtherOrientation() {
			return getOther(this);
		}
	}
	
	public enum SideOrientation {
		MINUS,
		PLUS,
		/** extends both sides*/
		CROSSING, 
		PLUS_OR_MINUS, // only used in analysis
		;
		public static SideOrientation getOther(SideOrientation orientation) {
			SideOrientation other = null;
			if (MINUS.equals(orientation)) {
				other = PLUS;
			} else if (PLUS.equals(orientation)) {
				other = MINUS;
			}
			return other;
		}
		
		public static List<SideOrientation> EMPTYLIST = Arrays.asList(new SideOrientation[]{});
		public static List<SideOrientation> MINUSLIST = Arrays.asList(new SideOrientation[]{MINUS});
		public static List<SideOrientation> PLUSLIST = Arrays.asList(new SideOrientation[]{PLUS});
		public static List<SideOrientation> MINUSPLUSLIST = Arrays.asList(new SideOrientation[]{MINUS, PLUS});
		
		public SideOrientation getOtherOrientation() {
			return getOther(this);
		}
		
	}
	
	enum Direction {
		LINE_DIR_1_2,
		LINE_DIR_2_1,
	}
	
	public enum CombType {
		MIXED,
		MINUS,
		PLUS,
		PLUS_OR_MINUS,
		CROSSING;
	}
	
	public enum Position {
		HIGHER,
		LOWER,
	}

	protected List<Joint> jointList;
	protected List<Joint> sortedJointList;
	protected SVGLine backbone;
	private LineOrientation backboneOrientation;
	private Direction backboneDirection;
	private double eps;
	private Double spacing;
	private double maxJointLength = 999.;
	private double minJointLength = 0.0;
	private CombType combType;
	private int minJointCount = 0;
	private int maxJointCount = 999;
	private boolean requirePerpendicularJoints = true;

	
	private ComplexLine(SVGLine backbone, LineOrientation backboneOrientation, double eps) {
		this.backbone = backbone;
		this.backboneOrientation = backboneOrientation;
		this.eps = eps;
	}
	
	public static ComplexLine createComplexLine(SVGLine backbone, double eps) {
		ComplexLine complexLine = null;
		LineOrientation backboneOrientation = ComplexLine.getLineOrientation(backbone, eps);
		if (backboneOrientation != null) {
			LOG.trace("BACKBONE PARENT"+backbone.getParent());
			complexLine = new ComplexLine(backbone, backboneOrientation, eps);
		}
		return complexLine; 
	}
	
	public static ComplexLine createComplexLineAndAddLines(
			SVGLine backbone, List<SVGLine> lines, double eps) {
		ComplexLine complexLine = createComplexLine(backbone, eps);
		complexLine.addLines(lines);
		return complexLine; 
	}
	
	public static List<ComplexLine> createComplexLineAndAddLines(
			List<SVGLine> lines, List<SVGLine> lines1, double eps) {
		List<ComplexLine> complexLines = new ArrayList<ComplexLine>();
		for (SVGLine line : lines) {
			ComplexLine complexLine = createComplexLine(line, eps);
			complexLine.addLines(lines1);
			complexLines.add(complexLine);
		}
		return complexLines; 
	}
	
	public int getMinJointCount() {
		return minJointCount;
	}

	public void setMinJointCount(int minJointCount) {
		this.minJointCount = minJointCount;
	}

	public int getMaxJointCount() {
		return maxJointCount;
	}

	public void setMaxJointCount(int maxJointCount) {
		this.maxJointCount = maxJointCount;
	}

	public void setMaxJointLength(double maxJointLength) {
		this.maxJointLength = maxJointLength;
	}

	public void setMinJointLength(double minJointLength) {
		this.minJointLength = minJointLength;
	}

	public double getMaxJointLength() {
		return maxJointLength;
	}

	public double getMinJointLength() {
		return minJointLength;
	}

	public void setMinMaxJointLength(double minJointLength, double maxJointLength) {
		if (minJointLength > maxJointLength) {
			throw new IllegalArgumentException("Bad tick constraints: "+minJointLength+", "+maxJointLength);
		}
		setMinJointLength(minJointLength);
		setMaxJointLength(maxJointLength);
	}

	public void setMinJoints(int minJoints) {
		this.minJointCount  = minJoints;
	}

	public boolean isRequirePerpendicularJoints() {
		return requirePerpendicularJoints;
	}

	public void setRequirePerpendicularJoints(boolean requirePerpendicularJoints) {
		this.requirePerpendicularJoints = requirePerpendicularJoints;
	}

	public Joint createPerpendicularJoint(SVGLine line, double eps) {
		Joint joint = null;
		Real2 point = null;
		RealRange backboneXRange = this.backbone.getReal2Range().getXRange();
		RealRange backboneYRange = this.backbone.getReal2Range().getYRange();
		RealRange lineXRange = line.getReal2Range().getXRange();
		RealRange lineYRange = line.getReal2Range().getYRange();
		SideOrientation sideOrientation = null;
		Double lineLength = line.getLength();
		if (horizontalAndVerticalLinesMeet(this.backbone, line, eps)) {
			if (!isRequirePerpendicularJoints() || backbone.isPerpendicularTo(line, eps)) {
				LineOrientation currentJointOrientation = ComplexLine.getLineOrientation(line, eps);
				this.createPointAndSideOrientation(currentJointOrientation);
				if (currentJointOrientation == null) {
					// not orthogonal
				} else if (this.backboneOrientation.equals(LineOrientation.HORIZONTAL) &&
					currentJointOrientation.equals(LineOrientation.VERTICAL)) {
					point = new Real2(lineXRange.getMin(), backboneYRange.getMin());
					sideOrientation = this.getSideOrientation( point.getY(), lineYRange, eps);
				} else if (this.backboneOrientation.equals(LineOrientation.VERTICAL) && 
					currentJointOrientation.equals(LineOrientation.HORIZONTAL)) {
					point = new Real2(backboneXRange.getMin(), lineYRange.getMin());
					sideOrientation = this.getSideOrientation( point.getX(), lineXRange, eps);
				}
			}
		}
		if (point != null && lineLength <= this.getMaxJointLength() && lineLength >= this.getMinJointLength()) {
			joint = new Joint(point, this.backbone, line, sideOrientation, eps);
		}
		return joint;
	}

	private void createPointAndSideOrientation(LineOrientation lineOrientation) {
		// TODO Auto-generated method stub
		
	}

	/** gets boundingBox of backbone and joints.
	 * 
	 * @return null if no backbone
	 */
	public Real2Range getBoundingBoxIncludingJoints() {
		Real2Range bbox = null; 
		if (backbone != null) {
			bbox = backbone.getBoundingBox();
			if (jointList != null) {
				for (Joint joint : jointList) {
					bbox = bbox.plus(joint.getBoundingBox());
				}
			}
		}
		return bbox;
	}

	/** gets boundingBox of backbone without joints.
	 * 
	 * @return null if no backbone
	 */
	public Real2Range getBoundingBoxWithoutJoints() {
		return (backbone == null) ? null : backbone.getBoundingBox();
	}

	public static Direction getLineDirection(SVGLine line, double eps) {
		Direction direction = null;
		Real2 coord0 = line.getXY(0);
		Real2 coord1 = line.getXY(1);
		if (Real.isEqual(coord0.getX(), coord1.getX(), eps)) {
			direction = (coord0.getY() > coord1.getY()) ? Direction.LINE_DIR_2_1 : Direction.LINE_DIR_1_2;
		} else if (Real.isEqual(coord0.getY(), coord1.getY(), eps)) {
			direction = (coord0.getX() > coord1.getX()) ? Direction.LINE_DIR_2_1 : Direction.LINE_DIR_1_2;
		}
		return direction;
	}

	public static LineOrientation getLineOrientation(SVGLine line, double eps) {
		LineOrientation lineOrientation = null;
		Real2 xy0 = line.getXY(0);
		Real2 xy1 = line.getXY(1);
		if (Real.isEqual(xy0.getX(), xy1.getX(), eps)) {
			lineOrientation = LineOrientation.VERTICAL;
		} else if (Real.isEqual(xy0.getY(), xy1.getY(), eps)) {
			lineOrientation = LineOrientation.HORIZONTAL;
		}
		return lineOrientation;
	}

	private SideOrientation getSideOrientation(Double pointCoord, RealRange range, double eps) {
		SideOrientation orientation = SideOrientation.CROSSING;
		if (Real.isEqual(pointCoord, range.getMin(), eps)) {
			orientation = SideOrientation.PLUS;
		} else if (Real.isEqual(pointCoord, range.getMax(), eps)) {
			orientation = SideOrientation.MINUS;
		}
		return orientation;
	}

	public static boolean horizontalAndVerticalLinesMeet(SVGLine line1, SVGLine line2, double eps) {
		Real2Range range1 = line1.getBoundingBox();
		Real2Range range2 = line2.getBoundingBox();
		RealRange overlapX = getRangeWithTolerance(range1.getXRange(), eps).
				intersectionWith(getRangeWithTolerance(range2.getXRange(), eps));
		RealRange overlapY = getRangeWithTolerance(range1.getYRange(), eps).
				intersectionWith(getRangeWithTolerance(range2.getYRange(), eps));
		return overlapX != null && overlapY != null;
	}

	private static RealRange getRangeWithTolerance(RealRange lineRange, double eps) {
		return new RealRange(lineRange.getMin()-eps, lineRange.getMax()+eps);
	}
	
	public LineOrientation getBackboneOrientation() {
		if (backboneOrientation == null) {
			backboneOrientation = getLineOrientation(backbone, eps);
		}
		return backboneOrientation;
	}
	
	public Direction getBackboneDirection() {
		if (backboneDirection == null) {
			backboneDirection = getLineDirection(backbone, eps);
		}
		return backboneDirection;
	}

	/** creates joints.
	 * 
	 * 
	 * @param lines
	 * @return empty list if no lines
	 */
	public List<Joint> addLines(List<SVGLine> lines) {
		jointList = new ArrayList<Joint>();
		if (lines != null) {
			LOG.trace("adding lines: "+lines.size());
			for (SVGLine line : lines) {
				Joint joint = this.addLine(line);
			}
		}
		return jointList;
	}

	public Joint addLine(SVGLine line) {
		Joint joint = this.createPerpendicularJoint(line, eps);
		if (joint != null) {
			ensureJointList();
			jointList.add(joint);
		} else {
			LOG.trace("null "+line);
		}
		return joint;
	}

	private void ensureJointList() {
		if (jointList == null) {
			jointList = new ArrayList<Joint>();
		}
	}

	public List<Joint> getJointList() {
		return jointList;
	}

	public CombType getCombType() {
		if (combType == null) {
			combType = getCombType(jointList, minJointCount, maxJointCount);
		}
		return combType;
	}

	public static CombType getCombType(List<Joint> jointList, int minJointCount, int maxJointCount) {
		CombType combType = null;
		if (jointList.size() >= minJointCount && jointList.size() <= maxJointCount) {
			SideOrientation sideOrientation0 = null;
			List<SideOrientation> sideOrientations = Joint.getSideOrientations(jointList);
			sideOrientation0 = getSideOrientation(sideOrientation0, sideOrientations);
			combType = getCombTypeFromSideOrientation(sideOrientation0, sideOrientations);
		}
		return combType;
	}

	private static SideOrientation getSideOrientation(SideOrientation sideOrientation0,
			List<SideOrientation> sideOrientations) {
		for  (SideOrientation sideOrientation : sideOrientations) {
			if (sideOrientation0 == null) {
				sideOrientation0 = sideOrientation;
			} else if (sideOrientation.equals(sideOrientation0)) {
				// no change
			} else if (sideOrientation0.equals(SideOrientation.MINUS) && 
					sideOrientation.equals(SideOrientation.PLUS)) {
				sideOrientation0 = SideOrientation.PLUS_OR_MINUS;
			} else if (sideOrientation0.equals(SideOrientation.PLUS) && 
					sideOrientation.equals(SideOrientation.MINUS)) {
				sideOrientation0 = SideOrientation.PLUS_OR_MINUS;
			} else if (sideOrientation0.equals(SideOrientation.PLUS_OR_MINUS) && 
					sideOrientation.equals(SideOrientation.MINUS)) {
				// no change
			} else if (sideOrientation0.equals(SideOrientation.PLUS_OR_MINUS) && 
					sideOrientation.equals(SideOrientation.PLUS)) {
				// no change
			} else {
				sideOrientation0 = null;
				break;
			}
		}
		return sideOrientation0;
	}

	private static CombType getCombTypeFromSideOrientation(SideOrientation sideOrientation0,
			List<SideOrientation> sideOrientations) {
		CombType combType = null;
		if (sideOrientation0 == null) {
			if (sideOrientations.size() > 0) {
				combType = CombType.MIXED;
			}
		} else if (sideOrientation0.equals(SideOrientation.PLUS_OR_MINUS)) {
			combType = CombType.PLUS_OR_MINUS;
		} else if (sideOrientation0.equals(SideOrientation.CROSSING)) {
			combType = CombType.CROSSING;
		} else if (sideOrientation0.equals(SideOrientation.MINUS)) {
			combType = CombType.MINUS;
		} else if (sideOrientation0.equals(SideOrientation.PLUS)) {
			combType = CombType.PLUS;
		}
		return combType;
	}
	
	public static List<ComplexLine> createComplexLines(List<SVGLine> lines, double eps) {
		List<ComplexLine> complexLines = new ArrayList<ComplexLine>();
		for (SVGLine line : lines) {
			ComplexLine complexLine = ComplexLine.createComplexLine(line, eps);
			complexLines.add(complexLine);
		}
		return complexLines;
	}
	
	public static List<SVGLine> createSubset(List<SVGLine> svgLines, ComplexLine.LineOrientation lineOrientation, double eps) {
		List<SVGLine> subset = new ArrayList<SVGLine>();
		if (lineOrientation != null) {
			for (SVGLine svgLine : svgLines) {
				if (LineOrientation.ZERO.equals(lineOrientation)) {
					if (svgLine.isZero(eps)) {
						subset.add(svgLine);
					}
				} else {
					LineOrientation lOrientation = ComplexLine.getLineOrientation(svgLine, eps);
					if (lineOrientation.equals(lOrientation)) {
						subset.add(svgLine);
					}
				}
			}
		}
		return subset;
	}

	public SVGLine getBackbone() {
		return backbone;
	}

//	private static Double getCoord(SVGElement element, BoxEdge boxEdge) {
//		return boxEdge.getCoord(element.getBoundingBox());
//	}

//	/** extracts those elements which are SVGLine */
//	public static List<SVGLine> extractLines(List<SVGElement> elements) {
//		List<SVGLine> svgLines = new ArrayList<SVGLine>();
//		for (SVGElement element : elements) {
//			if (element instanceof SVGLine) {
//				svgLines.add((SVGLine) element);
//			}
//		}
//		return svgLines;
//	}

	public void addLinesFrom(ElementNeighbourhoodManager enm) {
		ElementNeighbourhood neighbourhood = enm.getNeighbourhood(this.backbone);
		if (neighbourhood == null) {
			throw new RuntimeException("no neighbourhood for backbone "+this.backbone);
		}
		List<SVGElement> elements = neighbourhood.getNeighbourList();
		List<SVGLine> lines = SVGLine.extractLines(elements);
		this.addLines(lines);
	}
	
	public static List<ComplexLine> createComplexLines(List<SVGLine> lines, ElementNeighbourhoodManager enm, double eps) {
		List<ComplexLine> complexLines = ComplexLine.createComplexLines(lines, eps);
		for (ComplexLine complexLine : complexLines) {
			complexLine.addLinesFrom(enm);
		}
		return complexLines;
	}

	public List<Joint> getSortedJointList() {
		if (sortedJointList == null) {
			Joint[] joints = jointList.toArray(new Joint[0]);
			Arrays.sort(joints);
			sortedJointList = Arrays.asList(joints);
		}
		return sortedJointList;
	}

	public List<Joint> getJoints(SideOrientation orient) {
		if (orient == null) {
			throw new RuntimeException("null orientation");
		}
		ensureJointList();
		List<Joint> jointsWithType = new ArrayList<Joint>();
		if (jointList != null) {
			for (Joint joint : jointList) {
				SideOrientation jointOrient= joint.getSideOrientation();
				if (orient.equals(jointOrient)) {
					jointsWithType.add(joint);
				}
			}
		}
		return jointsWithType;
	}

	public String getId() {
		return "B"+backbone.getId();
	}

	public static List<ComplexLine> createComplexLines(List<SVGLine> lines, List<SVGLine> otherLines, double eps) {
		List<ComplexLine> complexLineList = new ArrayList<ComplexLine>();
		for (SVGLine line : lines) {
			ComplexLine complexLine = ComplexLine.createComplexLine(line, eps);
			if (complexLine != null) {
				complexLine.addLines(otherLines);
				complexLineList.add(complexLine);
			}
		}
		return complexLineList;
	}

	static List<SVGLine> extractLineList(List<ComplexLine> complexLines) {
		List<SVGLine> lines = new ArrayList<SVGLine>(); 
		for (ComplexLine complexLine : complexLines) {
			lines.add(complexLine.getBackbone());
		}
		return lines;
	}

	/** creates subset and removes from svgLines
	 * 
	 * @param svgLines
	 * @param lineOrientation
	 * @param eps
	 * @return
	 */
	public static List<SVGLine> createSubsetAndRemove(List<SVGLine> svgLines, 
			ComplexLine.LineOrientation lineOrientation, double eps) {
		List<SVGLine> subset = ComplexLine.createSubset(svgLines, lineOrientation, eps);
		svgLines.removeAll(subset);
		return subset;
	}

	/** 
	 * returns lines with branch at either end, both or none
	 * @param complexLines
	 * @param minus 
	 * @param plus
	 * @return
	 */
	public static List<ComplexLine> extractLinesWithBranchAtEnd(List<ComplexLine> complexLines, List<SideOrientation> minusPlus) {
		List<ComplexLine> lines = new ArrayList<ComplexLine>();
		for (ComplexLine complexLine : complexLines) {
			boolean requiresMinus = minusPlus.contains(SideOrientation.MINUS);
			Joint minusJoint = complexLine.getJointAtEnd(SideOrientation.MINUS);
			if ((minusJoint == null) == requiresMinus) continue;
			boolean requiresPlus = minusPlus.contains(SideOrientation.PLUS);
			Joint plusJoint= complexLine.getJointAtEnd(SideOrientation.PLUS);
			if ((plusJoint == null) == requiresPlus) continue;
			lines.add(complexLine);
		}
		return lines;
	}

	public Joint getJointAtEnd(SideOrientation sideOrientation) {
		Joint joint = null;
		Real2 corner = getCornerAt(sideOrientation);
		if (corner != null) {
			joint = getJointAtEnd(corner, eps);
		}
		return joint;
	}

	public Real2 getCornerAt(SideOrientation sideOrientation) {
		Real2[] corners = backbone.getBoundingBox().getLLURCorners();
		Real2 corner = null;
		if (SideOrientation.MINUS.equals(sideOrientation)) {
			corner = corners[0];
		} else if (SideOrientation.PLUS.equals(sideOrientation)) {
			corner = corners[1];
		}
		return corner;
	}

	private Joint getJointAtEnd(Real2 corner, double eps) {
		Joint joint = null;
		List<Joint> jointList = this.getJointList();
		for (Joint jointx : jointList) {
			if (jointx.point.isEqualTo(corner, eps)) {
				joint = jointx;
				break;
			}
		}
		return joint;
	}

	public static List<SVGLine> createZeroLengthSubsetAndRemove(List<SVGLine> svgLines, double eps) {
		List<SVGLine> subset = ComplexLine.createZeroLengthSubset(svgLines, eps);
		svgLines.removeAll(subset);
		return subset;
	}

	public static List<SVGLine> createZeroLengthSubset(List<SVGLine> svgLines, double eps) {
		List<SVGLine> subset = new ArrayList<SVGLine>();
		for (SVGLine svgLine : svgLines) {
			if (svgLine.isZero(eps)) {
				subset.add(svgLine);
			}
		}
		return subset;
	}
	
	public static Double calculateInterJointSpacing(List<Joint> jointList, double jointEps) {
		Double spacing = null;
		if (jointList != null && jointList.size() > 1) {
			Joint lastJoint = jointList.get(0);
			for (int i = 1; i < jointList.size(); i++) {
				Joint joint = jointList.get(i);
				Double length = joint.getPoint().getDistance(lastJoint.getPoint());
				if (length < jointEps) {
					// coincident joint???
				} else if (spacing == null) {
					spacing = length;
				} else {
					if (Math.abs(spacing - length) > jointEps) {
						spacing = null;
						break;
					}
				}
				lastJoint = joint;
			}
		}
		return spacing;
	}

	public void debug(String string) {
		LOG.debug("===");
		LOG.debug("jointSpacing: "+calculateInterJointSpacing(jointList, JOINT_EPS));
		for (Joint joint : jointList) {
			LOG.debug("J "+joint);
		}
	}

	/** detaches all SVG components.
	 * 
	 * To show effect of removal (e.g. of axes) on diagram
	 */
	public void detach() {
		for (Joint joint : jointList){
			joint.detach();
		}
		backbone.detach();
	}

	/** detaches all ComplexLines in list
	 * 
	 * @param complexLineList
	 */
	public static void detach(List<ComplexLine> complexLineList) {
		if (complexLineList != null) {
			for (ComplexLine complexLine : complexLineList) {
				complexLine.detach();
			}
		}
	}

}
