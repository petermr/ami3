package org.contentmine.graphics.svg.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;

/** holds a "paragraph".
 * 
 * Currently driven by <p> elements emitted by Tesseract. These in turn hold lines and words.
 * Still exploratory
 * 
 * @author pm286
 *
 */
public class SVGWordPara extends SVGG {

	
	private static final Logger LOG = LogManager.getLogger(SVGWordPara.class);
public static final String CLASS = "para";
	
	public SVGWordPara() {
		super();
		this.setSVGClassName(CLASS);
	}

	public List<SVGWordLine> getSVGLineList() {
		List<Element> elements = XMLUtil.getQueryElements(this, "*[@class='"+SVGWordLine.CLASS+"']");
		List<SVGWordLine> wordLineList = new ArrayList<SVGWordLine>();
		for (Element element : elements) {
			wordLineList.add((SVGWordLine) element);
		}
		return wordLineList;
	}


}
