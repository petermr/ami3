package org.contentmine.norma.sections;

import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSPElement extends JATSElement implements HasMixedContent {

	public static String TAG = "p";

	public JATSPElement(Element element) {
		super(element);
	}
	
	public String debugString(int level) {
		return "<p>:"+Util.truncateAndAddEllipsis(getValue(), 40)+"\n";
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlP());
	}


}
