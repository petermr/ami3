package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSPubIdElement extends JATSElement implements IsInline {

	public static String TAG = "pub-id";

	public JATSPubIdElement(Element element) {
		super(element);
	}
	
	
	public String debugString(int level) {
		return "";
//		return "p: "+this.getValue();
	}


}
