package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlColgroup;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSColgroupElement extends AbstractJATSHtmlElement implements IsBlock {

	public static String TAG = "colgroup";

	public JATSColgroupElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlColgroup());
	}

	
	

}
