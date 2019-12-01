package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSMediaElement extends JATSElement implements IsAnchor {

	public static String TAG = "media";

	public JATSMediaElement(Element element) {
		super(element);
	}
	
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute(TAG);
		return deepCopyAndTransform(div);
	}



}
