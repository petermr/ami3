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
public class SVGWordBlock extends SVGG {

	
	private static final Logger LOG = LogManager.getLogger(SVGWordBlock.class);
public static final String CLASS = "wordBlock";
	
	public SVGWordBlock() {
		super();
		this.setSVGClassName(CLASS);
	}

	public List<SVGWordPara> getSVGParaList() {
		List<Element> elements = XMLUtil.getQueryElements(this, "*[@class='"+SVGWordPara.CLASS+"']");
		List<SVGWordPara> wordParaList = new ArrayList<SVGWordPara>();
		for (Element element : elements) {
			wordParaList.add((SVGWordPara) element);
		}
		return wordParaList;
	}



}
