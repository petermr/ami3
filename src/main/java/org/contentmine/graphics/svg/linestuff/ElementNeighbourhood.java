package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;

/** the geometric neighbourhood of an element.
 * <p>
 * Used to determine whether elements are touching, need merging, etc.
 * </p>
 * @author pm286
 *
 */
public class ElementNeighbourhood {
	private SVGElement element;
	private List<SVGElement> neighbourList;
	private Real2Range extendedBBox;
	
	public ElementNeighbourhood(SVGElement element) {
		this.element = element;
	}

	public AbstractCMElement getElement() {
		return element;
	}

	public List<SVGElement> getNeighbourList() {
		ensureNeighbourList();
		return neighbourList;
	}
	
	private Real2Range ensureExtendedBoundingBox(double eps) {
		if (extendedBBox == null) {
			extendedBBox = BoundingBoxManager.createExtendedBox(element, eps);
		}
		return extendedBBox;
	}

	boolean isTouching(SVGElement fpn, double eps) {
		ensureExtendedBoundingBox(eps);
		Real2Range fpnBox = fpn.getBoundingBox();
		return fpnBox.intersectionWith(extendedBBox) != null;
	}

	void addNeighbour(SVGElement neighbour) {
		ensureNeighbourList();
		neighbourList.add(neighbour);
	}

	void addNeighbourList(List<SVGElement> newNeighbourList) {
		ensureNeighbourList();
		neighbourList.addAll(newNeighbourList);
	}

	private void ensureNeighbourList() {
		if (neighbourList == null) {
			neighbourList = new ArrayList<SVGElement>();
		}
	}

	private void remove(AbstractCMElement oldElem) {
		if (neighbourList != null) {
			if (!neighbourList.remove(oldElem))  {
				throw new RuntimeException("cannot remove oldElem"); 
			}
		}
	}
	
	/*
	private SVGElement element;
	private List<SVGElement> neighbourList;
	private Real2Range extendedBBox;
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n ......element "+ element.getId());
		sb.append("\n ......neighbours: "+((neighbourList == null) ? 0 : neighbourList.size()));
		if (neighbourList != null) {
			for (SVGElement neighbour : neighbourList) {
				sb.append("\n ........."+neighbour.getId());
			}
		}
		return sb.toString();
	}
}
