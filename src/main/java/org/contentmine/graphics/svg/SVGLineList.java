package org.contentmine.graphics.svg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.linestuff.LineMerger;
import org.contentmine.graphics.svg.linestuff.LineMerger.MergeMethod;

public class SVGLineList extends SVGG implements Iterable<SVGLine> {
	
	public enum SiblingType {
		HORIZONTAL_SIBLINGS, // with common Y
		VERTICAL_SIBLINGS, // with common X
	}
	
	private static Logger LOG = Logger.getLogger(SVGLineList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double siblingEps = 0.2; // difference in common coordinate
	protected List<SVGLine> lineList;
	private SiblingType type;

	public SVGLineList() {
		super();
	}
	
	public SVGLineList(List<SVGLine> lines) {
		this.lineList = new ArrayList<SVGLine>(lines);
	}

	/** adds all SVGLines in collection to new SVGLineList
	 * 
	 * @param elements List which potentially contains SVGLine elements
	 * @return empty list if no lines
	 */
	public static SVGLineList createLineList(List<SVGElement> elements) {
		SVGLineList lineList = new SVGLineList();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGLine) {
				lineList.add((SVGLine) element);
			}
		}
		return lineList;
	}

	public void setType(SiblingType type) {
		ensureLines();
		if (checkLines(type)) {
			this.type = type;
		}
	}
	
	/** maybe create a SiblingLines class.
	 * 
	 * @param type
	 * @return
	 */
	public boolean checkLines(SiblingType type) {
		if (lineList == null || lineList.size() == 0) return false;
		Double commonCoord = null;
		for (SVGLine line : lineList) {
			Double coord = null;
			if (type.equals(SiblingType.HORIZONTAL_SIBLINGS)) {
				if (!line.isHorizontal(siblingEps)) {
					throw new RuntimeException("Lines do not obey type: "+type);
				}
				coord = line.getMidPoint().getY();
			} else if (type.equals(SiblingType.VERTICAL_SIBLINGS)) {
				if (! line.isVertical(siblingEps)) {
					throw new RuntimeException("Lines do not obey type: "+type);
				}
			}
			coord = line.getMidPoint().getY();
			if (commonCoord == null) {
				commonCoord = coord;
			} else if (!Real.isEqual(commonCoord, coord, siblingEps)) {
				return false;
			}
		}
		return true;
	}

	public List<SVGLine> getLineList() {
		ensureLines();
		return lineList;
	}

	public Iterator<SVGLine> iterator() {
		ensureLines();
		return lineList.iterator();
	}

	protected void ensureLines() {
		if (lineList == null) {
			lineList = new ArrayList<SVGLine>();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ensureLines();
		sb.append(lineList.toString());
		return sb.toString();
	}

	public SVGLine get(int i) {
		ensureLines();
		return (i < 0 || i >= lineList.size()) ? null : lineList.get(i);
	}

	public int size() {
		ensureLines();
		return lineList.size();
	}

	public SVGElement remove(int i) {
		ensureLines();
		if (get(i) != null) {
			return lineList.remove(i);
		}
		return null;
	}
	
	public boolean add(SVGLine line) {
		ensureLines();
		return lineList.add(line);
	}

	public Real2Range getBoundingBox() {
		Real2Range bbox = null;
		for (SVGLine line : lineList) {
			Real2Range bbox0 = line.getBoundingBox();
			if (bbox == null) {
				bbox = bbox0;
			} else {
				bbox = bbox.plus(bbox0);
			}
		}
		return bbox;
	}
	
	public Real2Array createMidPoints() {
		Real2Array points = new Real2Array();
		for (SVGLine line : this) {
			points.addElement(line.getMidPoint());
		}
		return points;
	}

	public void addAll(List<SVGLine> lines) {
		ensureLines();
		lineList.addAll(lines);
	}

	public void addAll(SVGLineList lines) {
		ensureLines();
		lineList.addAll(lines.getLineList());
	}
	
	public SVGG createSVGElement() {
		SVGG g = new SVGG();
		for (SVGLine line : this) {
			SVGLine line1 = new SVGLine(line);
			line1.setStrokeWidth(1.0);
			line1.setStroke("black");
			g.appendChild(line1);
		}
		return g;
	}
	
	/** merges lines (H or V).
	 * See LineMerger
	 * modifies 'this'
	 * 
	 * @param eps
	 * @param mergeMethod
	 * @return 
	 */
	public List<SVGLine> mergeLines(double eps, MergeMethod mergeMethod) {
		lineList = LineMerger.mergeLines(lineList, eps, mergeMethod);
		return lineList;
	}

	public RealArray getLowXArray() {
		return getLowArray(Axis2.X);
	}
	public RealArray getHighXArray() {
		return getHighArray(Axis2.X);
	}
	public RealArray getLowYArray() {
		return getLowArray(Axis2.Y);
	}
	public RealArray getHighYArray() {
		return getHighArray(Axis2.Y);
	}

	public RealArray getLowArray(Axis2 axis) {
		ensureLines();
		RealArray array = new RealArray();
		for (SVGLine line : lineList) {
			double coord0 = Axis2.X.equals(axis) ? line.getXY(0).getX() : line.getXY(0).getY();
			double coord1 = Axis2.X.equals(axis) ? line.getXY(1).getX() : line.getXY(1).getY();
			array.addElement(Math.min(coord0, coord1));
		}
		return array;
	}
	public RealArray getHighArray(Axis2 axis) {
		ensureLines();
		RealArray array = new RealArray();
		for (SVGLine line : lineList) {
			double coord0 = Axis2.X.equals(axis) ? line.getXY(0).getX() : line.getXY(0).getY();
			double coord1 = Axis2.X.equals(axis) ? line.getXY(1).getX() : line.getXY(1).getY();
			array.addElement(Math.max(coord0, coord1));
		}
		return array;
	}

	public List<String> writeLineEndsAsCSVRow() {
		RealArray yArray = getLowArray(Axis2.Y).format(2);
		RealArray lowXArray = getLowArray(Axis2.X).format(2);
		RealArray highXArray = getHighArray(Axis2.X).format(2);
		List<String> rowList = new ArrayList<String>();
		rowList.add("row, y, low, high\n");
		for (int i = 0; i < lowXArray.size(); i++) {
			String row = (i+1)+","+yArray.elementAt(i)+","+lowXArray.elementAt(i)+","+highXArray.elementAt(i)+"\n";
			rowList.add(row);
		}
		return rowList;
	}

}
