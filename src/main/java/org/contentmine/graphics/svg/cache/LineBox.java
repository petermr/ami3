package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;

/** box containing intersecting horizontal and vertical lines.
 * 
 * creates a bounding box which can intersect with other lines and boxes.
 * two boxes can merge.
 * 
 * @author pm286
 *
 */
public class LineBox {
	private static final Logger LOG = Logger.getLogger(LineBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGLine> horizontalLineList;
	private List<SVGLine> verticalLineList;
	private Real2Range boundingBox;
	
	public LineBox() {
		init();
	}
	
	private void init() {
		horizontalLineList = new ArrayList<>();
		verticalLineList = new ArrayList<>();
	}
	
	public void addHorizontalLine(SVGLine horizontalLine) {
		addToBoundingBox(horizontalLine);
		if (!horizontalLineList.contains(horizontalLine)) {
			horizontalLineList.add(horizontalLine);
		}
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
		verticalLineList.add(verticalLine);
		if (!verticalLineList.contains(verticalLine)) {
			verticalLineList.add(verticalLine);
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
		for (SVGLine line : horizontalLineList) {
			g.appendChild(line.setStroke("black").setStrokeWidth(1.0).copy());
		}
		for (SVGLine line : verticalLineList) {
			g.appendChild(line.setStroke("black").setStrokeWidth(1.0).copy());
		}
		return g;
	}


}
