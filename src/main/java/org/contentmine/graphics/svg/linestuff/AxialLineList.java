package org.contentmine.graphics.svg.linestuff;

import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.SVGLineList;

/** lines parallel to axes
 * 
 * @author pm286
 *
 */
public class AxialLineList extends SVGLineList {
	private static final Logger LOG = Logger.getLogger(AxialLineList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private LineDirection direction;
	private double eps = SVGLine.EPS;

	public AxialLineList(LineDirection direction) {
		this.direction = direction;
	}
	
	public void sort() {
		Comparator<SVGLine> comparator = 
			direction.isHorizontal() ? new HorizontalLineComparator() : new VerticalLineComparator();
		if (lineList != null) {
			Collections.sort(lineList, comparator);
		}
	}
	
	public boolean add(SVGLine line) {
		ensureLines();
		if (direction.isHorizontal() && line.isHorizontal(eps)) {
			return lineList.add(line);
		} else if (direction.isVertical() && line.isVertical(eps)) {
			return lineList.add(line);
		} else {
			return false;
		}
	}
	
	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}


	
}

