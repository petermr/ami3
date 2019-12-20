package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSub;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSSubElement extends AbstractJATSHtmlElement implements IsInline {

	public static String TAG = "sub";

	public JATSSubElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlSub());
	}

	

}
