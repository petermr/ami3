package org.contentmine.norma.sections;

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
	
	
	

}
