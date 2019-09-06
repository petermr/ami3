package org.contentmine.svg2xml.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;


@Deprecated // moved to SVGß
public class BoundingBoxManager {

	private final static Logger LOG = Logger.getLogger(BoundingBoxManager.class);
	
	public enum BoxEdge {
		XMIN,
		XMAX,
		YMIN,
		YMAX;
		/** gets appropriate coordinate of box */
		public Double getCoord(Real2Range r2r) {
			Double coord = null;
			if (r2r == null) {
				return coord;
			} else if (XMIN.equals(this)) {
				RealRange xRange = r2r.getXRange();
				coord = (xRange == null) ? null : xRange.getMin();
			} else if (XMAX.equals(this)) {
				RealRange xRange = r2r.getXRange();
				coord = (xRange == null) ? null : xRange.getMax();
			} else if (YMIN.equals(this)) {
				RealRange yRange = r2r.getYRange();
				coord = (yRange == null) ? null : yRange.getMin();
			} else if (YMAX.equals(this)) {
				RealRange yRange = r2r.getYRange();
				coord = (yRange == null) ? null : yRange.getMax();
			}
			return coord;
		}

		/** return +1 for MAX and -1 for MIN */
		public Integer getSign() {
			Integer sign = null;
			if (XMIN.equals(this) || YMIN.equals(this)) {
				sign = -1;
			} else if (XMAX.equals(this) || YMAX.equals(this)) {
				sign = 1;
			}
			return sign;
		}
		
		/** return HORIZONTAL fo X and VERTICAL for Y */
		public LineOrientation getOrientation() {
			ComplexLine.LineOrientation orientation = null;
			if (XMIN.equals(this) || XMAX.equals(this)) {
				orientation = LineOrientation.HORIZONTAL;
			} else if (YMIN.equals(this) || YMAX.equals(this)) {
				orientation = LineOrientation.VERTICAL;
			}
			return orientation;
		}
		
		public BoxEdge getOppositeEdge() {
			BoxEdge otherEdge = null;
			if (XMIN.equals(this)) {
				otherEdge = XMAX;
			} else if (XMAX.equals(this)) {
				otherEdge = XMIN;
			} else if (YMIN.equals(this)) {
				otherEdge = YMAX;
			} else if (YMAX.equals(this)) {
				otherEdge = YMIN;
			}
			return otherEdge;
		}
		
		public Double getCoordAtOppositeEndOfBox(Real2Range box) {
			BoxEdge otherEdge = getOppositeEdge();
			return otherEdge.getCoord(box);
		}

	}
	
	private List<Real2Range> bboxList;
	private Real2Range totalBox = null;
	private List<SVGElement> elementList;


	public BoundingBoxManager() {
	}

