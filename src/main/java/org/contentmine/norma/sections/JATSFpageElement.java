package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSFpageElement extends JATSElement implements IsInline {

	public static String TAG = "fpage";

	public JATSFpageElement(Element element) {
		super(element);
	}
	
	public JATSFpageElement() {
		super(TAG);
	}
	
	public String debugString(int level) {
		return "f: "+this.getValue();
	}

	

}
