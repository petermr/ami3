package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSBoldElement extends AbstractJATSHtmlElement implements IsInline {

	public static String TAG = "bold";

	public JATSBoldElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlB());
	}
	
	

}
