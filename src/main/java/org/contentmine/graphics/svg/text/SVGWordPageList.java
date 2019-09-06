package org.contentmine.graphics.svg.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;

public class SVGWordPageList extends SVGG {
	
	private static final Logger LOG = Logger.getLogger(SVGWordPageList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String CLASS = "wordPageList";
	private List<SVGWordPage> wordPageList;
	
	/**
	 * TESSERACT O/P
	 *   <div class='ocr_page' id='page_1' title='image "ijs.0.003566-0-000.pbm.png"; bbox 0 0 1065 592; ppageno 0'>
           <div class='ocr_carea' id='block_1_1' title="bbox 3 36 1064 550"> ...
	 */
	public SVGWordPageList() {
		super();
		this.setSVGClassName(CLASS);
	}

	public SVGWordPage get(int i) {
		getWordPageList();
		return wordPageList.get(i);
	}

	public List<SVGWordPage> getWordPageList() {
		if (wordPageList == null) {
			List<Element> elements = XMLUtil.getQueryElements(this, "*[@class='"+SVGWordPage.CLASS+"']");
			wordPageList = new ArrayList<SVGWordPage>();
			for (Element element : elements) {
				wordPageList.add((SVGWordPage) element);
			}
		}
		return wordPageList;
	}

	/** number of wordPages.
	 * 
	 * @return 0 if none 
	 */
	public int size() {
		return wordPageList == null ? 0 : wordPageList.size();
	}

}
