package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlUl;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSListElement extends JATSElement implements IsBlock {

	public static String TAG = "list";

	public JATSListElement(Element element) {
		super(element);
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlUl ul = new HtmlUl();
		return deepCopyAndTransform(ul);
	}


	

}
