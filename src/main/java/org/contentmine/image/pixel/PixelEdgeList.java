package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;

public class PixelEdgeList implements Iterable<PixelEdge> {

	private List<PixelEdge> list;
	private SVGG svgg;

	public PixelEdgeList() {
		ensureList();
	}
	
	public Iterator<PixelEdge> iterator() {
		ensureList();
		return list.iterator();
	}

	private void ensureList() {
		if (list == null) {
			list = new ArrayList<PixelEdge>();
		}
	}

	public void add(PixelEdge edge) {
		ensureList();
		list.add(edge);
	}

	public int size() {
		ensureList();
		return list.size();
	}

	public List<PixelEdge> getList() {
		ensureList();
		return list;
	}

	/** gets edges regardless of node order.
	 * 
	 * @param pixel0
	 * @param pixel1
	 * @return edgeList
	 */
	public PixelEdgeList getEdges(Pixel pixel0, Pixel pixel1) {
		PixelEdgeList edgeList = new PixelEdgeList(); 
		for (PixelEdge edge : this) {
			Pixel nodePixel0 = edge.getPixelNode(0).getCentrePixel();
			Pixel nodePixel1 = edge.getPixelNode(1).getCentrePixel();
			if ( (nodePixel0.equals(pixel0) && nodePixel1.equals(pixel1)) ||
			     (nodePixel0.equals(pixel1) && nodePixel1.equals(pixel0)) ) {
				edgeList.add(edge);
			}
		}
		return edgeList;
	}

	public PixelEdge get(int i) {
		ensureList();
		return i >= list.size() ? null : list.get(i);
	}

	// {(1,2)(0,3)(1,4)(2,4)(3,4)(4,3)(3,2)}/[(1,2), (3,2)]{(1,2)(2,2)(3,2)}/[(1,2), (3,2)]
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (PixelEdge edge : this) {
			sb.append(edge.toString());
		}
		return sb.toString();
	}

	public boolean contains(PixelEdge edge) {
		ensureList();
		return list.contains(edge);
	}

	public boolean remove(PixelEdge edge) {
		ensureList();
		return list.remove(edge);
	}

	public SVGElement getOrCreateSVG() {
		if (svgg == null) {
			svgg = new SVGG();
			for (PixelEdge edge : this) {
				svgg.appendChild(edge.getOrCreateSVG());
			}
		}
		return svgg;
	}
	
	/**
	 * sorts by comparator.
	 * 
	 * only one direction
	 * 
	 */
	public void sort(ComparatorType comparatorType) {
		Collections.sort(list, new PixelEdgeComparator(comparatorType));
	}

	void segmentAllEdges(double segmentCreationTolerance) {
		for (PixelEdge edge : this) {
			PixelSegmentList segments = edge.getOrCreateSegmentList(segmentCreationTolerance);
		}
	}

	public void reverse() {
		Collections.reverse(list);
	}

	public void addAll(PixelEdgeList edgeList1) {
		if (edgeList1 != null) {
			for (PixelEdge edge1 : edgeList1) {
				this.add(edge1);
			}
		}
	}

}
