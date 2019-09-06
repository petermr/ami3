package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ElementNeighbourhoodManager {

	private final static Logger LOG = Logger.getLogger(ElementNeighbourhoodManager.class);
	
	private Map<SVGElement, ElementNeighbourhood> neighbourhoodByElementMap;
	private Multimap<Integer, SVGElement> elementsByXMap;
	private Integer deltaX = 5;
	private Multimap<Integer, SVGElement> elementsByYMap;
	private Integer deltaY = 5;
	private List<SVGElement> elementList;
	private BoundingBoxManager bboxManager;
	private Real2Range totalBox;
	private RealRange totalXRange;
	private RealRange totalYRange;
	
	public Integer getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(Integer deltaX) {
		this.deltaX = deltaX;
	}

	public Integer getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(Integer deltaY) {
		this.deltaY = deltaY;
	}

//	public List<ElementNeighbourhood> getElementNeighboursList() {
//		return elementNeighbourhoodList;
//	}
//
	public Map<SVGElement, ElementNeighbourhood> getElement2NeighboursMap() {
		return neighbourhoodByElementMap;
	}

	public Multimap<Integer, SVGElement> getElementsByXMap() {
		return elementsByXMap;
	}

	public Multimap<Integer, SVGElement> getElementsByYMap() {
		return elementsByYMap;
	}

	public List<SVGElement> getElementList() {
		return elementList;
	}

	public BoundingBoxManager getBboxManager() {
		return bboxManager;
	}

	public Real2Range getTotalBox() {
		return totalBox;
	}

	public RealRange getTotalXRange() {
		return totalXRange;
	}

	public RealRange getTotalYRange() {
		return totalYRange;
	}

	public ElementNeighbourhoodManager() {
		
	}

	public ElementNeighbourhoodManager(List<? extends SVGElement> elementList) {
		setElementList(elementList);
		createAndPopulateBoundingBoxManager();
		createAndPopulateIntegerIndexes();
	}

	private void setElementList(List<? extends SVGElement> elementList) {
		this.elementList = new ArrayList<SVGElement>();
		for (SVGElement element : elementList) {
			this.elementList.add(element);
		}
	}

	private BoundingBoxManager createAndPopulateBoundingBoxManager() {
		this.bboxManager = new BoundingBoxManager();
		bboxManager.setBBoxList(BoundingBoxManager.createBBoxList(elementList));
		totalBox = bboxManager.getTotalBox();
		return bboxManager;
	}
	
	private void createAndPopulateIntegerIndexes() {
		indexElementsByIntegers();
	}

	private void indexElementsByIntegers() {
		ensureElementsByXMap();
		ensureElementsByYMap();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			addElementToIntegerIndex(element, bbox.getXRange(), deltaX, elementsByXMap);
			addElementToIntegerIndex(element, bbox.getYRange(), deltaY, elementsByYMap);
		}
	}

	private void addElementToIntegerIndex(SVGElement element, RealRange range, Integer delta, Multimap<Integer, SVGElement> elementsByMap) {
		List<Integer> ii = mapToIntegers(range, delta);
		for (Integer i : ii) {
			elementsByMap.put(i, element);
		}
	}

	private void ensureElementsByXMap() {
		if (elementsByXMap == null) {
			elementsByXMap = ArrayListMultimap.create();
		}
	}

	private void ensureElementsByYMap() {
		if (elementsByYMap == null) {
			elementsByYMap = ArrayListMultimap.create();
		}
	}
	
	private Integer mapXToInteger(double x) {
		return mapToInteger(x, deltaX);
	}

	private Integer mapYToInteger(double y) {
		return mapToInteger(y, deltaY);
	}
	
	private Integer mapToInteger(double d, Integer delta) {
		return new Integer (((int)d) / delta) * delta;
	}
	
	private List<Integer> mapToIntegers(RealRange range, Integer delta) {
		List<Integer> integers = new ArrayList<Integer>();
		Integer low = mapToInteger(range.getMin(), delta);
		Integer high = mapToInteger(range.getMax(), delta) + delta;
		for (Integer i = low; i <= high; i+=delta) {
			integers.add(i);
		}
		return integers;
	}

	private Set<SVGElement> findNeighboursByIntegerGrid(Real2Range r2r) {
		if (elementsByXMap == null || elementsByYMap == null) {
			throw new RuntimeException("coordinates have not been indexed");
		}
		List<Integer> xint = mapToIntegers(r2r.getXRange(), deltaX);
		Set<SVGElement> xelements = getElementSet(xint, elementsByXMap);
		List<Integer> yint = mapToIntegers(r2r.getYRange(), deltaY);
		Set<SVGElement> yelements = getElementSet(yint, elementsByYMap);
		return intersect(xelements, yelements);
	}

	private Set<SVGElement> getFirstPassNeighbours(SVGElement elem) {
		Real2Range bbox = elem.getBoundingBox();
		Set<SVGElement> firstPass = findNeighboursByIntegerGrid(bbox);
		firstPass.remove(elem);
		return firstPass;
	}

	private Set<SVGElement> intersect(Set<SVGElement> elems1, Set<SVGElement> elems2) {
		Set<SVGElement> intersectSet = new HashSet<SVGElement>();
		for (SVGElement elem : elems1) {
			if (elems2.contains(elem)) {
				intersectSet.add(elem);
			}
		}
		return intersectSet;
	}

	private Set<SVGElement> getElementSet(List<Integer> ints, Multimap<Integer, SVGElement> multimap) {
		Set<SVGElement> elementSet = new HashSet<SVGElement>();
		for (Integer i : ints) {
			Collection<SVGElement> elements = multimap.get(i);
			for (SVGElement element : elements) {
				elementSet.add(element);
			}
		}
		return elementSet;
	}

	private ElementNeighbourhood getTouchingNeighbours(SVGElement elem, double eps) {
		Set<SVGElement> fpNeighbours = getFirstPassNeighbours(elem);
		ElementNeighbourhood elementNeighbours = new ElementNeighbourhood(elem);
		for (SVGElement fpn : fpNeighbours) {
			if (elementNeighbours.isTouching(fpn, eps)) {
				elementNeighbours.addNeighbour(fpn);
			}
		}
		ensureNeighbourhoodByElementMap();
		neighbourhoodByElementMap.put(elem, elementNeighbours);
//		ensureNeighbourhoodList();
//		addUnique(elementNeighbourhoodList, elementNeighbours);
		return elementNeighbours;
	}
	

	private void ensureNeighbourhoodByElementMap() {
		if (neighbourhoodByElementMap == null) {
			neighbourhoodByElementMap = new HashMap<SVGElement, ElementNeighbourhood>();
		}
	}

