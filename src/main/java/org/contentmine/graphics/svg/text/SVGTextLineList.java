package org.contentmine.graphics.svg.text;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

public class SVGTextLineList extends SVGG implements List<SVGTextLine> {
	private static final Logger LOG = Logger.getLogger(SVGTextLineList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "textLineList";

	private List<SVGTextLine> textLineList;
	
	public SVGTextLineList() {
		super(TAG);
		textLineList = new ArrayList<SVGTextLine>();
	}

	public SVGTextLineList(List<SVGTextLine> textLineList) {
		this.textLineList = new ArrayList<SVGTextLine>(textLineList);
	}

	public int size() {
		return textLineList.size();
	}

	public boolean isEmpty() {
		return textLineList.isEmpty();
	}

	public boolean contains(Object o) {
		return textLineList.contains(o);
	}

	public Iterator<SVGTextLine> iterator() {
		return textLineList.iterator();
	}

	public Object[] toArray() {
		return textLineList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return textLineList.toArray(a);
	}

	public boolean add(SVGTextLine e) {
		return textLineList.add(e);
	}

	public boolean remove(Object o) {
		return textLineList.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return textLineList.containsAll(c);
	}

	public boolean addAll(Collection<? extends SVGTextLine> c) {
		return textLineList.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends SVGTextLine> c) {
		return textLineList.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return textLineList.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return textLineList.retainAll(c);
	}

	public void clear() {
		textLineList.clear();
	}

	public SVGTextLine get(int index) {
		return textLineList.get(index);
	}

	public SVGTextLine set(int index, SVGTextLine element) {
		return textLineList.set(index, element);
	}

	public void add(int index, SVGTextLine element) {
		textLineList.add(element);
	}

	public SVGTextLine remove(int index) {
		return textLineList.remove(index);
	}

	public int indexOf(Object o) {
		return textLineList.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return textLineList.lastIndexOf(o);
	}

	public ListIterator<SVGTextLine> listIterator() {
		return textLineList.listIterator();
	}

	public ListIterator<SVGTextLine> listIterator(int index) {
		return textLineList.listIterator(index);
	}

	public List<SVGTextLine> subList(int fromIndex, int toIndex) {
		return textLineList.subList(fromIndex, toIndex);
	}

	public RealArray calculateIndents(int ndecimal) {
		RealArray indents= new RealArray();
		for (SVGTextLine textLine : textLineList) {
			double x = textLine.getLeftX();
			indents.addElement(x);
		}
		indents.format(ndecimal);
		return indents;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SVGTextLine textLine : textLineList) {
			sb.append(textLine.toString()+"\n");
		}
		return sb.toString();
	}

	public SVGG createSVGElement() {
		SVGG g = new SVGG();
		for (SVGTextLine textLine : textLineList) {
			g.appendChild(textLine.forceFullSVGElement().copy());
		}
		return g;
	}

	public List<Double> getYCoords() {
		List<Double> yCoordList = new ArrayList<Double>();
		for (SVGTextLine textLine : textLineList) {
			yCoordList.add((Double)textLine.getY());
		}
		return yCoordList;
	}

	public List<SVGTextLine> getTextLineList() {
		return textLineList;
	}
	
	public Real2Range getBoundingBox() {
		Real2Range bbox = textLineList.size() == 0 ? null : textLineList.get(0).getBoundingBox();
		if (bbox != null) {
			for (int i = 1; i < textLineList.size(); i++) {
				bbox = bbox.plus(textLineList.get(i).getBoundingBox());
			}
		}
		return bbox;
	}

	/** removes lines in this which "are the same".
	 * crude quadratic since we don't yet know what equality is
	 * 
	 * @param textLineList
	 */
	public void removeDuplicates(SVGTextLineList textLineList2) {
		for (int index = this.size() - 1; index >= 0; index--) {
			SVGTextLine lineIndex = this.get(index);
			for (int index2 = 0; index2 < textLineList2.size(); index2++) {
				SVGTextLine lineIndex2 = textLineList2.get(index2);
				if (lineIndex.compareTo(lineIndex2) == 0) {
					this.remove(index);
				}
			}
		}
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (SVGTextLine textLine : this) {
			sb.append("\n");
			sb.append(textLine.getText());
		}
		return sb.toString();
	}

	/** annotate with type of word
	 * @return 
	 * 
	 */
	public List<String> getOrCreateTypeAnnotations() {
		List<String> typeAnnotations = new ArrayList<>();
		for (SVGTextLine textLine : this) {
			typeAnnotations.add(textLine.getOrCreateTypeAnnotatedString());
		}
		return typeAnnotations;
	}
	
	/** re-reads textLines 
	 *   Finds all g's of form
	 *   <g class="textLine">
	 *      <text x="14.0" y="56.0" class="text" style="font-size:13.0px;">Kuklo</text>
	 *   </g>

	 * @param svgElement
	 * @return
	 */
	public static SVGTextLineList createSVGTextLineList(SVGElement svgElement) {
		String xpath = ".//*[local-name()='"+SVGG.TAG+"' and @class='"+SVGTextLine.TAG+"' and *[local-name()='"+SVGText.TAG+"']]";
		List<SVGElement> textLines = SVGUtil.getQuerySVGElements(svgElement, 
				xpath);
		SVGTextLineList textLineList = new SVGTextLineList();
		for (SVGElement tl : textLines) {
			SVGTextLine textLine = SVGTextLine.createSVGTextLine(tl);
//			LOG.debug(">>"+textLine.getOrCreateTypeAnnotatedString());
			textLineList.add(textLine);
		}
		return textLineList;
	}
	
	public void splitAtCharacters(String splitters) {
		for (SVGTextLine textLine : this) {
			textLine.splitAtCharacters(splitters);
		}
	}

}
