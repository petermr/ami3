package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;

/** box containing intersecting horizontal and vertical lines.
 * 
 * creates a bounding box which can intersect with other lines and boxes.
 * two boxes can merge.
 * 
 * 
 * 
 * @author pm286
 *
 */
public class LineBox extends SVGG {
	private static final Logger LOG = LogManager.getLogger(LineBox.class);
private List<SVGLine> horizontalLineList;
	private List<SVGLine> verticalLineList;
	private Real2Range boundingBox;
	private double eps = 0.0001;
	
	public LineBox() {
		super();
		this.addSVGClassName("linebox");
		init();
	}
	
	protected void init() {
		super.init();
		horizontalLineList = new ArrayList<>();
		verticalLineList = new ArrayList<>();
	}
	
	public boolean addHorizontalLine(SVGLine horizontalLine) {
		return addLine(horizontalLineList, horizontalLine);
	}

	public boolean addVerticalLine(SVGLine verticalLine) {
		return addLine(verticalLineList, verticalLine);
	}

	private boolean addLine(List<SVGLine> lineList,  SVGLine line) {
		addToBoundingBox(line);
		boolean add = false;
		if (!lineList.contains(line)) {
			lineList.add(line);
			line.detach();
			this.appendChild(line);
			add = true;
		}
		return add;
	}
	
	private void addToBoundingBox(SVGLine line) {
		Real2Range box = new Real2Range(line.getBoundingBox());
		if (boundingBox == null) {
			boundingBox = box;
		} else {
			boundingBox = boundingBox.plusEquals(box);
		}
	}
	
	public List<SVGLine> getHorizontalLineList() {
		return horizontalLineList;
	}

	public List<SVGLine> getVerticalLineList() {
		return verticalLineList;
	}

	public Real2Range getBoundingBox() {
		return boundingBox;
	}

	public SVGElement getSVGElement() {
		SVGG g = new SVGG();
//		for (SVGLine line : horizontalLineList) {
//			g.appendChild(((SVGLine)line.copy()).setStroke("black").setStrokeWidth(1.0));
//			line.detach();
//		}
//		
//		for (SVGLine line : verticalLineList) {
//			g.appendChild(((SVGLine)line.copy()).setStroke("black").setStrokeWidth(1.0));
//			line.detach();
//		}
		g.appendChild(this.copy());
		g.appendChild(SVGRect.createFromReal2Range(boundingBox).setStroke("brown").setStrokeWidth(1.5).setFill("none"));
		return g;
	}

	public void merge(LineBox lineBox) {
		addList(this.horizontalLineList, lineBox.horizontalLineList);
		addList(this.verticalLineList, lineBox.verticalLineList);
	}

	private void addList(List<SVGLine> lineList1, List<SVGLine> lineList2) {
		for (SVGLine line : lineList2) {
			if (!lineList1.contains(line)) {
				lineList1.add(line);
				this.boundingBox = this.boundingBox.plus(line.getBoundingBox());
			} else {
				LOG.debug("duplicate "+line);
			}
		}
	}

	boolean intersectsLine(SVGLine unusedLine) {
		return getBoundingBox().intersects(unusedLine.getBoundingBox());
	}

	public void addLine(SVGLine line) {
		if (line.isHorizontal(eps )) {
			addHorizontalLine(line);
		} else if (line.isVertical(eps)) {
			addVerticalLine(line);
		} else {
			LOG.debug("Cannot add non-orthog line "+line);
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("bbox "+boundingBox+"; ");
		sb.append("hlines "+((horizontalLineList == null) ? 0 : horizontalLineList.size())+"; ");
		sb.append("vlines "+((verticalLineList == null) ? 0 : verticalLineList.size())+"; ");
		return sb.toString();
		
	}


}
