package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlCol;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSColElement extends AbstractJATSHtmlElement implements IsInline {

	public static String TAG = "col";

	public JATSColElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlCol());
	}


	

}
