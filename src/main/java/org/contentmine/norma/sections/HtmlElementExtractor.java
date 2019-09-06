package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlElement;

/** defines an abstractor of divs
 * 
 * @author pm286
 *
 */
public interface HtmlElementExtractor {
	public HtmlElement getHtmlElement(JATSSectionTagger tagger);
}
	
