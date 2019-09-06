package org.contentmine.graphics.svg.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

	
	private static final Logger LOG = Logger.getLogger(SVGWordBlock.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
