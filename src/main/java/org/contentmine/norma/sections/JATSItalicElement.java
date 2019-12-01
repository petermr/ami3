package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlI;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSItalicElement extends AbstractJATSHtmlElement implements IsInline {

	public static String TAG = "italic";

	public JATSItalicElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlI());
	}

	

}
