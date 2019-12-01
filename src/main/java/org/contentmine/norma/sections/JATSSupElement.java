package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSup;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSSupElement extends AbstractJATSHtmlElement implements IsInline {

	public static String TAG = "sup";

	public JATSSupElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlSup());
	}

	
	

}
