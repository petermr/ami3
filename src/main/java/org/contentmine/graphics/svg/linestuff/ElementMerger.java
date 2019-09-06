package org.contentmine.graphics.svg.linestuff;

import org.contentmine.graphics.AbstractCMElement;

/** merges two or more SVGElements
 * <p>
 * An embryonic start of systematising element merging
 * </p>
 * @author pm286
 *
 */
public abstract class ElementMerger {

	protected AbstractCMElement elem0;
	protected double eps;

	/** this could be messy
	 * it is recommended to use a visitor pattern and reflection
	 * this is a quick lashup
	 * 
	 * @param elem to merge
	 * @return new element (null if none created)
	 */
//	private static ElementMerger createElementMerger(SVGElement elem, double eps) {
//		ElementMerger elementMerger = null;
//		if (elem != null && elem instanceof SVGLine) {
//			elementMerger = new LineMerger((SVGLine)elem, eps);
//		}
//		return elementMerger;
//	}
	
	public ElementMerger(AbstractCMElement elem, double eps) {
		this.elem0 = elem;
		this.eps = eps;
	}
	
	/** this could be messy
	 * it is recommended to use a visitor pattern and reflection
	 * this is a quick lashup
	 * 
	 * @param elem to merge
	 * @return new element (null if none created)
	 */
	public abstract AbstractCMElement createNewElement(AbstractCMElement elem);

}
