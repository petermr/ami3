package org.contentmine.graphics.svg;

import java.util.Comparator;

import org.contentmine.eucl.euclid.Real2Range;

/** compares a given edge coordinate of 2 rects.
 * 
 * @author pm286
 *
 */
public class RectComparator implements Comparator<SVGRect> {

	public enum RectEdge {
		LEFT_EDGE,
		RIGHT_EDGE,
		TOP_EDGE,
		BOTTOM_EDGE
	}

	private RectEdge edge;
	
	public RectComparator(RectEdge edge) {
		this.edge = edge;
	}

	/** 
	 * if 01 or o2 is null throws NPE
	 * if the bounding boxes are null, treats the coordinates as ,
	 * i.e. returns -1 for o1 and +1 for o2
	 */
	public int compare(SVGRect o1, SVGRect o2) {
		if (o1 == null) {
			throw new NullPointerException("arg 1 is null");
		}
		if (o2 == null) {
			throw new NullPointerException("arg 2 is null");
		}
		Real2Range bbox1 = o1.getBoundingBox();
		Real2Range bbox2 = o2.getBoundingBox();
		if (bbox1 == null) {
			return -1;
		}
		if (bbox2 == null) {
			return 1;
		}
		
		if (RectEdge.LEFT_EDGE.equals(edge)) {
			return  (int) (bbox1.getXMin() - bbox2.getXMin());
		}
		if (RectEdge.RIGHT_EDGE.equals(edge)) {
			return  (int) (bbox1.getXMax() - bbox2.getXMax());
		}
		if (RectEdge.TOP_EDGE.equals(edge)) {
			return  (int) (bbox1.getYMax() - bbox2.getYMax());
		}
		if (RectEdge.BOTTOM_EDGE.equals(edge)) {
			return  (int) (bbox1.getYMin() - bbox2.getYMin());
		} else {
			throw new RuntimeException("bad edge: "+edge);
		}
	}

}
