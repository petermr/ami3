package org.contentmine.graphics.html;

import java.util.List;

import nu.xom.Attribute;

/** properties possed by both th and td cells
 * 
 * @author pm286
 *
 */
public abstract class HtmlTCell extends HtmlElement {

	public final static String ROWSPAN = "rowspan";
	
	protected HtmlTCell(String tag) {
		super(tag);
	}
	
	public int getColspan() {
		return getSpan(HasColspan.COLSPAN);
	}
	
	public int getRowspan() {
		return getSpan(ROWSPAN);
	}
	
	int getSpan(String spanName) {
		String attVal = getAttributeValue(spanName);
		int span = (attVal) == null ? 1 : Integer.parseInt(attVal);
		return span;
	}
	
	int getMaxspan(String spanName, int maxspan) {
		int span = getSpan(spanName);
		if (span > maxspan) maxspan = span;
		return maxspan;
	}
	
	public void addDefault(String attName, String defalt) {
		String attValue = this.getAttributeValue(attName);
		if (attValue == null) {
			this.addAttribute(new Attribute(attName, defalt));
		}
	}



}
