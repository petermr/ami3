package org.contentmine.graphics.svg.util;

import java.util.Iterator;
import java.util.List;

import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;

import nu.xom.Elements;

public class SVGElementIterator implements Iterator<SVGElement> {

	private Iterator<? extends SVGElement> iterator;
	private ElementsIterator elementsIterator;
	
	public SVGElementIterator(List<? extends SVGElement> elementList) {
		iterator = elementList.iterator();
	}
	public SVGElementIterator(Elements elements) {
		elementsIterator = new ElementsIterator(elements);
	}
	public boolean hasNext() {
		return iterator != null ? iterator.hasNext() : elementsIterator.hasNext();
	}
	public void remove() {
		if (iterator != null) {
			iterator.remove();
		} else {
			elementsIterator.remove();
		}
	}
	

	public SVGElement next() {
		return iterator.next();
	}

}
class ElementsIterator {
	int index;
	private Elements elements;
	public ElementsIterator(Elements elements) {
		this.elements = elements;
		index = 0;
	}
	
	boolean hasNext() {
		return index < elements.size();
	}

	AbstractCMElement next() {
		return (AbstractCMElement) elements.get(index++);
	}
	
	// this adances the pointer. Apparently it's mandatory
	public void remove() {
		index++;
	}
	
}
