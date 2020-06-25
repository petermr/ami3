package org.contentmine.graphics.svg.text;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGG;

/** holds a phrase within a line.
 * 
 * A wordLine may have widely separated sections, here called phrases. A phrase is made up of words.
 * 
 * Currently driven by <p> elements emitted by Tesseract. These in turn hold lines and words.
 * Still exploratory
 * 
 * @author pm286
 *
 */
public class SVGWordPhrase extends SVGG {

	
	private static final Logger LOG = LogManager.getLogger(SVGWordPhrase.class);
public static final String CLASS = "wordPhrase";
	
	/**
	 * TESSERACT O/P
	 *   <div class='ocr_page' id='page_1' title='image "ijs.0.003566-0-000.pbm.png"; bbox 0 0 1065 592; ppageno 0'>
           <div class='ocr_carea' id='block_1_1' title="bbox 3 36 1064 550"> ...
	 */
	public SVGWordPhrase() {
		super();
		this.setSVGClassName(CLASS);
	}


}