//	private void ensureNeighbourhoodList() {
//		if (elementNeighbourhoodList == null) {
//			elementNeighbourhoodList = new ArrayList<ElementNeighbourhood>();
//		}
//	}

	void createTouchingNeighbours(double eps) {
		for (SVGElement elem : elementList) {
			LOG.trace("+++++++++++++++++ creating touching neighbours for "+elem.getId());
			ElementNeighbourhood en = this.getNeighbourhood(elem);
			LOG.trace(">>>>> "+((en == null) ? null : en.getNeighbourList().size()));
			this.getTouchingNeighbours(elem, eps);
		}
	}

	public ElementNeighbourhood getNeighbourhood(AbstractCMElement svgElement) {
		ensureNeighbourhoodByElementMap();
		return neighbourhoodByElementMap.get(svgElement);
	}

	/** should not be here (or change "Lines")
	 * 
	 * @param complexLine
	 * @param eps
	 */
	private void addLinesTo(ComplexLine complexLine, double eps) {
		ElementNeighbourhood neighbourhood = this.getTouchingNeighbours(complexLine.getBackbone(), eps);
		List<SVGElement> elements = neighbourhood.getNeighbourList();
		complexLine.addLines(SVGLine.extractLines(elements));
	}

	/** remove from all elements which have this as neighbour
	 * 
	 * @param oldLine
	 */
	private void removeElement(SVGElement element) {
		ElementNeighbourhood neighbourhood = this.getNeighbourhood(element);
		// remove neighbours
		if (neighbourhood != null) {
			List<SVGElement> neighbours = neighbourhood.getNeighbourList();
			neighbours.remove(element);
		}
		
		// remove neighbourhood of element
		neighbourhoodByElementMap.remove(element);
		removeElement(elementsByXMap, element);
		removeElement(elementsByYMap, element);
		elementList.remove(element);
	}

	/** this is very inefficient, but... 
	 * there are probably only a 100 keys
	 * 
	 * @param elementsByCoordMap
	 * @param element
	 */
	private void removeElement(Multimap<Integer, SVGElement> elementsByCoordMap, SVGElement element) {
		Set<Integer> keys = elementsByCoordMap.keySet();
		//because we cannot remove while in loop
		List<Integer> ii = new ArrayList<Integer>();
		for (Integer i : keys) {
			ii.add(i);
		}
		for (Integer i : ii) {
			if (elementsByCoordMap.remove(i, element)) {
				LOG.trace("removed: "+element.getId());
			}
		}
	}
	
	void replaceElementsByElement(SVGElement newElement, List<SVGElement> oldElements) {
		LOG.trace("new Element "+newElement.getId() + "replaces...");
		List<SVGElement> oldNeighbourList = new ArrayList<SVGElement>();
		for (SVGElement oldElement : oldElements) {
			LOG.trace(" ... "+oldElement.getId());
			ElementNeighbourhood oldNeighbourhood = this.getNeighbourhood(oldElement);
			if (oldNeighbourhood != null) {
				List<SVGElement> oldNeighbours = oldNeighbourhood.getNeighbourList();
				LOG.trace("      transferring "+oldNeighbours+" old neighbours");
				oldNeighbourList.addAll(oldNeighbours);
			}
			removeElement(oldElement);
		}
		for (SVGElement oldElement : oldElements) {
			removeElement(oldElement);
			oldNeighbourList.remove(oldElement);
		}
		for (SVGElement oldNeighbour : oldNeighbourList) {
			LOG.trace("... preserve ... "+oldNeighbour.getId());
		}
		addElement(newElement, oldNeighbourList);
	}

	private void addElement(SVGElement newElement, List<SVGElement> knownNeighbours) {
		ElementNeighbourhood neighbourhood = this.getNeighbourhood(newElement);
		if (neighbourhood != null) {
			throw new RuntimeException("neighbourhood manager already contains neighbourhood for "+newElement.getId());
		}
		neighbourhood = new ElementNeighbourhood(newElement);
		neighbourhood.addNeighbourList(knownNeighbours);
		neighbourhoodByElementMap.put(newElement, neighbourhood);
		ensureElementList();
		elementList.add(newElement);
		LOG.trace("after add: "+elementList.size());
	}
	
	private void ensureElementList() {
		if (elementList == null) {
			elementList = new ArrayList<SVGElement>();
		}
	}

	private void addElement(SVGElement newElement) {
		addElement(newElement, new ArrayList<SVGElement>());
	}

	/** never used - an attempt to generalize 
	 * */
