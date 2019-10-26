package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSLpageElement extends JATSElement implements IsInline {

	public static String TAG = "lpage";

	public JATSLpageElement(Element element) {
		super(element);
	}
	
	public String debugString(int level) {
//		return "p: "+this.getValue();
		return "";
	}

	
	

}
