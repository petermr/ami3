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
public class SVGWordPage extends SVGG {

	
	private static final Logger LOG = Logger.getLogger(SVGWordPage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String CLASS = "wordPage";
	
	/**
	 * TESSERACT O/P
	 *   <div class='ocr_page' id='page_1' title='image "ijs.0.003566-0-000.pbm.png"; bbox 0 0 1065 592; ppageno 0'>
           <div class='ocr_carea' id='block_1_1' title="bbox 3 36 1064 550"> ...
	 */
	public SVGWordPage() {
		super();
		this.setSVGClassName(CLASS);
	}

	public List<SVGWordBlock> getSVGBlockList() {
		List<Element> elements = XMLUtil.getQueryElements(this, "*[@class='"+SVGWordBlock.CLASS+"']");
		List<SVGWordBlock> wordBlockList = new ArrayList<SVGWordBlock>();
		for (Element element : elements) {
			wordBlockList.add((SVGWordBlock) element);
		}
		return wordBlockList;
	}

	public List<SVGWordLine> getSVGLineList() {
		List<Element> elements = XMLUtil.getQueryElements(this, ".//*[@class='"+SVGWordLine.CLASS+"']");
		List<SVGWordLine> wordLineList = new ArrayList<SVGWordLine>();
		for (Element element : elements) {
			wordLineList.add((SVGWordLine) element);
		}
		return wordLineList;
	}



}