//	private static List<SVGElement> mergeElements(List<SVGElement> elements, double eps) {
//		ElementNeighbourhoodManager enm = new ElementNeighbourhoodManager(elements);
//		List<SVGElement> elems;
//		while (true) {
//			enm.createTouchingNeighbours(eps);
//			elems = enm.getElementList();
//			SVGElement newElem = null;
//			SVGElement oldElem = null;
//			SVGElement oldElem1 = null;
//			for (int i = 0; i < elems.size(); i++) {
//				oldElem1 = elems.get(i);
//				ElementMerger elementMerger = ElementMerger.createElementMerger(oldElem1, eps);
//				ElementNeighbourhood neighbourhood = enm.getNeighbourhood(oldElem1);
//				if (neighbourhood == null) {
//					continue;
//				}
//				List<SVGElement> neighbours = neighbourhood.getNeighbourList();
//				for (SVGElement neighbour : neighbours) {
//					oldElem = neighbour;
//					newElem = elementMerger.createNewElement(oldElem);
//					if (newElem != null) {
//						LOG.trace(oldElem1.getId()+" + "+oldElem.getId()+" => "+newElem.getId());
//						break;
//					}
//				} 
//				if (newElem != null) {
//					enm.replaceElementsByElement(newElem, Arrays.asList(new SVGElement[] {oldElem, oldElem1}));
//					break;
//				}
//			} // end of loop through elements
//			if (newElem == null) {
//				break;
//			}
//		} // end of infinite loop
//		return enm.getElementList();
//	}

	/*
	 * 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (neighbourhoodByElementMap != null) {
			sb.append(" neighbourhoodByElementMap "+ neighbourhoodByElementMap.size()+"\n");
			for (ElementNeighbourhood en : neighbourhoodByElementMap.values()) {
				sb.append(" ... "+en+"\n");
			}
		}
		return sb.toString()+"\n";
	}
}
