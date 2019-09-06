package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.Direction;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.linestuff.ComplexLine.SideOrientation;

public class Joint implements Comparable<Joint> {
	
	protected Real2 point;
	private SideOrientation sideOrientation;
	private Direction lineDirection; 
	private LineOrientation backboneOrientation;
	private SVGLine backbone;
	private SVGLine line; 
	

	public Joint(Real2 point, SVGLine backbone, SVGLine line, SideOrientation sideOrientation, double eps) {
		this.point = point;
		this.backbone = backbone;
		this.backboneOrientation = ComplexLine.getLineOrientation(backbone, eps);
		this.line = line;
		this.lineDirection = ComplexLine.getLineDirection(line, eps);
		this.sideOrientation = sideOrientation;
	}

	public int compareTo(Joint joint) {
		int result = 0;
		if (joint != null && point != null && joint.point != null) {
			double thisCoord = LineOrientation.HORIZONTAL.equals(backboneOrientation) ? point.getX() : point.getY();
			double jointCoord = LineOrientation.HORIZONTAL.equals(backboneOrientation) ? joint.point.getX() : joint.point.getY();
			if (thisCoord > jointCoord) {
				result = 1;
			} else if (result < jointCoord) {
				result = -1;
			}
		}
		return result;
	}

	public Real2 getPoint() {
		return point;
	}

	public SideOrientation getSideOrientation() {
		return sideOrientation;
	}
	
	public Direction getLineDirection() {
		return lineDirection;
	}
	
	public static List<ComplexLine.SideOrientation> getSideOrientations(List<Joint> jointList) {
		List<ComplexLine.SideOrientation> sideOrientationList = new ArrayList<ComplexLine.SideOrientation>();
		for (Joint joint :jointList) {
			sideOrientationList.add(joint.getSideOrientation());
		}
		return sideOrientationList;
	}
	
	public static List<ComplexLine.Direction> getDirections(List<Joint> jointList) {
		List<ComplexLine.Direction> DirectionList = new ArrayList<ComplexLine.Direction>();
		for (Joint joint :jointList) {
			DirectionList.add(joint.getLineDirection());
		}
		return DirectionList;
	}

	public SVGLine getLine() {
		return line;
	}

	public boolean isAtEndOfBackbone(double eps) {
		Real2[] corners = backbone.getBoundingBox().getLLURCorners();
		boolean result0 = point.isEqualTo(corners[0], eps);
		boolean result1 = point.isEqualTo(corners[1], eps);
		return result0 || result1;
	}
	
	public Double getLength() {
		Double length = null;
		if (line != null) {
			length = line.getEuclidLine().getLength();
		}
		return length;
	}
	
	public String toString() {
		String s = "";
		s += backbone;
		if (line != null) { 
			s += /*line.toXML()+*/" "+line.getEuclidLine().getLength();
		}
		s += " "+point;
		s += "\n";
		return s;
	}

	/** detaches line SVG components.
	 * 
	 * To show effect of removal (e.g. of axes) on diagram. Backbone will not
	 * be removed.
	 * 
	 * 
	 */
	public void detach() {
		line.detach();
	}

	/** gets bounding box of line in joint.
	 * 
	 * @return null if no line
	 */
	public Real2Range getBoundingBox() {
		return (line == null) ? null : line.getBoundingBox();
	}


}
