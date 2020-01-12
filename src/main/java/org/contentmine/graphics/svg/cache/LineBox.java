package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
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
	private static final Logger LOG = Logger.getLogger(LineBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

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
	
	public void addHorizontalLine(SVGLine horizontalLine) {
		addToBoundingBox(horizontalLine);
		if (!horizontalLineList.contains(horizontalLine)) {
			horizontalLineList.add(horizontalLine);
		}
		horizontalLine.detach();
		this.appendChild(horizontalLine);
	}

	private void addToBoundingBox(SVGLine line) {
		Real2Range box = new Real2Range(line.getBoundingBox());
		if (boundingBox == null) {
			boundingBox = box;
		} else {
			boundingBox = boundingBox.plusEquals(box);
		}
	}
	
	public void addVerticalLine(SVGLine verticalLine) {
		addToBoundingBox(verticalLine);
//		verticalLineList.add(verticalLine);
//		if (!verticalLineList.contains(verticalLine)) {
//			verticalLineList.add(verticalLine);
//		}
		verticalLine.detach();
		this.appendChild(verticalLine);
	}
	
//	public List<SVGLine> getHorizontalLineList() {
//		return horizontalLineList;
//	}
//
//	public List<SVGLine> getVerticalLineList() {
//		return verticalLineList;
//	}
//
	public Real2Range getBoundingBox() {
		return boundingBox;
	}

	public SVGElement getSVGElement() {
//		SVGG g = new SVGG();
		for (SVGLine line : horizontalLineList) {
			this.appendChild(((SVGLine)line.copy()).setStroke("black").setStrokeWidth(1.0));
			line.detach();
		}
		
		for (SVGLine line : verticalLineList) {
			this.appendChild(((SVGLine)line.copy()).setStroke("black").setStrokeWidth(1.0));
			line.detach();
		}
		this.appendChild(SVGRect.createFromReal2Range(boundingBox).setStroke("brown").setStrokeWidth(1.5).setFill("none"));
		return this;
	}

	public void merge(LineBox lineBox) {
		addList(this.horizontalLineList, lineBox.horizontalLineList);
		addList(this.verticalLineList, lineBox.verticalLineList);
	}

	private void addList(List<SVGLine> lineList1, List<SVGLine> lineList2) {
		for (SVGLine line : lineList2) {
			if (!lineList1.contains(line) || true) {
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


}