	public static List<Real2Range> createBBoxList(List<? extends SVGElement> elementList) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		if (elementList != null) {
			for (SVGElement element : elementList) {
				bboxList.add(element.getBoundingBox());
			}
		}
		return bboxList;
	}
	
	public static List<Real2Range> createExtendedBBoxList(List<? extends SVGElement> elementList, double eps) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		if (elementList != null) {
			for (SVGElement element : elementList) {
				bboxList.add(createExtendedBox(element, eps));
			}
		}
		return bboxList;
	}

	/** adds extension to all boxes
	 * 
	 * @param elementList
	 * @param xExtension x0, y0 are increased by extension.min() so should be negative (for expansion)
	 * @param yExtension
	 * @return
	 */
	public static List<Real2Range> createExtendedBBoxList(List<? extends SVGElement> elementList, 
			RealRange xExtension, RealRange yExtension) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		if (elementList != null) {
			for (SVGElement element : elementList) {
				bboxList.add(createExtendedBox(element, xExtension, yExtension));
			}
		}
		return bboxList;
	}
	
	/** adds extension to all boxes
	 * 
	 * @param elementList
	 * @param xExtension x0, y0 are increased by extension.min() so should be negative (for expansion)
	 * @param yExtension
	 * @return
	 */
	public static List<Real2Range> createExtendedBBoxes(List<Real2Range> bList, 
			RealRange xExtension, RealRange yExtension) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		if (bList != null) {
			for (Real2Range bbox : bList) {
				bboxList.add(createExtendedBox(bbox, xExtension, yExtension));
			}
		}
		return bboxList;
	}
	
	public void setBBoxList(List<Real2Range> bboxList) {
		this.bboxList = bboxList;
	}
	
	public void add(Real2Range r2r) {
		ensureBoundingBoxList();
		bboxList.add(r2r);
	}
	
	private void ensureBoundingBoxList() {
		if (bboxList == null) {
			bboxList = new ArrayList<Real2Range>();
		}
	}

	public List<Real2Range> getBBoxList() {
		return bboxList;
	}
	
	/** main engine for sorting
	 * 
	 * @param edge
	 * @return
	 */
	private List<Real2Range> getBoxesSortedByEdge(BoxEdge edge) {
		if (bboxList == null) {
			LOG.trace("Null bboxList in BBManager");
			return null;
		}
		List<Real2Range> sortedList = new ArrayList<Real2Range>();
		Map<Integer, List<Real2Range>> bboxByXmin = new HashMap<Integer, List<Real2Range>>();
		for (Real2Range bbox : bboxList) {
			if (bbox != null) {
				Double dCoord = edge.getCoord(bbox); 
				if (dCoord != null) {
					Integer coord = (int) Math.round(dCoord); 
					List<Real2Range> bList = bboxByXmin.get(coord);
					if (bList == null) {
						bList = new ArrayList<Real2Range>();
						bboxByXmin.put(coord, bList);
					}
					bList.add(bbox);
				}
			} else {
//				System.err.println("null bbox");
			}
		}
		Integer[] ii = bboxByXmin.keySet().toArray(new Integer[bboxByXmin.size()]);
		Arrays.sort(ii);
		for (int i : ii) {
			List<Real2Range> bList = bboxByXmin.get(i);
			for (Real2Range bbox : bList) {
				sortedList.add(bbox);
			}
		}
		return sortedList;
	}

	/* sorts elements by the boxEdge
	 * maybe combine this with sorting boxes?
	 * 
	 */
	public static List<SVGElement> getElementsSortedByEdge(List<? extends SVGElement> elementList, BoxEdge edge) {
		List<SVGElement> sortedList = new ArrayList<SVGElement>();
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		Map<Real2Range, SVGElement> elementByBBox = new HashMap<Real2Range, SVGElement>();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			elementByBBox.put(bbox, element);
			bboxList.add(bbox);
		}
		BoundingBoxManager boundingBoxManager = new BoundingBoxManager();
		boundingBoxManager.setBBoxList(bboxList);
		List<Real2Range> bboxList1 = boundingBoxManager.getBoxesSortedByEdge(edge);
		for (Real2Range bbox : bboxList1) {
			SVGElement element1 = elementByBBox.get(bbox); 
			if (element1 == null) {
				throw new RuntimeException("rewrite!!");
			}
			sortedList.add(element1);
		}
		return sortedList;
	}

	/**
	 * return a list of empty boxes (at present spanning whole page in other direction)
	 * These are the whitespace "corridors" that break up the page
	 * 
	 * @param edge
	 * @return
	 */
	public List<Real2Range> createEmptyBoxList(BoxEdge edge) {
		List<Real2Range> sortedBoxes = getBoxesSortedByEdge(edge);
		if (sortedBoxes == null) {
			return null;
		}
		RealRange otherEdge = getRangeForOtherEdge(edge);
		List<Real2Range> emptyBoxList = new ArrayList<Real2Range>();
		Double frontOfLastBox = 0.;
		Iterator<Real2Range> boxIterator = sortedBoxes.iterator();
		while (boxIterator.hasNext()) {
			Real2Range box = boxIterator.next();
			RealRange xrange = box.getXRange();
			Double rearOfCurrentBox = edge.getCoord(box);
			Double frontOfCurrentBox = edge.getCoordAtOppositeEndOfBox(box);
			if (frontOfCurrentBox < frontOfLastBox) {
				// box is behind the dark front
			} else if (rearOfCurrentBox <= frontOfLastBox) {
				// move dark limit up if possible
				frontOfLastBox = Math.max(frontOfCurrentBox, frontOfLastBox);
			} else {
				// found some whitespace, create a box
				Real2Range emptyBox = createBox(frontOfLastBox, rearOfCurrentBox, edge, otherEdge);
				emptyBoxList.add(emptyBox);
				frontOfLastBox = frontOfCurrentBox;
			}
		}
		return emptyBoxList;
	}

	private Real2Range createBox(Double coord0, Double coord1, BoxEdge edge, RealRange otherRange) {
		Real2Range box = null;
		RealRange range = new RealRange(coord0, coord1);
		if (BoxEdge.XMAX.equals(edge) || BoxEdge.XMIN.equals(edge)) {
			box = new Real2Range(range, otherRange);
		} else if (BoxEdge.YMAX.equals(edge) || BoxEdge.YMIN.equals(edge)) {
			box = new Real2Range(otherRange, range);
		}
		return box;
	}

	private RealRange getRangeForOtherEdge(BoxEdge edge) {
		RealRange range = null;
		getTotalBox();
		if (totalBox != null) {
			if (BoxEdge.XMIN.equals(edge) || BoxEdge.XMAX.equals(edge)) {
				range = totalBox.getYRange();
			} else if (BoxEdge.YMIN.equals(edge) || BoxEdge.YMAX.equals(edge)) {
				range = totalBox.getXRange();
			}
		}
		return range;
	}

	public Real2Range getTotalBox() {
		if (totalBox == null) {
			if (bboxList != null) {
				for (Real2Range bbox : bboxList) {
					if (bbox != null) {
						if (totalBox == null) {
							totalBox = new Real2Range(bbox);
						} else {
							totalBox = totalBox.plusEquals(bbox);
						}
					}
				}
			}
		}
		return totalBox;
	}

	public int size() {
		return bboxList.size();
	}

	/** creates a box larger by eps on each corner.
	 * so size is incresed by 2*eps on each side
	 * equivalent to createExtendedBox(SVGElement elem, new Real2(-eps, -eps), new Real2(eps, eps))
	 * 
	 * @param elem
	 * @param eps
	 * @return
	 */
	public static Real2Range createExtendedBox(SVGElement elem, double eps) {
		Real2Range bbox = elem.getBoundingBox();
		if (bbox != null) {
			Real2[] corners = bbox.getLLURCorners();
			Real2Range extendedBBox = new Real2Range(
					new RealRange(corners[0].getX()-eps, corners[1].getX()+eps),
					new RealRange(corners[0].getY()-eps, corners[1].getY()+eps));
			return extendedBBox;
		}
		return null;
	}

	/** creates a box larger minExtension for x0, y0 and maxExtension on x1,y1.
	 * note to expand the box symmetrically minExtension will have 2 negative values
	 * @param elem
	 * @param minExtension
	 * @param maxExtension
	 * @return
	 */
	public static Real2Range createExtendedBox(SVGElement elem, RealRange xExtension, RealRange yExtension) {
		Real2Range bbox = elem.getBoundingBox();
		return createExtendedBox(bbox, xExtension, yExtension);
	}

	/** creates a box larger minExtension for x0, y0 and maxExtension on x1,y1.
	 * note to expand the box symmetrically minExtension will have 2 negative values
	 * @param elem
	 * @param minExtension
	 * @param maxExtension
	 * @return
	 */
	public static Real2Range createExtendedBox(Real2Range bbox, RealRange xExtension, RealRange yExtension) {
		Real2[] corners = bbox.getLLURCorners();
		Real2Range extendedBBox = new Real2Range(
			new RealRange(corners[0].getX()+xExtension.getMin(), corners[1].getX()+xExtension.getMax()),
			new RealRange(corners[0].getY()+yExtension.getMin(), corners[1].getY()+yExtension.getMax()));
		return extendedBBox;
	}

	public void addBoxesFromElementList(List<SVGElement> elementList) {
		List<Real2Range> bboxList = BoundingBoxManager.createBBoxList(elementList);
		this.setBBoxList(bboxList);
	}

}
