package org.contentmine.svg2xml.font;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGText;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class SVG2XMLCharacter /*extends SVGElement*/ implements Comparable<Object> {
	
	

	private final static Logger LOG = Logger.getLogger(SVG2XMLCharacter.class);
	
	public final static String TAG = "svgCharacter";
	private static final Double EPS = 0.1;
	private static final PrintStream SYSOUT = System.out;
	
	private Multiset<Double> pdfWidthSet;
	private Multiset<Double> observedWidthSet;
	private List<Double> observedSortedWidthList;
	private SVG2XMLFont font;
	private SVGText svgText;
	
	private Long unicodePoint;


	public SVG2XMLCharacter(long unicodePoint, SVG2XMLFont font) {
		this.setUnicodePoint(unicodePoint);
		this.font = font;
	}
	
	private void setUnicodePoint(long unicodePoint) {
		this.unicodePoint = unicodePoint;
	//	this.addAttribute(new Attribute("unicode", String.valueOf(unicodePoint)));
	}

	public SVG2XMLCharacter(SVGText text, SVG2XMLFont font) {
		if (text != null) {
			this.svgText = text;
			String value = text.getValue();
			if (value.length() == 1) {
		// crude - only BMP
				this.setUnicodePoint((long) text.getValue().charAt(0));
			} else {
				this.setUnicodePoint(new Long(127)); // equivalent of null?
			}
			this.font = font;
			Double width = text.getSVGXFontWidth();
			ensurePDFWidthSet();
			pdfWidthSet.add(width);
		}
	}

	private void ensurePDFWidthSet() {
		if (pdfWidthSet == null) {
			pdfWidthSet = HashMultiset.create();
		}
	}

	public int compareTo(Object object) {
		int result = -1;
		if (object instanceof SVG2XMLCharacter) {
			SVG2XMLCharacter objChar = (SVG2XMLCharacter) object; 
			result = unicodePoint.compareTo(objChar.unicodePoint); 
		}
		return result;
	}

	public Long getUnicodePoint() {
		return unicodePoint;
	}
	
	public void debug(String msg) {
		ensureObservedWidthSet();
		Set<Multiset.Entry<Double>> set = (observedWidthSet.size() > 0) ? observedWidthSet.entrySet() : null;
		if (set != null) {
			observedSortedWidthList = new ArrayList<Double>();
			for (Multiset.Entry<Double> me : set) {
				if (me != null) {
					observedSortedWidthList.add(me.getElement());
				}
			}
			Double[] dd = observedSortedWidthList.toArray(new Double[0]);
			Arrays.sort(dd);
			for (int i = 0; i < dd.length; i++) {
				observedSortedWidthList.set(i, dd[i]);
			}

			Double delta = null;
			if (pdfWidthSet !=null && pdfWidthSet.size() > 0 && observedSortedWidthList.size() > 0) {
				Double pdfWidth = pdfWidthSet.iterator().next();
				Double obsWidth = observedSortedWidthList.get(0);
				delta = (pdfWidth == null || obsWidth == null) ? null : pdfWidth - obsWidth;
			}
				
			LOG.trace(unicodePoint+
					" "+(char)(int)(long)unicodePoint+
					" pw: "+pdfWidthSet+
					" ow: "+set.size()+((delta != null) ? " ("+
					Util.format(delta, 1)+") " : "")+
					observedSortedWidthList+" >> "+
					"");
		}
	}

	public void addWidthCalculatedFrom(SVG2XMLCharacter character1) {
		SVGText svgText1 = character1.getSVGText();
		Real2 xy = svgText == null ? null : svgText.getXY();
		Real2 xy1 = svgText1 == null ? null : svgText1.getXY();
		ensureObservedWidthSet();
		if (Real.isEqual(xy.getY(), xy1.getY(), EPS) &&
				Real.isEqual(svgText.getFontSize(), svgText1.getFontSize(), EPS)) {
			Double width = xy1.getX() - xy.getX();
			width = (width / svgText.getFontSize()) * 1000;
			width = Util.format(width, 1);
			observedWidthSet.add(width);
			Set<Multiset.Entry<Double>> set = (observedWidthSet.size() > 0) ? observedWidthSet.entrySet() : null;
		}
	}

	private void ensureObservedWidthSet() {
		if (observedWidthSet == null) {
			observedWidthSet = HashMultiset.create();
			LOG.trace("obs widths "+observedWidthSet.size());
		}
	}

	private SVGText getSVGText() {
		return svgText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result
				+ ((unicodePoint == null) ? 0 : unicodePoint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SVG2XMLCharacter other = (SVG2XMLCharacter) obj;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (unicodePoint == null) {
			if (other.unicodePoint != null)
				return false;
		} else if (!unicodePoint.equals(other.unicodePoint))
			return false;
		return true;
	}
	
	


}
